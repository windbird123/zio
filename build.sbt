name := "zio"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.12"

scalacOptions := Seq(
  "-encoding",
  "UTF-8",                 // source files are in UTF-8
  "-deprecation",          // warn about use of deprecated APIs
  "-unchecked",            // warn about unchecked type parameters
  "-feature",              // warn about misused language features
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-Xlint",                // enable handy linter warnings
  "-Xfatal-warnings",      // turn compiler warnings into errors
  "-Ypartial-unification", // allow the compiler to unify type constructors of different arities
  "-language:implicitConversions"
)

val zioVersion = "1.0.0-RC18"

libraryDependencies ++= Seq(
  "dev.zio"                    %% "zio"            % zioVersion,
  "dev.zio"                    %% "zio-streams"    % zioVersion,
  "dev.zio"                    %% "zio-test"       % zioVersion % "test", // https://github.com/zio/zio-intellij/issues/29 ==> use 1.0.0-RC18 version
  "dev.zio"                    %% "zio-test-sbt"   % zioVersion % "test",
  "org.typelevel"              %% "simulacrum"     % "1.0.0",
  "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2",
  "ch.qos.logback"             % "logback-classic" % "1.2.3",
  "org.scalaj"                 %% "scalaj-http"    % "2.4.2"
)
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")
addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
)
