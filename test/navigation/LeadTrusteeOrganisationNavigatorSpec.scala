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

import base.SpecBase
import controllers.register.leadtrustee.organisation.routes._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.leadtrustee.organisation._

class LeadTrusteeOrganisationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new LeadTrusteeOrganisationNavigator

  "LeadTrusteeOrganisation Navigator" must {

    "UK registered yes no page -> YES -> Name page" in {
      val answers = emptyUserAnswers
        .set(UkRegisteredYesNoPage, true).success.value

      navigator.nextPage(UkRegisteredYesNoPage, fakeDraftId, answers)
        .mustBe(NameController.onPageLoad(fakeDraftId))
    }

    "UK registered yes no page -> NO -> Name page" in {
      val answers = emptyUserAnswers
        .set(UkRegisteredYesNoPage, false).success.value

      navigator.nextPage(UkRegisteredYesNoPage, fakeDraftId, answers)
        .mustBe(NameController.onPageLoad(fakeDraftId))
    }

    "Name page" when {

      "UK registered" must {
        "-> UTR page" in {
          val answers = emptyUserAnswers
            .set(UkRegisteredYesNoPage, true).success.value

          navigator.nextPage(NamePage, fakeDraftId, answers)
            .mustBe(UtrController.onPageLoad(fakeDraftId))
        }
      }

      "Not UK registered" must {
        "-> Address UK yes no page" in {
          val answers = emptyUserAnswers
            .set(UkRegisteredYesNoPage, false).success.value

          navigator.nextPage(NamePage, fakeDraftId, answers)
            .mustBe(AddressUkYesNoController.onPageLoad(fakeDraftId))
        }
      }
    }

    "Is address in UK page -> YES -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, true).success.value

      navigator.nextPage(AddressUkYesNoPage, fakeDraftId, answers)
        .mustBe(UkAddressController.onPageLoad(fakeDraftId))
    }

    "Is address in UK page -> NO -> International address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage, false).success.value

      navigator.nextPage(AddressUkYesNoPage, fakeDraftId, answers)
        .mustBe(InternationalAddressController.onPageLoad(fakeDraftId))
    }

    "UK address page -> Email address yes no page" in {
      navigator.nextPage(UkAddressPage, fakeDraftId, emptyUserAnswers)
        .mustBe(EmailAddressYesNoController.onPageLoad(fakeDraftId))
    }

    "International address page -> Email address yes no page" in {
      navigator.nextPage(InternationalAddressPage, fakeDraftId, emptyUserAnswers)
        .mustBe(EmailAddressYesNoController.onPageLoad(fakeDraftId))
    }

    "Email address yes no page -> YES -> Email address page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressYesNoPage, true).success.value

      navigator.nextPage(EmailAddressYesNoPage, fakeDraftId, answers)
        .mustBe(EmailAddressController.onPageLoad(fakeDraftId))
    }

    "Email address yes no page -> NO -> Telephone number page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressYesNoPage, false).success.value

      navigator.nextPage(EmailAddressYesNoPage, fakeDraftId, answers)
        .mustBe(TelephoneNumberController.onPageLoad(fakeDraftId))
    }

    "Email address page -> Telephone number page" in {
      navigator.nextPage(EmailAddressPage, fakeDraftId, emptyUserAnswers)
        .mustBe(TelephoneNumberController.onPageLoad(fakeDraftId))
    }

    "Telephone number page -> Check your answers page" in {
      navigator.nextPage(TelephoneNumberPage, fakeDraftId, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad(fakeDraftId))
    }
  }

}
