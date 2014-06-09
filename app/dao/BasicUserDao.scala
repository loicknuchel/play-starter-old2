package dao

import models.BasicUser
import models.BasicUserJsonFormat._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import reactivemongo.core.commands.LastError

object BasicUserDao {
  // Take a look at https://gist.github.com/almeidap/5685801
  private val USER_COLECTION_NAME = "basic-users"
  private def collection()(implicit db: DB): JSONCollection = db.collection[JSONCollection](USER_COLECTION_NAME)

  def create(user: BasicUser)(implicit db: DB): Future[LastError] = collection().insert(user)

  def find(id: String)(implicit db: DB): Future[Option[BasicUser]] = collection().find(Json.obj("id" -> id)).one[BasicUser]
  def find(filter: JsObject)(implicit db: DB): Future[Set[BasicUser]] = collection().find(filter).cursor[BasicUser].collect[Set]()
  def findAll()(implicit db: DB): Future[Set[BasicUser]] = collection().find(Json.obj()).cursor[BasicUser].collect[Set]()

  def update(id: String, user: BasicUser)(implicit db: DB): Future[LastError] = collection().update(Json.obj("id" -> id), Json.obj("$set" -> user).transform((__ \ '$set \ 'id).json.prune).get)
  
  def delete(id: String)(implicit db: DB): Future[LastError] = collection().remove(Json.obj("id" -> id))
}