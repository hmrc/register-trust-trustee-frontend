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
import controllers.register.trustees.individual.routes._
import controllers.register.trustees.routes
import generators.Generators
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.{Business, Individual}
import models.registration.pages.AddATrustee
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees._
import pages.register.trustees.individual._
import play.api.mvc.Call
import sections.Trustees

class TrusteeIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new TrusteeIndividualNavigator
  val index = 0

  private def registrationTaskList: Call = {
    Call("GET", frontendAppConfig.registrationProgressUrl(draftId))
  }

  implicit val config = frontendAppConfig

  "Trustee Individual Navigator" when {

    "there are no trustees" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers
              .set(AddATrusteePage, AddATrustee.YesNow).success.value
              .remove(Trustees).success.value

            navigator.nextPage(AddATrusteePage, fakeDraftId, answers)
              .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to the next trustee from AddATrusteeYesNoPage when selecting yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteeYesNoPage, true).success.value
              .remove(Trustees).success.value

            navigator.nextPage(AddATrusteeYesNoPage, fakeDraftId, answers)
              .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to the registration progress page from AddATrusteeYesNoPage when selecting no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddATrusteeYesNoPage, false).success.value
              .remove(Trustees).success.value

            navigator.nextPage(AddATrusteeYesNoPage, fakeDraftId, answers)
              .mustBe(registrationTaskList)
        }
      }

    }

    "there is at least one trustee" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {

        val answers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(AddATrusteePage, AddATrustee.YesNow).success.value

        navigator.nextPage(AddATrusteePage, fakeDraftId, answers)
          .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(1, fakeDraftId))
      }

      "go to RegistrationProgress from AddATrusteePage " when {
        "selecting add them later" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsThisLeadTrusteePage(index), true).success.value
                .set(AddATrusteePage, AddATrustee.YesLater).success.value

              navigator.nextPage(AddATrusteePage, fakeDraftId, answers)
                .mustBe(registrationTaskList)
          }
        }

        "selecting added them all" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(IsThisLeadTrusteePage(index), true).success.value
                .set(AddATrusteePage, AddATrustee.NoComplete).success.value

              navigator.nextPage(AddATrusteePage, fakeDraftId, answers)
                .mustBe(registrationTaskList)
          }
        }
      }

      "go to IndividualOrBusinessPage from IsThisLeadTrusteePage page when YES selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(IsThisLeadTrusteePage(index), fakeDraftId, userAnswers)
              .mustBe(controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteesNamePage from TrusteeIndividualOrBusinessPage" when {
        "for a lead trustee Individual" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers
                .set(IsThisLeadTrusteePage(index), true).success.value
                .set(TrusteeIndividualOrBusinessPage(index), Individual).success.value

              navigator.nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
                .mustBe(controllers.register.leadtrustee.individual.routes.NameController.onPageLoad(index, fakeDraftId))
          }
        }

        "for a non-lead trustee Individual" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers
                .set(IsThisLeadTrusteePage(index), false).success.value
                .set(TrusteeIndividualOrBusinessPage(index), Individual).success.value

              navigator.nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
                .mustBe(NameController.onPageLoad(index, fakeDraftId))
          }
        }

        "for a non-lead trustee Business" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers
                .set(IsThisLeadTrusteePage(index), false).success.value
                .set(TrusteeIndividualOrBusinessPage(index), Business).success.value

              navigator.nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
                .mustBe(controllers.register.trustees.organisation.routes.NameController.onPageLoad(index, fakeDraftId))
          }
        }


        "for a lead trustee Business" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val answers = userAnswers
                .set(IsThisLeadTrusteePage(index), true).success.value
                .set(TrusteeIndividualOrBusinessPage(index), Business).success.value

              navigator.nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
                .mustBe(controllers.register.leadtrustee.organisation.routes.UkRegisteredYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

      }

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

        "from TrusteesNinoPage" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(NinoPage(index), fakeDraftId, userAnswers)
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

      "go to TrusteesNinoYesNoPage" when {
        "from TrusteesDateOfBirthPage page" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              navigator.nextPage(DateOfBirthPage(index), fakeDraftId, userAnswers)
                .mustBe(NinoYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "from DateOfBirthYesNoPage page when user answers no" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(DateOfBirthYesNoPage(index), value = false).success.value

              navigator.nextPage(DateOfBirthPage(index), fakeDraftId, userAnswers)
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
        "from NinoYesNo when user answers No" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>

              val answers = userAnswers.set(NinoYesNoPage(index), value = false).success.value

              navigator.nextPage(NinoYesNoPage(index), fakeDraftId, answers)
                .mustBe(AddressYesNoController.onPageLoad(index, fakeDraftId))
          }
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

      "go to AddATrusteePage from TrusteeAnswersPage page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrusteesAnswerPage, fakeDraftId, userAnswers)
              .mustBe(routes.AddATrusteeController.onPageLoad(fakeDraftId))
        }
      }
    }

  }

}
