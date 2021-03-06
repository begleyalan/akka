package com.devsummit

import akka.remote.testkit.MultiNodeConfig

object ClientServerConfig extends MultiNodeConfig {
  val frontend = role("frontend")
  val backend = role("backend")
}
