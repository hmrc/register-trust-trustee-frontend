/*
 * Copyright 2022 HM Revenue & Customs
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
import controllers.register.trustees.individual.mld5.routes._
import controllers.register.trustees.individual.routes._
import generators.Generators
import models.YesNoDontKnow
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees.individual._
import pages.register.trustees.individual.mld5._

class TrusteeIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TrusteeIndividualNavigator
  val index = 0

  implicit val config: FrontendAppConfig = frontendAppConfig

  "Trustee Individual Navigator" when {

    "none taxable with at least one trustee" must {
      val noneTaxableAnswers = emptyUserAnswers.copy(isTaxable = false)

      "NamePage" when {
        "taxable=N -> DateOfBirthYesNoYesNoPage" in {
          navigator.nextPage(NamePage(index), fakeDraftId, noneTaxableAnswers)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirthYesNo" when {
        "taxable=N -> CountryOfNationalityYesNoPage when user answers No" in {
          val answers = noneTaxableAnswers.set(DateOfBirthYesNoPage(index), value = false).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirth" when {
        "taxable=N -> CountryOfNationalityYesNoPage" in {
          navigator.nextPage(DateOfBirthPage(index), fakeDraftId, noneTaxableAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityYesNoPage" must {
        "taxable=N -> CountryOfNationalityInTheUkYesNoPage when user answers yes" in {
          val answers = noneTaxableAnswers.set(CountryOfNationalityYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> TrusteesNinoYesNoPage when user answers no" in {
          val answers = noneTaxableAnswers.set(CountryOfNationalityYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityInTheUkYesNoPage" when {
        "taxable=N -> CountryOfNationalityYesNoPage when user answers no" in {
          val answers = noneTaxableAnswers.set(CountryOfNationalityInTheUkYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> TrusteesNinoYesNoPage when user answers yes" in {
          val answers = noneTaxableAnswers.set(CountryOfNationalityInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityInTheUkYesNoPage" when {
        "taxable=N -> TrusteesNinoYesNo page" in {
          navigator.nextPage(CountryOfNationalityPage(index), fakeDraftId, noneTaxableAnswers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidenceYesNoPage" when {
        "taxable=N -> CountryOfResidenceInTheUkYesNoPage when user answers yes" in {
          val answers = noneTaxableAnswers.set(CountryOfResidenceYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> MentalCapacityPage when user answers no" in {
          val answers = noneTaxableAnswers.set(CountryOfResidenceYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidenceInTheUkYesNo" when {
        "taxable=N -> CountryOfResidencePage when user answers no" in {
          val answers = noneTaxableAnswers.set(CountryOfResidenceInTheUkYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> MentalCapacityPage when user answers yes" in {
          val answers = noneTaxableAnswers.set(CountryOfResidenceInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidence" when {
        "taxable=N -> MentalCapacityPage" in {
          navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, noneTaxableAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsPage" when {
        "taxable=N -> MentalCapacityPage" in {
          navigator.nextPage(PassportDetailsPage(index), fakeDraftId, noneTaxableAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "TrusteeLivesInUKPage" when {
        "taxable=N -> TrusteesUkAddressPage when answer is yes" in {
          val answers = noneTaxableAnswers.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressYesNoPage" when {

        "taxable=N -> MentalCapacityPage when user answers No" in {
          val answers = noneTaxableAnswers.set(AddressYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> AddressUkYesNo when user answers Yes" in {
          val answers = noneTaxableAnswers.set(AddressYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressUkYesNo" when {
        "taxable=N -> InternationalAddressPage when answer is no" in {
          val answers = noneTaxableAnswers.set(AddressUkYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> UkAddressPage when answer is yes" in {
          val answers = noneTaxableAnswers.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "UkAddressPage" when {
        "taxable=N -> PassportDetailsYesNoPage when answer is yes" in {
          navigator.nextPage(UkAddressPage(index), fakeDraftId, noneTaxableAnswers)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "InternationalAddressPage" when {
        "taxable=N -> PassportDetailsYesNoPage  when answer is yes" in {
          navigator.nextPage(InternationalAddressPage(index), fakeDraftId, noneTaxableAnswers)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsYesNoPage" when {
        "taxable=N -> PassportDetailsPage when answer is yes" in {
          val answers = noneTaxableAnswers.set(PassportDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(PassportDetailsController.onPageLoad(index, fakeDraftId))
        }
        "taxable=N -> IdCardDetailsYesNoPage when answer is no" in {
          val answers = noneTaxableAnswers.set(PassportDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IDCardDetailsYesNoPage" when {
        "taxable=N -> IDCardDetailsPage when answer is yes" in {
          val answers = noneTaxableAnswers.set(IDCardDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> MentalCapacityPage when user answers No" in {
          val answers = noneTaxableAnswers.set(IDCardDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IdCardDetailsPage" when {
        "taxable=N -> MentalCapacityPage" in {
          navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, noneTaxableAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "LegallyIncapablePage"  when {

        "taxable=N -> TrusteeAnswersPage when answer is yes" in {
          val answers = noneTaxableAnswers.set(MentalCapacityYesNoPage(index), value = YesNoDontKnow.Yes).success.value

          navigator.nextPage(MentalCapacityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> TrusteeAnswersPage when answer is no" in {
          val answers = noneTaxableAnswers.set(MentalCapacityYesNoPage(index), value = YesNoDontKnow.No).success.value

          navigator.nextPage(MentalCapacityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }

        "taxable=N -> TrusteeAnswersPage when answer is don't know" in {
          val answers = noneTaxableAnswers.set(MentalCapacityYesNoPage(index), value = YesNoDontKnow.DontKnow).success.value

          navigator.nextPage(MentalCapacityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "taxable with at least one trustee" must {
      val taxableAnswers5mld = emptyUserAnswers.copy(isTaxable = true)
      val taxableAnswers5mldWithNoNino = emptyUserAnswers.copy(isTaxable = true).set(NinoYesNoPage(index), value = false).success.value

      "NamePage" when {
        "taxable=Y -> DateOfBirthYesNoYesNoPage" in {
          navigator.nextPage(NamePage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirthYesNo" when {
        "taxable=Y -> CountryOfNationalityYesNoPage when user answers No" in {
          val answers = taxableAnswers5mld.set(DateOfBirthYesNoPage(index), value = false).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirth" when {
        "taxable=Y -> CountryOfNationalityYesNoPage" in {
          navigator.nextPage(DateOfBirthPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityYesNoPage" must {
        "taxable=Y -> CountryOfNationalityInTheUkYesNoPage when user answers yes" in {
          val answers = taxableAnswers5mld.set(CountryOfNationalityYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y -> TrusteesNinoYesNoPage when user answers no" in {
          val answers = taxableAnswers5mld.set(CountryOfNationalityYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityInTheUkYesNoPage" when {
        "taxable=Y -> CountryOfNationalityYesNoPage when user answers no" in {
          val answers = taxableAnswers5mld.set(CountryOfNationalityInTheUkYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y -> TrusteesNinoYesNoPage when user answers yes" in {
          val answers = taxableAnswers5mld.set(CountryOfNationalityInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityInTheUkYesNoPage" when {
        "taxable=Y -> TrusteesNinoYesNo page" in {
          navigator.nextPage(CountryOfNationalityPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "NinoYesNoPage" when {
        "taxable=Y -> TrusteesNinoPage when user answers Yes" in {
          val answers = taxableAnswers5mld.set(NinoYesNoPage(index), value = true).success.value

          navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y -> CountryOfResidenceYesNoPage when user answers no" in {
          val answers = taxableAnswers5mld.set(NinoYesNoPage(index), value = false).success.value

          navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
        }


      }

      "TrusteesNinoPage" when {
        "taxable=Y -> CountryOfResidenceYesNoPage" in {
          navigator.nextPage(NinoPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
        }
      }


      "CountryOfResidenceYesNoPage" when {
        "taxable=Y -> MentalCapacityPage when the answer is no" in {
          val answers = taxableAnswers5mld
            .set(CountryOfResidenceYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y with No Nino-> AddressYesNo when user answers No" in {
          val answers = taxableAnswers5mldWithNoNino
            .set(CountryOfResidenceYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidenceInTheUkYesNo" when {
        "taxable=Y -> MentalCapacityPage when the answer is yes" in {
          val answers = taxableAnswers5mld
            .set(CountryOfResidenceInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y with no Nino -> AddressYesNo when user answers Yes" in {
          val answers = taxableAnswers5mldWithNoNino
            .set(CountryOfResidenceInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidence" when {
        "taxable=Y -> MentalCapacityPage" in {
          navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y with no Nino-> AddressYesNo" in {
          navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, taxableAnswers5mldWithNoNino)
            .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsPage" when {
        "taxable=Y -> MentalCapacityPage" in {
          navigator.nextPage(PassportDetailsPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "TrusteeLivesInUKPage" when {
        "taxable=Y -> TrusteesUkAddressPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressYesNoPage" when {

        "taxable=Y -> MentalCapacityPage when user answers No" in {
          val answers = taxableAnswers5mld.set(AddressYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y -> AddressUkYesNo when user answers Yes" in {
          val answers = taxableAnswers5mld.set(AddressYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressUkYesNo" when {
        "taxable=Y -> InternationalAddressPage when answer is no" in {
          val answers = taxableAnswers5mld.set(AddressUkYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y -> UkAddressPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "UkAddressPage" when {
        "taxable=Y -> PassportDetailsYesNoPage when answer is yes" in {
          navigator.nextPage(UkAddressPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "InternationalAddressPage" when {
        "taxable=Y -> PassportDetailsYesNoPage  when answer is yes" in {
          navigator.nextPage(InternationalAddressPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsYesNoPage" when {
        "taxable=Y -> PassportDetailsPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(PassportDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(PassportDetailsController.onPageLoad(index, fakeDraftId))
        }
        "taxable=Y -> IdCardDetailsYesNoPage when answer is no" in {
          val answers = taxableAnswers5mld.set(PassportDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IDCardDetailsYesNoPage" when {
        "taxable=Y -> IDCardDetailsPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(IDCardDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y -> MentalCapacityPage when user answers No" in {
          val answers = taxableAnswers5mld.set(IDCardDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IdCardDetailsPage" when {
        "taxable=Y -> MentalCapacityPage" in {
          navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "LegallyIncapablePage"  when {

        "taxable=Y -> TrusteeAnswersPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(MentalCapacityYesNoPage(index), value = YesNoDontKnow.Yes).success.value

          navigator.nextPage(MentalCapacityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }

        "taxable=Y -> TrusteeAnswersPage when answer is no" in {
          val answers = taxableAnswers5mld.set(MentalCapacityYesNoPage(index), value = YesNoDontKnow.No).success.value

          navigator.nextPage(MentalCapacityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }
    }
  }
}
