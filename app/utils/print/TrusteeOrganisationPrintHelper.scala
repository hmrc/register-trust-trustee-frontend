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

package utils.print

import com.google.inject.Inject
import controllers.register.routes.TrusteeIndividualOrBusinessController
import controllers.register.trustees.organisation.mld5.routes._
import controllers.register.trustees.organisation.routes._
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.trustees.organisation._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class TrusteeOrganisationPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def checkDetailsSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                         (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      rows = answers(userAnswers, name, index, draftId)
    )
  }

  private def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                     (implicit messages: Messages): Seq[AnswerRow] = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)
    val prefix: String = "trustee.organisation"

    Seq(
      bound.enumQuestion[IndividualOrBusiness](
        TrusteeIndividualOrBusinessPage(index),
        "trustee.individualOrBusiness",
        TrusteeIndividualOrBusinessController.onPageLoad(index, draftId).url,
        "individualOrBusiness"
      ),
      bound.stringQuestion(NamePage(index), s"$prefix.name", NameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(UtrYesNoPage(index), s"$prefix.utrYesNo", UtrYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(UtrPage(index), s"$prefix.utr", UtrController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(mld5.CountryOfResidenceYesNoPage(index), s"$prefix.5mld.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(mld5.CountryOfResidenceInTheUkYesNoPage(index), s"$prefix.5mld.countryOfResidenceInTheUkYesNo", CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(mld5.CountryOfResidenceInTheUkYesNoPage(index), mld5.CountryOfResidencePage(index),
        s"$prefix.5mld.countryOfResidence", CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), s"$prefix.addressYesNo", AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUkYesNoPage(index), s"$prefix.addressUkYesNo", AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(UkAddressPage(index), s"$prefix.ukAddress", UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(InternationalAddressPage(index), s"$prefix.internationalAddress", InternationalAddressController.onPageLoad(index, draftId).url)
    ).flatten
  }

}
