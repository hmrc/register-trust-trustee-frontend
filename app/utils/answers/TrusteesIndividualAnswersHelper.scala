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

package utils.answers

import models.UserAnswers
import pages.register.trustees._
import pages.register.trustees.individual._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

trait TrusteesIndividualAnswersHelper {

  val countryOptions: CountryOptions
  val userAnswers: UserAnswers
  val draftId: String
  val canEdit: Boolean

  implicit val  messages: Messages

  def trusteeFullName(index: Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(NamePage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(controllers.register.trustees.individual.routes.NameController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeLiveInTheUK(index: Int): Option[AnswerRow] = userAnswers.get(AddressUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trusteeLiveInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.individual.routes.LiveInTheUKYesNoController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(UkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteesUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.trustees.individual.routes.UkAddressController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteesDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.trustees.individual.routes.DateOfBirthController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def telephoneNumber(index: Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.individual.routes.TelephoneNumberController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeNinoYesNo(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeAUKCitizenPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.individual.routes.NinoYesNoController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesNino(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesNinoPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(controllers.register.trustees.individual.routes.NinoController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteePassportDetailsYesNo(index: Int): Option[AnswerRow] = userAnswers.get(PassportDetailsYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.individual.routes.PassportDetailsYesNoController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesPassportDetails(index: Int): Option[AnswerRow] = userAnswers.get(PassportDetailsPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        passportOrIDCard(x, countryOptions),
        Some(controllers.register.trustees.individual.routes.PassportDetailsController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeIDCardDetailsYesNo(index: Int): Option[AnswerRow] = userAnswers.get(IDCardDetailsYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.individual.routes.IDCardDetailsYesNoController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesIDCardDetails(index: Int): Option[AnswerRow] = userAnswers.get(IDCardDetailsPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        passportOrIDCard(x, countryOptions),
        Some(controllers.register.trustees.individual.routes.IDCardDetailsController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

}
