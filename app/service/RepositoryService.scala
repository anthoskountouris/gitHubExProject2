package service

import models.{APIError, GitHubUser}
import org.mongodb.scala.result
import play.api.libs.json.JsValue
import repositories.MockRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RepositoryService  @Inject()(mockRepository: MockRepository)(implicit ex: ExecutionContext){

  def index():Future[Either[APIError.BadAPIResponse, Seq[GitHubUser]]] = mockRepository.index()

  def create(user: GitHubUser): Future[Either[JsValue, GitHubUser]] = mockRepository.create(user)

  def read(username:String): Future[Either[JsValue, GitHubUser]] = mockRepository.read(username)

  def update(username:String, user: GitHubUser): Future[result.UpdateResult] = mockRepository.update(username, user)

  def delete(username:String): Future[Either[JsValue, result.DeleteResult]] = mockRepository.delete(username)
}
