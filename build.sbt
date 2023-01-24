name := "ClassAttendanceRegister"

version := "0.1"

scalaVersion := "2.13.10"

// Workaround for scala-java8-compat issue affecting Lagom dev-mode
// https://github.com/lagom/lagom/issues/3344
ThisBuild / libraryDependencySchemes +=
  "org.scala-lang.modules" %% "scala-java8-compat" % VersionScheme.Always

lazy val macwire = "com.softwaremill.macwire" %% "macros" % "2.5.8" % "provided"
lazy val scalatest = "org.scalatest" %% "scalatest" % "3.2.15" % Test


lazy val `common` = (project in file("common"))
  .settings(
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.3"
  )

// classroom
lazy val `classroom-api` = (project in file("classroom-api"))
  .settings(
    libraryDependencies += lagomScaladslApi
  )
  .dependsOn(`common`)

lazy val `classroom-impl` = (project in file("classroom-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      macwire,
      lagomScaladslTestKit,
      scalatest
    )
  )
  .dependsOn(`classroom-api`, `common`)

// student
lazy val `student-api` = (project in file("student-api"))
  .settings(
    libraryDependencies += lagomScaladslApi
  )
  .dependsOn(`common`)

lazy val `student-impl` = (project in file("student-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      macwire,
      lagomScaladslTestKit,
      scalatest
    )
  )
  .dependsOn(`common`, `student-api`)