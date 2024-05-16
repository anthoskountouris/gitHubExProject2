package service

import cats.data.EitherT
import connector.LibraryConnector
import models.{APIError, GitHubUser}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryService @Inject()(connector:LibraryConnector){
  def getGithubUser(urlOverride: Option[String] = None, username:String)(implicit ex: ExecutionContext): EitherT[Future, APIError, GitHubUser] =
    connector.get[GitHubUser](urlOverride.getOrElse(s"https://api.github.com/users/$username"))
}