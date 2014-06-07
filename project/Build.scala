import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {

  val appName         = "play-framework-showcase"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq(
    "org.reactivemongo"         %% "reactivemongo"          % "0.10.0",
    "org.reactivemongo"         %% "play2-reactivemongo"    % "0.10.2",

    "org.scalatest"             %% "scalatest"              % "2.1.0"                % "test",
    "com.github.simplyscala"    %% "scalatest-embedmongo"   % "0.2.2-SNAPSHOT"       % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )
}
