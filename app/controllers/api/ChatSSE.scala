package controllers.api

import models.Message
import models.MessageJsonFormat._
import dao.MessageDao
import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.iteratee.{ Concurrent, Enumeratee }
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.modules.reactivemongo.MongoController
import reactivemongo.bson.BSONObjectID

object ChatSSE extends Controller with MongoController {
  implicit val DB = db

  /** Central hub for distributing chat messages */
  val (chatOut, chatChannel) = Concurrent.broadcast[JsValue]

  /** Enumeratee for filtering messages based on room */
  def filter(room: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "room").as[String] == room }

  /** Enumeratee for detecting disconnect of SSE stream */
  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] = Enumeratee.onIterateeDone { () => println(addr + " - SSE disconnected") }

  /** Controller action for GETing chat messages */
  def getMessagesAsync(room: String) = Action.async {
    MessageDao.find(Json.obj("room" -> room)).map { message => Ok(Json.toJson(message)) }
  }

  /** Controller action for POSTing chat messages */
  def postMessageAsync(room: String) = Action.async(parse.json) { req =>
    val id = BSONObjectID.generate.stringify
    val msg: JsObject = req.body.as[JsObject] ++ Json.obj("id" -> id) ++ Json.obj("room" -> room)
    msg.validate[Message].map { message =>
      MessageDao.insert(message).map { lastError =>
        chatChannel.push(messageFormat.writes(message))
        Created(Json.obj("id" -> message.id, "msg" -> "Message Created"))
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  /** Controller action serving activity based on room */
  def chatFeed(room: String) = Action { req =>
    println(req.remoteAddress + " - SSE connected")
    Ok.feed(chatOut
      &> filter(room)
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()).as("text/event-stream")
  }
}
