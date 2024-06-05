package models

import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.json.{Json, OFormat}

case class UpdatedFile(message:String, content:String, sha:String)


object UpdatedFile{
  implicit val formats: OFormat[UpdatedFile] = Json.format[UpdatedFile]

  val fileForm:Form[UpdatedFile] = Form(
    mapping(
      "message" -> text,
      "content" -> text,
      "sha" -> text
    )(UpdatedFile.apply)(UpdatedFile.unapply)
  )
}