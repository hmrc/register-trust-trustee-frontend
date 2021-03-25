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

package services

import base.SpecBase
import connectors.TrustsIndividualCheckConnector
import models._
import models.core.pages.FullName
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.RecoverMethods.recoverToSucceededIf
import pages.register.leadtrustee.individual._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.SessionId

import java.time.LocalDate
import scala.concurrent.Future

class TrustsIndividualCheckServiceSpec extends SpecBase {

  private val sessionId = "sessionId"
  private val id = s"$sessionId~$fakeDraftId"

  override implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId)))

  private val index = 0

  private val firstName = "joe"
  private val firstNameCapitalised = "Joe"
  private val lastName = "bloggs"
  private val lastNameCapitalised = "Bloggs"
  private val nino = "AA000000A"
  private val date = "1996-02-03"

  private val idMatchRequest = IdMatchRequest(id, nino, firstNameCapitalised, lastNameCapitalised, date)

  "TrustsIndividualCheck" when {

    ".matchLeadTrustee" when {

      "4mld" must {
        "return ServiceNotIn5mldModeResponse" in {

          val mockConnector = mock[TrustsIndividualCheckConnector]
          val service = new TrustsIndividualCheckService(mockConnector)

          val userAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

          val result = service.matchLeadTrustee(userAnswers, index)

          whenReady(result) { res =>
            res mustBe ServiceNotIn5mldModeResponse
            verify(mockConnector, never()).matchLeadTrustee(any())(any(), any())
          }
        }
      }

      "5mld" when {

        val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true)

        "sufficient data to assemble IdMatchRequest body" must {

          val userAnswers = baseAnswers
            .set(TrusteesNamePage(index), FullName(firstName, None, lastName)).success.value
            .set(TrusteesDateOfBirthPage(index), LocalDate.parse(date)).success.value
            .set(TrusteesNinoPage(index), nino).success.value

          "return SuccessfulMatchResponse" when {
            "successfully matched" in {

              val mockConnector = mock[TrustsIndividualCheckConnector]
              val service = new TrustsIndividualCheckService(mockConnector)

              when(mockConnector.matchLeadTrustee(any())(any(), any()))
                .thenReturn(Future.successful(SuccessfulOrUnsuccessfulMatchResponse(id, idMatch = true)))

              val result = service.matchLeadTrustee(userAnswers, index)

              whenReady(result) { res =>
                res mustBe SuccessfulMatchResponse
                verify(mockConnector).matchLeadTrustee(eqTo(idMatchRequest))(any(), any())
              }
            }
          }

          "return UnsuccessfulMatchResponse" when {

            "unsuccessfully matched" in {

              val mockConnector = mock[TrustsIndividualCheckConnector]
              val service = new TrustsIndividualCheckService(mockConnector)

              when(mockConnector.matchLeadTrustee(any())(any(), any()))
                .thenReturn(Future.successful(SuccessfulOrUnsuccessfulMatchResponse(id, idMatch = false)))

              val result = service.matchLeadTrustee(userAnswers, index)

              whenReady(result) { res =>
                res mustBe UnsuccessfulMatchResponse
                verify(mockConnector).matchLeadTrustee(eqTo(idMatchRequest))(any(), any())
              }
            }

            "NINO not found" in {

              val mockConnector = mock[TrustsIndividualCheckConnector]
              val service = new TrustsIndividualCheckService(mockConnector)

              when(mockConnector.matchLeadTrustee(any())(any(), any()))
                .thenReturn(Future.successful(NinoNotFoundResponse))

              val result = service.matchLeadTrustee(userAnswers, index)

              whenReady(result) { res =>
                res mustBe UnsuccessfulMatchResponse
                verify(mockConnector).matchLeadTrustee(eqTo(idMatchRequest))(any(), any())
              }
            }
          }

          "return LockedMatchResponse" when {
            "attempt limit exceeded" in {

              val mockConnector = mock[TrustsIndividualCheckConnector]
              val service = new TrustsIndividualCheckService(mockConnector)

              when(mockConnector.matchLeadTrustee(any())(any(), any()))
                .thenReturn(Future.successful(AttemptLimitExceededResponse))

              val result = service.matchLeadTrustee(userAnswers, index)

              whenReady(result) { res =>
                res mustBe LockedMatchResponse
                verify(mockConnector).matchLeadTrustee(eqTo(idMatchRequest))(any(), any())
              }
            }
          }

          "return ServiceUnavailableErrorResponse" when {
            "service unavailable" in {

              val mockConnector = mock[TrustsIndividualCheckConnector]
              val service = new TrustsIndividualCheckService(mockConnector)

              when(mockConnector.matchLeadTrustee(any())(any(), any()))
                .thenReturn(Future.successful(ServiceUnavailableResponse))

              val result = service.matchLeadTrustee(userAnswers, index)

              whenReady(result) { res =>
                res mustBe ServiceUnavailableErrorResponse
                verify(mockConnector).matchLeadTrustee(eqTo(idMatchRequest))(any(), any())
              }
            }
          }

          "return TechnicalDifficultiesErrorResponse" when {

            "invalid match ID" in {

              val mockConnector = mock[TrustsIndividualCheckConnector]
              val service = new TrustsIndividualCheckService(mockConnector)

              when(mockConnector.matchLeadTrustee(any())(any(), any()))
                .thenReturn(Future.successful(InvalidIdMatchResponse))

              val result = service.matchLeadTrustee(userAnswers, index)

              whenReady(result) { res =>
                res mustBe TechnicalDifficultiesErrorResponse
                verify(mockConnector).matchLeadTrustee(eqTo(idMatchRequest))(any(), any())
              }
            }

            "internal server error" in {

              val mockConnector = mock[TrustsIndividualCheckConnector]
              val service = new TrustsIndividualCheckService(mockConnector)

              when(mockConnector.matchLeadTrustee(any())(any(), any()))
                .thenReturn(Future.successful(InternalServerErrorResponse))

              val result = service.matchLeadTrustee(userAnswers, index)

              whenReady(result) { res =>
                res mustBe TechnicalDifficultiesErrorResponse
                verify(mockConnector).matchLeadTrustee(eqTo(idMatchRequest))(any(), any())
              }
            }
          }
        }

        "insufficient data to assemble IdMatchRequest body" must {
          "return IssueBuildingPayloadResponse" in {

            val mockConnector = mock[TrustsIndividualCheckConnector]
            val service = new TrustsIndividualCheckService(mockConnector)

            val result = service.matchLeadTrustee(baseAnswers, index)

            whenReady(result) { res =>
              res mustBe IssueBuildingPayloadResponse
              verify(mockConnector, never()).matchLeadTrustee(any())(any(), any())
            }
          }
        }
      }
    }

    ".failedAttempts" when {

      "header carrier has a session ID" must {
        "make call to connector" in {

          implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId)))

          val mockConnector = mock[TrustsIndividualCheckConnector]
          val service = new TrustsIndividualCheckService(mockConnector)

          val numberOfFailedAttempts = 1

          when(mockConnector.failedAttempts(any())(any(), any()))
            .thenReturn(Future.successful(numberOfFailedAttempts))

          val result = service.failedAttempts(fakeDraftId)

          whenReady(result) { res =>
            res mustBe numberOfFailedAttempts
            verify(mockConnector).failedAttempts(any())(any(), any())
          }
        }
      }

      "header carrier doesn't have a session ID" must {
        "throw error" in {

          implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = None)

          val mockConnector = mock[TrustsIndividualCheckConnector]
          val service = new TrustsIndividualCheckService(mockConnector)

          val result = service.failedAttempts(fakeDraftId)

          recoverToSucceededIf[Exception](result)
        }
      }
    }
  }
}
