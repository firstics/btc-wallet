package btc.wallet.actors

import akka.actor.{Actor, DiagnosticActorLogging}
import btc.wallet.models.ServerRequest


class ServerActor extends Actor with DiagnosticActorLogging {
  override def receive: Receive = {
    case r: ServerRequest => {
      log.info(s"Starting new request: ${r.key}")
    }
  }

  override def aroundReceive(receive: Receive, msg: Any): Unit = {
    try {
      log.getMDC
      super.aroundReceive(receive, msg)
    }
    finally {
      log.clearMDC
    }
  }
}
