package repositories

import com.google.inject.ImplementedBy
import models.{APIError, GitHubUser}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.{Filters, IndexModel, Indexes, ReplaceOptions}
import org.mongodb.scala.result
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DataRepository])
trait MockRepository {
  def index(): Future[Either[APIError.BadAPIResponse, Seq[GitHubUser]]]
  def create(user: GitHubUser): Future[Either[JsValue, GitHubUser]]
  def read(username: String): Future[Either[JsValue,GitHubUser]]
  def update(username: String, user:GitHubUser): Future[result.UpdateResult]
  def delete(user: String): Future[Either[JsValue, result.DeleteResult]]
//  def findByName(name:String): Future[Either[JsValue, GitHubUser]]
//  def updateByField(id:String, fieldName: String, value:String): Future[Either[JsValue, result.UpdateResult]]
}

@Singleton
class DataRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[GitHubUser](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = GitHubUser.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("id")
  )),
  replaceIndexes = false
  /*
  - "dataModels" is the name of the collection (you can set this to whatever you like).
  - DataModel.formats uses the implicit val formats we created earlier.
    It tells the driver how to read and write between a DataModel and JSON
    (the format that data is stored in Mongo)
  - indexes is shows the structure of the data stored in Mongo, notice we can ensure the bookId to be unique
   */
) with MockRepository {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[GitHubUser]]] = {
    collection.find().toFuture().map {
      case user: Seq[GitHubUser] => Right(user)
      case _ => Left(APIError.BadAPIResponse(404, "User cannot be found")
      )
    }
  }

  private def byUsername(username: String): Bson =
    Filters.and(
      Filters.equal("login", username)
    )

  def create(user: GitHubUser): Future[Either[JsValue, GitHubUser]] = {
    collection.find(byUsername(user.login)).first().toFuture() flatMap  {
      case usr: GitHubUser => Future(Left(Json.toJson(s"The user with the username: ${usr.login} already exists.")))
      case _ => collection
        .insertOne(user)
        .toFuture()
        .map(_ => Right(user))
    }
  }

  def read(username: String): Future[Either[JsValue, GitHubUser]] =
    collection.find(byUsername(username)).headOption flatMap {
      case Some(data) =>
        Future(Right(data))
      case None => Future(Left(Json.toJson(s"The user with the username: ${username} already exists.")))
    }

  def update(username: String, user: GitHubUser): Future[result.UpdateResult] = {
    collection.replaceOne(
      filter = byUsername(username),
      replacement = user,
      options = new ReplaceOptions().upsert(true)
    ).toFuture()
  }

  def delete(username: String): Future[Either[JsValue, result.DeleteResult]] =
    collection.find(byUsername(username)).first().toFuture() flatMap {
      case usr: GitHubUser if usr != null => collection.deleteOne(
        filter = byUsername(username)
      ).toFuture().map(deleteResult => Right(deleteResult))
      case _ => Future(Left(Json.toJson(s"No user found with the username: ${username}.")))
    }

}
