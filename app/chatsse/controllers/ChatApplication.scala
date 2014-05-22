package chatsse.controllers

import play.api.mvc._
import play.api.libs.iteratee.{ Concurrent, Enumeratee }
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import chatsse.models.Message
import chatsse.models.MessageNoId
import chatsse.models.MessageJsonFormat._
import chatsse.models.MessageNoIdJsonFormat._
import chatsse.dao.MessageDao
import play.modules.reactivemongo.MongoController
import scala.concurrent.Future

object ChatApplication extends Controller with MongoController {
  implicit val DB = db

  /** Central hub for distributing chat messages */
  val (chatOut, chatChannel) = Concurrent.broadcast[JsValue]

  /** Enumeratee for filtering messages based on room */
  def filter(room: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "room").as[String] == room }

  /** Enumeratee for detecting disconnect of SSE stream */
  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] = Enumeratee.onIterateeDone { () => println(addr + " - SSE disconnected") }

  /** Controller action serving activity based on room */
  def chatFeed(room: String) = Action { req =>
    println(req.remoteAddress + " - SSE connected")
    Ok.feed(chatOut
      &> filter(room)
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()).as("text/event-stream")
  }

  /** Controller action for POSTing chat messages */
  def postMessage(room: String) = Action(parse.json) { req =>
    val msg: JsObject = req.body.as[JsObject] ++ Json.obj("room" -> room)
    chatChannel.push(msg);
    Ok
  }

  def postMessageAsync(room: String) = Action.async(parse.json) { req =>
    val msg: JsObject = req.body.as[JsObject] ++ Json.obj("room" -> room)

    val id = BSONObjectID.generate.stringify
    msg.validate[MessageNoId]
      .map { messageNoId => Message(id, messageNoId.room, messageNoId.user, messageNoId.text, messageNoId.time) }
      .map {
        MessageDao.insert(_).map { lastError =>
          chatChannel.push(msg)
          Created(Json.obj("id" -> id, "msg" -> "User Created"))
        }
      }
      .getOrElse(Future.successful(BadRequest("invalid json")))
  }

}
