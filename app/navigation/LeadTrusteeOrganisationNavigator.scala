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

package navigation

import config.FrontendAppConfig
import controllers.register.leadtrustee.organisation.routes._
import models.ReadableUserAnswers
import pages.Page
import pages.register.leadtrustee.organisation._
import play.api.mvc.Call

class LeadTrusteeOrganisationNavigator extends Navigator {

  override def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case UkRegisteredYesNoPage(index) => _ => NameController.onPageLoad(index, draftId)
    case NamePage(index) => ua => nameRoute(ua, index, draftId)
    case UtrPage(index) => _ => AddressUkYesNoController.onPageLoad(index, draftId)
    case UkAddressPage(index) => _ => EmailAddressYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ => EmailAddressYesNoController.onPageLoad(index, draftId)
    case EmailAddressPage(index) => _ => TelephoneNumberController.onPageLoad(index, draftId)
    case TelephoneNumberPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
  }

  override def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUkYesNoPage(index),
        UkAddressController.onPageLoad(index, draftId),
        InternationalAddressController.onPageLoad(index, draftId)
      )
    case EmailAddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        EmailAddressYesNoPage(index),
        EmailAddressController.onPageLoad(index, draftId),
        TelephoneNumberController.onPageLoad(index, draftId)
      )
  }

  private def nameRoute(ua: ReadableUserAnswers, index: Int, draftId: String): Call = {
    ua.get(UkRegisteredYesNoPage(index)) match {
      case Some(true) => UtrController.onPageLoad(index, draftId)
      case Some(false) => AddressUkYesNoController.onPageLoad(index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
