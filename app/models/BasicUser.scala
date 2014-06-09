package models

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps

case class BasicUser(
  id: String,
  name: String,
  bio: String)

object BasicUserJsonFormat {
  import play.api.libs.json.Json

  implicit val basicUserFormat = Json.format[BasicUser]
}
