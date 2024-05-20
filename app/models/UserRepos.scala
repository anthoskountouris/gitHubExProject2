package models

import play.api.libs.json.{Json, OFormat}

case class UserRepos(id: Int, name:String, owner:Owner)

object UserRepos{
  implicit val formats: OFormat[UserRepos] = Json.format[UserRepos]
}

case class Owner(login:String)

object Owner{
  implicit val formats: OFormat[Owner] = Json.format[Owner]
}

//case class ApiResponse(items: List[UserRepos])
//
//object ApiResponse {
//  implicit val formats: OFormat[ApiResponse] = Json.format[ApiResponse]
//}