package controllers

import models._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import play.filters.csrf.CSRF
import repositories.DataRepository
import service.{LibraryService, RepositoryService}

import java.nio.charset.StandardCharsets
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


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
        BadRequest(views.html.unsuccessfulPage(apiError.reason))
//        BadRequest(Json.obj("error" -> apiError.reason))
    }
  }

  def getRepoContent(username: String, repoName: String): Action[AnyContent] = Action.async { implicit request =>
    libService.getRepoContent(username = username, repoName = repoName).value.map {
      case Right(contentt) =>
        Ok(views.html.repoContent(contentt, username, repoName))
      case Left(apiError: APIError) =>
        BadRequest(views.html.unsuccessfulPage(apiError.reason))
//        BadRequest((Json.obj("error" -> apiError.reason)))
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
          println("contentNew: " + contentNew)
          views.html.fileContent(contentNew, username, repoName, path)
        }
        case Left(content2) => Ok {
//          println(Json.toJson(content2))
          views.html.dirContent(content2, username, repoName, path)

        }
      }
      case Left(apiError: APIError) =>
//        BadRequest{Json.obj("error" -> apiError.reason)}
        BadRequest(views.html.unsuccessfulPage(apiError.reason))

    }
  }

  def createFile(username: String, repoName: String, path: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[NewFile] match {
      case JsSuccess(dataModel: NewFile, _) =>
        println(s"Received request to create file: $dataModel")
        libService.createFile(username = username, repoName = repoName, path = path, dataModel = dataModel).map{
          case Right(response) => {
            println(s"Response from GitHub API: ${response.json}")
            println(s"Response body from GitHub API: ${response.body}")
            Created
          }
          case Left(apiError: APIError) => {
//            BadRequest{Json.obj("Status"-> apiError.httpResponseStatus, "error" -> apiError.reason) }
            BadRequest(views.html.unsuccessfulPage(apiError.reason))
          }
        }

      case JsError(errors) =>
        println(s"Request validation failed: $errors")
        Future.successful(BadRequest)
    }
}

  def updateFile(username:String, repoName:String, path:String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UpdatedFile] match {
      case JsSuccess(dataModel: UpdatedFile, _) =>
        libService.updateFile(username = username, repoName = repoName, path = path, dataModel = dataModel).map{
          case Right(response) => {
            println(s"Response from GitHub API: ${response.json}")
            Created
          }
          case Left(apiError: APIError) => {
//            BadRequest{Json.obj("Status"-> apiError.httpResponseStatus, "error" -> apiError.reason) }
            BadRequest(views.html.unsuccessfulPage(apiError.reason))
          }
        }

      case JsError(errors) =>
        println(s"Request validation failed: $errors")
//        Future.successful(BadRequest)
        Future.successful(BadRequest(views.html.unsuccessfulPage(errors.toString())))

    }
  }

  def accessToken(implicit request: Request[_]) = {
    CSRF.getToken
  }

  def deleteFile(username:String, repoName:String, path:String):Action[JsValue] = Action.async(parse.json) { implicit request =>
    println("I am in delete")
    accessToken
    request.body.validate[DeleteFile] match {
      case JsSuccess(dataModel: DeleteFile, _) =>
        println(s"Received request to delete file: $dataModel")
        libService.deleteFile(username = username, repoName = repoName, path = path, dataModel = dataModel).map{
          case Right(response) => {
            println(s"Response from GitHub API: ${response.json}")
            println(s"Response body from GitHub API: ${response.body}")
            Accepted("File deleted successfully.")
          }
          case Left(apiError:APIError) => {
//            BadRequest{Json.obj("Status"-> apiError.httpResponseStatus, "error" -> apiError.reason) }
            BadRequest(views.html.unsuccessfulPage(apiError.reason))

          }
        }

      case JsError(errors) =>
        println(s"Request validation failed: $errors")
        Future.successful(BadRequest(views.html.unsuccessfulPage(errors.toString())))
//        Future.successful(BadRequest)
        Future.successful(BadRequest(views.html.unsuccessfulPage(errors.toString())))
    }
  }


  def addFile(username:String, repoName:String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    println("I am In **********")
    Future.successful(Ok(views.html.addFileForm(NewFile.fileForm, username, repoName)))
  }

  def addFileForm(username:String, repoName:String):  Action[AnyContent] =  Action.async { implicit request =>
    accessToken //call the accessToken method
    println("addFileForm method called")
    NewFile.fileForm
      .bindFromRequest()
      .fold( //from the implicit request we want to bind this to the form in our companion object
        formWithErrors => {
          //here write what you want to do if the form has errors
          Future.successful(BadRequest(views.html.addFileForm(formWithErrors, username, repoName)))
        },
        formData => {
          val path = formData.path
          println("path "+path)
          //here write how you would use this data to create a new book (DataModel)
          libService.createFile(username = username, repoName = repoName, path = path, dataModel = formData).map{
                case Right(response) => Created{
                  println(s"Response from GitHub API: ${response.json}")
                  println(s"Response body from GitHub API: ${response.body}")
                  val er = "File successfully created"
                  views.html.successPage(username, repoName, path, er)
                }
                case Left(_) =>
                  BadRequest(Json.obj("error" -> "Failed"))
          } recover
//            { case _ => InternalServerError(Json.toJson("File was not created"))}
            { case _ => InternalServerError(views.html.unsuccessfulPage("File has not been created"))}

        }
      )
  }
  def editFile(username:String, repoName:String, file_path:String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    println("I am In **********")
    val fileContent = libService.getFileOrDirContent(username = username, repoName = repoName, path = file_path).value.map {
      case Right(content) => content match {
        case Right(content1) =>
          val textDecoded:String = new String(java.util.Base64.getDecoder.decode(content1.content.replace("\n","")), StandardCharsets.UTF_8)
          content1.copy(content = textDecoded)
        case Left(content2) => BadRequest {
          //          println(Json.toJson(content2))
          views.html.unsuccessfulPage("Something went wrong")

        }
      }
      case Left(apiError: APIError) =>
        //        BadRequest{Json.obj("error" -> apiError.reason)}
        BadRequest(views.html.unsuccessfulPage(apiError.reason))

    }
//    fileContent.map { file =>
//      Ok(views.html.updateFileForm(UpdatedFile.fileForm, username, repoName, file_path, file))
//    }
    fileContent.map {
      case file: FileContent => Ok(views.html.updateFileForm(UpdatedFile.fileForm, username, repoName, file_path, file))
      case _ => BadRequest(views.html.unsuccessfulPage("Something went wrong"))
    }
  }

  def editFileForm(username:String, repoName:String, file_path:String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    accessToken //call the accessToken method
    println("editFileForm method called")
    libService.getFileOrDirContent(username = username, repoName = repoName, path = file_path).value.map {
      case Right(content) => content match {
        case Right(content1) =>
          val textDecoded: String = new String(java.util.Base64.getDecoder.decode(content1.content.replace("\n", "")), StandardCharsets.UTF_8)
          content1.copy(content = textDecoded)
      }
    }.flatMap {file =>
      UpdatedFile.fileForm
        .bindFromRequest()
        .fold( //from the implicit request we want to bind this to the form in our companion object
          formWithErrors => {
            //here write what you want to do if the form has errors
            Future(BadRequest(views.html.updateFileForm(formWithErrors, username, repoName, file_path, file)))
          },
          formData => {
            println(formData)
            val sha = file.sha
            val dataModelCompleted = UpdatedFile(
              message = formData.message,
              content = formData.content,
              sha = sha
            )
            println("model: " + dataModelCompleted)
            println("sha " + sha)
            //here write how you would use this data to create a new book (DataModel)

            libService.updateFile(username = username, repoName = repoName, path = file_path, dataModel = dataModelCompleted).map{
              case Right(response) => Created{
                println(s"Response from GitHub API: ${response.json}")
                println(s"Response body from GitHub API: ${response.body}")
                val msg:String = "File updated successfully"
                views.html.successPage(username, repoName, file_path, msg)
              }
              case Left(_) =>
                BadRequest(Json.obj("error" -> "Failed"))
            } recover
            { case _ => InternalServerError(views.html.unsuccessfulPage("File has not been edited"))}
          }
        )
    }
  }

  def deleteFileForm(username:String, repoName:String, path:String): Action[AnyContent] = Action.async { implicit request =>
    println("I am in delete")
    accessToken
    libService.getFileOrDirContent(username = username, repoName = repoName, path = path).value.map {
      case Right(content) => content match {
        case Right(content1) =>
          val textDecoded: String = new String(java.util.Base64.getDecoder.decode(content1.content.replace("\n", "")), StandardCharsets.UTF_8)
          content1.copy(content = textDecoded)
      }
    }.flatMap{file =>
      val delFile = DeleteFile(
        message = "Deletion of the file",
        sha = file.sha)
        libService.deleteFile(username = username, repoName = repoName, path = path, dataModel = delFile).map{
          case Right(response) => Accepted{
            println(s"Response from GitHub API: ${response.json}")
            println(s"Response body from GitHub API: ${response.body}")
            val msg:String = "File deleted successfully"
            views.html.successPage(username, repoName, path, msg)
          }
          case Left(apiError:APIError) => {
            BadRequest{Json.obj("Status"-> apiError.httpResponseStatus, "error" -> apiError.reason) }
          }
        } recover {
          { case _ => InternalServerError(Json.toJson("The file has not been deleted")) }
        }
    }

  }
}