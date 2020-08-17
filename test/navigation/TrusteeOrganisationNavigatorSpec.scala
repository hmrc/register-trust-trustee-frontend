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

import controllers.register.trustees.organisation.routes._
import base.SpecBase
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees.organisation._

class TrusteeOrganisationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new TrusteeOrganisationNavigator
  val index = 0

  "TrusteeOrganisation Navigator" must {

    "Name page -> UTR yes no page" in {
      navigator.nextPage(NamePage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(UtrYesNoController.onPageLoad(index, fakeDraftId))
    }

    "UTR yes no page -> YES -> UTR page" in {
      val answers = emptyUserAnswers
        .set(UtrYesNoPage(index), true).success.value

      navigator.nextPage(UtrYesNoPage(index), fakeDraftId, answers)
        .mustBe(UtrController.onPageLoad(index, fakeDraftId))
    }

    "UTR yes no page -> NO -> Do you know address page" in {
      val answers = emptyUserAnswers
        .set(UtrYesNoPage(index), false).success.value

      navigator.nextPage(UtrYesNoPage(index), fakeDraftId, answers)
        .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
    }

    "UTR page -> Check your answers page" in {
      navigator.nextPage(UtrPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad(index, draftId))
    }

    "Do you know address page -> YES -> Is address in UK page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage(index), true).success.value

      navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
        .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
    }

    "Do you know address page -> NO -> Check your answers page" in {
      val answers = emptyUserAnswers
        .set(AddressYesNoPage(index), false).success.value

      navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
        .mustBe(CheckDetailsController.onPageLoad(index, draftId))
    }

    "Is address in UK page -> YES -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage(index), true).success.value

      navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
    }

    "Is address in UK page -> NO -> International address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage(index), false).success.value

      navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
    }

    "UK address page -> Check your answers page" in {
      navigator.nextPage(UkAddressPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad(index, draftId))
    }

    "International address page -> Check your answers page" in {
      navigator.nextPage(InternationalAddressPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad(index, draftId))
    }
  }

}
