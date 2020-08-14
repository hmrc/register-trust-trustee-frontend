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

import controllers.register.leadtrustee.organisation.routes._
import models.ReadableUserAnswers
import pages.Page
import pages.register.leadtrustee.organisation._
import play.api.mvc.Call

class LeadTrusteeOrganisationNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case UkRegisteredYesNoPage => _ => NameController.onPageLoad(draftId)
    case NamePage => ua => nameRoute(ua, draftId)
    case UtrPage => _ => AddressUkYesNoController.onPageLoad(draftId)
    case UkAddressPage | InternationalAddressPage => _ => EmailAddressYesNoController.onPageLoad(draftId)
    case EmailAddressPage => _ => TelephoneNumberController.onPageLoad(draftId)
    case TelephoneNumberPage => _ => CheckDetailsController.onPageLoad(draftId)
  }

  private def conditionalNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AddressUkYesNoPage => ua =>
      yesNoNav(
        ua,
        AddressUkYesNoPage,
        UkAddressController.onPageLoad(draftId),
        InternationalAddressController.onPageLoad(draftId)
      )
    case EmailAddressYesNoPage => ua =>
      yesNoNav(
        ua,
        EmailAddressYesNoPage,
        EmailAddressController.onPageLoad(draftId),
        TelephoneNumberController.onPageLoad(draftId)
      )
  }

  private def nameRoute(ua: ReadableUserAnswers, draftId: String): Call = {
    ua.get(UkRegisteredYesNoPage) match {
      case Some(true) => UtrController.onPageLoad(draftId)
      case Some(false) => AddressUkYesNoController.onPageLoad(draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse
      conditionalNavigation(draftId)
}
