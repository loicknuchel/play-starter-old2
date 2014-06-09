package models

import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._
import reactivemongo.bson._

case class UserProfile(
  id: String,
  name: String)

object UserProfile {
  val mapForm = mapping(
    "id" -> text,
    "name" -> nonEmptyText)(UserProfile.apply)(UserProfile.unapply)
}

object UserProfileFormat {
  import play.api.libs.json.Json
  implicit val userProfileFormat = Json.format[UserProfile]
}