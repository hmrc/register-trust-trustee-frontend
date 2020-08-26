/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.filters.IndexActionFilterProvider
import forms.TrusteeOrLeadTrusteeFormProvider
import javax.inject.Inject
import models.core.pages.TrusteeOrLeadTrustee.Trustee
import navigation.Navigator
import pages.register.TrusteeOrLeadTrusteePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.addAnother.TrusteeViewModel
import views.html.register.TrusteeOrLeadTrusteeView

import scala.concurrent.{ExecutionContext, Future}

class TrusteeOrLeadTrusteeController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                implicit val frontendAppConfig: FrontendAppConfig,
                                                registrationsRepository: RegistrationsRepository,
                                                navigator: Navigator,
                                                identify: RegistrationIdentifierAction,
                                                getData: DraftIdRetrievalActionProvider,
                                                requireData: RegistrationDataRequiredAction,
                                                validateIndex : IndexActionFilterProvider,
                                                formProvider: TrusteeOrLeadTrusteeFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: TrusteeOrLeadTrusteeView
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  private def actions(index : Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, Trustees)

  def onPageLoad(index : Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      def renderView = {
        val preparedForm = request.userAnswers.get(TrusteeOrLeadTrusteePage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Future.successful(Ok(view(preparedForm, draftId, index)))
      }

      def leadTrustee : Option[(TrusteeViewModel, Int)] = {
        val trustees = request.userAnswers.get(Trustees).getOrElse(Nil).zipWithIndex

        trustees.find{ case (trustee, _) => trustee.isLead}
      }

      leadTrustee match {
        case Some((_, i)) =>
          def currentIndexIsNotTheLeadTrustee = i != index

          // A lead trustee has already been added, if the current index is not the lead trustee
          // answer the question on behalf of the user and redirect to next page
          if (currentIndexIsNotTheLeadTrustee) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteeOrLeadTrusteePage(index), Trustee))
              _              <- registrationsRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(TrusteeOrLeadTrusteePage(index), draftId, updatedAnswers))
          } else {
            renderView
          }
        case None =>
          renderView
      }
  }

  def onSubmit(index : Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteeOrLeadTrusteePage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrusteeOrLeadTrusteePage(index), draftId, updatedAnswers))
        }
      )
  }
}
