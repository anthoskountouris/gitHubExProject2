package controllers

import models.{APIError, DeleteFile, DirContent, FileContent, GitHubUser, NewFile, UpdatedFile, UserRepos}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.mvc.Results.redirect
import repositories.DataRepository
import service.{LibraryService, RepositoryService}

import java.nio.charset.StandardCharsets
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.reflect.io.Path


@Singleton
class ApplicationController @Inject() (val controllerComponents: ControllerComponents, val dataRepository: DataRepository, implicit val ec: ExecutionContext, val libService: LibraryService, val repService: RepositoryService) extends BaseController with play.api.i18n.I18nSupport {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    repService.index().map {
      case Right(item: Seq[GitHubUser]) => Ok(Json.toJson(item))
      case Left(apiError: APIError) => InternalServerError(Json.obj("error" -> apiError.upstreamMessage))
    }
  }

  def getGithubUser(username: String): Action[AnyContent] = Action.async { implicit request =>
    libService.getGithubUser(username = username).value.map {
      case Right(user) =>
//        println(Json.toJson(user))
        Ok(views.html.userInfo(user))
      //            Ok(Json.toJson(user))
      //          case JsError(errors) =>
      //            Future(BadRequest(Json.toJson("Invalid data model")))
      case Left(error) =>
        BadRequest(Json.toJson("Something went wrong"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[GitHubUser] match {
      case JsSuccess(dataModel: GitHubUser, _) =>
        repService.create(dataModel).map(_ => Created)
      case JsError(_) => Future(BadRequest) // The result of dataRepository.create() is a Future[Result], so even though we're not doing any lookup here, the type must be the same
    }
  }

  def read(username: String) = Action.async { implicit request =>
    repService.read(username).map {
      case Right(item) => Ok {
        Json.toJson(item)
      }
      case Left(_) => BadRequest {
        Json.toJson("Unable to find that user")
      }
    }
  }

  def update(username: String) = Action.async(parse.json) { implicit request =>
    request.body.validate[GitHubUser] match {
      case JsSuccess(dataModel: GitHubUser, _) =>
        repService.update(username, dataModel).map(_ => Accepted {
          Json.toJson(dataModel)
        })
      case JsError(_) => Future(BadRequest)
    }
  }

  def delete(username: String) = Action.async { implicit request =>
    repService.delete(username).map {
      case Right(item) => Accepted("User deleted successfully.")
      case Left(_) => BadRequest
    }
  }

  def addGithubUserFromApi(username: String): Action[AnyContent] = Action.async { implicit request =>
    libService.getGithubUser(username = username).value.flatMap {
      case Right(user) =>
        repService.create(user).map(_ => Created)
        Future(Redirect(routes.ApplicationController.getGithubUser(user.login)))

      //            Ok(Json.toJson(user))
      //          case JsError(errors) =>
      //            Future(BadRequest(Json.toJson("Invalid data model")))
      case Left(error) =>
        Future(BadRequest(Json.toJson("Something went wrong")))
    }
  }

  def getUserRepos(username: String): Action[AnyContent] = Action.async { implicit request =>
    libService.getUserRepos(username = username).value.map {
      case Right(repos) =>
        //      Ok {Json.toJson(repos)}
        Ok(views.html.repositories(repos, username))
      //            Ok(Json.toJson(user))
      //          case JsError(errors) =>
      //            Future(BadRequest(Json.toJson("Invalid data model")))
      case Left(apiError: APIError) =>
        BadRequest(Json.obj("error" -> apiError.reason))
    }
  }

  def getRepoContent(username: String, repoName: String): Action[AnyContent] = Action.async { implicit request =>
    libService.getRepoContent(username = username, repoName = repoName).value.map {
      case Right(contentt) =>
        Ok(views.html.repoContent(contentt, username, repoName))
      case Left(apiError: APIError) =>
        BadRequest((Json.obj("error" -> apiError.reason)))
    }
  }

//  def decode(base64: String): String =
//    new String(java.util.Base64.getDecoder.decode(base64)).split(" ").map(b => Integer.parseInt(b, 2).toChar).mkString


  def getFileOrDirContent(username: String, repoName: String, path: String): Action[AnyContent] = Action.async { implicit request =>
    libService.getFileOrDirContent(username = username, repoName = repoName, path = path).value.map {
      case Right(content) => content match {
        case Right(content1) => Ok {
//          println(Json.toJson(content1))
          val textDecoded:String = new String(java.util.Base64.getDecoder.decode(content1.content.replace("\n","")), StandardCharsets.UTF_8)
          val contentNew = content1.copy(content = textDecoded)
          views.html.fileContent(contentNew, username, repoName, path)
        }
        case Left(content2) => Ok {
//          println(Json.toJson(content2))
          views.html.dirContent(content2, username, repoName, path)

        }
      }
      case Left(apiError: APIError) =>
        BadRequest{Json.obj("error" -> apiError.reason)}
    }
  }

  def createFileOrDirectory(username: String, repoName: String, path: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[NewFile] match {
      case JsSuccess(dataModel: NewFile, _) =>
        println(s"Received request to create file: $dataModel")
        libService.createFileOrDirectory(username = username, repoName = repoName, path = path, dataModel = dataModel ).map{ response =>
          println(s"Response from GitHub API: ${response.json}")
          Created
        }

      case JsError(errors) =>
        println(s"Request validation failed: $errors")
        Future.successful(BadRequest)
    }
//  }
}

  def updateFile(username:String, repoName:String, path:String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UpdatedFile] match {
      case JsSuccess(dataModel: UpdatedFile, _) =>
//        println(s"Received request to create file: $dataModel")
//        val futurwSha = libService.getFileOrDirContent(username = username, repoName = repoName, path = path).value.map {
//          case Right(content) => content match {
//            case Right(content1) =>
//             content1.sha
//          }
//          case Left(_) => "Failed"
//    }
//        val shaValue:String = Await.result(futurwSha, 5.seconds)
//        val updatedDataModel = dataModel.copy(sha = shaValue)
        libService.updateFile(username = username, repoName = repoName, path = path, dataModel = dataModel).map{ response =>
          println(s"Response from GitHub API: ${response.json}")
          Created
        }

      case JsError(errors) =>
        println(s"Request validation failed: $errors")
        Future.successful(BadRequest)
    }

  }

  def deleteFile(username:String, repoName:String, path:String):Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DeleteFile] match {
      case JsSuccess(dataModel: DeleteFile, _) =>
        println(s"Received request to delete file: $dataModel")
        libService.deleteFile(username = username, repoName = repoName, path = path, dataModel = dataModel).map{response =>
          println(s"Response from GitHub API: ${response.json}")
          Accepted("File deleted successfully.")
        }

      case JsError(errors) =>
        println(s"Request validation failed: $errors")
        Future.successful(BadRequest)
    }
  }

  }