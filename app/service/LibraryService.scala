package service

import cats.data.EitherT
import connector.LibraryConnector
import models.{APIError, GitHubUser, RepoContent, UserRepos}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryService @Inject()(connector:LibraryConnector){
  def getGithubUser(urlOverride: Option[String] = None, username:String)(implicit ex: ExecutionContext): EitherT[Future, APIError, GitHubUser] =
    connector.get[GitHubUser](urlOverride.getOrElse(s"https://api.github.com/users/$username"))

  def getUserRepos(urlOverride: Option[String] = None, username:String)(implicit ex: ExecutionContext): EitherT[Future, APIError, List[UserRepos]] =
    connector.get2[UserRepos](urlOverride.getOrElse(s"https://api.github.com/users/$username/repos"))

  def getRepoContent(urlOverride: Option[String] = None, username:String, repoName:String)(implicit ex: ExecutionContext): EitherT[Future, APIError, List[RepoContent]] =
    connector.get2[RepoContent](urlOverride.getOrElse(s"https://api.github.com/repos/$username/$repoName/contents/"))
}





