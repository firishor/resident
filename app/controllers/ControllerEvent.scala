package controllers

import dto.DtoAverage
import play.api.libs.json.Json
import play.api.mvc._
import service.{Service, ServiceKinesis}

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ControllerEvent @Inject()()(implicit
                                  val controllerComponents: ControllerComponents,
                                  ec: ExecutionContext,
                                  service: Service,
                                  serviceKinesis: ServiceKinesis)
  extends BaseController {

  def average(eventType: String, from: Long, to: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    service.average(eventType, from, to)
      .map { accumulator =>
        val result = DtoAverage(
          eventType,
          accumulator.value,
          accumulator.processedCount
        )
        Ok(Json.toJson(result))
      }
  }

}
