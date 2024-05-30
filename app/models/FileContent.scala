package models

import play.api.libs.json.{Json, OFormat}

case class FileContent(name: String, path:String ,`type`:String, content:String)

object FileContent{
  implicit val formats: OFormat[FileContent] = Json.format[FileContent]
}
