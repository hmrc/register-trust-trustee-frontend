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

package controllers.register

import config.FrontendAppConfig
import controllers.actions.StandardActionSets
import forms.{AddATrusteeFormProvider, YesNoFormProvider}
import models.Status.Completed
import models.TaskStatus.TaskStatus
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import models.registration.pages.AddATrustee
import models.registration.pages.AddATrustee.{NoComplete, YesNow}
import models.requests.RegistrationDataRequest
import models.{Enumerable, TaskStatus, UserAnswers}
import navigation.Navigator
import pages.register.{AddATrusteePage, AddATrusteeYesNoPage, TrusteeOrLeadTrusteePage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import services.TrustsStoreService
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Constants.MAX
import utils.{AddATrusteeViewHelper, RegistrationProgress}
import views.html.InternalServerErrorPageView
import views.html.register.{AddATrusteeView, AddATrusteeYesNoView, MaxedOutView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AddATrusteeController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       implicit val frontendAppConfig: FrontendAppConfig,
                                       registrationsRepository: RegistrationsRepository,
                                       navigator: Navigator,
                                       standardActionSets: StandardActionSets,
                                       addAnotherFormProvider: AddATrusteeFormProvider,
                                       yesNoFormProvider: YesNoFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       addAnotherView: AddATrusteeView,
                                       yesNoView: AddATrusteeYesNoView,
                                       maxedOutView: MaxedOutView,
                                       trustsStoreService: TrustsStoreService,
                                       registrationProgress: RegistrationProgress,
                                       errorPageView: InternalServerErrorPageView
                                     )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport with Enumerable.Implicits with Logging {

  private val addAnotherForm = addAnotherFormProvider()
  private val yesNoForm = yesNoFormProvider.withPrefix("addATrusteeYesNo")

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.identifiedUserWithData(draftId)

  private def dynamicHeading(count: Int)(implicit mp: MessagesProvider): String = {
    count match {
      case x if x <= 1 => Messages("addATrustee.heading")
      case _ => Messages("addATrustee.count.heading", count)
    }
  }

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val trustees = new AddATrusteeViewHelper(request.userAnswers, draftId).rows

      val isLeadTrusteeDefined = request.userAnswers.get(Trustees).toList.flatten.exists(_.isLead)

      trustees.count match {
        case 0 =>
          Ok(yesNoView(yesNoForm, draftId))
        case x if (x == MAX - 1) && !isLeadTrusteeDefined =>
          Ok(addAnotherView(
            addAnotherForm,
            routes.AddATrusteeController.submitLead(draftId),
            trustees.inProgress,
            trustees.complete,
            isLeadTrusteeDefined,
            dynamicHeading(x)
          ))
        case x if x >= MAX =>
          Ok(maxedOutView(draftId, trustees.inProgress, trustees.complete, dynamicHeading(x)))
        case count =>
          Ok(addAnotherView(
            addAnotherForm,
            routes.AddATrusteeController.submitAnother(draftId),
            trustees.inProgress,
            trustees.complete,
            isLeadTrusteeDefined,
            dynamicHeading(count)
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
          request.userAnswers.set(AddATrusteeYesNoPage, value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                setTaskStatus(draftId, TaskStatus.InProgress)
                Redirect(navigator.nextPage(AddATrusteeYesNoPage, draftId, updatedAnswers))
              }
            case Failure(_) =>
              logger.error("[AddATrusteeController][submitOne] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
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
              dynamicHeading(trustees.count)
            )
          ))
        },
        value => {
          request.userAnswers.set(AddATrusteePage, value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map{ _ =>
                setTaskStatus(updatedAnswers, draftId, value)
                Redirect(navigator.nextPage(AddATrusteePage, draftId, updatedAnswers))
              }
            case Failure(_) =>
              logger.error("[AddATrusteeController][submitAnother] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
        }
      )
  }

  def submitLead(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val index: Int = MAX - 1

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
              dynamicHeading(trustees.count)
            )
          ))
        },
        value => {
          request.userAnswers.set(AddATrusteePage, value).flatMap(_.set(TrusteeOrLeadTrusteePage(index), LeadTrustee)) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                setTaskStatus(updatedAnswers, draftId, value)
                if (value == YesNow ) {
                  Redirect(routes.TrusteeIndividualOrBusinessController.onPageLoad(index, draftId))
                }
                else {
                  Redirect(navigator.nextPage(AddATrusteePage, draftId, updatedAnswers))
                }
              }
            case Failure(_) =>
              logger.error("[AddATrusteeController][submitLead] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
        }
      )
  }

  def submitComplete(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val status = NoComplete

      request.userAnswers.set(AddATrusteePage, status) match {
        case Success(updatedAnswers) =>
          registrationsRepository.set(updatedAnswers).map{ _ =>
            setTaskStatus(updatedAnswers, draftId, status)
            Redirect(navigator.nextPage(AddATrusteePage, draftId, updatedAnswers))
          }
        case Failure(_) =>
          logger.error("[AddATrusteeController][submitComplete] Error while storing user answers")
          Future.successful(InternalServerError(errorPageView()))
      }
  }

  private def setTaskStatus(userAnswers: UserAnswers, draftId: String, selection: AddATrustee)
                           (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val status = (selection, registrationProgress.trusteesStatus(userAnswers)) match {
      case (NoComplete, Some(Completed)) => TaskStatus.Completed
      case _ => TaskStatus.InProgress
    }

    setTaskStatus(draftId, status)
  }

  private def setTaskStatus(draftId: String, taskStatus: TaskStatus)
                           (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    trustsStoreService.updateTaskStatus(draftId, taskStatus)
  }
}
