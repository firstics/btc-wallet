package btc.wallet.models.responses

import btc.wallet.models.Error

final case class RecordResponder(result: Boolean, errors: Option[Error])
