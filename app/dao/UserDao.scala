package dao

import models.User
import models.UserJsonFormat._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import reactivemongo.core.commands.LastError

object UserDao {
  private val USER_COLECTION_NAME = "users"
  private def collection()(implicit db: DB): JSONCollection = db.collection[JSONCollection](USER_COLECTION_NAME)

  def create(user: User)(implicit db: DB): Future[LastError] = collection().insert(user)

  def find(id: String)(implicit db: DB): Future[Option[User]] = collection().find(Json.obj("id" -> id)).one[User]
  def find(filter: JsObject)(implicit db: DB): Future[Set[User]] = collection().find(filter).cursor[User].collect[Set]()
  def findAll()(implicit db: DB): Future[Set[User]] = collection().find(Json.obj()).cursor[User].collect[Set]()

  def update(id: String, user: User)(implicit db: DB): Future[LastError] = collection().update(Json.obj("id" -> id), Json.obj("$set" -> user).transform((__ \ '$set \ 'id).json.prune).get)
  
  def delete(id: String)(implicit db: DB): Future[LastError] = collection().remove(Json.obj("id" -> id))
}