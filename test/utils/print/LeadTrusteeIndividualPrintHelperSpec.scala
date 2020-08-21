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

package utils.print

import java.time.LocalDate

import base.SpecBase
import controllers.register.leadtrustee.individual.routes._
import controllers.register.trustees.routes.TrusteeIndividualOrBusinessController
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.Individual
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.DetailsChoice.{IdCard, Passport}
import models.registration.pages.PassportOrIdCardDetails
import pages.register.leadtrustee.individual.{TrusteesNamePage, _}
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeIndividualPrintHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: FullName = FullName("First", None, "Last")
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "NE1 1NE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "DE")
  private val email: String = "email@example.com"
  private val tel: String = "999"
  private val dob = LocalDate.parse("2020-08-21")
  private val passport = PassportOrIdCardDetails("DE", "0987654321234", LocalDate.parse("2020-08-21"))
  private val idCard = PassportOrIdCardDetails("DE", "0987654321234", LocalDate.parse("2020-08-21"))

  "LeadTrusteeIndividualPrintHelper" must {

    val helper = injector.instanceOf[LeadTrusteeIndividualPrintHelper]

    val baseAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), true).success.value
      .set(TrusteeIndividualOrBusinessPage(index), Individual).success.value
      .set(TrusteesNamePage(index), name).success.value

    "render a print section" when {

      "lead trustee is passport and uk address" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(TrusteesNamePage(index), name).success.value
          .set(TrusteesDateOfBirthPage(index), dob).success.value
          .set(TrusteeNinoYesNoPage(index), false).success.value
          .set(TrusteeDetailsChoicePage(index), Passport).success.value
          .set(PassportDetailsPage(index), passport).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(UkAddressPage(index), ukAddress).success.value
          .set(EmailAddressYesNoPage(index), false).success.value
          .set(TelephoneNumberPage(index), tel).success.value

        val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Individual"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.name.checkYourAnswersLabel", Html("First Last"), Some(NameController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.dateOfBirth.checkYourAnswersLabel", Html("21 August 2020"), Some(DateOfBirthController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.ninoYesNo.checkYourAnswersLabel", Html("No"), Some(NinoYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.trusteeDetailsChoice.checkYourAnswersLabel", Html("passport"), Some(TrusteeDetailsChoiceController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.passportDetails.checkYourAnswersLabel", Html("Germany<br />0987654321234<br />21 August 2020"), Some(PassportDetailsController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", Html("Yes"), Some(LiveInTheUKYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.ukAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />NE1 1NE"), Some(UkAddressController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel", Html("No"), Some(EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.telephoneNumber.checkYourAnswersLabel", Html("999"), Some(TelephoneNumberController.onPageLoad(index, fakeDraftId).url), name.toString)
          )
        )
      }

      "lead trustee is not UK registered with international address and an email" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(TrusteesNamePage(index), name).success.value
          .set(TrusteesDateOfBirthPage(index), dob).success.value
          .set(TrusteeNinoYesNoPage(index), false).success.value
          .set(TrusteeDetailsChoicePage(index), IdCard).success.value
          .set(PassportDetailsPage(index), idCard).success.value
          .set(TrusteeAUKCitizenPage(index), false).success.value
          .set(InternationalAddressPage(index), internationalAddress).success.value
          .set(EmailAddressYesNoPage(index), true).success.value
          .set(EmailAddressPage(index), email).success.value
          .set(TelephoneNumberPage(index), tel).success.value

        val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Individual"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.name.checkYourAnswersLabel", Html("First Last"), Some(NameController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.dateOfBirth.checkYourAnswersLabel", Html("21 August 2020"), Some(DateOfBirthController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.ninoYesNo.checkYourAnswersLabel", Html("No"), Some(NinoYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.trusteeDetailsChoice.checkYourAnswersLabel", Html("idCard"), Some(TrusteeDetailsChoiceController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.iDCardDetails.checkYourAnswersLabel", Html("Germany<br />0987654321234<br />21 August 2020"), Some(IDCardDetailsController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", Html("yes"), Some(LiveInTheUKYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.internationalAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Germany"), Some(InternationalAddressController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel", Html("Yes"), Some(EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.email.checkYourAnswersLabel", Html("email@example.com"), Some(EmailAddressController.onPageLoad(index, fakeDraftId).url), name.toString),
            AnswerRow("leadTrustee.individual.telephoneNumber.checkYourAnswersLabel", Html("999"), Some(TelephoneNumberController.onPageLoad(index, fakeDraftId).url), name.toString)
          )
        )
      }
    }
  }
}
