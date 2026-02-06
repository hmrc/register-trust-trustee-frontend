/*
 * Copyright 2026 HM Revenue & Customs
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
import controllers.register.trustees.individual.mld5.{routes => mld5Rts}
import controllers.register.trustees.individual.routes._
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.trustees.individual._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class TrusteeIndividualPrintHelper @Inject() (answerRowConverter: AnswerRowConverter) {

  def checkDetailsSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)(implicit
    messages: Messages
  ): AnswerSection =
    AnswerSection(
      rows = answers(userAnswers, name, index, draftId)
    )

  private def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)(implicit
    messages: Messages
  ): Seq[AnswerRow] = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)
    val prefix: String                  = "trustee.individual"

    Seq(
      bound.enumQuestion[IndividualOrBusiness](
        TrusteeIndividualOrBusinessPage(index),
        "trustee.individualOrBusiness",
        TrusteeIndividualOrBusinessController.onPageLoad(index, draftId).url,
        "individualOrBusiness"
      ),
      bound.nameQuestion(NamePage(index), s"$prefix.name", NameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(
        DateOfBirthYesNoPage(index),
        s"$prefix.dateOfBirthYesNo",
        DateOfBirthYesNoController.onPageLoad(index, draftId).url
      ),
      bound.dateQuestion(
        DateOfBirthPage(index),
        s"$prefix.dateOfBirth",
        DateOfBirthController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        mld5.CountryOfNationalityYesNoPage(index),
        s"$prefix.5mld.countryOfNationalityYesNo",
        mld5Rts.CountryOfNationalityYesNoController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        mld5.CountryOfNationalityInTheUkYesNoPage(index),
        s"$prefix.5mld.countryOfNationalityInTheUkYesNo",
        mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId).url
      ),
      bound.countryQuestion(
        mld5.CountryOfNationalityInTheUkYesNoPage(index),
        mld5.CountryOfNationalityPage(index),
        s"$prefix.5mld.countryOfNationality",
        mld5Rts.CountryOfNationalityController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        NinoYesNoPage(index),
        s"$prefix.ninoYesNo",
        NinoYesNoController.onPageLoad(index, draftId).url
      ),
      bound.ninoQuestion(NinoPage(index), s"$prefix.nino", NinoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(
        mld5.CountryOfResidenceYesNoPage(index),
        s"$prefix.5mld.countryOfResidenceYesNo",
        mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        mld5.CountryOfResidenceInTheUkYesNoPage(index),
        s"$prefix.5mld.countryOfResidenceInTheUkYesNo",
        mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url
      ),
      bound.countryQuestion(
        mld5.CountryOfResidenceInTheUkYesNoPage(index),
        mld5.CountryOfResidencePage(index),
        s"$prefix.5mld.countryOfResidence",
        mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        AddressYesNoPage(index),
        s"$prefix.addressYesNo",
        AddressYesNoController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        AddressUkYesNoPage(index),
        s"$prefix.addressUkYesNo",
        AddressUkYesNoController.onPageLoad(index, draftId).url
      ),
      bound.addressQuestion(
        UkAddressPage(index),
        s"$prefix.ukAddress",
        UkAddressController.onPageLoad(index, draftId).url
      ),
      bound.addressQuestion(
        InternationalAddressPage(index),
        s"$prefix.internationalAddress",
        InternationalAddressController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        PassportDetailsYesNoPage(index),
        s"$prefix.passportDetailsYesNo",
        PassportDetailsYesNoController.onPageLoad(index, draftId).url
      ),
      bound.passportDetailsQuestion(
        PassportDetailsPage(index),
        s"$prefix.passportDetails",
        PassportDetailsController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        IDCardDetailsYesNoPage(index),
        s"$prefix.idCardDetailsYesNo",
        IDCardDetailsYesNoController.onPageLoad(index, draftId).url
      ),
      bound.passportDetailsQuestion(
        IDCardDetailsPage(index),
        s"$prefix.idCardDetails",
        IDCardDetailsController.onPageLoad(index, draftId).url
      ),
      bound.enumQuestion(
        mld5.MentalCapacityYesNoPage(index),
        s"$prefix.5mld.mentalCapacityYesNo",
        mld5Rts.MentalCapacityYesNoController.onPageLoad(index, draftId).url,
        "site"
      )
    ).flatten

  }

}
