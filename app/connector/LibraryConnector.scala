package connector

import cats.data.EitherT
import com.google.common.io.BaseEncoding
import models._
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat}
import play.api.libs.ws.{WSClient, WSResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {
  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response.map {
          result =>
            result.json.validate[Response] match {
              case JsSuccess(dataModel: GitHubUser, _) =>
                Right(result.json.as[Response])
              case JsError(_) => Left(APIError.BadAPIResponse(400, "Something is wrong with the data retrieved from the API"))

            }

        }
        .recover { case _: WSResponse =>
          Left(APIError.BadAPIResponse(500, "Could not connect"))
        }
    }
  }


//  def get2[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, List[Response]] = {
//    val request = ws.url(url)
//    val response = request.get()
//    EitherT {
//      response.map {
//        result =>
//          result.status match{
//            case 200 => Right(result.json.as[List[Response]])
//            case _ =>
//              Left(APIError.BadAPIResponse(result.status, result.statusText))
//
//          }
//          } .recover { case _: WSResponse =>
//        Left(APIError.BadAPIResponse(500, "Could not connect"))
//      }
//        }
//    }



  def get2[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, List[Response]] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response.map {
          result =>
            result.status match {
              case 200 =>
                result.json.validate[List[Response]] match {
                  case JsSuccess(dataModel: List[UserRepos], _) =>
                    Right(result.json.as[List[Response]])
                  case JsError(_) => Left(APIError.BadAPIResponse(400, "Something is wrong with the data retrieved from the API"))
                }
              case _ =>
                Left(APIError.BadAPIResponse(result.status, result.statusText))
            }
        }.recover { case _: WSResponse =>
          Left(APIError.BadAPIResponse(500, "Could not connect"))
        }
    }
  }

  def get3[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Either[List[DirContent], FileContent]] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response.map {
        result =>
          result.status match {
            case 200 => result.json.validate[List[DirContent]] match {
              case JsSuccess(dataModel: List[DirContent], _) =>
                Right(Left(result.json.as[List[DirContent]]))
              case JsError(_) => result.json.validate[FileContent] match {
                case JsSuccess(dataModel: FileContent, _) =>
                  Right(Right(result.json.as[FileContent]))
                case JsError(_) => Left(APIError.BadAPIResponse(result.status, result.statusText))
              }
            }
            case _ =>
              Left(APIError.BadAPIResponse(result.status, result.statusText))
          }
      }.recover { case _: WSResponse =>
        Left(APIError.BadAPIResponse(500, "Could not connect"))
      }
    }
  }

  def post(url: String, dataModel: NewFile): Future[Either[APIError, WSResponse]]  = {
//    println("Sys.env: "+sys.env)
    val token = sys.env.getOrElse("AuthPassword", throw new RuntimeException("AuthPassword environment variable not set"))
    println("token: " + token)
//    println(sys.env)
//    val request = ws.url(url)
    val contentBase64 = BaseEncoding.base64().encode(dataModel.content.getBytes("UTF-8"))
    val dataModelJson = Json.toJson(dataModel.copy(content = contentBase64))
//    val dataModelJson = Json.toJson(dataModel)
    val request = ws.url(url).withMethod("PUT").withHttpHeaders("Authorization" -> s"Bearer $token")
    println(s"POST URL: $url")
    println(s"Payload: $dataModelJson")
    request.put(dataModelJson).map(response => Right(response)).recover {
      case ex: Throwable =>
        println(s"Error occurred: ${ex.getMessage}")
        Left(APIError.BadAPIResponse(500, ex.getMessage))
//        println(s"Error occurred: ${ex.getMessage}")
        // You can provide a default WSResponse or rethrow the exception if needed
    }
  }

  def put(url:String, dataModel: UpdatedFile): Future[Either[APIError, WSResponse]] = {
    val token = sys.env.getOrElse("AuthPassword", throw new RuntimeException("AuthPassword environment variable not set"))
    println("token: " + token)
    val contentBase64 = BaseEncoding.base64().encode(dataModel.content.getBytes("UTF-8"))
    val dataModelJson = Json.toJson(dataModel.copy(content = contentBase64))
    val request = ws.url(url).withMethod("PUT").withHttpHeaders("Authorization" -> s"Bearer $token")
    println(s"POST URL: $url")
    println(s"Payload: $dataModelJson")
    request.put(dataModelJson).map(response => Right(response)).recover {
      case ex: Throwable =>
        println(s"Error occurred: ${ex.getMessage}")
        Left(APIError.BadAPIResponse(500, ex.getMessage))
    }
  }

  def delete(url:String, payload:DeleteFile): Future[Either[APIError, WSResponse]] = {
    val token = sys.env.getOrElse("AuthPassword", throw new RuntimeException("AuthPassword environment variable not set"))
    println("token: " + token)
    val request = ws.url(url)
      .withMethod("DELETE")
      .withHttpHeaders("Authorization" -> s"Bearer $token")
      .withBody(Json.toJson(payload))
    println(s"DELETE URL: $url")
    request.delete().map(Right(_)).recover {
      case ex: Throwable =>
        println(s"Error occurred: ${ex.getMessage}")
        Left(APIError.BadAPIResponse(500, ex.getMessage))
    }
  }

}