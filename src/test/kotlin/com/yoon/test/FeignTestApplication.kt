package com.yoon.test

import org.mockserver.client.MockServerClient
import org.mockserver.model.Delay
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.TimeUnit

@SpringBootApplication
@EnableFeignClients
class FeignTestApplication {

  @RestController
  @FeignClient(name = "mock", fallbackFactory = TestFeignClientFallbackFactory::class)
  interface TestFeignClient {
    @GetMapping("/scucess-or-error")
    fun success(@RequestParam code: String): ResponseEntity<String>
  }

  @Component
  class TestFeignClientFallbackFactory : FallbackFactory<TestFeignClient> {
    override fun create(cause: Throwable?): TestFeignClient {
      return object : TestFeignClient {
        override fun success(@RequestParam code: String): ResponseEntity<String> {
          return ResponseEntity.internalServerError().build()
        }
      }
    }
  }

  @Bean(destroyMethod = "stop")
  fun mockServer(registry: DynamicPropertyRegistry): MockServerContainer {
    val mockServerContainer = MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"))
    mockServerContainer.start()
    println("${mockServerContainer.endpoint}/mockserver/dashboard")
    registry.add("spring.cloud.openfeign.client.config.mock.url", mockServerContainer::getEndpoint)
    return mockServerContainer
  }

  @Bean
  fun mockServiceClient(mockServerContainer: MockServerContainer): MockServerClient {
    val mockServerClient = MockServerClient(
      mockServerContainer.host,
      mockServerContainer.serverPort
    )

    testWhen(mockServerClient)
    return mockServerClient
  }

  private fun testWhen(mockServerClient: MockServerClient) {
    mockServerClient.`when`(
      request()
        .withMethod("GET")
        .withPath("/scucess-or-error")
    ).respond { httpRequest: HttpRequest? ->
      if (httpRequest?.getFirstQueryStringParameter("code") == "200") {
        response()
          .withStatusCode(200)
          .withBody("success")
          .withDelay(Delay.delay(TimeUnit.MILLISECONDS, 100))
      } else if(httpRequest?.getFirstQueryStringParameter("code") == "406") {
        response()
          .withStatusCode(406)
          .withBody("not acceptable")
          .withDelay(Delay.delay(TimeUnit.MILLISECONDS, 100))
      } else {
        response()
          .withStatusCode(500)
          .withBody("internal server error")
          .withDelay(Delay.delay(TimeUnit.MILLISECONDS, 100))
      }
    }
  }
}

fun main() {
  runApplication<FeignTestApplication>()
}

