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
import controllers.register.leadtrustee.individual.routes._
import controllers.register.trustees.routes._
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import pages.register.leadtrustee.individual._
import pages.register.trustees.TrusteeIndividualOrBusinessPage
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

    Seq(
      bound.enumQuestion[IndividualOrBusiness](
        TrusteeIndividualOrBusinessPage(index),
        "leadTrusteeIndividualOrBusiness",
        TrusteeIndividualOrBusinessController.onPageLoad(index, draftId).url,
        "individualOrBusiness"
      ),
      bound.nameQuestion(TrusteesNamePage(index),
        "leadTrustee.individual.name", NameController.onPageLoad(index, draftId).url),
      bound.dateQuestion(DateOfBirthPage(index),
        "leadTrustee.individual.dateOfBirth", DateOfBirthController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(TrusteeNinoYesNoPage(index),
        "leadTrustee.individual.ninoYesNo", NinoYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(TrusteesNinoPage(index),
        "leadTrustee.individual.nino", NinoController.onPageLoad(index, draftId).url),
      bound.enumQuestion(TrusteeDetailsChoicePage(index),
        "leadTrustee.individual.trusteeDetailsChoice", TrusteeDetailsChoiceController.onPageLoad(index, draftId).url,
        "passport"),
      bound.passportDetailsQuestion(IDCardDetailsPage(index),
        "leadTrustee.individual.iDCardDetails", IDCardDetailsController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(PassportDetailsPage(index),
        "leadTrustee.individual.passportDetails", PassportDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(TrusteeAUKCitizenPage(index),
        "leadTrustee.individual.liveInTheUkYesNo", LiveInTheUKYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(UkAddressPage(index),
        "leadTrustee.individual.ukAddress", UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(InternationalAddressPage(index),
        "leadTrustee.individual.internationalAddress", InternationalAddressController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(EmailAddressYesNoPage(index),
        "leadTrustee.individual.emailAddressYesNo", EmailAddressYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(EmailAddressPage(index),
        "leadTrustee.individual.email", EmailAddressController.onPageLoad(index, draftId).url),
      bound.stringQuestion(TelephoneNumberPage(index),
        "leadTrustee.individual.telephoneNumber", TelephoneNumberController.onPageLoad(index, draftId).url)
    ).flatten
  }

}
