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

import controllers.actions.StandardActionSets
import controllers.actions.register.TrusteeNameRequest
import controllers.actions.register.leadtrustee.individual.NameRequiredActionImpl
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.leadtrustee.individual.MatchingLockedView

import javax.inject.Inject

class MatchingLockedController @Inject()(
                                          val controllerComponents: MessagesControllerComponents,
                                          standardActionSets: StandardActionSets,
                                          nameAction: NameRequiredActionImpl,
                                          view: MatchingLockedView
                                        ) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String): ActionBuilder[TrusteeNameRequest, AnyContent] =
    standardActionSets.indexValidated(draftId, index) andThen nameAction(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      Ok(view(draftId, index, request.trusteeName))
  }
}
