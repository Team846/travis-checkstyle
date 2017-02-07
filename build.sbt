sbtPlugin := true

organization := "com.lynbrookrobotics"

name := "travis-scalastyle"

version := "0.2.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies += "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.5"

publishTo := Some(Resolver.file("gh-pages-repo", baseDirectory.value / ".." / "repo"))
