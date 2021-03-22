/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import models._
import org.scalatest.{MustMatchers, OptionValues}
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import utils.WireMockHelper

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TrustsIndividualCheckConnectorSpec extends SpecBase with MustMatchers with OptionValues with WireMockHelper {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(Seq(
      "microservice.services.trusts-individual-check.port" -> server.port(),
      "auditing.enabled" -> false): _*
    ).build()

  private lazy val connector = injector.instanceOf[TrustsIndividualCheckConnector]

  private val url = s"/trusts-individual-check/individual-check"

  private val id: String = "id"

  private val idMatchRequest: IdMatchRequest = IdMatchRequest(
    id = id,
    nino = "nino",
    surname = "JOE",
    forename = "BLOGGS",
    birthDate = "1996-02-03"
  )

  "TrustsIndividualCheckConnector" must {

    "return SuccessfulOrUnsuccessfulMatchResponse" when {
      "OK status received" when {

        "successful match response" in {

          val idMatch = true

          server.stubFor(
            post(urlEqualTo(url))
              .willReturn(
                aResponse()
                  .withStatus(Status.OK)
                  .withBody(Json.stringify(Json.toJson(SuccessfulOrUnsuccessfulMatchResponse(id, idMatch))))
              )
          )

          val result = Await.result(connector.matchLeadTrustee(idMatchRequest), Duration.Inf)
          result mustBe SuccessfulOrUnsuccessfulMatchResponse(id, idMatch)
        }

        "unsuccessful match response" in {

          val idMatch = false

          server.stubFor(
            post(urlEqualTo(url))
              .willReturn(
                aResponse()
                  .withStatus(Status.OK)
                  .withBody(Json.stringify(Json.toJson(SuccessfulOrUnsuccessfulMatchResponse(id, idMatch))))
              )
          )

          val result = Await.result(connector.matchLeadTrustee(idMatchRequest), Duration.Inf)
          result mustBe SuccessfulOrUnsuccessfulMatchResponse(id, idMatch)
        }
      }
    }

    "return InvalidIdMatchResponse" when {
      "BAD_REQUEST status received" in {

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(Status.BAD_REQUEST)
                .withBody(Json.stringify(Json.toJson(IdMatchErrorResponse(Seq("Could not validate the request")))))
            )
        )

        val result = Await.result(connector.matchLeadTrustee(idMatchRequest), Duration.Inf)
        result mustBe InvalidIdMatchResponse
      }
    }

    "return AttemptLimitExceededResponse" when {
      "FORBIDDEN status received" in {

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(Status.FORBIDDEN)
                .withBody(Json.stringify(Json.toJson(IdMatchErrorResponse(Seq("Individual check - retry limit reached (3)")))))
            )
        )

        val result = Await.result(connector.matchLeadTrustee(idMatchRequest), Duration.Inf)
        result mustBe AttemptLimitExceededResponse
      }
    }

    "return NinoNotFoundResponse" when {
      "NOT_FOUND status received" in {

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(Status.NOT_FOUND)
                .withBody(Json.stringify(Json.toJson(IdMatchErrorResponse(Seq("Dependent service indicated that no data can be found")))))
            )
        )

        val result = Await.result(connector.matchLeadTrustee(idMatchRequest), Duration.Inf)
        result mustBe NinoNotFoundResponse
      }
    }

    "return InternalServerErrorResponse" when {
      "5xx status received" in {

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(Status.INTERNAL_SERVER_ERROR)
            )
        )

        val result = Await.result(connector.matchLeadTrustee(idMatchRequest), Duration.Inf)
        result mustBe InternalServerErrorResponse
      }
    }
  }
}
