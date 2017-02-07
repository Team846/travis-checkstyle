package com.lynbrookrobotics.style

import sbt._

object TravisScalaStylePlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin

  val autoImport = Keys

  override lazy val projectSettings = Seq(
    Keys.publishStyleComments in Compile := Tasks.publishStyleComments.value
  )
}
