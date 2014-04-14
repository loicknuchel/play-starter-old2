package models

case class User(
  id: String,
  name: String,
  bio: String)

object UserJsonFormat {
  import play.api.libs.json.Json

  implicit val userFormat = Json.format[User]

  /*implicit object UserBSONReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User =
      User(
        doc.getAs[String]("id"),
        doc.getAs[String]("name").get,
        doc.getAs[String]("bio").get)
  }

  /** serialize a User into a BSON **/
  implicit object UserBSONWriter extends BSONDocumentWriter[User] {
    def write(user: User): BSONDocument =
      BSONDocument(
        "id" -> user.id/*.getOrElse(BSONObjectID.generate)*/,
        "name" -> user.name,
        "bio" -> user.bio)
  }*/
}