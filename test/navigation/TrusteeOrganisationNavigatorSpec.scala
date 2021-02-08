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

import base.SpecBase
import controllers.register.trustees.organisation.routes._
import controllers.register.trustees.organisation.mld5.{routes => mld5Rts}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees.organisation.mld5.CountryOfResidencePage
import pages.register.trustees.organisation.mld5.CountryOfResidenceInTheUkYesNoPage
import pages.register.trustees.organisation.mld5.CountryOfResidenceYesNoPage
import pages.register.trustees.organisation._

class TrusteeOrganisationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new TrusteeOrganisationNavigator
  val index = 0

  implicit val config = frontendAppConfig

  "TrusteeOrganisation Navigator" when {

    "A 4mld Trust" must {

      val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

      "Name page -> UTR yes no page" in {
        navigator.nextPage(NamePage(index), fakeDraftId, baseAnswers)
          .mustBe(UtrYesNoController.onPageLoad(index, fakeDraftId))
      }

      "UTR yes no page -> YES -> UTR page" in {
        val answers = baseAnswers
          .set(UtrYesNoPage(index), true).success.value

        navigator.nextPage(UtrYesNoPage(index), fakeDraftId, answers)
          .mustBe(UtrController.onPageLoad(index, fakeDraftId))
      }

      "UTR yes no page -> NO -> Do you know address page" in {
        val answers = baseAnswers
          .set(UtrYesNoPage(index), false).success.value

        navigator.nextPage(UtrYesNoPage(index), fakeDraftId, answers)
          .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
      }

      "UTR page -> Check your answers page" in {
        navigator.nextPage(UtrPage(index), fakeDraftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "Do you know address page -> YES -> Is address in UK page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage(index), true).success.value

        navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
          .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Do you know address page -> NO -> Check your answers page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage(index), false).success.value

        navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "Is address in UK page -> YES -> UK address page" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage(index), true).success.value

        navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
      }

      "Is address in UK page -> NO -> International address page" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage(index), false).success.value

        navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
      }

      "UK address page -> Check your answers page" in {
        navigator.nextPage(UkAddressPage(index), fakeDraftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "International address page -> Check your answers page" in {
        navigator.nextPage(InternationalAddressPage(index), fakeDraftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

    }
    "A 5mld Trust" must {

      val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true)

      "Country Of Residence Yes No page -> Yes -> Country Of Residence UK yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence Yes No page -> No -> Check details page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence UK yes no page -> No -> Country of Residence page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence UK yes no page -> Yes -> Address Yes No page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence page -> Address Yes No page" in {

        navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, baseAnswers)
          .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Address Yes No page -> Yes -> Address Uk Yes no page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage(index), true).success.value

        navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
          .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
      }
      "Address Yes No page -> No -> Check your answers" in {
        val answers = baseAnswers
          .set(AddressYesNoPage(index), false).success.value

        navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }

      "Address Uk Yes No page -> Yes -> Uk address page" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage(index), true).success.value

        navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
      }
      "Address Uk Yes No page -> No -> International address page" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage(index), false).success.value

        navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
      }
      "Uk Address page -> Check your answers" in {

        navigator.nextPage(UkAddressPage(index), fakeDraftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }

      "International address page -> Check your answers" in {
        navigator.nextPage(InternationalAddressPage(index), fakeDraftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }


    }
  }
}
