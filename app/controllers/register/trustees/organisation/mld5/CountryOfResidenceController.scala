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

package controllers.register.trustees.organisation.mld5

import config.FrontendAppConfig
import config.annotations.TrusteeOrganisation
import controllers.actions.StandardActionSets
import controllers.actions.register.trustees.organisation.NameRequiredActionImpl
import forms.CountryFormProvider
import navigation.Navigator
import pages.register.trustees.organisation.mld5.CountryOfResidencePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.trustees.organisation.mld5.CountryOfResidenceView
import javax.inject.Inject
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

class CountryOfResidenceController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              implicit val frontendAppConfig: FrontendAppConfig,
                                              registrationsRepository: RegistrationsRepository,
                                              @TrusteeOrganisation navigator: Navigator,
                                              actions: StandardActionSets,
                                              requireName: NameRequiredActionImpl,
                                              formProvider: CountryFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: CountryOfResidenceView,
                                              val countryOptions: CountryOptionsNonUK
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[String] = formProvider.withPrefix("trustee.organisation.5mld.countryOfResidence")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    actions.identifiedUserWithData(draftId).andThen(requireName(index)) {
      implicit request =>

        val preparedForm = request.userAnswers.get(CountryOfResidencePage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, countryOptions.options, draftId, index, request.trusteeName))
    }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    actions.identifiedUserWithData(draftId).andThen(requireName(index)).async {
      implicit request =>

        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, countryOptions.options, draftId, index, request.trusteeName))),

          value => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfResidencePage(index), value))
              _              <- registrationsRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(CountryOfResidencePage(index), draftId, updatedAnswers))
          }
        )
    }
}
