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

object UserDao {
  private val USER_COLECTION_NAME = "users"

  def all()(implicit db: DB): Future[Set[User]] = collection().find(Json.obj()).cursor[User].collect[Set]()

  def insert(user: User)(implicit db: DB): Future[LastError] = collection().insert(user)

  def find(id: String)(implicit db: DB): Future[Option[User]] = collection().find(Json.obj("id" -> id)).one[User]

  def delete(id: String)(implicit db: DB): Future[LastError] = collection().remove(Json.obj("id" -> id))

  private def collection()(implicit db: DB): JSONCollection = db.collection[JSONCollection](USER_COLECTION_NAME)
}