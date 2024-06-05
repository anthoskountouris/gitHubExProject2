package controllers

import baseSpec.BaseSpecWithApplication
import com.jayway.jsonpath.EvaluationListener.FoundResult
import models.{DeleteFile, GitHubUser, NewFile, UpdatedFile}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.http.Status
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsFormUrlEncoded, Request, Result}
import play.api.test
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.Helpers.{GET, POST, await, contentAsJson, contentAsString, contentType, defaultAwaitTimeout, route, status, writeableOf_AnyContentAsEmpty}
import play.api.test.{FakeRequest, Injecting}
import play.libs.ws.WSResponse

import java.awt.AWTEvent
import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication with Injecting {
  val TestApplicationController = new ApplicationController(
    component, repository, executionContext, service, repService)

  private val dataModel: GitHubUser = GitHubUser(
    login = "sebdroid",
    created_at = Some("2013-06-11T16:59:23Z"),
    followers = Some(16),
    following = Some(16)
  )

  private val dataModel2: GitHubUser = GitHubUser(
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
      val existingUsername: String = "anthoskountouris"
      val resultFuture = TestApplicationController.getGithubUser(existingUsername)(FakeRequest())
      await(resultFuture).header.status shouldBe OK
    }

    "return 400" in {
      val nonExistingUsername: String = "panikkos1212121"
      val resultFuture = TestApplicationController.getGithubUser(nonExistingUsername)(FakeRequest())
      await(resultFuture).header.status shouldBe BAD_REQUEST
    }
  }

  "ApplicationController .create()" should {
    "create a user in the database (return 201 CREATED)" in {
      val request: FakeRequest[JsValue] = buildPost("/create").withBody[JsValue](Json.toJson(dataModel))
      val result: Future[Result] = TestApplicationController.create()(request)
      await(result).header.status shouldBe CREATED

      val readResult: Future[Result] = TestApplicationController.read("sebdroid")(FakeRequest())
      await(readResult).header.status shouldBe OK
    }
  }

  "ApplicationController .read()" should {

    val request: FakeRequest[JsValue] = buildPost("/create").withBody[JsValue](Json.toJson(dataModel))
    val result: Future[Result] = TestApplicationController.create()(request)
    await(result).header.status shouldBe CREATED

    "return a user from the database based on name (return 200 OK)" in {

      val readResult: Future[Result] = TestApplicationController.read("sebdroid")(FakeRequest())
      await(readResult).header.status shouldBe OK
    }
    "user not in database (return 400 BAD_REQUEST)" in {

      val readResult2: Future[Result] = TestApplicationController.read("sebefrferdroid")(FakeRequest())
      await(readResult2).header.status shouldBe BAD_REQUEST

    }
  }

  "ApplicationController .update()" should {
    "update the user in the database based on username" in {
      val request: FakeRequest[JsValue] = buildPost("/create").withBody[JsValue](Json.toJson(dataModel))
      val result: Future[Result] = TestApplicationController.create()(request)
      await(result).header.status shouldBe CREATED

      val readResult: Future[Result] = TestApplicationController.read("sebdroid")(FakeRequest())
      await(readResult).header.status shouldBe OK
      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(dataModel)

      val updateRequest: FakeRequest[JsValue] = FakeRequest().withBody[JsValue](Json.toJson(dataModel2))
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
      val existingUsername: String = "anthoskountouris"
      val resultFuture = TestApplicationController.getUserRepos(existingUsername)(FakeRequest())
      println("resultFuture", resultFuture)
      await(resultFuture).header.status shouldBe OK
    }

    "return 400" in {
      val nonExistingUsername: String = "panikkos1212121"
      val resultFuture = TestApplicationController.getUserRepos(nonExistingUsername)(FakeRequest())
      await(resultFuture).header.status shouldBe BAD_REQUEST
    }
  }

  "ApplicationController .getRepoContent" should {
    "return files/dirs of a repository (return 200 OK)" in {
      val existingUsername: String = "anthoskountouris"
      val existingRepository: String = "Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
      val resultFuture: Future[Result] = TestApplicationController.getRepoContent(existingUsername, existingRepository)(FakeRequest())
      await(resultFuture).header.status shouldBe OK
    }

    "return 400" in {
      val existingUsername: String = "anthoskountouris"
      val existingRepository: String = "randomRepo"
      val resultFuture: Future[Result] = TestApplicationController.getRepoContent(existingUsername, existingRepository)(FakeRequest())
      await(resultFuture).header.status shouldBe BAD_REQUEST
      println(await(resultFuture))
    }
  }

  "ApplicationController .getFileOrDirContent" should {
    "return a view a file's content or a dir's content " in {
      val existingUsername: String = "anthoskountouris"
      val existingRepository: String = "Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
      val filePath: String = "/.gitignore"
      val resultFuture: Future[Result] = TestApplicationController.getFileOrDirContent(existingUsername, existingRepository, filePath).apply(FakeRequest(GET, "/github/users/anthoskountouris/addFileForm/repos/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media").withCSRFToken)
      await(resultFuture).header.status shouldBe OK
      println(await(resultFuture).body)
    }

    "return a view a dir's content " in {
      val existingUsername: String = "anthoskountouris"
      val existingRepository: String = "Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
      val filePath: String = "/Twitter_API"
      val resultFuture: Future[Result] = TestApplicationController.getFileOrDirContent(existingUsername, existingRepository, filePath)(FakeRequest())
      await(resultFuture).header.status shouldBe OK
      println(await(resultFuture).body)
    }
  }

  private val dataModelCreate: NewFile = NewFile(
    message = "New commit",
    content = "Apoel Ultras",
    path = "random10.py"
  )

  "ApplicationController .createFile" should {
    val existingUsername: String = "anthoskountouris"
    val existingRepository: String = "Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
    val filePath: String = "/random10.py"

    "creates a file in a specified repository" in {
      val request: FakeRequest[JsValue] = buildPut(s"/github/users/${existingUsername}/repos/${existingRepository}/create${filePath}").withBody[JsValue](Json.toJson(dataModelCreate))
      val resultFuture: Future[Result] = TestApplicationController.createFile(existingUsername, existingRepository, filePath)(request)
      await(resultFuture).header.status shouldBe CREATED
    }

    "return 400 when having a not acceptable json" in {
      val request: FakeRequest[JsValue] = buildPut(s"/github/users/${existingUsername}/repos/${existingRepository}/create${filePath}").withBody[JsValue](Json.toJson(dataModel))
      val resultFuture: Future[Result] = TestApplicationController.createFile(existingUsername, existingRepository, filePath)(request)
      await(resultFuture).header.status shouldBe BAD_REQUEST
    }
  }

  private val dataModelUpdate: UpdatedFile = UpdatedFile(
    message = "New commit",
    content = "Apoel Ultras",
    sha = "123xewxem3121"
  )

  "ApplicationController .updateFile" should {
    val existingUsername: String = "anthoskountouris"
    val existingRepository: String = "Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
    val filePath: String = "/random10.py"

    "updates a file in a specified repository" in {
      val request: FakeRequest[JsValue] = buildPut(s"/github/users/${existingUsername}/repos/${existingRepository}/update${filePath}").withBody[JsValue](Json.toJson(dataModelUpdate))
      val resultFuture: Future[Result] = TestApplicationController.updateFile(existingUsername, existingRepository, filePath)(request)
      await(resultFuture).header.status shouldBe CREATED
    }

    "return 400 when having a not acceptable json" in {
      val request: FakeRequest[JsValue] = buildPut(s"/github/users/${existingUsername}/repos/${existingRepository}/update${filePath}").withBody[JsValue](Json.toJson(dataModel))
      val resultFuture: Future[Result] = TestApplicationController.updateFile(existingUsername, existingRepository, filePath)(request)
      await(resultFuture).header.status shouldBe BAD_REQUEST
    }
  }

  private val dataModelDelete: DeleteFile = DeleteFile(
    message = "New commit",
    sha = "123xewxem3121"
  )

  "ApplicationController .deleteFile" should {
    val existingUsername: String = "anthoskountouris"
    val existingRepository: String = "Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
    val filePath: String = "/random10.py"

    "deletes a file in a specified repository" in {
      val request: FakeRequest[JsValue] = buildDelete(s"/github/users/${existingUsername}/repos/${existingRepository}/delete${filePath}").withBody[JsValue](Json.toJson(dataModelDelete))
      val resultFuture: Future[Result] = TestApplicationController.deleteFile(existingUsername, existingRepository, filePath)(request)
      await(resultFuture).header.status shouldBe ACCEPTED
    }

    "return 400 when having a not acceptable json" in {
      val request: FakeRequest[JsValue] = buildDelete(s"/github/users/${existingUsername}/repos/${existingRepository}/delete${filePath}").withBody[JsValue](Json.toJson(dataModel))
      val resultFuture: Future[Result] = TestApplicationController.deleteFile(existingUsername, existingRepository, filePath)(request)
      await(resultFuture).header.status shouldBe BAD_REQUEST
    }
  }

  "ApplicationController .addFile" should {
    val existingUsername: String = "anthoskountouris"
    val existingRepository: String = "GitHubTest"
    val filePath: String = "/random10.py"

    "render the file form page from a new instance" in {
      val addFormPage = TestApplicationController.addFile(existingUsername, existingRepository).apply(FakeRequest(GET, s"/github/users/${existingUsername}/addFileForm/repos/${existingRepository}").withCSRFToken)
      status(addFormPage) mustBe OK
      contentType(addFormPage) mustBe Some("text/html")
      contentAsString(addFormPage) must include("Add File")
    }

    "render the form page from the application" in {
      val controller = inject[ApplicationController]
      val formPage = controller.addFile(existingUsername, existingRepository).apply(FakeRequest(GET, s"/github/users/${existingUsername}/addFileForm/repos/${existingRepository}").withCSRFToken)
      status(formPage) mustBe OK
      contentType(formPage) mustBe Some("text/html")
      contentAsString(formPage) must include("Add File")
    }

    "render the form page from the router" in {
      val request = FakeRequest(GET, s"/github/users/${existingUsername}/addFileForm/repos/${existingRepository}").withCSRFToken
      val formPage = route(app, request).get
      status(formPage) mustBe OK
      contentType(formPage) mustBe Some("text/html")
      contentAsString(formPage) must include("Add File")
    }

  }

  "ApplicationController .addFileForm" should {
    val existingUsername: String = "anthoskountouris"
    val existingRepository: String = "GitHubTest"
    val filePath: String = "apoel.py"
    "redirect on valid form submission" in {
      implicit val request: Request[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, s"/github/users/${existingUsername}/addFileForm/repos/${existingRepository}")
          .withFormUrlEncodedBody(
            "path" -> filePath,
            "message" -> "Apoel",
            "content" -> "Apoel Test")
          .withCSRFToken

      val result = TestApplicationController.addFileForm(existingUsername, existingRepository).apply(request)
      status(result) mustBe CREATED
    }

    "redirect BadRequest on invalid form submission (path empty)" in {
      implicit val request: Request[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, s"/github/users/${existingUsername}/addFileForm/repos/${existingRepository}")
          .withFormUrlEncodedBody(
            "path" -> "",
            "message" -> "Apoel",
            "content" -> "Apoel Test")
          .withCSRFToken

      val result = TestApplicationController.addFileForm(existingUsername, existingRepository).apply(request)
      status(result) mustBe BAD_REQUEST
      println(result)
    }
  }

  "ApplicationController .editFile" should {
    val existingUsername: String = "anthoskountouris"
    val existingRepository: String = "GitHubTest"
    val filePath: String = "apoel.py"

    "render the file form page from a new instance" in {
      val updateFormPage = TestApplicationController.editFile(existingUsername, existingRepository, filePath).apply(FakeRequest(GET, s"/github/users/${existingUsername}/updateFileForm/repos/${existingRepository}/${filePath}").withCSRFToken)
      status(updateFormPage) mustBe OK
      contentType(updateFormPage) mustBe Some("text/html")
      contentAsString(updateFormPage) must include("Update File")
    }

    "render the form page from the application" in {
      val controller = inject[ApplicationController]
      val updateFormPage = controller.editFile(existingUsername, existingRepository, filePath).apply(FakeRequest(GET, s"/github/users/${existingUsername}/addFileForm/repos/${existingRepository}/${filePath}").withCSRFToken)
      status(updateFormPage) mustBe OK
      contentType(updateFormPage) mustBe Some("text/html")
      contentAsString(updateFormPage) must include("Update File")
    }

    "render the form page from the router" in {
      val request = FakeRequest(GET, s"/github/users/${existingUsername}/updateFileForm/repos/${existingRepository}/${filePath}").withCSRFToken
      val updateFormPage = route(app, request).get
      status(updateFormPage) mustBe OK
      contentType(updateFormPage) mustBe Some("text/html")
      contentAsString(updateFormPage) must include("Update File")
    }
  }

  "ApplicationController .editFileForm" should {
    val existingUsername: String = "anthoskountouris"
    val existingRepository: String = "GitHubTest"
    val filePath: String = "apoel.py"
    "redirect on valid form submission" in {
      implicit val request: Request[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, s"/github/users/${existingUsername}/updateFileForm/repos/${existingRepository}/${filePath}")
          .withFormUrlEncodedBody(
            "sha" -> "",
            "message" -> "Apoel",
            "content" -> "Apoel Test")
          .withCSRFToken

      val result = TestApplicationController.editFileForm(existingUsername, existingRepository, filePath).apply(request)
      status(result) mustBe CREATED
    }
  }

  "ApplicationController .deleteFileForm" should {
    val existingUsername: String = "anthoskountouris"
    val existingRepository: String = "GitHubTest"
    val filePath: String = "apoel.py"

    "redirect on valid form submission" in {
      implicit val request: Request[AnyContentAsFormUrlEncoded] =
        FakeRequest(POST, s"/github/users/${existingUsername}/delete/repos/${existingRepository}/${filePath}")
          .withFormUrlEncodedBody(
            "sha" -> "",
            "message" -> "mew")
          .withCSRFToken

      val result = TestApplicationController.deleteFileForm(existingUsername, existingRepository, filePath).apply(request)
      status(result) mustBe ACCEPTED
    }
  }
}
