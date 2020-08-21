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

///*
// * Copyright 2020 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package utils.print
//
//import base.SpecBase
//import controllers.register.leadtrustee.organisation.routes._
//import controllers.register.leadtrustee.individual.routes._
//import models.UserAnswers
//import models.core.pages.IndividualOrBusiness.Business
//import models.core.pages.{FullName, InternationalAddress, UKAddress}
//import models.registration.pages.DetailsChoice.Passport
//import pages.register.leadtrustee.individual.TrusteesNamePage
//import pages.register.leadtrustee.individual._
//import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
//import play.twirl.api.Html
//import viewmodels.{AnswerRow, AnswerSection}
//
//class LeadTrusteeIndividualPrintHelperSpec extends SpecBase {
//
//  private val index: Int = 0
//  private val name: FullName = FullName("First", None, "Last")
//  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "NE1 1NE")
//  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "DE")
//  private val email: String = "email@example.com"
//  private val tel: String = "999"
//  private val dob = ""
//
//  "LeadTrusteeIndividualPrintHelper" must {
//
//    val helper = injector.instanceOf[LeadTrusteeIndividualPrintHelper]
//
//    val baseAnswers = emptyUserAnswers
//      .set(IsThisLeadTrusteePage(index), true).success.value
//      .set(TrusteeIndividualOrBusinessPage(index), Business).success.value
//      .set(TrusteesNamePage(index), name).success.value
//      .set(TelephoneNumberPage(index), tel).success.value
//
//    "render a print section" when {
//
//      "lead trustee is passport and uk address" in {
//        val userAnswers: UserAnswers = baseAnswers
//          .set(TrusteesNamePage(index), name).success.value
//          .set(TrusteesDateOfBirthPage(index), dob).success.value
//          .set(TrusteeNinoYesNoPage(index), false).success.value
//          .set(TrusteeDetailsChoicePage(index), Passport).success.value
//          .set(TrusteeAUKCitizenPage(index), true).success.value
//          .set(UkAddressPage(index), ukAddress).success.value
//          .set(EmailAddressYesNoPage(index), false).success.value
//          .set(TelephoneNumberPage(index), tel).success.value
//
//        val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)
//
//        result mustBe AnswerSection(
//          None,
//          Seq(
//            AnswerRow("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Business"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.ukRegisteredYesNo.checkYourAnswersLabel", Html("Yes"), Some(UkRegisteredYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.name.checkYourAnswersLabel", Html("Name"), Some(NameController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.utr.checkYourAnswersLabel", Html("1234567890"), Some(UtrController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.addressUkYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.ukAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />NE1 1NE"), Some(UkAddressController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.emailYesNo.checkYourAnswersLabel", Html("Yes"), Some(EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.email.checkYourAnswersLabel", Html("email@example.com"), Some(EmailAddressController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.telephoneNumber.checkYourAnswersLabel", Html("999"), Some(TelephoneNumberController.onPageLoad(index, fakeDraftId).url), name.toString)
//          )
//        )
//      }
//
//      "lead trustee is not UK registered with international address and an email" in {
//        val userAnswers: UserAnswers = baseAnswers
//          .set(TrusteesNamePage(index), name).success.value
//          .set(TrusteesDateOfBirthPage(index), dob).success.value
//          .set(TrusteeNinoYesNoPage(index), false).success.value
//          .set(TrusteeDetailsChoicePage(index), Passport).success.value
//          .set(TrusteeAUKCitizenPage(index), false).success.value
//          .set(InternationalAddressPage(index), internationalAddress).success.value
//          .set(EmailAddressYesNoPage(index), true).success.value
//          .set(EmailAddressPage(index), email).success.value
//          .set(TelephoneNumberPage(index), tel).success.value
//
//        val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)
//
//        result mustBe AnswerSection(
//          None,
//          Seq(
//            AnswerRow("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Business"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.ukRegisteredYesNo.checkYourAnswersLabel", Html("No"), Some(UkRegisteredYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.name.checkYourAnswersLabel", Html("Name"), Some(NameController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.addressUkYesNo.checkYourAnswersLabel", Html("No"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.internationalAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Germany"), Some(InternationalAddressController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.emailYesNo.checkYourAnswersLabel", Html("No"), Some(EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url), name.toString),
//            AnswerRow("leadTrustee.individual.telephoneNumber.checkYourAnswersLabel", Html("999"), Some(TelephoneNumberController.onPageLoad(index, fakeDraftId).url), name.toString)
//          )
//        )
//      }
//    }
//  }
//}
