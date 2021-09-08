package service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import model.Accumulator
import org.specs2.execute.Result
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ServiceSpec extends Specification {
  implicit val sys = ActorSystem("MyTest")
  implicit val mat = ActorMaterializer()
  implicit val service: Service = new Service()

  "A Service" should {
    "average should calculate the average of a given eventType between two timestamps" in averageBetween2TimestampsTest
    "average should calculate the average of a given eventType between two equal timestamps" in averageForTheSameTimestampsTest
    "average should be 0, 0 in case we have no values between the timestamps" in averageForNoValuesBetweenTimestampsTest
    "average should be 0, 0 in case a type does not exist" in averageForNonExistingTypeTest
  }

  def averageBetween2TimestampsTest: Result = {
    val res = Await.result(service.average("earth", 1567365890, 1567365891), Duration.Inf)

    res must beEqualTo(Accumulator(9.269, 13))
  }

  def averageForTheSameTimestampsTest: Result = {
    val res = Await.result(service.average("earth", 1567365890, 1567365890), Duration.Inf)

    res must beEqualTo(Accumulator(1.464, 3))
  }

  def averageForNoValuesBetweenTimestampsTest: Result = {
    val res = Await.result(service.average("earth", 123, 124), Duration.Inf)

    res must beEqualTo(Accumulator(0, 0))
  }

  def averageForNonExistingTypeTest: Result = {
    val res = Await.result(service.average("saturn", 1567365890, 1567365891), Duration.Inf)

    res must beEqualTo(Accumulator(0, 0))
  }


}
