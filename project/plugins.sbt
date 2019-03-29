


addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.20")

addSbtPlugin("com.frugalmechanic" % "fm-sbt-s3-resolver" % "0.14.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")


//addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

//addSbtPlugin("org.scalastyle" %%  "scalastyle-sbt-plugin" % "0.8.0")

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.9.7"
  , "org.joda" % "joda-convert" % "1.7"
)
