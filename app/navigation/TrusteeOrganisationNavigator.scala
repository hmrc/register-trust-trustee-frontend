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
import controllers.register.trustees.organisation.routes._
import controllers.register.trustees.organisation.mld5.{routes => mld5}
import models.ReadableUserAnswers
import pages.Page
import pages.register.trustees.organisation._
import pages.register.trustees.organisation.mld5._
import play.api.mvc.Call

class TrusteeOrganisationNavigator extends Navigator {

  override def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => _ => UtrYesNoController.onPageLoad(index, draftId)
    case UtrPage(index) => ua => navigateAwayFromUTRQuestions(draftId, index, ua)
    case UkAddressPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index) => ua => addressOrCheckAnswersRoute(draftId, index, ua)
  }

  override def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ UtrYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = UtrController.onPageLoad(index, draftId),
        noCall = navigateAwayFromUTRQuestions(draftId, index, ua)
      )
    case page @ AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = AddressUkYesNoController.onPageLoad(index, draftId),
        noCall = CheckDetailsController.onPageLoad(index, draftId)
      )
    case page @ AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = UkAddressController.onPageLoad(index, draftId),
        noCall = InternationalAddressController.onPageLoad(index, draftId)
      )
    case page @ CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = mld5.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = addressOrCheckAnswersRoute(draftId, index, ua)
      )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = addressOrCheckAnswersRoute(draftId, index, ua),
        noCall = mld5.CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def navigateAwayFromUTRQuestions(draftId: String, index: Int, userAnswers: ReadableUserAnswers): Call = {
    if (userAnswers.is5mldEnabled) {
      mld5.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      addressOrCheckAnswersRoute(draftId, index, userAnswers)
    }
  }

  private def addressOrCheckAnswersRoute(draftId: String, index: Int, userAnswers: ReadableUserAnswers): Call = {
    yesNoNav(
      ua = userAnswers,
      fromPage = UtrYesNoPage(index),
      yesCall = CheckDetailsController.onPageLoad(index, draftId),
      noCall = AddressYesNoController.onPageLoad(index, draftId)
    )
  }
}
