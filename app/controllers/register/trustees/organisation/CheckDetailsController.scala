/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.register.trustees.organisation

import config.FrontendAppConfig
import controllers.actions._
import controllers.actions.register.trustees.organisation.NameRequiredActionImpl
import models.Status.Completed
import navigation.Navigator
import pages.entitystatus.TrusteeStatus
import pages.register.TrusteesAnswerPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.TrusteeOrganisationPrintHelper
import viewmodels.Section
import views.html.InternalServerErrorPageView
import views.html.register.trustees.organisation.CheckDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        implicit val frontendAppConfig: FrontendAppConfig,
                                        registrationsRepository: RegistrationsRepository,
                                        navigator: Navigator,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: TrusteeOrganisationPrintHelper,
                                        nameAction: NameRequiredActionImpl,
                                        errorPageView: InternalServerErrorPageView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen nameAction(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val section: Section = printHelper.checkDetailsSection(request.userAnswers, request.trusteeName, index, draftId)

      Ok(view(section, index, draftId))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      request.userAnswers.set(TrusteeStatus(index), Completed) match {
        case Success(updatedAnswers) =>
          registrationsRepository.set(updatedAnswers).map{ _ =>
            Redirect(navigator.nextPage(TrusteesAnswerPage, draftId, request.userAnswers))
          }
        case Failure(_) =>
          logger.error("[CheckDetailsController][onSubmit] Error while storing user answers")
          Future.successful(InternalServerError(errorPageView()))
      }
  }
}
