package btc.wallet.wrappers.interfaces

import com.typesafe.config.Config

trait IConfigurationWrapper {
  def getConfig: Config
  def getSettingConfig(key: String): String
  def getDBConfig(key: String): String
}

