package service

import com.github.cloudyrock.mongock.driver.mongodb.v3.driver.MongoCore3Driver
import com.github.cloudyrock.standalone.MongockStandalone
import com.mongodb.client.{MongoClient, MongoClients}
import config.ConfigMongoDb
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class MongoMigrationService @Inject()(implicit
                                     applicationLifecycle: ApplicationLifecycle,
                                     config: ConfigMongoDb) {

  import ServiceMongoMigration._

  logger.info("starting Mongock migrations")

  val mongoClient: MongoClient = MongoClients.create(config.mongoUri)
  val driver: MongoCore3Driver = MongoCore3Driver
    .withLockSetting(
      mongoClient,
      config.mongoDbName,
      config.migrationMaxDuration.toMinutes,
      config.migrationWaitingForLockTimeout.toMinutes,
      config.migrationMaxAcquireTries)
  driver.disableTransaction()
  driver.setChangeLogCollectionName(changeLogCollectionName)
  driver.setLockCollectionName(lockCollectionName)

  val runner: MongockStandalone.Runner = MongockStandalone.builder()
    .setDriver(driver)
    .addChangeLogsScanPackage(migrationPackage)
    .buildRunner()

  applicationLifecycle.addStopHook { () => Future.successful(mongoClient.close()) }



  try {
    runner.execute()
  } finally {
    mongoClient.close()
  }
}

object ServiceMongoMigration {
  val migrationPackage = "mongo.migrations"
  val changeLogCollectionName = "mongockChangeLog"
  val lockCollectionName = "mongockLock"
  val logger = Logger(this.getClass)
}