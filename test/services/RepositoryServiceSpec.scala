package services

import baseSpec.BaseSpec
import com.mongodb.client.result.{DeleteResult, UpdateResult}
import models.{APIError, GitHubUser}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import repositories.MockRepository
import service.RepositoryService

import scala.concurrent.{ExecutionContext, Future}

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  val mockRepository: MockRepository = mock[MockRepository]

  val testService = new RepositoryService(mockRepository)

  val user1: JsValue = Json.obj(
    "login" -> "sebdroid",
    "created_at" -> "2013-06-11T16:59:23Zs",
    "followers" -> 16,
    "following" -> 16
  )

  val user2: JsValue = Json.obj(
    "login" -> "anthosk",
    "created_at" -> "2013-06-11T16:59:23Zs",
    "followers" -> 136,
    "following" -> 146
  )

  val user3: JsValue = Json.obj(
    "login" -> "apoel123",
    "created_at" -> "2013-06-11T16:59:23Zs",
    "followers" -> 162,
    "following" -> 516
  )

  val dataModels: Seq[JsValue] = Seq(user1, user2, user3)

  val emptyDataModels: Seq[JsValue]= Seq()

  //  implicit val dataModelReads: Reads[DataModel] = Json.reads[DataModel]
  //  println(dataModelReads)

  "RepositoryService" should {
    "index" should {
      "return data models when index is successful" in {
        (mockRepository.index _) // or (() => mockRepository.index) // converting the method call into a function value
          .expects()
          .returning(Future(Right(dataModels.map(json => json.validate[GitHubUser] match {
            case JsSuccess(value, _) => value
            case JsError(errors) => fail("JSON validation error: " + errors.toString)
          }))))

        whenReady(testService.index()) { result =>
          result shouldBe Right(dataModels.map(_.as[GitHubUser]))
        }
      }

      "return APIError.BadAPIResponse when index fails" in {
        (mockRepository.index _)
          .expects()
          .returning(Future(Left(APIError.BadAPIResponse(404, "Users cannot be found"))))

        whenReady(testService.index()) { result =>
          result shouldBe Left(APIError.BadAPIResponse(404, "Users cannot be found"))
        }
      }
    }

    "create" should {
      "return a new data model if the operation is successful" in {
        (mockRepository.create _)
          .expects(user1.as[GitHubUser])
          .returning(Future(Right(user1.as[GitHubUser])))

        whenReady(testService.create(user1.as[GitHubUser])) { result =>
          result shouldBe Right(user1.as[GitHubUser])
        }
      }

      "return an error message if the operation fails" in{
        (mockRepository.create _)
          .expects(user1.as[GitHubUser])
          .returning(Future(Left(Json.toJson(s"The user already exists."))))

        whenReady(testService.create(user1.as[GitHubUser])) { result =>
          result shouldBe Left(Json.toJson(s"The user already exists."))
        }
      }
    }

    "read" should {
      "return a data model if the operation is successful" in {
        (mockRepository.read _)
          .expects("anthosk")
          .returning(Future(Right(user2.as[GitHubUser])))

        whenReady(testService.read("anthosk")) { result =>
          result shouldBe Right(user2.as[GitHubUser])
        }
      }

      "return an error message if the operation fails" in {
        (mockRepository.read _)
          .expects("vfvfvf")
          .returning(Future(Left(Json.toJson("The user with the username:vfvfvf does not exist."))))

        whenReady(testService.read("vfvfvf")) { result =>
          result shouldBe Left(Json.toJson("The user with the username:vfvfvf does not exist."))
        }
      }
    }

    "update" should {
      "update a data model if the operation is successful" in {
        val fakeUpdateRequest = UpdateResult.acknowledged(1, 1L, null) // https://mongodb.github.io/mongo-java-driver/3.5/javadoc/com/mongodb/client/result/UpdateResult.html#acknowledged-long-java.lang.Long-org.bson.BsonValue-
        (mockRepository.update _)
          .expects("apoel123", user3.as[GitHubUser])
          .returning(Future(fakeUpdateRequest))

        whenReady(testService.update("apoel123", user3.as[GitHubUser] )) { result =>
          result shouldBe fakeUpdateRequest
        }
      }
    }

    "delete" should {
      "delete a data model if the operation is successful" in {
        val fakeDeletedResult = DeleteResult.acknowledged(1)
        (mockRepository.delete _)
          .expects("anthosk")
          .returning(Future(Right(fakeDeletedResult)))

        whenReady(testService.delete("anthosk")) { result =>
          result shouldBe Right(fakeDeletedResult)
        }
      }

      "return an error message if the operation fails" in {
        //        val fakeDeletedResult = DeleteResult.unacknowledged
        (mockRepository.delete _)
          .expects("anthoskkk")
          .returning(Future(Left(Json.toJson(s"No user found with this username."))))

        whenReady(testService.delete("anthoskkk")) { result =>
          result shouldBe Left(Json.toJson(s"No user found with this username."))
        }
      }
    }
  }
}
