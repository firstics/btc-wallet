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

  override def saveRecord(date: Timestamp, amount: Float): (Boolean, String) = {
    val query: String = s"INSERT INTO $TABLE_NAME (amount, date_time) VALUES(?, ?) RETURNING id"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.asInstanceOf[Connection].prepareStatement(query)
    preparedStatement.setFloat(1, amount)
    preparedStatement.setTimestamp(2, date)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    if(returnSet._2 == "" && returnSet._1.next()) (true, returnSet._2)
    else (false, returnSet._2)
  }

  override def updateRecord(date: Timestamp, amount: Float): (Boolean, String) = {
    val query: String = s"UPDATE $TABLE_NAME SET amount = ? WHERE date_time = ? RETURNING id"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.asInstanceOf[Connection].prepareStatement(query)
    preparedStatement.setFloat(1, amount)
    preparedStatement.setTimestamp(2, date)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    if(returnSet._2 == "" && returnSet._1.next()) (true, returnSet._2)
    else (false, returnSet._2)
  }

  override def getLatestRecord: Transaction = {
    val query: String = s"SELECT amount, date_time from $TABLE_NAME ORDER BY date_time DESC LIMIT 1"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.asInstanceOf[Connection].prepareStatement(query)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    if(returnSet._2 == "" && returnSet._1.next()) {
      Transaction(returnSet._1.getTimestamp("date_time"), returnSet._1.getInt("amount"))
    }
    else {
      null
    }
  }

  override def getRecord(date: Timestamp): Transaction = {
    val query: String = s"SELECT amount, date_time from $TABLE_NAME WHERE date_time = ?"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.asInstanceOf[Connection].prepareStatement(query)
    preparedStatement.setTimestamp(1, date)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    if(returnSet._2 == "" && returnSet._1.next()) {
      Transaction(returnSet._1.getTimestamp("date_time"), returnSet._1.getInt("amount"))
    }
    else {
      null
    }
  }

  override def getRecords(startDate: Timestamp, endDate: Timestamp): List[Transaction] = ???
}
