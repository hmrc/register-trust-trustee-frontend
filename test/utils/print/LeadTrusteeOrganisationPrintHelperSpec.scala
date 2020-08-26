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

import base.SpecBase
import controllers.register.leadtrustee.organisation.routes._
import controllers.register.routes.TrusteeIndividualOrBusinessController
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.Business
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import models.core.pages.{InternationalAddress, UKAddress}
import pages.register.leadtrustee.organisation._
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeOrganisationPrintHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: String = "Name"
  private val utr: String = "1234567890"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "NE1 1NE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "DE")
  private val email: String = "email@example.com"
  private val tel: String = "999"

  "LeadTrusteeOrganisationPrintHelper" must {

    val helper = injector.instanceOf[LeadTrusteeOrganisationPrintHelper]

    val baseAnswers = emptyUserAnswers
      .set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
      .set(TrusteeIndividualOrBusinessPage(index), Business).success.value
      .set(NamePage(index), name).success.value
      .set(TelephoneNumberPage(index), tel).success.value

    "render a print section" when {

      "lead trustee is UK registered with UK address and email" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(UkRegisteredYesNoPage(index), true).success.value
          .set(UtrPage(index), utr).success.value
          .set(AddressUkYesNoPage(index), true).success.value
          .set(UkAddressPage(index), ukAddress).success.value
          .set(EmailAddressYesNoPage(index), true).success.value
          .set(EmailAddressPage(index), email).success.value

        val result = helper.checkDetailsSection(userAnswers, name, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Business"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.ukRegisteredYesNo.checkYourAnswersLabel", Html("Yes"), Some(UkRegisteredYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.name.checkYourAnswersLabel", Html("Name"), Some(NameController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.utr.checkYourAnswersLabel", Html("1234567890"), Some(UtrController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.addressUkYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.ukAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />NE1 1NE"), Some(UkAddressController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.emailYesNo.checkYourAnswersLabel", Html("Yes"), Some(EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.email.checkYourAnswersLabel", Html("email@example.com"), Some(EmailAddressController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.telephoneNumber.checkYourAnswersLabel", Html("999"), Some(TelephoneNumberController.onPageLoad(index, fakeDraftId).url), name)
          )
        )
      }

      "lead trustee is not UK registered with international address and no email" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(UkRegisteredYesNoPage(index), false).success.value
          .set(AddressUkYesNoPage(index), false).success.value
          .set(InternationalAddressPage(index), internationalAddress).success.value
          .set(EmailAddressYesNoPage(index), false).success.value

        val result = helper.checkDetailsSection(userAnswers, name, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Business"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.ukRegisteredYesNo.checkYourAnswersLabel", Html("No"), Some(UkRegisteredYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.name.checkYourAnswersLabel", Html("Name"), Some(NameController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.addressUkYesNo.checkYourAnswersLabel", Html("No"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.internationalAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Germany"), Some(InternationalAddressController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.emailYesNo.checkYourAnswersLabel", Html("No"), Some(EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("leadTrustee.organisation.telephoneNumber.checkYourAnswersLabel", Html("999"), Some(TelephoneNumberController.onPageLoad(index, fakeDraftId).url), name)
          )
        )
      }
    }
  }
}
