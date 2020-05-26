name := "Big_Data_Hw3"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0" %"provided"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.0" % "provided"

libraryDependencies+="org.apache.spark" %% "spark-streaming" % "2.4.0" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka" % "1.6.3"

libraryDependencies += "jp.co.bizreach" %% "elastic-scala-httpclient" % "4.0.0"

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}
