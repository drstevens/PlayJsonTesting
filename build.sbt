resolvers ++= Seq("Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/", "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/")

scalacOptions ++= Seq("-language:postfixOps", "-language:implicitConversions", "-language:higherKinds", "-deprecation", "-feature")
 
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.2.0",
  "org.specs2"        %% "specs2"    % "2.3.7" % "test"
)
