package models

//import play.api.data.Form
import play.api.libs.json.{Json, OFormat}
import play.api.data._
import play.api.data.Forms._


case class NewFile(path:String, message:String, content:String)


object NewFile{
  implicit val formats: OFormat[NewFile] = Json.format[NewFile]

  val fileForm:Form[NewFile] = Form(
    mapping(
      "path" -> text,
      "message" -> text,
      "content" -> text,
    )(NewFile.apply)(NewFile.unapply)
  )
}