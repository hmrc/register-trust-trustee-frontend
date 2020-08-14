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

import com.google.inject.Inject
import controllers.register.leadtrustee.organisation.routes._
import controllers.register.leadtrustee.routes._
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import pages.register.leadtrustee.IndividualOrBusinessPage
import pages.register.leadtrustee.organisation._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeOrganisationPrintHelper @Inject()(answerRowConverter: AnswerRowConverter, countryOptions: CountryOptions) {

  def checkDetailsSection(userAnswers: UserAnswers, name: String, draftId: String)
                         (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      None,
      answers(userAnswers, name, draftId)
    )
  }

  private def answers(userAnswers: UserAnswers, name: String, draftId: String)
                     (implicit messages: Messages): Seq[AnswerRow] = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)
    val prefix: String = "leadTrustee.organisation"

    Seq(
      bound.enumQuestion[IndividualOrBusiness](
        IndividualOrBusinessPage,
        "leadTrusteeIndividualOrBusiness",
        IndividualOrBusinessController.onPageLoad(draftId).url,
        "individualOrBusiness"
      ),
      bound.yesNoQuestion(UkRegisteredYesNoPage, s"$prefix.ukRegisteredYesNo", UkRegisteredYesNoController.onPageLoad(draftId).url),
      bound.stringQuestion(NamePage, s"$prefix.name", NameController.onPageLoad(draftId).url),
      bound.stringQuestion(UtrPage, s"$prefix.utr", UtrController.onPageLoad(draftId).url),
      bound.yesNoQuestion(AddressUkYesNoPage, s"$prefix.addressUkYesNo", AddressUkYesNoController.onPageLoad(draftId).url),
      bound.addressQuestion(UkAddressPage, s"$prefix.ukAddress", UkAddressController.onPageLoad(draftId).url),
      bound.addressQuestion(InternationalAddressPage, s"$prefix.internationalAddress", InternationalAddressController.onPageLoad(draftId).url),
      bound.yesNoQuestion(EmailAddressYesNoPage, s"$prefix.emailYesNo", EmailAddressYesNoController.onPageLoad(draftId).url),
      bound.stringQuestion(EmailAddressPage, s"$prefix.email", EmailAddressController.onPageLoad(draftId).url),
      bound.stringQuestion(TelephoneNumberPage, s"$prefix.telephoneNumber", TelephoneNumberController.onPageLoad(draftId).url)
    ).flatten
  }

}
