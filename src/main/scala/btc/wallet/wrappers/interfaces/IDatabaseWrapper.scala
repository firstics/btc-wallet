package btc.wallet.wrappers.interfaces

import java.sql.Timestamp
import scala.concurrent.Future

trait IDatabaseWrapper {
  def read[T: Manifest](key: String): Future[T]
  def write(datetime: Timestamp, amount: Int): Future[Boolean]
}
