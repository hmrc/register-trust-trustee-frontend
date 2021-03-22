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

package controllers.register.leadtrustee.individual

import config.FrontendAppConfig
import config.annotations.LeadTrusteeIndividual
import controllers.actions.StandardActionSets
import handlers.ErrorHandler
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.leadtrustee.individual.FailedMatchingPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.leadtrustee.individual.FailedMatchingView

import javax.inject.Inject

class FailedMatchingController @Inject()(
                                          val controllerComponents: MessagesControllerComponents,
                                          implicit val frontendAppConfig: FrontendAppConfig,
                                          standardActionSets: StandardActionSets,
                                          view: FailedMatchingView,
                                          @LeadTrusteeIndividual navigator: Navigator,
                                          errorHandler: ErrorHandler
                                        ) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.indexValidated(draftId, index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      request.userAnswers.get(FailedMatchingPage(index)) match {
        case Some(numberOfFailedAttempts) =>
          Ok(view(draftId, index, numberOfFailedAttempts))
        case _ =>
          InternalServerError(errorHandler.internalServerErrorTemplate)
      }
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      Redirect(navigator.nextPage(FailedMatchingPage(index), draftId, request.userAnswers))
  }
}
