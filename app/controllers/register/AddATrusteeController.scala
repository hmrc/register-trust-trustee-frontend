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

package controllers.register

import config.FrontendAppConfig
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.{AddATrusteeFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.Enumerable
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import models.registration.pages.AddATrustee.{NoComplete, YesNow}
import navigation.Navigator
import pages.register.{AddATrusteePage, AddATrusteeYesNoPage, TrusteeOrLeadTrusteePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddATrusteeViewHelper
import views.html.register.{AddATrusteeView, AddATrusteeYesNoView, MaxedOutView}

import scala.concurrent.{ExecutionContext, Future}

class AddATrusteeController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       implicit val frontendAppConfig: FrontendAppConfig,
                                       registrationsRepository: RegistrationsRepository,
                                       navigator: Navigator,
                                       identify: RegistrationIdentifierAction,
                                       getData: DraftIdRetrievalActionProvider,
                                       requireData: RegistrationDataRequiredAction,
                                       addAnotherFormProvider: AddATrusteeFormProvider,
                                       yesNoFormProvider: YesNoFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       addAnotherView: AddATrusteeView,
                                       yesNoView: AddATrusteeYesNoView,
                                       maxedOutView: MaxedOutView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  private val addAnotherForm = addAnotherFormProvider()
  private val yesNoForm = yesNoFormProvider.withPrefix("addATrusteeYesNo")

  private def actions(draftId: String) =
    identify andThen getData(draftId) andThen requireData

  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addATrustee.heading")
      case 1 => Messages("addATrustee.singular.heading")
      case size => Messages("addATrustee.count.heading", size)
    }
  }

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val trustees = new AddATrusteeViewHelper(request.userAnswers, draftId).rows

      val isLeadTrusteeDefined = request.userAnswers.get(Trustees).toList.flatten.exists(trustee => trustee.isLead)

      trustees.count match {
        case 0 =>
          Ok(yesNoView(yesNoForm, draftId))
        case x @ 24 if !isLeadTrusteeDefined =>
          Ok(addAnotherView(
            addAnotherForm,
            routes.AddATrusteeController.submitLead(draftId),
            trustees.inProgress,
            trustees.complete,
            isLeadTrusteeDefined,
            heading(x)
          ))
        case x @ 25 =>
          Ok(maxedOutView(draftId, trustees.inProgress, trustees.complete, heading(x)))
        case count =>
          Ok(addAnotherView(
            addAnotherForm,
            routes.AddATrusteeController.submitAnother(draftId),
            trustees.inProgress,
            trustees.complete,
            isLeadTrusteeDefined,
            heading(count)
          ))
      }
  }

  def submitOne(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors, draftId)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteeYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddATrusteeYesNoPage, draftId, updatedAnswers))
        }
      )
  }

  def submitAnother(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val trustees = new AddATrusteeViewHelper(request.userAnswers, draftId).rows

          val isLeadTrusteeDefined = request.userAnswers.get(Trustees).toList.flatten.exists(trustee => trustee.isLead)

          Future.successful(BadRequest(
            addAnotherView(
              formWithErrors,
              routes.AddATrusteeController.submitAnother(draftId),
              trustees.inProgress,
              trustees.complete,
              isLeadTrusteeDefined,
              heading(trustees.count)
            )
          ))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteePage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddATrusteePage, draftId, updatedAnswers))
        }
      )
  }

  def submitLead(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val index: Int = 24

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val trustees = new AddATrusteeViewHelper(request.userAnswers, draftId).rows

          Future.successful(BadRequest(
            addAnotherView(
              formWithErrors,
              routes.AddATrusteeController.submitLead(draftId),
              trustees.inProgress,
              trustees.complete,
              isLeadTrusteeDefined = false,
              heading(trustees.count)
            )
          ))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers
              .set(AddATrusteePage, value)
              .flatMap(_.set(TrusteeOrLeadTrusteePage(index), LeadTrustee))
            )
            _ <- registrationsRepository.set(updatedAnswers)
          } yield {
            if (value == YesNow) {
              Redirect(routes.TrusteeIndividualOrBusinessController.onPageLoad(index, draftId))
            } else {
              Redirect(navigator.nextPage(AddATrusteePage, draftId, updatedAnswers))
            }
          }
        }
      )
  }

  def submitComplete(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(AddATrusteePage, NoComplete))
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(AddATrusteePage, draftId, updatedAnswers))
  }
}
