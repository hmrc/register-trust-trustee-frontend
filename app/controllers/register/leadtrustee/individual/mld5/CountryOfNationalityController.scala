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

package controllers.register.leadtrustee.individual.mld5

import config.FrontendAppConfig
import config.annotations.LeadTrusteeIndividual
import controllers.actions.StandardActionSets
import controllers.actions.register.leadtrustee.individual.NameRequiredActionImpl
import forms.CountryFormProvider
import navigation.Navigator
import pages.register.leadtrustee.individual.mld5.CountryOfNationalityPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.leadtrustee.individual.mld5.CountryOfNationalityView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfNationalityController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              registrationsRepository: RegistrationsRepository,
                                              @LeadTrusteeIndividual navigator: Navigator,
                                              standardActionSets: StandardActionSets,
                                              nameAction: NameRequiredActionImpl,
                                              formProvider: CountryFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: CountryOfNationalityView,
                                              val countryOptions: CountryOptionsNonUK
                                            )(implicit ec: ExecutionContext, appConfig: FrontendAppConfig) extends FrontendBaseController with I18nSupport {

  private val form: Form[String] = formProvider.withPrefix("leadTrustee.individual.5mld.countryOfNationality")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
      implicit request =>

        val preparedForm = request.userAnswers.get(CountryOfNationalityPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, countryOptions.options, draftId, index, request.trusteeName))
    }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
      implicit request =>

        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, countryOptions.options, draftId, index, request.trusteeName))),

          value => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfNationalityPage(index), value))
              _              <- registrationsRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(CountryOfNationalityPage(index), draftId, updatedAnswers))
          }
        )
    }
}
