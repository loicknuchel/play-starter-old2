package crud.dao

import scala.concurrent.Future
import crud.models.User
import reactivemongo.api.DB
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import crud.models.UserJsonFormat._
import reactivemongo.core.commands.LastError
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.JsObject
import play.api.libs.json._

object UserDao {
  // Take a look at https://gist.github.com/almeidap/5685801
  private val USER_COLECTION_NAME = "users"
  private def collection()(implicit db: DB): JSONCollection = db.collection[JSONCollection](USER_COLECTION_NAME)

  def create(user: User)(implicit db: DB): Future[LastError] = collection().insert(user)

  def find(id: String)(implicit db: DB): Future[Option[User]] = collection().find(Json.obj("id" -> id)).one[User]
  def find(filter: JsObject)(implicit db: DB): Future[Set[User]] = collection().find(filter).cursor[User].collect[Set]()
  def findAll()(implicit db: DB): Future[Set[User]] = collection().find(Json.obj()).cursor[User].collect[Set]()

  def update(id: String, user: User)(implicit db: DB): Future[LastError] = collection().update(Json.obj("id" -> id), Json.obj("$set" -> user).transform((__ \ '$set \ 'id).json.prune).get)
  
  def delete(id: String)(implicit db: DB): Future[LastError] = collection().remove(Json.obj("id" -> id))
}