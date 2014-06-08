package models

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps

case class User(
  id: String,
  name: String,
  bio: String)

object UserJsonFormat {
  import play.api.libs.json.Json

  implicit val userFormat = Json.format[User]
}
