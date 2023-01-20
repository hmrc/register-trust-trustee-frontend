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

package controllers.register

import controllers.actions._
import controllers.actions.register.{RemoveIndexRequest, TrusteeRequiredActionImpl}
import forms.RemoveIndexFormProvider
import play.api.Logging

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import sections.Trustee
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.addAnother.TrusteeViewModel
import views.html.{InternalServerErrorPageView, RemoveIndexView}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class RemoveIndexController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       standardActionSets: StandardActionSets,
                                       trusteeAction: TrusteeRequiredActionImpl,
                                       formProvider: RemoveIndexFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: RemoveIndexView,
                                       errorPageView: InternalServerErrorPageView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen trusteeAction(index, draftId)

  private def redirect(draftId: String): Result = Redirect(controllers.register.routes.AddATrusteeController.onPageLoad(draftId))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val form: Form[Boolean] = formProvider(prefix(request.trustee))

      Ok(view(form, draftId, index, name(request.trustee), prefix(request.trustee)))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val form: Form[Boolean] = formProvider(prefix(request.trustee))

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, name(request.trustee), prefix(request.trustee)))),

        remove => {
          if (remove) {
            request.userAnswers.deleteAtPath(Trustee(index).path) match {
              case Success(updatedAnswers) => registrationsRepository.set(updatedAnswers).map{ _ =>
                redirect(draftId)
              }
              case Failure(_) =>
                logger.error("[RemoveIndexController][onSubmit] Error while storing user answers")
                Future.successful(InternalServerError(errorPageView()))
            }
          } else {
            Future.successful(redirect(draftId))
          }
        }
      )
  }

  private def name(trustee: TrusteeViewModel)(implicit request: RemoveIndexRequest[AnyContent]): String = {
    trustee.name match {
      case Some(name) => name
      case None => Messages(s"${prefix(trustee)}.default")
    }
  }

  private def prefix(trustee: TrusteeViewModel) = {
    if (trustee.isLead) "removeLeadTrusteeYesNo" else "removeTrusteeYesNo"
  }
}
