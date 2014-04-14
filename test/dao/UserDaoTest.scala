package dao

import utils.MongoSuite
import com.github.simplyscala.MongodProps
import reactivemongo.api.{MongoDriver, DB}
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Await
import scala.concurrent.duration._
import models.User
import scala.concurrent.ExecutionContext.Implicits.global

class UserDaoTest extends MongoSuite {
  var mongoProps: MongodProps = null
  override def beforeAll() { mongoProps = mongoStart(27018) }
  override def afterAll() { mongoStop(mongoProps) }

  before { drop(usersCollForTest) }

  implicit val connection: DB = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27018"))
    connection("test")
  }

  implicit val usersCollForTest = connection[JSONCollection]("users")

  test(" should find all user") {
    // Given
    val expectedUser = User("id", "name", "bio")

    initData(expectedUser)

    // When
    val result = Await.result(UserDao.all(), 2 seconds)

    // Then
    result should have size 1
    result shouldBe Set(expectedUser)
  }
}