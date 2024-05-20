package models

import play.api.libs.json.{Json, OFormat}

case class RepoContent(name:String, url:String)

object RepoContent{
  implicit val formats: OFormat[RepoContent] = Json.format[RepoContent]
}
