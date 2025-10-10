/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.register.leadtrustee.individual

import config.FrontendAppConfig
import controllers.actions.StandardActionSets
import controllers.actions.register.TrusteeNameRequest
import controllers.actions.register.leadtrustee.individual.NameRequiredActionImpl
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionBuilder, AnyContent, Result}
import services.TrustsIndividualCheckService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait NinoControllerHelper {
  implicit def ec: ExecutionContext

  def standardActionSets: StandardActionSets

  def nameAction: NameRequiredActionImpl

  def trustsIndividualCheckService: TrustsIndividualCheckService

  def frontendAppConfig: FrontendAppConfig

  protected def actions(index: Int, draftId: String): ActionBuilder[TrusteeNameRequest, AnyContent] =
    standardActionSets.indexValidated(draftId, index) andThen nameAction(index)

  protected def isLeadTrusteeMatched(index: Int)(implicit request: TrusteeNameRequest[_]) =
    request.userAnswers.isLeadTrusteeMatched(index)

  protected def maxFailedAttemptsReached(draftId: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    trustsIndividualCheckService.failedAttempts(draftId).map { attempts =>
      attempts >= frontendAppConfig.maxMatchingAttempts
    }

  protected def redirectToFailedAttemptsPage(index: Int, draftId: String): Result = {
    Redirect(controllers.register.leadtrustee.individual.routes.MatchingLockedController.onPageLoad(index, draftId))
  }
}
