package models

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
  password: String)

object User {
  val mapForm = mapping(
    "id" -> text,
    "name" -> nonEmptyText,
    "profile" -> UserProfile.mapForm,
    "mail" -> nonEmptyText,
    "password" -> nonEmptyText)(User.apply)(User.unapply)
}

import models.UserProfileFormat._

object UserJsonFormat {
  import play.api.libs.json.Json

  implicit val userFormat = Json.format[User]
}
