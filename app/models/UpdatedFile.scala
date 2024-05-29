package models

import play.api.libs.json.{Json, OFormat}

case class UpdatedFile(message:String, content:String, sha:String)


object UpdatedFile{
  implicit val formats: OFormat[UpdatedFile] = Json.format[UpdatedFile]
}