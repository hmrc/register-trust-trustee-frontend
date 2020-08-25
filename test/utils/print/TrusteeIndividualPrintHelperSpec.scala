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
import controllers.register.trustees.individual.routes._
import controllers.register.trustees.routes._
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.Individual
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import pages.register.trustees.individual._
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class TrusteeIndividualPrintHelperSpec extends SpecBase {

  private val index: Int = 0
  private val fullName: FullName = FullName("John", None, "Doe")
  private val displayName: String = fullName.toString
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val nino: String = "AA000000A"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "NE1 1NE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "DE")
  private val passportOrIdCardDetails: PassportOrIdCardDetails =
    PassportOrIdCardDetails("DE", "1234567890", date)

  "TrusteeIndividualPrintHelper" must {

    val helper = injector.instanceOf[TrusteeIndividualPrintHelper]

    val baseAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), false).success.value
      .set(TrusteeIndividualOrBusinessPage(index), Individual).success.value
      .set(NamePage(index), fullName).success.value

    "render a print section" when {

      "trustee has no date of birth, NINO or address" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(DateOfBirthYesNoPage(index), false).success.value
          .set(NinoYesNoPage(index), false).success.value
          .set(AddressYesNoPage(index), false).success.value

        val result = helper.checkDetailsSection(userAnswers, displayName, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Individual"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.name.checkYourAnswersLabel", Html("John Doe"), Some(NameController.onPageLoad(index, fakeDraftId).url)),
            AnswerRow("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", Html("No"), Some(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.ninoYesNo.checkYourAnswersLabel", Html("No"), Some(NinoYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.addressYesNo.checkYourAnswersLabel", Html("No"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), displayName)
          )
        )
      }

      "trustee has date of birth and NINO" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(DateOfBirthYesNoPage(index), true).success.value
          .set(DateOfBirthPage(index), date).success.value
          .set(NinoYesNoPage(index), true).success.value
          .set(NinoPage(index), nino).success.value

        val result = helper.checkDetailsSection(userAnswers, displayName, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Individual"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.name.checkYourAnswersLabel", Html("John Doe"), Some(NameController.onPageLoad(index, fakeDraftId).url)),
            AnswerRow("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", Html("Yes"), Some(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.dateOfBirth.checkYourAnswersLabel", Html("3 February 1996"), Some(DateOfBirthController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.ninoYesNo.checkYourAnswersLabel", Html("Yes"), Some(NinoYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.nino.checkYourAnswersLabel", Html("AA 00 00 00 A"), Some(NinoController.onPageLoad(index, fakeDraftId).url), displayName)
          )
        )
      }

      "trustee has UK address and passport details" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(DateOfBirthYesNoPage(index), false).success.value
          .set(NinoYesNoPage(index), false).success.value
          .set(AddressYesNoPage(index), true).success.value
          .set(AddressUkYesNoPage(index), true).success.value
          .set(UkAddressPage(index), ukAddress).success.value
          .set(PassportDetailsYesNoPage(index), true).success.value
          .set(PassportDetailsPage(index), passportOrIdCardDetails).success.value

        val result = helper.checkDetailsSection(userAnswers, displayName, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Individual"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.name.checkYourAnswersLabel", Html("John Doe"), Some(NameController.onPageLoad(index, fakeDraftId).url)),
            AnswerRow("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", Html("No"), Some(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.ninoYesNo.checkYourAnswersLabel", Html("No"), Some(NinoYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.addressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.addressUkYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.ukAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />NE1 1NE"), Some(UkAddressController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.passportDetailsYesNo.checkYourAnswersLabel", Html("Yes"), Some(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.passportDetails.checkYourAnswersLabel", Html("Germany<br />1234567890<br />3 February 1996"), Some(PassportDetailsController.onPageLoad(index, fakeDraftId).url), displayName)
          )
        )
      }

      "trustee has international address and ID card details" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(DateOfBirthYesNoPage(index), false).success.value
          .set(NinoYesNoPage(index), false).success.value
          .set(AddressYesNoPage(index), true).success.value
          .set(AddressUkYesNoPage(index), false).success.value
          .set(InternationalAddressPage(index), internationalAddress).success.value
          .set(PassportDetailsYesNoPage(index), false).success.value
          .set(IDCardDetailsYesNoPage(index), true).success.value
          .set(IDCardDetailsPage(index), passportOrIdCardDetails).success.value

        val result = helper.checkDetailsSection(userAnswers, displayName, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Individual"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.name.checkYourAnswersLabel", Html("John Doe"), Some(NameController.onPageLoad(index, fakeDraftId).url)),
            AnswerRow("trustee.individual.dateOfBirthYesNo.checkYourAnswersLabel", Html("No"), Some(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.ninoYesNo.checkYourAnswersLabel", Html("No"), Some(NinoYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.addressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.addressUkYesNo.checkYourAnswersLabel", Html("No"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.internationalAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Germany"), Some(InternationalAddressController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.passportDetailsYesNo.checkYourAnswersLabel", Html("No"), Some(PassportDetailsYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.idCardDetailsYesNo.checkYourAnswersLabel", Html("Yes"), Some(IDCardDetailsYesNoController.onPageLoad(index, fakeDraftId).url), displayName),
            AnswerRow("trustee.individual.idCardDetails.checkYourAnswersLabel", Html("Germany<br />1234567890<br />3 February 1996"), Some(IDCardDetailsController.onPageLoad(index, fakeDraftId).url), displayName)
          )
        )
      }
    }
  }
}