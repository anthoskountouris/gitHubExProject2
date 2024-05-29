package models

import play.api.libs.json.{Json, OFormat}

case class DeleteFile(message:String, sha:String)


object DeleteFile{
  implicit val formats: OFormat[DeleteFile] = Json.format[DeleteFile]
}