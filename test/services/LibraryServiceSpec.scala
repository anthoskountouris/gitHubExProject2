package services

import baseSpec.BaseSpec
import cats.data.EitherT
import connector.LibraryConnector
import models.{APIError, GitHubUser, RepoContent, UserRepos}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}
import service.LibraryService

import scala.concurrent.ExecutionContext

class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {
  val mockConnector: LibraryConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new LibraryService(mockConnector)

  val user1: JsValue = Json.obj(
    "login" -> "sebdroid",
    "created_at" -> "2013-06-11T16:59:23Zs",
    "followers" -> 16,
    "following" -> 16
  )

  "getGithubUser" should {
    val url: String = "testUrl"

    "return a user" in {
      (mockConnector.get[GitHubUser](_: String)(_: OFormat[GitHubUser], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT(user1.as[GitHubUser]))
        .once()

      whenReady(testService.getGithubUser(urlOverride = Some(url), username = "").value) { result =>
        result shouldBe Right(user1.as[GitHubUser])
      }
    }

    "return an error" in {
      val url: String = "testurl"

      (mockConnector.get[GitHubUser]( _:String)( _:OFormat[GitHubUser], _:ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.leftT(APIError.BadAPIResponse(500, "Could not connect")))
        .once()

      whenReady(testService.getGithubUser(urlOverride = Some(url), username = "").value) {
        result => result shouldBe Left(APIError.BadAPIResponse(500, "Could not connect"))
      }
    }
  }

  val rep1: JsValue = Json.obj(
    "id" -> 12312312,
    "name" -> "Astral",
    "owner" -> Json.obj(
      "login" -> "sebdroid"
    )
  )

  val rep2: JsValue = Json.obj(
    "id" -> 12312332,
    "name" -> "Astral_2",
    "owner" -> Json.obj(
      "login" -> "sebdroid"
    )
  )

  val rep3: JsValue = Json.obj(
    "id" -> 1211332,
    "name" -> "Astral_3",
    "owner" -> Json.obj(
      "login" -> "sebdroid"
    )
  )

  val listOfRepos:List[JsValue] = List(rep1, rep2, rep3)

  "getUserRepos" should {
    val url:String = "testurl"

    "return repositories" in {
      (mockConnector.get2[UserRepos](_: String)(_: OFormat[UserRepos], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT(listOfRepos.map(x => x.as[UserRepos])))
        .once()

      whenReady(testService.getUserRepos(urlOverride = Some(url), username = "").value) { result =>
        result shouldBe Right(listOfRepos.map(x => x.as[UserRepos]))
      }
    }

    "return an error" in {
      (mockConnector.get2[UserRepos](_: String)(_: OFormat[UserRepos], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.leftT(APIError.BadAPIResponse(500, "Could not connect")))
        .once()

      whenReady(testService.getUserRepos(urlOverride = Some(url), username = "").value) { result =>
        result shouldBe Left(APIError.BadAPIResponse(500, "Could not connect"))
      }
    }
  }

  val repFile1:JsValue = Json.obj(
    "name" -> ".gitignore",
    "url" -> "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/.gitignore?ref=master"
  )

  val repFile2:JsValue = Json.obj(
    "name" -> "Analysis.ipynb",
    "url" -> "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Analysis.ipynb?ref=master"
  )

  val repFile3:JsValue = Json.obj(
    "name" -> "Twitter_API",
    "url" -> "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API?ref=master"
  )

  val listOfFiles:List[JsValue] = List(repFile1, repFile2, repFile3)

  "getRepoContent" should {
    val url:String = "testurl"

    "return files/dir of a repository"in {
      (mockConnector.get2[RepoContent](_: String)(_: OFormat[RepoContent], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT(listOfFiles.map(x => x.as[RepoContent])))
        .once()

      whenReady(testService.getRepoContent(urlOverride = Some(url), username = "", repoName = "").value) {
        result =>
          result shouldBe Right(listOfFiles.map(x => x.as[RepoContent]))
      }
    }

    "return an error" in {
      (mockConnector.get2[RepoContent](_: String)(_: OFormat[RepoContent], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.leftT(APIError.BadAPIResponse(500, "Could not connect")))
        .once

      whenReady(testService.getRepoContent(urlOverride = Some(url), username = "", repoName = "").value) {
        result => result shouldBe Left(APIError.BadAPIResponse(500, "Could not connect"))
      }
    }
  }



}
