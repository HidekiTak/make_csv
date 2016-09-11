name := """make_csv"""

organization := "jp.hotbrain"

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)

mainClass in assembly := Some("Main")

crossPaths := false

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions += "-target:jvm-1.7"

version := "1.0.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository"
  , "Local Ivy Repository" at "file:///" + Path.userHome.absolutePath + "/.ivy2/repository"
)

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "junit" % "junit" % "4.12" % "test"
  , "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  , "org.hamcrest" % "hamcrest-junit" % "2.0.0.0" % "test"
  , "org.hamcrest" % "hamcrest-core" % "1.3" % "test"
  , "mysql" % "mysql-connector-java" % "5.1.39" % "test"
  , "joda-time" % "joda-time" % "2.9.4"
  , "org.joda" % "joda-convert" % "1.8.1"
)

//val classPath = Seq(
//  ".",
//  "./lib/")
//
//packageOptions += Package.ManifestAttributes(
//  "Class-Path" -> classPath.mkString(" ")
//)
