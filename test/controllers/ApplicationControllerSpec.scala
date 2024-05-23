package controllers

import baseSpec.BaseSpecWithApplication
import models.GitHubUser
import play.api.http.Status
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test
import play.api.test.Helpers.{await, contentAsJson, defaultAwaitTimeout}
import play.api.test.{FakeRequest, Injecting}

import java.awt.AWTEvent
import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication with Injecting {
  val TestApplicationController = new ApplicationController(
    component, repository, executionContext, service, repService)

  private val dataModel:GitHubUser = GitHubUser (
    login = "sebdroid",
    created_at = Some("2013-06-11T16:59:23Z"),
    followers = Some(16),
    following = Some(16)
  )

  private val dataModel2:GitHubUser = GitHubUser (
    login = "kokos",
    created_at = Some("2013-06-11T16:59:23Z"),
    followers = Some(133),
    following = Some(22)
  )


  "ApplicationController .index" should {

    "return 200 OK" in {
      val resultFuture = TestApplicationController.index()(FakeRequest())
      await(resultFuture).header.status shouldBe OK
    }
  }

  "ApplicationController .getGithubUser()" should {

    "return a user from the api (return 200 OK)" in {
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

  "ApplicationController .create()" should {
    "create a user in the database (return 201 CREATED)" in {
      val request:FakeRequest[JsValue] = buildPost("/create").withBody[JsValue](Json.toJson(dataModel))
      val result: Future[Result] = TestApplicationController.create()(request)
      await(result).header.status shouldBe CREATED

      val readResult:Future[Result] = TestApplicationController.read("sebdroid")(FakeRequest())
      await(readResult).header.status shouldBe OK
    }
  }

  "ApplicationController .read()" should {

    val request:FakeRequest[JsValue] = buildPost("/create").withBody[JsValue](Json.toJson(dataModel))
    val result: Future[Result] = TestApplicationController.create()(request)
    await(result).header.status shouldBe CREATED

    "return a user from the database based on name (return 200 OK)" in {

      val readResult:Future[Result] = TestApplicationController.read("sebdroid")(FakeRequest())
      await(readResult).header.status shouldBe OK
    }
    "user not in database (return 400 BAD_REQUEST)" in {

      val readResult2:Future[Result] = TestApplicationController.read("sebefrferdroid")(FakeRequest())
      await(readResult2).header.status shouldBe BAD_REQUEST

    }
  }

  "ApplicationController .update()" should {
    "update the user in the database based on username" in {
      val request:FakeRequest[JsValue] = buildPost("/create").withBody[JsValue](Json.toJson(dataModel))
      val result: Future[Result] = TestApplicationController.create()(request)
      await(result).header.status shouldBe CREATED

      val readResult:Future[Result] = TestApplicationController.read("sebdroid")(FakeRequest())
      await(readResult).header.status shouldBe OK
      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(dataModel)

      val updateRequest:FakeRequest[JsValue] = FakeRequest().withBody[JsValue](Json.toJson(dataModel2))
      val updateResult: Future[Result] = TestApplicationController.update(dataModel.login)(updateRequest)
      await(updateResult).header.status shouldBe ACCEPTED
      contentAsJson(updateResult).as[JsValue] shouldBe Json.toJson(dataModel2)
    }
  }


  "ApplicationController .delete()" should {
    "delete the book in the database based on id" in {
      //      beforeEach()
      // Creating a request for the creation of the dataModel
      val request: FakeRequest[JsValue] = buildPost("/create").withBody[JsValue](Json.toJson((dataModel)))

      // Calling the .create function in the ApplicationController
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      await(createdResult).header.status shouldBe CREATED

      // Calling the .read function in the ApplicationController
      val readResult: Future[Result] = TestApplicationController.read("sebdroid")(FakeRequest())
      await(readResult).header.status shouldBe OK

      // Calling the .delete function in the ApplicationController
      val deleteRequest: Future[Result] = TestApplicationController.delete(dataModel.login)(FakeRequest())
     await(deleteRequest).header.status shouldBe ACCEPTED

      val deleteRequestFailed: Future[Result] = TestApplicationController.delete("Kokos")(FakeRequest())
      await(deleteRequestFailed).header.status shouldBe BAD_REQUEST
    }
  }

  "ApplicationController .getUserRepos" should {
    "return repositories of a user (return 200 OK)" in {
      val existingUsername:String = "anthoskountouris"
      val resultFuture = TestApplicationController.getUserRepos(existingUsername)(FakeRequest())
      println("resultFuture",resultFuture)
      await(resultFuture).header.status shouldBe OK
    }

    "return 400" in {
      val nonExistingUsername:String = "panikkos1212121"
      val resultFuture = TestApplicationController.getUserRepos(nonExistingUsername)(FakeRequest())
      await(resultFuture).header.status shouldBe BAD_REQUEST
    }
  }

  "ApplicationController .getRepoContent" should {
    "return files/dirs of a repository (return 200 OK)" in {
      val existingUsername:String = "anthoskountouris"
      val existingRepository:String ="Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
      val resultFuture: Future[Result] = TestApplicationController.getRepoContent(existingUsername, existingRepository)(FakeRequest())
      await(resultFuture).header.status shouldBe OK
    }

    "return 400" in {
      val existingUsername:String = "anthoskountouris"
      val existingRepository:String ="randomRepo"
      val resultFuture: Future[Result] = TestApplicationController.getRepoContent(existingUsername, existingRepository)(FakeRequest())
      await(resultFuture).header.status shouldBe BAD_REQUEST
      println(await(resultFuture))
    }
  }

  "ApplicationController .getFileOrDirContent" should {
    "return a view a file's content or a dir's content " in {
      val existingUsername:String = "anthoskountouris"
      val existingRepository:String ="Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
      val filePath:String = "/.gitignore"
      val resultFuture: Future[Result] = TestApplicationController.getFileOrDirContent(existingUsername, existingRepository, filePath)(FakeRequest())
      await(resultFuture).header.status shouldBe OK
      println(await(resultFuture).body)
    }

    "return a view a dir's content " in {
      val existingUsername:String = "anthoskountouris"
      val existingRepository:String ="Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
      val filePath:String = "/Twitter_API"
      val resultFuture: Future[Result] = TestApplicationController.getFileOrDirContent(existingUsername, existingRepository, filePath)(FakeRequest())
      await(resultFuture).header.status shouldBe OK
      println(await(resultFuture).body)
    }
  }



}
