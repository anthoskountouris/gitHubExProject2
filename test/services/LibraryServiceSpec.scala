package services

import baseSpec.BaseSpec
import cats.data.EitherT
import connector.LibraryConnector
import models.{APIError, GitHubUser, UserRepos}
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

//    (mockConnector.get[GitHubUser]( _:String)( _:OFormat[GitHubUser], _:ExecutionContext))
//      .expects(url, *, *)
//      .returning(EitherT.leftT(APIError.BadAPIResponse(500, "Could not connect")))
//      .once()
//
//    whenReady(testService.getGithubUser(urlOverride = Some(url), username = "").value) {
//      result => result shouldBe Left(APIError.BadAPIResponse(500, "Could not connect"))
//    }

  }


}
