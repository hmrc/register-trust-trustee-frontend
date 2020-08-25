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

import controllers.register.trustees.organisation.routes._
import controllers.register.routes.TrusteeIndividualOrBusinessController
import com.google.inject.Inject
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.trustees.organisation._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class TrusteeOrganisationPrintHelper @Inject()(answerRowConverter: AnswerRowConverter, countryOptions: CountryOptions) {

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
    val prefix: String = "trustee.organisation"

    Seq(
      bound.enumQuestion[IndividualOrBusiness](
        TrusteeIndividualOrBusinessPage(index),
        "trusteeIndividualOrBusiness",
        TrusteeIndividualOrBusinessController.onPageLoad(index, draftId).url,
        "individualOrBusiness"
      ),
      bound.stringQuestion(NamePage(index), s"$prefix.name", NameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(UtrYesNoPage(index), s"$prefix.utrYesNo", UtrYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(UtrPage(index), s"$prefix.utr", UtrController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), s"$prefix.addressYesNo", AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUkYesNoPage(index), s"$prefix.addressUkYesNo", AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(UkAddressPage(index), s"$prefix.ukAddress", UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(InternationalAddressPage(index), s"$prefix.internationalAddress", InternationalAddressController.onPageLoad(index, draftId).url)
    ).flatten
  }

}
