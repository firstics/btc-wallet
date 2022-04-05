package btc.wallet

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import btc.wallet.wrappers.interfaces.IConfigurationWrapper

import scala.concurrent.ExecutionContextExecutor

class ServerRouting(implicit val configurationWrapper: IConfigurationWrapper,
                    implicit val system: ActorSystem,
                    implicit val executionContextExecutor: ExecutionContextExecutor) extends Directives{
  def route: Route = {
    val baseRoute = pathPrefix("services" / "consent")
    baseRoute {
      concat(
        get(path("healthcheck") {
          withRequestTimeout(120.seconds) {
            healthCheckController.healthCheck()
          }
        }),
        get(path("status" ~ Slash ~ "company" ~ Slash ~ Segment ~ Slash ~ "sub-company" ~ Slash ~ Segment ~ Slash ~ Segment) {
          (company, subCompany, token) => {
            extractRequest {
              requester => {
                requester.getHeader("Authorization").asScala match {
                  case Some(HttpHeader(_, authorization)) => {
                    consentController.getConsentStatus(ConsentRequester(company, subCompany, token),
                      authorization)
                  }
                  case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
                }
              }
            }
          }
        }),
        get(pathPrefix("swagger") {
          swaggerController.swagger()
        })
      )
    }
  }
}
