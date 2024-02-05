/*
 * Copyright 2024 HM Revenue & Customs
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

package utils

import base.SpecBase
import models.Status.{Completed, InProgress}
import models.core.pages.TrusteeOrLeadTrustee.{LeadTrustee, Trustee}
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.registration.pages.AddATrustee
import models.{Status, UserAnswers}
import pages.entitystatus.TrusteeStatus
import pages.register.leadtrustee.{individual => ltind}
import pages.register.trustees.{organisation => torg}
import pages.register.{AddATrusteePage, TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}

import java.time.LocalDate

class RegistrationProgressSpec extends SpecBase {

  private val registrationProgress: RegistrationProgress = injector.instanceOf[RegistrationProgress]

  private implicit class AddTrusteesToUserAnswers(userAnswers: UserAnswers) {

    def addIndividualLeadTrustee(index: Int): UserAnswers = {
      userAnswers.set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
        .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
        .set(ltind.TrusteesNamePage(index), FullName("first name", Some("middle name"), "Last Name")).success.value
        .set(ltind.TrusteesDateOfBirthPage(index), LocalDate.of(2000, 10, 10)).success.value
        .set(ltind.TrusteeNinoYesNoPage(index), true).success.value
        .set(ltind.TrusteesNinoPage(index), "AB123456C").success.value
        .set(ltind.AddressUkYesNoPage(index), true).success.value
        .set(ltind.UkAddressPage(index), UKAddress("line1", "line2", None, None, "NE65QA")).success.value
        .set(ltind.EmailAddressYesNoPage(index), false).success.value
        .set(ltind.TelephoneNumberPage(index), "0191 1111111").success.value
        .set(TrusteeStatus(index), Status.Completed).success.value
    }
    
    def addOrganisationTrustee(index: Int): UserAnswers = {
      userAnswers
        .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
        .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
        .set(torg.NamePage(index), "Org Name1").success.value
        .set(TrusteeStatus(index), Status.Completed).success.value
    }

  }

  "RegistrationProgress" must {

    "return None" when {

      "there are no trustees or lead trustees" in {

        val userAnswers = emptyUserAnswers
        val result = registrationProgress.trusteesStatus(userAnswers)
        result mustBe None
      }
    }

    "return Some(Completed)" when {

      "there is a completed lead trustee, and section flagged as complete" in {

        val userAnswers = emptyUserAnswers
          .addIndividualLeadTrustee(0)
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        val result = registrationProgress.trusteesStatus(userAnswers)
        result mustBe Some(Completed)
      }

      "there is a completed lead trustee and trustee, and section flagged as complete" in {

        val userAnswers = emptyUserAnswers
          .addIndividualLeadTrustee(0)
          .addOrganisationTrustee(1)
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        val result = registrationProgress.trusteesStatus(userAnswers)
        result mustBe Some(Completed)
      }
    }

    "return Some(InProgress)" when {

      "there are trustees that are incomplete" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
          .set(TrusteeOrLeadTrusteePage(1), Trustee).success.value
          .set(TrusteeStatus(1), Status.Completed).success.value

        val result = registrationProgress.trusteesStatus(userAnswers)
        result mustBe Some(InProgress)
      }

      "there are trustees that are complete, but section flagged not complete" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
          .set(TrusteeStatus(0), Status.Completed).success.value
          .set(TrusteeOrLeadTrusteePage(1), Trustee).success.value
          .set(TrusteeStatus(1), Status.Completed).success.value
          .set(AddATrusteePage, AddATrustee.YesLater).success.value

        val result = registrationProgress.trusteesStatus(userAnswers)
        result mustBe Some(InProgress)
      }

      "there are completed trustees, the section is flagged as completed, but there is no lead trustee" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
          .set(TrusteeStatus(0), Status.Completed).success.value
          .set(TrusteeOrLeadTrusteePage(1), Trustee).success.value
          .set(TrusteeStatus(1), Status.Completed).success.value
          .set(AddATrusteePage, AddATrustee.NoComplete).success.value

        val result = registrationProgress.trusteesStatus(userAnswers)
        result mustBe Some(InProgress)
      }
    }
  }
}
