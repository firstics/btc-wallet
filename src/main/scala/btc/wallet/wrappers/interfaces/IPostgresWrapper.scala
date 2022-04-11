package btc.wallet.wrappers.interfaces

import java.sql.{PreparedStatement, ResultSet}
import scala.concurrent.Future

trait IPostgresWrapper {
  def read[T: Manifest](key: String): Future[T]
  def write(preparedStatement: PreparedStatement): (ResultSet, String)
  def getConnection[T: Manifest]: T
}
