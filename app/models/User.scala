package models

import models.UserProfileFormat._
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._

case class User(
  id: String,
  name: String,
  profile: UserProfile,
  mail: String,
  password: String){
  def toForm = UserForm(id, name, profile.id, mail, password)
}
  
case class UserForm(
  id: String,
  name: String,
  profileId: String,
  mail: String,
  password: String){
  def withProfile(profile: UserProfile) = User(id, name, profile, mail, password)
}

object UserForm {
  val mapForm = mapping(
    "id" -> text,
    "name" -> nonEmptyText,
    "profileId" -> nonEmptyText,
    "mail" -> nonEmptyText,
    "password" -> nonEmptyText)(UserForm.apply)(UserForm.unapply)
}

object UserFormat {
  import play.api.libs.json.Json

  implicit val userFormat = Json.format[User]
}
