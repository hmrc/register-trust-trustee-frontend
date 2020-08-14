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

import javax.inject.Inject
import mapping.reads._
import models.UserAnswers
import pages.register.trustees._
import pages.register.trustees.individual._
import pages.register.trustees.organisation._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

trait TrusteesOrgAnswersHelper {

  val countryOptions: CountryOptions
  val userAnswers: UserAnswers
  val draftId: String
  val canEdit: Boolean

  implicit val messages: Messages

  def orgTelephoneNumber(index: Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeOrgTelephoneNumberController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeOrgName(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeOrgNamePage(index)) map {
    x =>
      AnswerRow(
        "trusteeBusinessName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeBusinessNameController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeUtrYesNo(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeUtrYesNoPage(index)) map {
    x =>
      AnswerRow(
        "leadTrusteeUtrYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeUtrYesNoController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeUtr(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesUtrPage(index)) map {
    x =>
      AnswerRow(
        "trusteeUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeUtrController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def orgAddressInTheUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeOrgAddressUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgAddressUkYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeOrgAddressUkYesNoController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesOrgUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeOrgAddressUkPage(index)) map {
    x =>
      AnswerRow(
        "trusteesOrgUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.trustees.organisation.routes.TrusteesOrgUkAddressController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeOrgInternationalAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeOrgAddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgAddressInternational.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.trustees.organisation.routes.TrusteeOrgAddressInternationalController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

}
