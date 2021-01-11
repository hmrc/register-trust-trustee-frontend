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

package utils.print

import com.google.inject.Inject
import controllers.register.{routes => regRts}
import controllers.register.leadtrustee.organisation.{routes => rts}
import controllers.register.leadtrustee.organisation.nonTaxable.{routes => ntRts}
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.leadtrustee.organisation._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeOrganisationPrintHelper @Inject()(answerRowConverter: AnswerRowConverter, countryOptions: CountryOptions) {

  def checkDetailsSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                         (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      None,
      answers(userAnswers, name, index, draftId)
    )
  }

  private def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                     (implicit messages: Messages): Seq[AnswerRow] = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)
    val prefix: String = "leadTrustee.organisation"

    Seq(
      bound.enumQuestion[IndividualOrBusiness](
        TrusteeIndividualOrBusinessPage(index),
        "leadTrusteeIndividualOrBusiness",
        regRts.TrusteeIndividualOrBusinessController.onPageLoad(index, draftId).url,
        "individualOrBusiness"
      ),
      bound.yesNoQuestion(UkRegisteredYesNoPage(index), s"$prefix.ukRegisteredYesNo", rts.UkRegisteredYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(NamePage(index), s"$prefix.name", rts.NameController.onPageLoad(index, draftId).url),
      bound.stringQuestion(UtrPage(index), s"$prefix.utr", rts.UtrController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(nonTaxable.CountryOfResidenceInTheUkYesNoPage(index), s"$prefix.5mld.countryOfResidenceInTheUkYesNo", ntRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(nonTaxable.CountryOfResidenceInTheUkYesNoPage(index), nonTaxable.CountryOfResidencePage(index), s"$prefix.5mld.countryOfResidence", ntRts.CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUkYesNoPage(index), s"$prefix.addressUkYesNo", rts.AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(UkAddressPage(index), s"$prefix.ukAddress", rts.UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(InternationalAddressPage(index), s"$prefix.internationalAddress", rts.InternationalAddressController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(EmailAddressYesNoPage(index), s"$prefix.emailYesNo", rts.EmailAddressYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(EmailAddressPage(index), s"$prefix.email", rts.EmailAddressController.onPageLoad(index, draftId).url),
      bound.stringQuestion(TelephoneNumberPage(index), s"$prefix.telephoneNumber", rts.TelephoneNumberController.onPageLoad(index, draftId).url)
    ).flatten
  }

}
