/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.register.trustees.individual.mld5

import config.FrontendAppConfig
import config.annotations.TrusteeIndividual
import controllers.actions._
import forms.YesNoFormProvider
import javax.inject.Inject
import navigation.Navigator
import controllers.actions.register.trustees.individual.NameRequiredActionImpl
import pages.register.trustees.individual.mld5.CountryOfNationalityYesNoPage
import play.api.data.Form
import play.api.i18n._
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.trustees.individual.mld5.CountryOfNationalityYesNoView

import scala.concurrent.{ExecutionContext, Future}

class CountryOfNationalityYesNoController @Inject()(
                                                   val controllerComponents: MessagesControllerComponents,
                                                   implicit val frontendAppConfig: FrontendAppConfig,
                                                   repository: RegistrationsRepository,
                                                   @TrusteeIndividual navigator: Navigator,
                                                   standardActionSets: StandardActionSets,
                                                   nameAction: NameRequiredActionImpl,
                                                   formProvider: YesNoFormProvider,
                                                   view: CountryOfNationalityYesNoView
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = formProvider.withPrefix("trustee.individual.5mld.countryOfNationalityYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
      implicit request =>

        val preparedForm = request.userAnswers.get(CountryOfNationalityYesNoPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, draftId, index, request.trusteeName))
    }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
      implicit request =>

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfNationalityYesNoPage(index), value))
              _              <- repository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, updatedAnswers))
        )
    }
}
