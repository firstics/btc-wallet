package btc.wallet.repositories

import btc.wallet.models.Transaction
import btc.wallet.repositories.interfaces.IRecordRepository
import btc.wallet.wrappers.interfaces.{IConfigurationWrapper, IPostgresWrapper}

import java.sql.{Connection, PreparedStatement, ResultSet, Timestamp}
import scala.concurrent.{ExecutionContextExecutor, Future}

class RecordRepository(implicit val configurationWrapper: IConfigurationWrapper,
                       implicit val postgresWrapper: IPostgresWrapper,
                       implicit val executionContext: ExecutionContextExecutor) extends IRecordRepository {

  lazy val TABLE_NAME: String = configurationWrapper.getDBConfig("recordTable")

  override def saveRecord(date: Timestamp, amount: Int): Future[(Boolean, String)] = Future {
    val query: String = s"INSERT INTO $TABLE_NAME (amount, date_time) VALUES(?, ?) RETURNING id"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.asInstanceOf[Connection].prepareStatement(query)
    preparedStatement.setInt(1, amount)
    preparedStatement.setTimestamp(2, date)
    val returnSet: (ResultSet, String) = postgresWrapper.write(preparedStatement)
    if(returnSet._2 == "" && returnSet._1.next()) (true, returnSet._2)
    else (false, returnSet._2)
  }

  override def getRecord(startDate: Timestamp, endDate: Timestamp): Future[List[Transaction]] = ???
}
