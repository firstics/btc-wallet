package btc.wallet.wrappers

import btc.wallet.wrappers.interfaces.IDatabaseWrapper

import java.sql.Timestamp
import scala.concurrent.Future

class PostgresWrapper extends IDatabaseWrapper{
  override def read[T: Manifest](key: String): Future[T] = ???

  override def write(datetime: Timestamp, amount: Int): Future[Boolean] = ???
}
