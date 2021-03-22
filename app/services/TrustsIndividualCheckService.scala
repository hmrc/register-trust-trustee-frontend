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

import connectors.TrustsIndividualCheckConnector
import models._
import pages.register.leadtrustee.individual._
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustsIndividualCheckService @Inject()(connector: TrustsIndividualCheckConnector) extends Logging {

  def matchLeadTrustee(userAnswers: UserAnswers, index: Int)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsIndividualCheckServiceResponse] = {

    if (userAnswers.is5mldEnabled) {
      val body: Option[IdMatchRequest] = for {
        sessionId <- hc.sessionId
        id = s"${sessionId.value}~${userAnswers.draftId}"
        nino <- userAnswers.get(TrusteesNinoPage(index))
        name <- userAnswers.get(TrusteesNamePage(index))
        dob <- userAnswers.get(TrusteesDateOfBirthPage(index))
      } yield {
        IdMatchRequest(id, nino, name.firstName.capitalize, name.lastName.capitalize, dob.toString)
      }

      body match {
        case Some(idMatchRequest) =>
          connector.matchLeadTrustee(idMatchRequest) map {
            case SuccessfulOrUnsuccessfulMatchResponse(_, true) => SuccessfulMatchResponse
            case _ => UnsuccessfulMatchResponse
          }
        case _ =>
          logger.error(s"[matchLeadTrustee] Unable to build request body.")
          Future.successful(IssueBuildingPayloadResponse)
      }
    } else {
      Future.successful(ServiceNotIn5mldModeResponse)
    }
  }
}
