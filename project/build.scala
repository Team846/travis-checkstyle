import sbt._

object build extends Build {
  override def projects = Seq(root)
  lazy val root = Project("travis-checkstyle", file(".")) settings addSbtPlugin("com.etsy" % "sbt-checkstyle-plugin" % "2.0.0")
}
