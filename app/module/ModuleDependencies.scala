package module

import akka.actor.ActorSystem
import com.github.matsluni.akkahttpspi.AkkaHttpClient
import com.typesafe.config.Config
import config.{ConfigKinesis, ConfigMongoDb}
import dao.DaoEvent
import org.mongodb.scala.{MongoClient, MongoDatabase}
import play.api.Configuration
import play.api.inject._
import service.MongoMigrationService
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient

import scala.concurrent.ExecutionContext

class ModuleDependencies extends SimpleModule(
  (_, conf) => {

    implicit lazy val as: ActorSystem = ActorSystem()
    implicit lazy val applicationConfig: Configuration = conf
    implicit lazy val rawWholeConfig: Config = applicationConfig.underlying
    implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    implicit lazy val mongoConfig: ConfigMongoDb = new ConfigMongoDb()
    implicit lazy val kinesisConfig: ConfigKinesis = new ConfigKinesis()

    implicit lazy val mongoClient: MongoClient = {
      MongoClient(mongoConfig.mongoUri)
    }

    implicit lazy val mongoDb: MongoDatabase = mongoClient.getDatabase(mongoConfig.mongoDbName)

    implicit lazy val dao: DaoEvent = new DaoEvent()

    implicit lazy val amazonKinesisAsync: KinesisAsyncClient =
      KinesisAsyncClient
        .builder()
        .region(Region.US_EAST_1)
        .httpClient(AkkaHttpClient.builder().withActorSystem(as).build())
        .build()

    Seq(
      bind[MongoClient].toInstance(mongoClient),
      bind[MongoDatabase].toInstance(mongoDb),
      bind[MongoMigrationService].toSelf.eagerly(),
      bind[DaoEvent].toInstance(dao),
      bind[KinesisAsyncClient].toInstance(amazonKinesisAsync)
    )
  })
