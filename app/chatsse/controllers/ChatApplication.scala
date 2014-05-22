package chatsse.controllers

import play.api.mvc._
import play.api.libs.iteratee.{ Concurrent, Enumeratee }
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

object ChatApplication extends Controller {

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

}
