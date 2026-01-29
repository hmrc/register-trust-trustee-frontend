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
import controllers.register.routes
import controllers.register.trustees.individual.routes._
import generators.Generators
import models.Status.Completed
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.{Business, Individual}
import models.core.pages.TrusteeOrLeadTrustee._
import models.registration.pages.AddATrustee
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.entitystatus.TrusteeStatus
import pages.register._
import pages.register.trustees.organisation.NamePage
import play.api.mvc.Call
import sections.Trustees

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator
  val index     = 0

  private def registrationTaskList: Call =
    Call("GET", frontendAppConfig.registrationProgressUrl(draftId))

  implicit val config: FrontendAppConfig = frontendAppConfig

  "Navigator" when {

    "there are no trustees" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val answers = userAnswers
            .set(AddATrusteePage, AddATrustee.YesNow)
            .success
            .value
            .remove(Trustees)
            .success
            .value

          navigator
            .nextPage(AddATrusteePage, fakeDraftId, answers)
            .mustBe(routes.TrusteeOrLeadTrusteeController.onPageLoad(index, fakeDraftId))
        }

      "go to the next trustee from AddATrusteeYesNoPage when selecting yes" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val answers = userAnswers
            .set(AddATrusteeYesNoPage, true)
            .success
            .value
            .remove(Trustees)
            .success
            .value

          navigator
            .nextPage(AddATrusteeYesNoPage, fakeDraftId, answers)
            .mustBe(routes.TrusteeOrLeadTrusteeController.onPageLoad(index, fakeDraftId))
        }

      "go to the registration progress page from AddATrusteeYesNoPage when selecting no" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val answers = userAnswers
            .set(AddATrusteeYesNoPage, false)
            .success
            .value
            .remove(Trustees)
            .success
            .value

          navigator
            .nextPage(AddATrusteeYesNoPage, fakeDraftId, answers)
            .mustBe(registrationTaskList)
        }

    }

    "there is at least one trustee" must {

      "go to the next trustee from AddATrusteePage when selected add them now" in {

        val answers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), Trustee)
          .success
          .value
          .set(TrusteeIndividualOrBusinessPage(index), Business)
          .success
          .value
          .set(NamePage(index), "Name")
          .success
          .value
          .set(TrusteeStatus(index), Completed)
          .success
          .value
          .set(AddATrusteePage, AddATrustee.YesNow)
          .success
          .value

        navigator
          .nextPage(AddATrusteePage, fakeDraftId, answers)
          .mustBe(routes.TrusteeOrLeadTrusteeController.onPageLoad(1, fakeDraftId))
      }

      "go to RegistrationProgress from AddATrusteePage " when {
        "selecting add them later" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers
              .set(TrusteeOrLeadTrusteePage(index), LeadTrustee)
              .success
              .value
              .set(AddATrusteePage, AddATrustee.YesLater)
              .success
              .value

            navigator
              .nextPage(AddATrusteePage, fakeDraftId, answers)
              .mustBe(registrationTaskList)
          }

        "selecting added them all" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers
              .set(TrusteeOrLeadTrusteePage(index), LeadTrustee)
              .success
              .value
              .set(AddATrusteePage, AddATrustee.NoComplete)
              .success
              .value

            navigator
              .nextPage(AddATrusteePage, fakeDraftId, answers)
              .mustBe(registrationTaskList)
          }
      }

      "go to IndividualOrBusinessPage from TrusteeOrLeadTrusteePage page when YES selected" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          navigator
            .nextPage(TrusteeOrLeadTrusteePage(index), fakeDraftId, userAnswers)
            .mustBe(routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId))
        }

      "go to TrusteesNamePage from TrusteeIndividualOrBusinessPage" when {
        "for a lead trustee Individual" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers
              .set(TrusteeOrLeadTrusteePage(index), LeadTrustee)
              .success
              .value
              .set(TrusteeIndividualOrBusinessPage(index), Individual)
              .success
              .value

            navigator
              .nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
              .mustBe(controllers.register.leadtrustee.individual.routes.NameController.onPageLoad(index, fakeDraftId))
          }

        "for a non-lead trustee Individual" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers
              .set(TrusteeOrLeadTrusteePage(index), Trustee)
              .success
              .value
              .set(TrusteeIndividualOrBusinessPage(index), Individual)
              .success
              .value

            navigator
              .nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
              .mustBe(NameController.onPageLoad(index, fakeDraftId))
          }

        "for a non-lead trustee Business" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers
              .set(TrusteeOrLeadTrusteePage(index), Trustee)
              .success
              .value
              .set(TrusteeIndividualOrBusinessPage(index), Business)
              .success
              .value

            navigator
              .nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
              .mustBe(controllers.register.trustees.organisation.routes.NameController.onPageLoad(index, fakeDraftId))
          }

        "for a lead trustee Business" in
          forAll(arbitrary[UserAnswers]) { userAnswers =>
            val answers = userAnswers
              .set(TrusteeOrLeadTrusteePage(index), LeadTrustee)
              .success
              .value
              .set(TrusteeIndividualOrBusinessPage(index), Business)
              .success
              .value

            navigator
              .nextPage(TrusteeIndividualOrBusinessPage(index), fakeDraftId, answers)
              .mustBe(
                controllers.register.leadtrustee.organisation.routes.UkRegisteredYesNoController
                  .onPageLoad(index, fakeDraftId)
              )
          }

      }

      "go to AddATrusteePage from TrusteeAnswersPage page" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          navigator
            .nextPage(TrusteesAnswerPage, fakeDraftId, userAnswers)
            .mustBe(routes.AddATrusteeController.onPageLoad(fakeDraftId))
        }
    }

  }

}
