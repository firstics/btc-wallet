package btc.wallet.models.responses

import btc.wallet.models.Error

final case class RecordResponder(results: String, errors: Option[List[Error]])
