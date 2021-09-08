package config

import play.api.Configuration

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration

@Singleton
class ConfigMongoDb @Inject()()(implicit val applicationConfig: Configuration){
  val mongoUri: String = applicationConfig.get[String]("mongodb.uri")
  val mongoDbName: String = applicationConfig.get[String]("mongodb.db-name")
  val migrationMaxDuration: Duration = applicationConfig.get[Duration]("mongodb.migration-lock.max-duration")
  val migrationWaitingForLockTimeout: Duration = applicationConfig.get[Duration]("mongodb.migration-lock.waiting-for-lock-timeout")
  val migrationMaxAcquireTries: Int = applicationConfig.get[Int]("mongodb.migration-lock.max-acquire-tries")
}
