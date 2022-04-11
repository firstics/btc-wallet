package btc.wallet.models.responses

import btc.wallet.models.Transaction
import btc.wallet.models.Error

final case class HistoryResponder(results: List[Transaction], errors: Option[List[Error]])
