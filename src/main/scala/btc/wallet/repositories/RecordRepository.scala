package btc.wallet.repositories

import btc.wallet.models.{DisplayTransaction, Transaction}
import btc.wallet.repositories.interfaces.IRecordRepository
import btc.wallet.wrappers.interfaces.{IConfigurationWrapper, IPostgresWrapper}

import java.sql.{PreparedStatement, ResultSet, Timestamp}
import scala.concurrent.ExecutionContextExecutor

class RecordRepository(implicit val configurationWrapper: IConfigurationWrapper,
                       implicit val postgresWrapper: IPostgresWrapper,
                       implicit val executionContext: ExecutionContextExecutor) extends IRecordRepository {

  lazy val TABLE_NAME: String = configurationWrapper.getDBConfig("recordTable")

  override def saveRecord(date: Timestamp, amount: Float): (Boolean, String) = {
    val query: String = s"INSERT INTO $TABLE_NAME (amount, date_time) VALUES(?, ?) RETURNING id"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
    preparedStatement.setFloat(1, amount)
    preparedStatement.setTimestamp(2, date)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    println(returnSet._1.next())
    if(returnSet._2 == "" && returnSet._1.next()) (true, returnSet._2)
    else (false, returnSet._2)
  }

  override def updateRecord(date: Timestamp, amount: Float): (Boolean, String) = {
    val query: String = s"UPDATE $TABLE_NAME SET amount = ? WHERE date_time = ? RETURNING id"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
    preparedStatement.setFloat(1, amount)
    preparedStatement.setTimestamp(2, date)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    if(returnSet._2 == "" && returnSet._1.next()) (true, returnSet._2)
    else (false, returnSet._2)
  }

  override def getLatestRecord: Transaction = {
    val query: String = s"SELECT amount, date_time from $TABLE_NAME ORDER BY date_time DESC LIMIT 1"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    if(returnSet._2 == "" && returnSet._1.next()) {
      Transaction(returnSet._1.getTimestamp("date_time"), returnSet._1.getFloat("amount"))
    }
    else {
      null
    }
  }

  override def getRecord(date: Timestamp): Transaction = {
    val query: String = s"SELECT amount, date_time from $TABLE_NAME WHERE date_time = ?"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
    preparedStatement.setTimestamp(1, date)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    if(returnSet._2 == "" && returnSet._1.next()) {
      Transaction(returnSet._1.getTimestamp("date_time"), returnSet._1.getFloat("amount"))
    }
    else {
      null
    }
  }

  override def getRecords(startDate: Timestamp, endDate: Timestamp): (List[DisplayTransaction], String) = {
    var txs: List[DisplayTransaction] = List.empty
    val query: String = s"SELECT amount, date_time from $TABLE_NAME WHERE date_time BETWEEN '$startDate' and '$endDate'"
    val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
    val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
    if(returnSet._2.isEmpty) {
      while(returnSet._1.next()) {
        val ts: String = returnSet._1.getTimestamp("date_time").toString
          .replace(" ", "T")
          .replace(".0","+00:00")
        txs = txs :+ DisplayTransaction(ts, returnSet._1.getFloat("amount"))
      }
      (txs, returnSet._2)
    }
    else {
      (List.empty, returnSet._2)
    }
  }
}
