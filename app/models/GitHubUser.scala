package models
import akka.http.scaladsl.model.headers.Date
import play.api.libs.json.{Json, OFormat}

case class GitHubUser(login: String, created_at: Option[String], followers:Option[Int], following:Option[Int])

object GitHubUser{
  implicit val formats: OFormat[GitHubUser] = Json.format[GitHubUser]
}

