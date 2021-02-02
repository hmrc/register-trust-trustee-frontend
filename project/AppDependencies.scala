import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "logback-json-logger"            % "4.6.0",
    "uk.gov.hmrc"       %% "govuk-template"                 % "5.61.0-play-27",
    "uk.gov.hmrc"       %% "play-health"                    % "3.15.0-play-27",
    "uk.gov.hmrc"       %% "play-ui"                        % "8.21.0-play-27",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.5.0-play-27",
    "uk.gov.hmrc"       %% "domain"                         % "5.10.0-play-27",
    "uk.gov.hmrc"       %% "emailaddress"                   % "3.5.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27"     % "2.25.0",
    "com.typesafe.play" %% "play-json-joda"                 % "2.7.4"
 )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"            %% "scalatest"             % "3.0.7",
    "org.scalatestplus.play"   %% "scalatestplus-play"    % "4.0.0",
    "org.pegdown"              %  "pegdown"               % "1.6.0",
    "org.jsoup"                %  "jsoup"                 % "1.10.3",
    "com.typesafe.play"        %% "play-test"             % PlayVersion.current,
    "org.mockito"              %  "mockito-all"           % "1.10.19",
    "org.scalacheck"           %% "scalacheck"            % "1.14.0",
    "wolfendale"               %% "scalacheck-gen-regexp" % "0.1.2",
    "com.github.tomakehurst"   % "wiremock-standalone"    % "2.25.1"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion
  )
}
