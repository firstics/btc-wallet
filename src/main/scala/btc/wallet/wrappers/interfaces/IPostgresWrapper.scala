package btc.wallet.wrappers.interfaces

import java.sql.{PreparedStatement, ResultSet}
import scala.concurrent.Future

trait IPostgresWrapper {
  def executeQuery(preparedStatement: PreparedStatement): (ResultSet, String)
  def getConnection[T: Manifest]: T
}
