/*
 * Copyright 2024 HM Revenue & Customs
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

import config.FrontendAppConfig
import play.api.http.Status.NOT_FOUND
import models.{RegistrationSubmission, SubmissionDraftResponse}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionDraftConnector @Inject()(http: HttpClientV2, config : FrontendAppConfig) {

  private val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"

  def setDraftSectionSet(draftId: String, section: String, data: RegistrationSubmission.DataSet)
                        (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] =
    http
      .post(url"$submissionsBaseUrl/$draftId/set/$section")
      .withBody(Json.toJson(data))
      .execute[HttpResponse]


  def getDraftSection(draftId: String, section: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[SubmissionDraftResponse] =
    http
      .get(url"$submissionsBaseUrl/$draftId/$section")
      .execute[SubmissionDraftResponse]


  def getIsTrustTaxable(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    http
      .get(url"$submissionsBaseUrl/$draftId/is-trust-taxable")
      .execute[Boolean]
      .recover { case _ =>
        true
      }
  }

  def getTrustUtr(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[String]] = {
    http
      .get(url"$submissionsBaseUrl/$draftId/trust-utr")
      .execute[String]
      .map(Some(_))
      .recover {
        case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND => None
      }
  }

}
