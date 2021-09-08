package config

import play.api.Configuration

import java.time.Duration
import javax.inject.{Inject, Singleton}

@Singleton
class ConfigKinesis @Inject()()(implicit val applicationConfig: Configuration){
  val streamName: String = applicationConfig.get[String]("kinesis.stream-name")
  val insertBatchNumber: Int = applicationConfig.get[Int]("kinesis.insert-batch-number")
  val parallelism: Int = applicationConfig.get[Int]("kinesis.parallelism")
  val refreshInterval: Int = applicationConfig.get[Int]("kinesis.refresh-interval")
  val limit: Int = applicationConfig.get[Int]("kinesis.limit")
}