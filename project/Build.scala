import sbt._

object ApplicationBuild extends Build {

  val appName         = "simple-crud-scala-play-mongo-angular"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq(
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
}
