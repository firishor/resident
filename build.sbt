name := "resident"
 
version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
      
scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies ++= Seq(
  "com.lightbend.akka"                  %% "akka-stream-alpakka-kinesis"  % "2.0.2",
  "org.scalatestplus.play"              %% "scalatestplus-play"           % "5.0.0"     % Test,
  "com.typesafe.akka"                   %% "akka-http"                    % "10.1.13",
  "ch.qos.logback"                      %  "logback-core"                 % "1.2.3",
  "ch.qos.logback"                      %  "logback-classic"              % "1.2.3",
  "com.typesafe.scala-logging"          %% "scala-logging"                % "3.9.2"
)

val mongockVersion = "4.1.17"
libraryDependencies ++= Seq(
  "org.mongodb"                         %  "mongo-java-driver"            % "3.12.7",
  "org.mongodb.scala"                   %% "mongo-scala-driver"           % "2.9.0",
  "ch.rasc"                             %  "bsoncodec"                     % "1.0.1",
  "com.github.cloudyrock.mongock"       %  "mongock-bom"                   % mongockVersion,
  "com.github.cloudyrock.mongock"       %  "mongock-standalone"            % mongockVersion,
  "com.github.cloudyrock.mongock"       %  "mongodb-v3-driver"             % mongockVersion
)

scalacOptions := Seq(
  "-deprecation",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Wdead-code",
  "-Wunused:imports",
  "-Ymacro-annotations"
)

libraryDependencies ++= Seq(
  specs2                                                                    % Test,
)