package controllers

import baseSpec.BaseSpecWithApplication
import play.api.http.Status._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.{FakeRequest, Injecting}

class ApplicationControllerSpec extends BaseSpecWithApplication with Injecting {
  val TestApplicationController = new ApplicationController(
    component, repository, executionContext, service, repService)


  "ApplicationController .index" should {

    "return 200 OK" in {
      val resultFuture = TestApplicationController.index()(FakeRequest())
      await(resultFuture).header.status shouldBe OK
    }
  }

  "ApplicationController .getGithubUser" should {

    "return 200 OK" in {
      val existingUsername:String = "anthoskountouris"
      val resultFuture = TestApplicationController.getGithubUser(existingUsername)(FakeRequest())
      await(resultFuture).header.status shouldBe OK
    }

    "return 400" in {
      val nonExistingUsername:String = "panikkos1212121"
      val resultFuture = TestApplicationController.getGithubUser(nonExistingUsername)(FakeRequest())
      await(resultFuture).header.status shouldBe BAD_REQUEST
    }
  }
}
