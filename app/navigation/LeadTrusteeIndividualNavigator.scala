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

package navigation

import config.FrontendAppConfig
import controllers.register.leadtrustee.individual.routes._
import models.ReadableUserAnswers
import models.registration.pages.DetailsChoice.{IdCard, Passport}
import pages.Page
import pages.register.leadtrustee.individual._
import play.api.mvc.Call

class LeadTrusteeIndividualNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers)
                       (implicit config: FrontendAppConfig): Call = routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case TrusteesNamePage(index) => _ => DateOfBirthController.onPageLoad(index, draftId)
    case TrusteesDateOfBirthPage(index) => _ => NinoYesNoController.onPageLoad(index, draftId)
    case TrusteesNinoPage(index) => _ => LiveInTheUKYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => _ => LiveInTheUKYesNoController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index) => _ => LiveInTheUKYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ => EmailAddressYesNoController.onPageLoad(index, draftId)
    case UkAddressPage(index) => _ => EmailAddressYesNoController.onPageLoad(index, draftId)
    case EmailAddressPage(index) => _ => TelephoneNumberController.onPageLoad(index, draftId)
    case TelephoneNumberPage(index) => _ => controllers.register.trustees.routes.TrusteesAnswerPageController.onPageLoad(index, draftId)
  }

  private def conditionalNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case TrusteeNinoYesNoPage(index) => ua =>
      yesNoNav(ua, TrusteeNinoYesNoPage(index), NinoController.onPageLoad(index, draftId), TrusteeDetailsChoiceController.onPageLoad(index, draftId))
    case TrusteeAUKCitizenPage(index) => ua =>
      yesNoNav(ua, TrusteeAUKCitizenPage(index), UkAddressController.onPageLoad(index, draftId), InternationalAddressController.onPageLoad(index, draftId))
    case TrusteeDetailsChoicePage(index) => ua =>
      detailsRoutes(ua, index, draftId)
    case EmailAddressYesNoPage(index) => ua =>
      yesNoNav(ua, EmailAddressYesNoPage(index), EmailAddressController.onPageLoad(index, draftId), TelephoneNumberController.onPageLoad(index, draftId))
  }

  private def detailsRoutes(answers: ReadableUserAnswers, index: Int, draftId: String): Call = answers.get(TrusteeDetailsChoicePage(index)) match {
    case Some(IdCard) => IDCardDetailsController.onPageLoad(index, draftId)
    case Some(Passport) => PassportDetailsController.onPageLoad(index, draftId)
    case None => controllers.routes.SessionExpiredController.onPageLoad()
  }

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse
      conditionalNavigation(draftId)
}
