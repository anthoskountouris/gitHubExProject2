package models

import play.api.libs.json.{Json, OFormat}

case class NewFile(message:String, content:String)


object NewFile{
  implicit val formats: OFormat[NewFile] = Json.format[NewFile]
}