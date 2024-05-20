package controllers

import models.{APIError, GitHubUser, UserRepos}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.mvc.Results.redirect
import repositories.DataRepository
import service.{LibraryService, RepositoryService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApplicationController @Inject() (val controllerComponents: ControllerComponents, val dataRepository: DataRepository, implicit val ec: ExecutionContext, val libService: LibraryService, val repService: RepositoryService) extends BaseController with play.api.i18n.I18nSupport {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    repService.index().map {
      case Right(item:Seq[GitHubUser]) => Ok(Json.toJson(item))
      case Left(apiError: APIError) => InternalServerError(Json.obj("error" -> apiError.upstreamMessage))
    }
  }

  def getGithubUser(username:String): Action[AnyContent] = Action.async { implicit request =>
    libService.getGithubUser(username = username).value.map {
      case Right(user) =>
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

    def read(username:String) = Action.async { implicit request =>
      repService.read(username).map{
        case Right(item) => Ok {Json.toJson(item)}
        case Left(_) => BadRequest{Json.toJson("Unable to find that user")}
      }
    }

    def update(username:String) = Action.async(parse.json) { implicit request =>
      request.body.validate[GitHubUser] match {
        case JsSuccess(dataModel: GitHubUser, _) =>
          repService.update(username, dataModel).map(_ => Accepted{Json.toJson(dataModel)})
        case JsError(_) => Future(BadRequest)
      }
    }

    def delete(username:String) = Action.async { implicit request =>
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

  def getUserRepos(username:String): Action[AnyContent] = Action.async { implicit request =>
    libService.getUserRepos(username = username).value.map {
      case Right(repos) =>
      //      Ok {Json.toJson(repos)}
        Ok(views.html.repositories(repos))
      //            Ok(Json.toJson(user))
      //          case JsError(errors) =>
      //            Future(BadRequest(Json.toJson("Invalid data model")))
      case Left(apiError: APIError) =>
        BadRequest(Json.obj("error" -> apiError.reason))
    }
  }

}
