import ReleaseTransformations._


lazy val root = project
  .in(file("."))
  .settings(
    name := """graphics""",
    homepage := Some(url("https://github.com/ruimo/graphics")),
    description := "Graphics module.",
    organization := "com.ruimo",
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    releaseProcess -= publishArtifacts,
    crossScalaVersions := List("2.11.12", "2.12.15", "2.13.7", "3.0.2"),
    licenses := Seq(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "ruimo",
        "Shisei Hanai",
        "ruimo.uno@gmail.com",
        url("https://github.com/ruimo")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/ruimo/graphics"),
        "scm:git@github.com:ruimo/graphics.git"
      )
    ),
    scalacOptions in Test ++= Seq("-Yrangepos"),
    libraryDependencies ++= Seq(
      "com.ruimo" %% "scoins" % "1.29",
      "ch.qos.logback" % "logback-core" % "1.2.9",
      "ch.qos.logback" % "logback-classic" % "1.2.9",
      "org.slf4j" % "slf4j-api" % "1.7.32",
      "org.scalatest" %% "scalatest" % "3.2.10" % Test,
      "org.scalactic" %% "scalactic" % "3.2.10" % Test
    )
  )

