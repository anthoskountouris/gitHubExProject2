package models

import play.api.libs.json.{Json, OFormat}

case class DirContent(name:String, path:String, `type`:String)

object DirContent{
  implicit val formats: OFormat[DirContent] = Json.format[DirContent]
}