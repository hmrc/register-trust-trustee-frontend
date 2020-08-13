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

package utils

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

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)
                                      (userAnswers: UserAnswers,
                                       draftId: String,
                                       canEdit: Boolean)
                                      (implicit messages: Messages) {


  def trustees: Option[Seq[AnswerSection]] = {
    for {
      trustees <- userAnswers.get(Trustees)
      indexed = trustees.zipWithIndex
    } yield indexed.map {
      case (trustee, index) =>

        val trusteeIndividualOrBusinessMessagePrefix = if (trustee.isLead) "leadTrusteeIndividualOrBusiness" else "trusteeIndividualOrBusiness"
        val trusteeFullNameMessagePrefix = if (trustee.isLead) "leadTrusteesName" else "trusteesName"
        val questions = trustee match {
          case _: TrusteeIndividual | _: LeadTrusteeIndividual =>
            Seq(
              trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
              trusteeFullName(index, trusteeFullNameMessagePrefix),
              trusteesDateOfBirth(index),
              trusteeAUKCitizen(index),
              trusteesNino(index),
              trusteeLiveInTheUK(index),
              trusteesUkAddress(index),
              //TODO - international address, passport/ID card details etc.
              telephoneNumber(index)
            ).flatten
          case _: TrusteeOrganisation | _: LeadTrusteeOrganisation =>
            Seq(
              trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
              trusteeUtrYesNo(index),
              trusteeOrgName(index),
              trusteeUtr(index),
              orgAddressInTheUkYesNo(index),
              trusteesOrgUkAddress(index),
              trusteeOrgInternationalAddress(index)/*,
              orgTelephoneNumber(index)*/
            ).flatten
        }


        val sectionKey = if (index == 0) Some(messages("answerPage.section.trustees.heading")) else None

        AnswerSection(
          headingKey = Some(Messages("answerPage.section.trustee.subheading") + " " + (index + 1)),
          rows = questions,
          sectionKey = sectionKey
        )
    }
  }

  def trusteesNino(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesNinoPage(index)) map {
    x =>
      AnswerRow(
        "trusteesNino.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(controllers.register.trustees.individual.routes.TrusteesNinoController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeLiveInTheUK(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeAddressInTheUKPage(index)) map {
    x =>
      AnswerRow(
        "trusteeLiveInTheUK.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.individual.routes.TrusteeLiveInTheUKController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesUkAddress(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesUkAddressPage(index)) map {
    x =>
      AnswerRow(
        "trusteesUkAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.register.trustees.individual.routes.TrusteesUkAddressController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteesDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(TrusteesDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "trusteesDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.register.trustees.individual.routes.TrusteesDateOfBirthController.onPageLoad(index, draftId).url),
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

  /*def orgTelephoneNumber(index: Int): Option[AnswerRow] = userAnswers.get(TelephoneNumberPage(index)) map {
    x =>
      AnswerRow(
        "telephoneNumber.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(controllers.register.trustees.organisation.routes.TrusteeOrgTelephoneNumberController.onPageLoad(index, draftId).url),
        orgName(index, userAnswers),
        canEdit = canEdit
      )
  }*/

  def trusteeAUKCitizen(index: Int): Option[AnswerRow] = userAnswers.get(TrusteeAUKCitizenPage(index)) map {
    x =>
      AnswerRow(
        "trusteeAUKCitizen.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.individual.routes.TrusteeAUKCitizenController.onPageLoad(index, draftId).url),
        trusteeName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def trusteeFullName(index: Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteesNamePage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(controllers.register.trustees.individual.routes.TrusteesNameController.onPageLoad(index, draftId).url),
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

  def trusteeIndividualOrBusiness(index: Int, messagePrefix: String): Option[AnswerRow] = userAnswers.get(TrusteeIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        s"$messagePrefix.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"individualOrBusiness.$x")),
        Some(controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, draftId).url),
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

  def isThisLeadTrustee(index: Int): Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage(index)) map {
    x =>
      AnswerRow(
        "isThisLeadTrustee.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(index, draftId).url),
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
