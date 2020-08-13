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
import config.FrontendAppConfig
import controllers.register.trustees.routes
import generators.Generators
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.{Business, Individual}
import models.registration.pages.AddATrustee
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees._
import pages.register.trustees.individual._
import pages.register.trustees.organisation.{TrusteeOrgNamePage, TrusteeUtrYesNoPage}
import play.api.mvc.Call
import sections.Trustees

trait TestTrusteeRoutes {

  self: ScalaCheckPropertyChecks with Generators with SpecBase =>

  val index = 0

  private val config = injector.instanceOf[FrontendAppConfig]

  private def registrationTaskList: Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  def trusteeRoutes()(implicit navigator: Navigator): Unit = {

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

    "there is atleast one trustee" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {

            val answers = emptyUserAnswers
              .set(IsThisLeadTrusteePage(index), true).success.value
              .set(AddATrusteePage, AddATrustee.YesNow).success.value

            navigator.nextPage(AddATrusteePage, fakeDraftId, answers)
              .mustBe(routes.IsThisLeadTrusteeController.onPageLoad(1, fakeDraftId))
      }

    }

    "go to RegistrationProgress from AddATrusteePage when selecting add them later" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsThisLeadTrusteePage(index), true).success.value
            .set(AddATrusteePage, AddATrustee.YesLater).success.value

          navigator.nextPage(AddATrusteePage, fakeDraftId, answers)
            .mustBe(registrationTaskList)
      }
    }

    "go to RegistrationProgress from AddATrusteePage when selecting added them all" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsThisLeadTrusteePage(index), true).success.value
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          navigator.nextPage(AddATrusteePage, fakeDraftId, answers)
            .mustBe(registrationTaskList)
      }
    }

    "go to TrusteeIndividualOrBusinessPage from IsThisLeadTrusteePage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(IsThisLeadTrusteePage(index), fakeDraftId, userAnswers)
            .mustBe(routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to TrusteesNamePage from TrusteeIndividualOrBusinessPage page for a lead trustee Individual" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers
            .set(IsThisLeadTrusteePage(index), true).success.value
            .set(TrusteeIndividualOrBusinessPage(index), Individual).success.value

          navigator.nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
            .mustBe(controllers.register.trustees.individual.routes.TrusteesNameController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to TrusteesNamePage from TrusteeIndividualOrBusinessPage page for a non-lead trustee Individual" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers
            .set(IsThisLeadTrusteePage(index), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index), Individual).success.value

          navigator.nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
            .mustBe(controllers.register.trustees.individual.routes.TrusteesNameController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to TrusteeUtrYesNoPage from TrusteeIndividualOrBusinessPage page for a lead trustee Business" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers
            .set(IsThisLeadTrusteePage(index), true).success.value
            .set(TrusteeIndividualOrBusinessPage(index), Business).success.value

          navigator.nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
            .mustBe(controllers.register.trustees.organisation.routes.UtrYesNoController.onPageLoad(index, fakeDraftId))
      }
    }

    "go to TrusteeUtrYesNoPage from TrusteeIndividualOrBusinessPage page for a non-lead trustee Business" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val answers = userAnswers
            .set(IsThisLeadTrusteePage(index), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index), Business).success.value

          navigator.nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
            .mustBe(routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId))
      }
    }




    "go to TrusteesDateOfBirthPage from TrusteesNamePage page" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          navigator.nextPage(TrusteesNamePage(index), fakeDraftId, userAnswers)
            .mustBe(controllers.register.trustees.individual.routes.TrusteesDateOfBirthController.onPageLoad(index, fakeDraftId))
      }
    }

    "non lead trustee" must {

      "go to TrusteeAnswersPage from TrusteesDateOfBirthPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(IsThisLeadTrusteePage(index), false).success.value

            navigator.nextPage(TrusteesDateOfBirthPage(index), fakeDraftId, answers)
              .mustBe(routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId))
        }
      }

    }

    "lead trustee" must {

      "go to TrusteeAUKCitizen from TrusteesDateOfBirthPage page" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(IsThisLeadTrusteePage(index), true).success.value

            navigator.nextPage(TrusteesDateOfBirthPage(index), fakeDraftId, answers)
              .mustBe(controllers.register.trustees.individual.routes.TrusteeAUKCitizenController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteesNinoPage from TrusteeAUKCitizen when user answers Yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrusteeAUKCitizenPage(index), value = true).success.value

            navigator.nextPage(TrusteeAUKCitizenPage(index), fakeDraftId, answers)
              .mustBe(controllers.register.trustees.individual.routes.TrusteesNinoController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteeAUKCitizenPage from TrusteeAUKCitizen when user answers No" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrusteeAUKCitizenPage(index), value = false).success.value

            navigator.nextPage(TrusteeAUKCitizenPage(index), fakeDraftId, answers)
              .mustBe(controllers.register.trustees.individual.routes.TrusteeAUKCitizenController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteeLivesInUKPage from TrusteesNinoPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrusteesNinoPage(index), fakeDraftId, userAnswers)
              .mustBe(controllers.register.trustees.individual.routes.TrusteeLiveInTheUKController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteesUkAddressPage from TrusteeLivesInUKPage when answer is yes" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrusteeAddressInTheUKPage(index), value = true).success.value

            navigator.nextPage(TrusteeAddressInTheUKPage(index), fakeDraftId, answers)
              .mustBe(controllers.register.trustees.individual.routes.TrusteesUkAddressController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteeLivesInUKPage from TrusteeLivesInUKPage when answer is no" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(TrusteeAddressInTheUKPage(index), value = false).success.value

            navigator.nextPage(TrusteeAddressInTheUKPage(index), fakeDraftId, answers)
              .mustBe(controllers.register.trustees.individual.routes.TrusteeLiveInTheUKController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteeTelephoneNumberPage from TrusteesUkAddressPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TrusteesUkAddressPage(index), fakeDraftId, userAnswers)
              .mustBe(controllers.register.trustees.individual.routes.TelephoneNumberController.onPageLoad(index, fakeDraftId))
        }
      }

      "go to TrusteeAnswerPage from TrusteeTelephoneNumberPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            navigator.nextPage(TelephoneNumberPage(index), fakeDraftId, userAnswers)
              .mustBe(routes.TrusteesAnswerPageController.onPageLoad(index, fakeDraftId))
        }
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
