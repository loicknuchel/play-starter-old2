package models

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps

case class Message(
  id: String,
  room: String,
  user: String,
  text: String,
  time: Long)

object MessageJsonFormat {
  import play.api.libs.json.Json

  implicit val messageFormat = Json.format[Message]
}
