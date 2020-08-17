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
import controllers.register.trustees.organisation.routes._
import controllers.register.trustees.routes._
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.Business
import models.core.pages.{InternationalAddress, UKAddress}
import pages.register.trustees.organisation._
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class TrusteeOrganisationPrintHelperSpec extends SpecBase {

  private val index: Int = 0
  private val name: String = "Name"
  private val utr: String = "1234567890"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "NE1 1NE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "DE")

  "TrusteeOrganisationPrintHelper" must {

    val helper = injector.instanceOf[TrusteeOrganisationPrintHelper]

    val baseAnswers = emptyUserAnswers
      .set(IsThisLeadTrusteePage(index), false).success.value
      .set(TrusteeIndividualOrBusinessPage(index), Business).success.value
      .set(NamePage(index), name).success.value

    "render a print section" when {

      "trustee has no UTR or address" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(UtrYesNoPage(index), false).success.value
          .set(AddressYesNoPage(index), false).success.value

        val result = helper.checkDetailsSection(userAnswers, name, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Business"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.name.checkYourAnswersLabel", Html("Name"), Some(NameController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.utrYesNo.checkYourAnswersLabel", Html("No"), Some(UtrYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.addressYesNo.checkYourAnswersLabel", Html("No"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), name)
          )
        )
      }

      "trustee has UTR" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(UtrYesNoPage(index), true).success.value
          .set(UtrPage(index), utr).success.value

        val result = helper.checkDetailsSection(userAnswers, name, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Business"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.name.checkYourAnswersLabel", Html("Name"), Some(NameController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.utrYesNo.checkYourAnswersLabel", Html("Yes"), Some(UtrYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.utr.checkYourAnswersLabel", Html("1234567890"), Some(UtrController.onPageLoad(index, fakeDraftId).url), name)
          )
        )
      }

      "trustee has UK address" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(UtrYesNoPage(index), false).success.value
          .set(AddressYesNoPage(index), true).success.value
          .set(AddressUkYesNoPage(index), true).success.value
          .set(UkAddressPage(index), ukAddress).success.value

        val result = helper.checkDetailsSection(userAnswers, name, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Business"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.name.checkYourAnswersLabel", Html("Name"), Some(NameController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.utrYesNo.checkYourAnswersLabel", Html("No"), Some(UtrYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.addressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.addressUkYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.ukAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />NE1 1NE"), Some(UkAddressController.onPageLoad(index, fakeDraftId).url), name)
          )
        )
      }

      "trustee has international address" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(UtrYesNoPage(index), false).success.value
          .set(AddressYesNoPage(index), true).success.value
          .set(AddressUkYesNoPage(index), false).success.value
          .set(InternationalAddressPage(index), internationalAddress).success.value

        val result = helper.checkDetailsSection(userAnswers, name, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", Html("Business"), Some(TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.name.checkYourAnswersLabel", Html("Name"), Some(NameController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.utrYesNo.checkYourAnswersLabel", Html("No"), Some(UtrYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.addressYesNo.checkYourAnswersLabel", Html("Yes"), Some(AddressYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.addressUkYesNo.checkYourAnswersLabel", Html("No"), Some(AddressUkYesNoController.onPageLoad(index, fakeDraftId).url), name),
            AnswerRow("trustee.organisation.internationalAddress.checkYourAnswersLabel", Html("Line 1<br />Line 2<br />Germany"), Some(InternationalAddressController.onPageLoad(index, fakeDraftId).url), name)
          )
        )
      }
    }
  }
}
