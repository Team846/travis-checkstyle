package com.lynbrookrobotics.style

import sbt._

object Keys {
  lazy val publishStyleComments = taskKey[Unit]("Publish style comments to the PR")
}
