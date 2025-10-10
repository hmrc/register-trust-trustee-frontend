/*
 * Copyright 2025 HM Revenue & Customs
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
import config.FrontendAppConfig
import controllers.register.trustees.organisation.mld5.{routes => mld5Rts}
import controllers.register.trustees.organisation.routes._
import generators.Generators
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees.organisation._
import pages.register.trustees.organisation.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}

class TrusteeOrganisationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TrusteeOrganisationNavigator
  val index = 0

  implicit val config: FrontendAppConfig = frontendAppConfig

  "TrusteeOrganisation Navigator" when {

    "A taxable Trust" must {

      val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

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

      "UTR yes no page -> NO -> Do you know country of residency" in {
        val answers = baseAnswers
          .set(UtrYesNoPage(index), false).success.value

        navigator.nextPage(UtrYesNoPage(index), fakeDraftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
      }

      "UTR page -> Do you know country of residency" in {
        navigator.nextPage(UtrPage(index), fakeDraftId, baseAnswers)
          .mustBe(mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }


      "Country Of Residence Yes No page -> Yes -> Country Of Residence UK yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence Yes No page -> No (with UTR) -> Check details page" in {
        val answers = baseAnswers
          .set(UtrYesNoPage(index), true).success.value
          .set(CountryOfResidenceYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence Yes No page -> No (without UTR) -> Address Yes No Page" in {
        val answers = baseAnswers
          .set(UtrYesNoPage(index), false).success.value
          .set(CountryOfResidenceYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
          .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence UK yes no page -> No -> Country of Residence page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence UK yes no page -> Yes (With UTR) -> Check details Page" in {
        val answers = baseAnswers
          .set(UtrYesNoPage(index), true).success.value
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence UK yes no page -> Yes (without UTR) -> Address Yes No page" in {
        val answers = baseAnswers
          .set(UtrYesNoPage(index), false).success.value
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfResidencePage -> (with UTR) -> CheckDetailsPage" in {
        forAll(arbitrary[UserAnswers]) {
          baseAnswers =>

            val mld5Answers = baseAnswers.copy(isTaxable = true)
              .set(UtrYesNoPage(index), true).success.value
              .set(CountryOfResidencePage(index), "FR").success.value

            navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, mld5Answers)
              .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidencePage -> (without UTR) -> AddressYesNoPage" in {
        forAll(arbitrary[UserAnswers]) {
          baseAnswers =>

            val mld5Answers = baseAnswers.copy(isTaxable = true)
              .set(UtrYesNoPage(index), false).success.value
              .set(CountryOfResidencePage(index), "FR").success.value

            navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, mld5Answers)
              .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
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

    "A none taxable Trust" must {

      val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

      "Name page -> Country Of Residence page" in {
        navigator.nextPage(NamePage(index), fakeDraftId, baseAnswers)
          .mustBe(mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence Yes No page -> Yes -> Country Of Residence UK yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence Yes No page -> No -> Check Details Page" in {
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

      "Country Of Residence UK yes no page -> Yes -> Check details Page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfResidencePage -> CheckDetailsPage" in {
        val mld5Answers = baseAnswers
          .set(CountryOfResidencePage(index), "FR").success.value

        navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, mld5Answers)
          .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
      }
    }
  }
}
