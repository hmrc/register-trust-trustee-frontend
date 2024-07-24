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

package controllers.register.leadtrustee.individual

import config.FrontendAppConfig
import controllers.actions.StandardActionSets
import handlers.ErrorHandler
import models.requests.RegistrationDataRequest
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import services.TrustsIndividualCheckService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.leadtrustee.individual.MatchingFailedView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MatchingFailedController @Inject()(
                                          val controllerComponents: MessagesControllerComponents,
                                          implicit val frontendAppConfig: FrontendAppConfig,
                                          standardActionSets: StandardActionSets,
                                          view: MatchingFailedView,
                                          errorHandler: ErrorHandler,
                                          service: TrustsIndividualCheckService
                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.indexValidated(draftId, index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      service.failedAttempts(draftId) map {
        case x if x < frontendAppConfig.maxMatchingAttempts =>
          Ok(view(draftId, index, x, frontendAppConfig.maxMatchingAttempts - x))
        case _ =>
          Redirect(routes.MatchingLockedController.onPageLoad(index, draftId))
      } recoverWith {
        case e =>
          logger.error(s"Failed to retrieve number of failed matching attempts: ${e.getMessage}")
          errorHandler.onServerError(request, new Exception("Could not retrieve retrieve number of failed matching attempts."))
      }
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    Redirect(routes.NameController.onPageLoad(index, draftId))
  }
}
