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
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(val countryOptions: CountryOptions)
                                      (val userAnswers: UserAnswers,
                                       val draftId: String,
                                       val canEdit: Boolean)
                                      (implicit val messages: Messages)
  extends TrusteesIndividualAnswersHelper with TrusteesOrgAnswersHelper {

  def trustees: Option[Seq[AnswerSection]] = {
    for {
      trustees <- userAnswers.get(Trustees)
      indexed = trustees.zipWithIndex
    } yield indexed.map {
      case (trustee, index) =>

        val trusteeIndividualOrBusinessMessagePrefix = if (trustee.isLead) "leadTrusteeIndividualOrBusiness" else "trusteeIndividualOrBusiness"

        val questions = trustee match {
          case _: TrusteeIndividual | _: LeadTrusteeIndividual =>
            Seq(
              trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
              trusteeFullName(index),
              trusteesDateOfBirth(index),
              trusteeNinoYesNo(index),
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

  def isThisLeadTrustee(index: Int): Option[AnswerRow] = userAnswers.get(IsThisLeadTrusteePage(index)) map {
    x =>
      AnswerRow(
        "isThisLeadTrustee.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(index, draftId).url),
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

}
