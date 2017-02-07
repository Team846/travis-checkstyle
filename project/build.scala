import sbt._

object build extends Build {
  override def projects = Seq(root)
  lazy val root = Project("travis-scalastyle", file(".")) settings addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "0.8.0")
}
