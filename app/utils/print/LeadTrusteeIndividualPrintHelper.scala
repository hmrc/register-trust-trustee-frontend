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
import controllers.register.leadtrustee.individual.mld5.{routes => mld5Rts}
import controllers.register.leadtrustee.individual.{routes => rts}
import controllers.register.{routes => regRts}
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.leadtrustee.individual._
import pages.register.trustees.individual.DateOfBirthPage
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class LeadTrusteeIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def checkDetailsSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                         (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      rows = answers(userAnswers, name, index, draftId)
    )
  }

  private def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                     (implicit messages: Messages): Seq[AnswerRow] = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)
    val prefix: String = "leadTrustee.individual"
    val isLeadTrusteeMatched = userAnswers.isLeadTrusteeMatched(index)

    Seq(
      bound.enumQuestion[IndividualOrBusiness](
        TrusteeIndividualOrBusinessPage(index),
        "leadTrustee.individualOrBusiness",
        regRts.TrusteeIndividualOrBusinessController.onPageLoad(index, draftId).url,
        "individualOrBusiness",
        canEdit = !isLeadTrusteeMatched
      ),
      bound.nameQuestion(TrusteesNamePage(index), s"$prefix.name", rts.NameController.onPageLoad(index, draftId).url, canEdit = !isLeadTrusteeMatched),
      bound.dateQuestion(DateOfBirthPage(index), s"$prefix.dateOfBirth", rts.DateOfBirthController.onPageLoad(index, draftId).url, canEdit = !isLeadTrusteeMatched),
      bound.yesNoQuestion(mld5.CountryOfNationalityInTheUkYesNoPage(index), s"$prefix.5mld.countryOfNationalityInTheUkYesNo", mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(mld5.CountryOfNationalityInTheUkYesNoPage(index), mld5.CountryOfNationalityPage(index), s"$prefix.5mld.countryOfNationality", mld5Rts.CountryOfNationalityController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(TrusteeNinoYesNoPage(index), s"$prefix.ninoYesNo", rts.NinoYesNoController.onPageLoad(index, draftId).url, canEdit = !isLeadTrusteeMatched),
      bound.ninoQuestion(TrusteesNinoPage(index), s"$prefix.nino", rts.NinoController.onPageLoad(index, draftId).url, canEdit = !isLeadTrusteeMatched),
      bound.enumQuestion(
        TrusteeDetailsChoicePage(index),
        s"$prefix.trusteeDetailsChoice",
        rts.TrusteeDetailsChoiceController.onPageLoad(index, draftId).url,
        s"$prefix.trusteeDetailsChoice"
      ),
      bound.passportDetailsQuestion(IDCardDetailsPage(index), s"$prefix.iDCardDetails", rts.IDCardDetailsController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(PassportDetailsPage(index), s"$prefix.passportDetails", rts.PassportDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(mld5.CountryOfResidenceInTheUkYesNoPage(index), s"$prefix.5mld.countryOfResidenceInTheUkYesNo", mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(mld5.CountryOfResidenceInTheUkYesNoPage(index), mld5.CountryOfResidencePage(index), s"$prefix.5mld.countryOfResidence", mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUkYesNoPage(index), s"$prefix.liveInTheUkYesNo", rts.LiveInTheUKYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(UkAddressPage(index), s"$prefix.ukAddress", rts.UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(InternationalAddressPage(index), s"$prefix.internationalAddress", rts.InternationalAddressController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(EmailAddressYesNoPage(index), s"$prefix.emailAddressYesNo", rts.EmailAddressYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(EmailAddressPage(index), s"$prefix.email", rts.EmailAddressController.onPageLoad(index, draftId).url),
      bound.stringQuestion(TelephoneNumberPage(index), s"$prefix.telephoneNumber", rts.TelephoneNumberController.onPageLoad(index, draftId).url)
    ).flatten
  }

}
