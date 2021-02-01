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
import config.FrontendAppConfig
import controllers.register.trustees.individual.routes._
import controllers.register.trustees.individual.mld5.routes._
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees.individual._
import pages.register.trustees.individual.mld5._

class TrusteeIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TrusteeIndividualNavigator
  val index = 0

  implicit val config: FrontendAppConfig = frontendAppConfig

  "Trustee Individual Navigator" when {
    "4MLD with at least one trustee" must {
      val noneTaxableAnswers4mld = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = false)
      val taxableAnswers4mld = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true)

      "NamePage" when {
        "5MLD=N, taxable=N -> DateOfBirthYesNoYesNoPage" in {
          navigator.nextPage(NamePage(index), fakeDraftId, noneTaxableAnswers4mld)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
        }
        "5MLD=N, taxable=Y -> DateOfBirthYesNoYesNoPage" in {
          navigator.nextPage(NamePage(index), fakeDraftId, taxableAnswers4mld)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirthYesNo" when {
        "5MLD=N, taxable=N -> TrusteesDateOfBirthPage from DateOfBirthYesNoPage page when user answers Yes" in {
          val answers = noneTaxableAnswers4mld.set(DateOfBirthYesNoPage(index), value = true).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
            .mustBe(DateOfBirthController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=N, taxable=Y -> TrusteesNinoYesNoPage when user answers No" in {
          val answers = noneTaxableAnswers4mld.set(DateOfBirthYesNoPage(index), value = false).success.value

          navigator.nextPage(DateOfBirthPage(index), fakeDraftId, answers)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirth" when {
        "5MLD=N, taxable=N -> TrusteesNinoYesNoPage" in {
          navigator.nextPage(DateOfBirthPage(index), fakeDraftId, noneTaxableAnswers4mld)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
        "5MLD=N, taxable=Y -> TrusteesDateOfBirthPage from DateOfBirthYesNoPage page when user answers Yes" in {
          val answers = taxableAnswers4mld.set(DateOfBirthYesNoPage(index), value = true).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
            .mustBe(DateOfBirthController.onPageLoad(index, fakeDraftId))
        }
      }
      "NinoYesNoPage" when {
        "5MLD=N, taxable=N -> TrusteesNinoPage when user answers Yes" in {
          val answers = noneTaxableAnswers4mld.set(NinoYesNoPage(index), value = true).success.value

          navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=N, taxable=N -> AddressYesNo when user answers No" in {
          val answers = noneTaxableAnswers4mld.set(NinoYesNoPage(index), value = false).success.value

          navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "TrusteesNinoPage" when {
        "5MLD=N, taxable=N -> TrusteeAnswersPage" in {
          navigator.nextPage(NinoPage(index), fakeDraftId, noneTaxableAnswers4mld)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }


      "PassportDetailsPage" when {
        "5MLD=N, taxable=N -> TrusteeAnswersPage" in {
          navigator.nextPage(PassportDetailsPage(index), fakeDraftId, noneTaxableAnswers4mld)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }

      "TrusteeLivesInUKPage" when {
        "5MLD=N, taxable=N -> TrusteesUkAddressPage when answer is yes" in {
          val answers = noneTaxableAnswers4mld.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressYesNoPage" when {
        "5MLD=N, taxable=N -> TrusteeAnswersPage when user answers No" in {
          val answers = noneTaxableAnswers4mld.set(AddressYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=N, taxable=N -> AddressUkYesNo when user answers Yes" in {
          val answers = noneTaxableAnswers4mld.set(AddressYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressUkYesNo" when {
        "5MLD=N, taxable=N -> InternationalAddressPage when answer is no" in {
          val answers = noneTaxableAnswers4mld.set(AddressUkYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=N, taxable=N -> UkAddressPage when answer is yes" in {
          val answers = noneTaxableAnswers4mld.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "UkAddressPage" when {
        "5MLD=N, taxable=N -> PassportDetailsYesNoPage when answer is yes" in {
          navigator.nextPage(UkAddressPage(index), fakeDraftId, noneTaxableAnswers4mld)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "InternationalAddressPage" when {
        "5MLD=N, taxable=N -> PassportDetailsYesNoPage  when answer is yes" in {
          navigator.nextPage(InternationalAddressPage(index), fakeDraftId, noneTaxableAnswers4mld)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsYesNoPage" when {
        "5MLD=N, taxable=N -> PassportDetailsPage when answer is yes" in {
          val answers = noneTaxableAnswers4mld.set(PassportDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(PassportDetailsController.onPageLoad(index, fakeDraftId))
        }
        "5MLD=N, taxable=N -> IdCardDetailsYesNoPage when answer is no" in {
          val answers = noneTaxableAnswers4mld.set(PassportDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IDCardDetailsYesNoPage" when {
        "5MLD=N, taxable=N -> IDCardDetailsPage when answer is yes" in {
          val answers = noneTaxableAnswers4mld.set(IDCardDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=N, taxable=N -> TrusteeAnswersPage when user answers No" in {
          val answers = noneTaxableAnswers4mld.set(IDCardDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }

      "IdCardDetailsPage" when {
        "5MLD=N, taxable=N -> TrusteeAnswersPage" in {
          navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, noneTaxableAnswers4mld)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "5MLD none taxable with at least one trustee" must {
      val noneTaxableAnswers5mld = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)

      "NamePage" when {
        "5MLD=Y, taxable=N -> DateOfBirthYesNoYesNoPage" in {
          navigator.nextPage(NamePage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirthYesNo" when {
        "5MLD=Y, taxable=N -> CountryOfNationalityYesNoPage when user answers No" in {
          val answers = noneTaxableAnswers5mld.set(DateOfBirthYesNoPage(index), value = false).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirth" when {
        "5MLD=Y, taxable=N -> CountryOfNationalityYesNoPage" in {
          navigator.nextPage(DateOfBirthPage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityYesNoPage" must {
        "5MLD=Y, taxable=N -> CountryOfNationalityInTheUkYesNoPage when user answers yes" in {
          val answers = noneTaxableAnswers5mld.set(CountryOfNationalityYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> TrusteesNinoYesNoPage when user answers no" in {
          val answers = noneTaxableAnswers5mld.set(CountryOfNationalityYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityInTheUkYesNoPage" when {
        "5MLD=Y, taxable=N -> CountryOfNationalityYesNoPage when user answers no" in {
          val answers = noneTaxableAnswers5mld.set(CountryOfNationalityInTheUkYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> TrusteesNinoYesNoPage when user answers yes" in {
          val answers = noneTaxableAnswers5mld.set(CountryOfNationalityInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityInTheUkYesNoPage" when {
        "5MLD=Y, taxable=N -> TrusteesNinoYesNo page" in {
          navigator.nextPage(CountryOfNationalityPage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "NinoYesNoPage" when {
        "5MLD=Y, taxable=N -> TrusteesNinoPage when user answers Yes" in {
          val answers = noneTaxableAnswers5mld.set(NinoYesNoPage(index), value = true).success.value

          navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> CountryOfResidenceYesNoPage when user answers no" in {
          val answers = noneTaxableAnswers5mld.set(NinoYesNoPage(index), value = false).success.value

          navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
        }


      }

      "TrusteesNinoPage" when {
        "5MLD=Y, taxable=N -> LegallyIncapablePage" in {
          navigator.nextPage(NinoPage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }


      "CountryOfResidenceYesNoPage" when {
        "5MLD=Y, taxable=N -> CountryOfResidenceInTheUkYesNoPage when user answers yes" in {
          val answers = noneTaxableAnswers5mld.set(CountryOfResidenceYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> LegalyIncapablePage when user answers no" in {
          val answers = noneTaxableAnswers5mld.set(CountryOfResidenceYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidenceInTheUkYesNo" when {
        "5MLD=Y, taxable=N -> CountryOfResidencePage when user answers no" in {
          val answers = noneTaxableAnswers5mld.set(CountryOfResidenceInTheUkYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> LegalyIncapablePage when user answers yes" in {
          val answers = noneTaxableAnswers5mld.set(CountryOfResidenceInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidence" when {
        "5MLD=Y, taxable=N -> LegalyIncapablePage" in {
          navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsPage" when {
        "5MLD=Y, taxable=N -> LegalyIncapablePage" in {
          navigator.nextPage(PassportDetailsPage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "TrusteeLivesInUKPage" when {
        "5MLD=Y, taxable=N -> TrusteesUkAddressPage when answer is yes" in {
          val answers = noneTaxableAnswers5mld.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressYesNoPage" when {

        "5MLD=Y, taxable=N -> LegalyIncapablePage when user answers No" in {
          val answers = noneTaxableAnswers5mld.set(AddressYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> AddressUkYesNo when user answers Yes" in {
          val answers = noneTaxableAnswers5mld.set(AddressYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressUkYesNo" when {
        "5MLD=Y, taxable=N -> InternationalAddressPage when answer is no" in {
          val answers = noneTaxableAnswers5mld.set(AddressUkYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> UkAddressPage when answer is yes" in {
          val answers = noneTaxableAnswers5mld.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "UkAddressPage" when {
        "5MLD=Y, taxable=N -> PassportDetailsYesNoPage when answer is yes" in {
          navigator.nextPage(UkAddressPage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "InternationalAddressPage" when {
        "5MLD=Y, taxable=N -> PassportDetailsYesNoPage  when answer is yes" in {
          navigator.nextPage(InternationalAddressPage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsYesNoPage" when {
        "5MLD=Y, taxable=N -> PassportDetailsPage when answer is yes" in {
          val answers = noneTaxableAnswers5mld.set(PassportDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(PassportDetailsController.onPageLoad(index, fakeDraftId))
        }
        "5MLD=Y, taxable=N -> IdCardDetailsYesNoPage when answer is no" in {
          val answers = noneTaxableAnswers5mld.set(PassportDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IDCardDetailsYesNoPage" when {
        "5MLD=Y, taxable=N -> IDCardDetailsPage when answer is yes" in {
          val answers = noneTaxableAnswers5mld.set(IDCardDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> LegalyIncapablePage when user answers No" in {
          val answers = noneTaxableAnswers5mld.set(IDCardDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IdCardDetailsPage" when {
        "5MLD=Y, taxable=N -> LegalyIncapablePage" in {
          navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, noneTaxableAnswers5mld)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "LegallyIncapablePage"  when {

        "5MLD=Y, taxable=N -> TrusteeAnswersPage when answer is yes" in {
          val answers = noneTaxableAnswers5mld.set(LegallyIncapableYesNoPage(index), value = true).success.value

          navigator.nextPage(LegallyIncapableYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=N -> TrusteeAnswersPage when answer is no" in {
          val answers = noneTaxableAnswers5mld.set(LegallyIncapableYesNoPage(index), value = false).success.value

          navigator.nextPage(LegallyIncapableYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "5MLD taxable with at least one trustee" must {
      val taxableAnswers5mld = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true)
      val taxableAnswers5mldWithNoNino = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = true).set(NinoYesNoPage(index), value = false).success.value

      "NamePage" when {
        "5MLD=Y, taxable=Y -> DateOfBirthYesNoYesNoPage" in {
          navigator.nextPage(NamePage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirthYesNo" when {
        "5MLD=Y, taxable=Y -> CountryOfNationalityYesNoPage when user answers No" in {
          val answers = taxableAnswers5mld.set(DateOfBirthYesNoPage(index), value = false).success.value

          navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "DateOfBirth" when {
        "5MLD=Y, taxable=Y -> CountryOfNationalityYesNoPage" in {
          navigator.nextPage(DateOfBirthPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityYesNoPage" must {
        "5MLD=Y, taxable=Y -> CountryOfNationalityInTheUkYesNoPage when user answers yes" in {
          val answers = taxableAnswers5mld.set(CountryOfNationalityYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y -> TrusteesNinoYesNoPage when user answers no" in {
          val answers = taxableAnswers5mld.set(CountryOfNationalityYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityInTheUkYesNoPage" when {
        "5MLD=Y, taxable=Y -> CountryOfNationalityYesNoPage when user answers no" in {
          val answers = taxableAnswers5mld.set(CountryOfNationalityInTheUkYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfNationalityController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y -> TrusteesNinoYesNoPage when user answers yes" in {
          val answers = taxableAnswers5mld.set(CountryOfNationalityInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfNationalityInTheUkYesNoPage" when {
        "5MLD=Y, taxable=Y -> TrusteesNinoYesNo page" in {
          navigator.nextPage(CountryOfNationalityPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "NinoYesNoPage" when {
        "5MLD=Y, taxable=Y -> TrusteesNinoPage when user answers Yes" in {
          val answers = taxableAnswers5mld.set(NinoYesNoPage(index), value = true).success.value

          navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
            .mustBe(NinoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y -> CountryOfResidenceYesNoPage when user answers no" in {
          val answers = taxableAnswers5mld.set(NinoYesNoPage(index), value = false).success.value

          navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
        }


      }

      "TrusteesNinoPage" when {
        "5MLD=Y, taxable=Y -> CountryOfResidenceYesNoPage" in {
          navigator.nextPage(NinoPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
        }
      }


      "CountryOfResidenceYesNoPage" when {
        "5MLD=Y, taxable=Y -> LegalyIncapablePage when the answer is no" in {
          val answers = taxableAnswers5mld
            .set(CountryOfResidenceYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y with No Nino-> AddressYesNo when user answers No" in {
          val answers = taxableAnswers5mldWithNoNino
            .set(CountryOfResidenceYesNoPage(index), value = false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidenceInTheUkYesNo" when {
        "5MLD=Y, taxable=Y -> LegalyIncapablePage when the answer is yes" in {
          val answers = taxableAnswers5mld
            .set(CountryOfResidenceInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y with no Nino -> AddressYesNo when user answers Yes" in {
          val answers = taxableAnswers5mldWithNoNino
            .set(CountryOfResidenceInTheUkYesNoPage(index), value = true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "CountryOfResidence" when {
        "5MLD=Y, taxable=Y -> LegalyIncapablePage" in {
          navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y with no Nino-> AddressYesNo" in {
          navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, taxableAnswers5mldWithNoNino)
            .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsPage" when {
        "5MLD=Y, taxable=Y -> LegalyIncapablePage" in {
          navigator.nextPage(PassportDetailsPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "TrusteeLivesInUKPage" when {
        "5MLD=Y, taxable=Y -> TrusteesUkAddressPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressYesNoPage" when {

        "5MLD=Y, taxable=Y -> LegalyIncapablePage when user answers No" in {
          val answers = taxableAnswers5mld.set(AddressYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y -> AddressUkYesNo when user answers Yes" in {
          val answers = taxableAnswers5mld.set(AddressYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
            .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "AddressUkYesNo" when {
        "5MLD=Y, taxable=Y -> InternationalAddressPage when answer is no" in {
          val answers = taxableAnswers5mld.set(AddressUkYesNoPage(index), value = false).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y -> UkAddressPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(AddressUkYesNoPage(index), value = true).success.value

          navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
            .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "UkAddressPage" when {
        "5MLD=Y, taxable=Y -> PassportDetailsYesNoPage when answer is yes" in {
          navigator.nextPage(UkAddressPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "InternationalAddressPage" when {
        "5MLD=Y, taxable=Y -> PassportDetailsYesNoPage  when answer is yes" in {
          navigator.nextPage(InternationalAddressPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "PassportDetailsYesNoPage" when {
        "5MLD=Y, taxable=Y -> PassportDetailsPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(PassportDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(PassportDetailsController.onPageLoad(index, fakeDraftId))
        }
        "5MLD=Y, taxable=Y -> IdCardDetailsYesNoPage when answer is no" in {
          val answers = taxableAnswers5mld.set(PassportDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IDCardDetailsYesNoPage" when {
        "5MLD=Y, taxable=Y -> IDCardDetailsPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(IDCardDetailsYesNoPage(index), value = true).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(IDCardDetailsController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y -> LegalyIncapablePage when user answers No" in {
          val answers = taxableAnswers5mld.set(IDCardDetailsYesNoPage(index), value = false).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "IdCardDetailsPage" when {
        "5MLD=Y, taxable=Y -> LegalyIncapablePage" in {
          navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, taxableAnswers5mld)
            .mustBe(LegallyIncapableYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "LegallyIncapablePage"  when {

        "5MLD=Y, taxable=Y -> TrusteeAnswersPage when answer is yes" in {
          val answers = taxableAnswers5mld.set(LegallyIncapableYesNoPage(index), value = true).success.value

          navigator.nextPage(LegallyIncapableYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }

        "5MLD=Y, taxable=Y -> TrusteeAnswersPage when answer is no" in {
          val answers = taxableAnswers5mld.set(LegallyIncapableYesNoPage(index), value = false).success.value

          navigator.nextPage(LegallyIncapableYesNoPage(index), fakeDraftId, answers)
            .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
        }
      }
    }
  }
}
