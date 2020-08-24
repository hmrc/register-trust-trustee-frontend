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
import pages.register.trustees.organisation._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

trait TrusteesOrgAnswersHelper {

  val countryOptions: CountryOptions
  val userAnswers: UserAnswers
  val draftId: String
  val canEdit: Boolean

  implicit val messages: Messages

  def trusteeOrgName(index: Int): Option[AnswerRow] = userAnswers.get(NamePage(index)) map {
    x =>
      AnswerRow(
        "trusteeBusinessName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.NameController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeUtrYesNo(index: Int): Option[AnswerRow] = userAnswers.get(UtrYesNoPage(index)) map {
    x =>
      AnswerRow(
        "leadTrusteeUtrYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.organisation.routes.UtrYesNoController.onPageLoad(index, draftId).url),
        canEdit = canEdit
      )
  }

  def trusteeUtr(index: Int): Option[AnswerRow] = userAnswers.get(UtrPage(index)) map {
    x =>
      AnswerRow(
        "trusteeUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.UtrController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def orgAddressInTheUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(AddressUkYesNoPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgAddressUkYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.organisation.routes.AddressUkYesNoController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesOrgUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(UkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteesOrgUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.trustees.organisation.routes.UkAddressController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeOrgInternationalAddress(index: Int): Option[AnswerRow] = userAnswers.get(InternationalAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteeOrgAddressInternational.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.register.trustees.organisation.routes.InternationalAddressController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }

}
