
lazy val root = (project in file("."))
  .settings(
    name := """make_csv""",
    organization := "jp.hotbrain",
    version := "1.0.0",

    crossPaths := true,

    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions += "-target:jvm-1.8",
    scalaVersion := "2.11.7",

    libraryDependencies ++= Seq(
      // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
      "org.junit.jupiter" % "junit-jupiter-api" % "5.3.1" % Test
      // https://mvnrepository.com/artifact/org.junit.vintage/junit-vintage-engine
      , "org.junit.vintage" % "junit-vintage-engine" % "5.3.1" % Test
      // https://mvnrepository.com/artifact/org.junit.platform/junit-platform-launcher
      , "org.junit.platform" % "junit-platform-launcher" % "1.3.1" % Test
      //, "org.hamcrest" % "hamcrest-junit" % "2.0.0.0" % Test
      //      , "org.hamcrest" % "hamcrest-core" % "1.3" % Test
      , "mysql" % "mysql-connector-java" % "5.1.39" % Provided
      , "joda-time" % "joda-time" % "2.9.7"
      , "org.joda" % "joda-convert" % "1.7"
    )
  )

