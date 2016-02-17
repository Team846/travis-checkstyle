package com.lynbrookrobotics.style

import com.etsy.sbt.checkstyle.Checkstyle
import sbt._

object TravisCheckStylePlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin && Checkstyle

  val autoImport = Keys

  override lazy val projectSettings = Seq(
    Keys.publishStyleComments in Compile := Tasks.publishStyleComments.value
  )
}
