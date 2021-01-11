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
import controllers.register.leadtrustee.individual.routes._
import controllers.register.routes.TrusteeIndividualOrBusinessController
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.leadtrustee.individual._
import pages.register.trustees.individual.DateOfBirthPage
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter, countryOptions: CountryOptions) {

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
    val prefix: String = "leadTrustee.individual"

    Seq(
      bound.enumQuestion[IndividualOrBusiness](
        TrusteeIndividualOrBusinessPage(index),
        "leadTrusteeIndividualOrBusiness",
        TrusteeIndividualOrBusinessController.onPageLoad(index, draftId).url,
        "individualOrBusiness"
      ),
      bound.nameQuestion(TrusteesNamePage(index), s"$prefix.name", NameController.onPageLoad(index, draftId).url),
      bound.dateQuestion(DateOfBirthPage(index), s"$prefix.dateOfBirth", DateOfBirthController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(TrusteeNinoYesNoPage(index), s"$prefix.ninoYesNo", NinoYesNoController.onPageLoad(index, draftId).url),
      bound.ninoQuestion(TrusteesNinoPage(index), s"$prefix.nino", NinoController.onPageLoad(index, draftId).url),
      bound.enumQuestion(TrusteeDetailsChoicePage(index),
        s"$prefix.trusteeDetailsChoice", TrusteeDetailsChoiceController.onPageLoad(index, draftId).url,
        s"$prefix.trusteeDetailsChoice"
      ),
      bound.passportDetailsQuestion(IDCardDetailsPage(index), s"$prefix.iDCardDetails", IDCardDetailsController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(PassportDetailsPage(index), s"$prefix.passportDetails", PassportDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUkYesNoPage(index), s"$prefix.liveInTheUkYesNo", LiveInTheUKYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(UkAddressPage(index), s"$prefix.ukAddress", UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(InternationalAddressPage(index), s"$prefix.internationalAddress", InternationalAddressController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(EmailAddressYesNoPage(index), s"$prefix.emailAddressYesNo", EmailAddressYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(EmailAddressPage(index), s"$prefix.email", EmailAddressController.onPageLoad(index, draftId).url),
      bound.stringQuestion(TelephoneNumberPage(index), s"$prefix.telephoneNumber", TelephoneNumberController.onPageLoad(index, draftId).url)
    ).flatten
  }

}
