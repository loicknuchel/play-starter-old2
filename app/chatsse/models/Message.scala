package chatsse.models

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

case class MessageNoId(
  room: String,
  user: String,
  text: String,
  time: Long)

object MessageNoIdJsonFormat {
  import play.api.libs.json.Json

  implicit val messageNoIdFormat = Json.format[MessageNoId]
}