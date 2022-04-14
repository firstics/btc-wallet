package btc.wallet.models.responses

import btc.wallet.models.DisplayTransaction
import btc.wallet.models.Error

final case class HistoryResponder(results: List[DisplayTransaction], errors: Option[Error])
