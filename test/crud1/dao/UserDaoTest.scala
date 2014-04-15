package crud1.dao

import crud1.utils.MongoSuite
import com.github.simplyscala.MongodProps
import reactivemongo.api.{ MongoDriver, DB }
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Await
import scala.concurrent.duration._
import crud1.models.User
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json
import crud1.models.UserJsonFormat._
import crud1.dao.UserDao

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

  test("should find all user") {
    // Given
    val expectedUser = User("id", "name", "bio")

    initData(expectedUser)

    // When
    val result = Await.result(UserDao.all(), 2 seconds)

    // Then
    result should have size 1
    result shouldBe Set(expectedUser)
  }

  test("should insert user") {
    // Given
    val expectedUser = User("id", "name", "bio")

    // When
    Await.ready(UserDao.insert(expectedUser), 2 seconds)

    // Then
    val result = Await.result(usersCollForTest.find(Json.obj()).cursor[User].collect[Set](), 2 seconds)
    result shouldBe Set(expectedUser)
  }

  test("should find User by its id") {
    // Given
    val expectedUser = User("expected_id", "expectedUSer", "bio")
    val unexpectedUser = User("unexpected_id", "unexpectedUSer", "bio")

    initData(expectedUser, unexpectedUser)

    // When
    val result = Await.result(UserDao.find(expectedUser.id), 2 seconds)

    // Then
    result shouldBe Option(expectedUser)
  }

  test("should delete User from its id") {
    // Given
    val expectedUser = User("expected_id", "expectedUSer", "bio")
    val unexpectedUser = User("unexpected_id", "unexpectedUSer", "bio")

    initData(expectedUser, unexpectedUser)

    // When
    Await.ready(UserDao.delete(unexpectedUser.id), 2 seconds)

    // Then
    val result = Await.result(usersCollForTest.find(Json.obj()).cursor[User].collect[Set](), 2 seconds)
    result shouldBe Set(expectedUser)
  }
}