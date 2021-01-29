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
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees.individual._
import pages.register.trustees.individual.mld5._

class TrusteeIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TrusteeIndividualNavigator
  val index = 0

  implicit val config: FrontendAppConfig = frontendAppConfig

  "Trustee Individual Navigator" when {

    "there is at least one trustee" must {

      "go to DateOfBirthYesNoPage from NamePage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(NamePage(index), fakeDraftId, userAnswers)
              .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteesDateOfBirthPage from DateOfBirthYesNoPage page when user answers yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(DateOfBirthYesNoPage(index), value = true).success.value

            navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
              .mustBe(DateOfBirthController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteeAnswersPage" when {

        "from TrusteesNinoPage if this is not 5MLD or Taxable" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(NinoPage(index), fakeDraftId, userAnswers.copy(is5mldEnabled = false, isTaxable = false))
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }


        "from TrusteesNinoPage if this is not 5MLD and isTaxable" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(NinoPage(index), fakeDraftId, userAnswers.copy(is5mldEnabled = false, isTaxable = true))
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from TrusteesNinoPage if this is 5MLD but not taxable" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(NinoPage(index), fakeDraftId, userAnswers.copy(is5mldEnabled = true, isTaxable = false))
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfResidenceYesNo when the answer is no if this is 5MLD and not taxable" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = false)
                .set(CountryOfResidenceYesNoPage(index), value = false).success.value

              navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, answers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfResidenceYesNo when the answer is no if this is 5MLD and taxable but a nino has been entered" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = true)
                .set(NinoYesNoPage(index), value = true).success.value
                .set(CountryOfResidenceYesNoPage(index), value = false).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))

          }
        }

        "from CountryOfResidenceInTheUkYesNo when the answer is yes if this is 5MLD and not taxable" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = false)
                .set(CountryOfResidenceInTheUkYesNoPage(index), value = false).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfResidenceInTheUkYesNo when the answer is yes if this is 5MLD and taxable but a nino has been entered" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = true)
                .set(NinoYesNoPage(index), value = true).success.value
                .set(CountryOfResidenceInTheUkYesNoPage(index), value = true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfResidence if this is 5MLD and not taxable" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, userAnswers.copy(is5mldEnabled = true, isTaxable = false))
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfResidence if this is 5MLD and is taxable but a nino has been entered" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = true).set(NinoYesNoPage(index), value = true).success.value
              navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, answers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from PassportDetailsPage" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(PassportDetailsPage(index), fakeDraftId, userAnswers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from IdCardDetailsPage" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, userAnswers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from AddressYesNoPage when user answers No" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(AddressYesNoPage(index), value = false).success.value

              navigator.nextPage(AddressYesNoPage(index), fakeDraftId, answers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }

        "from IDCardDetailsYesNoPage when user answers No" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IDCardDetailsYesNoPage(index), value = false).success.value

              navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
                .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "go to CountryOfNationalityYesNoPage" when {
        "from DateOfBirth page if this is 5MLD" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(DateOfBirthPage(index), fakeDraftId, userAnswers.copy(is5mldEnabled = true))
                .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from DateOfBirthYesNoPage page when user answers no if this is 5MLD" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.copy(is5mldEnabled = true).set(DateOfBirthYesNoPage(index), value = false).success.value

              navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
                .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "go to CountryOfNationalityInTheUkYesNoPage" when {
        "from CountryOfNationalityYesNoPage page when user answers yes" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(CountryOfNationalityYesNoPage(index), value = true).success.value

              navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
                .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "go to CountryOfNationalityPage" when {
        "from CountryOfNationalityInTheUkYesNoPage page when user answers no" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(CountryOfNationalityInTheUkYesNoPage(index), value = false).success.value

              navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
                .mustBe(CountryOfNationalityController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "go to CountryOfResidenceYesNoPage" when {
        "from NinoYesNoPage page when user answers no if this is 5MLD" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.copy(is5mldEnabled = true).set(NinoYesNoPage(index), value = false).success.value

              navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
                .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from TrusteesNinoPage page if this is 5MLD and isTaxable" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = true).set(NinoYesNoPage(index), value = false).success.value

              navigator.nextPage(NinoPage(index), fakeDraftId, answers)
                .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "go to CountryOfResidenceInTheUkYesNoPage" when {
        "from CountryOfResidenceYesNoPage page when user answers yes" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(CountryOfResidenceYesNoPage(index), value = true).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
                .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "go to CountryOfResidencePage" when {
        "from CountryOfResidenceInTheUkYesNoPage page when user answers no" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(CountryOfResidenceInTheUkYesNoPage(index), value = false).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
                .mustBe(CountryOfResidenceController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "go to TrusteesNinoYesNoPage" when {
        "from DateOfBirth page if this is 4MLD" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(DateOfBirthPage(index), fakeDraftId, userAnswers.copy(is5mldEnabled = false))
                .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from DateOfBirthYesNoPage page when user answers no if this is not 5MLD" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.copy(is5mldEnabled = false).set(DateOfBirthYesNoPage(index), value = false).success.value

              navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
                .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfNationalityPage page" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(CountryOfNationalityPage(index), fakeDraftId, userAnswers)
                .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfNationalityYesNoPage page when user answers no" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(CountryOfNationalityYesNoPage(index), value = false).success.value

              navigator.nextPage(CountryOfNationalityYesNoPage(index), fakeDraftId, answers)
                .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfNationalityInTheUkYesNoPage page when user answers yes" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(CountryOfNationalityInTheUkYesNoPage(index), value = true).success.value

              navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
                .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "go to TrusteesNinoPage from TrusteesNinoYesNoPage when user answers Yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(NinoYesNoPage(index), value = true).success.value

            navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
              .mustBe(NinoController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to AddressYesNo" when {
        "from NinoYesNo when user answers No if this is not 5MLD" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.copy(is5mldEnabled = false).set(NinoYesNoPage(index), value = false).success.value

              navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
                .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfResidenceYesNo when user answers No if this is 5MLD and taxable and we have no nino" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = true)
                .set(NinoYesNoPage(index), value = false).success.value
                .set(CountryOfResidenceYesNoPage(index), value = false).success.value

              navigator.nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId, answers)
                .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfResidenceInTheUkYesNo when user answers Yes if this is 5MLD and taxable and we have no nino" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = true)
                .set(NinoYesNoPage(index), value = false).success.value
                .set(CountryOfResidenceInTheUkYesNoPage(index), value = true).success.value

              navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
                .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from CountryOfResidence if this is 5MLD and taxable and we have no nino" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers.copy(is5mldEnabled = true, isTaxable = true)
                .set(NinoYesNoPage(index), value = false).success.value

              navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, answers)
                .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "from CountryOfResidence if this is not 5MLD and is taxable" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, userAnswers.copy(is5mldEnabled = false, isTaxable = true))
              .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteesUkAddressPage from TrusteeLivesInUKPage when answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddressUkYesNoPage(index), value = true).success.value

            navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
              .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to InternationalAddressPage from AddressUkYesNo when answer is no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddressUkYesNoPage(index), value = false).success.value

            navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
              .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to PassportDetailsYesNoPage from UkAddressPage when answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(UkAddressPage(index), fakeDraftId, userAnswers)
              .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to PassportDetailsYesNoPage from InternationalAddressPage when answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(InternationalAddressPage(index), fakeDraftId, userAnswers)
              .mustBe(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to PassportDetailsPage from PassportDetailsYesNoPage when answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(PassportDetailsYesNoPage(index), value = true).success.value

            navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
              .mustBe(PassportDetailsController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to IDCardDetailsPage from IDCardDetailsYesNoPage when answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(IDCardDetailsYesNoPage(index), value = true).success.value

            navigator.nextPage(IDCardDetailsYesNoPage(index), fakeDraftId, answers)
              .mustBe(IDCardDetailsController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to IdCardDetailsYesNoPage from PassportDetailsYesNoPage when answer is no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(PassportDetailsYesNoPage(index), value = false).success.value

            navigator.nextPage(PassportDetailsYesNoPage(index), fakeDraftId, answers)
              .mustBe(IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId))
        }
      }
    }
  }
}
