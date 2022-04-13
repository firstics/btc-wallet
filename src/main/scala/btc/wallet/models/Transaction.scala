package btc.wallet.models

import java.sql.Timestamp

final case class Transaction(dateTime: Timestamp, amount: Float)
