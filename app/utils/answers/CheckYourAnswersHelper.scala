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

package utils.answers

import mapping.reads._
import models.UserAnswers
import play.api.i18n.Messages
import utils.print._
import viewmodels.AnswerSection

import javax.inject.Inject

class CheckYourAnswersHelper @Inject()(printHelpers: PrintHelpers)
                                      (val userAnswers: UserAnswers,
                                       val draftId: String,
                                       val canEdit: Boolean)
                                      (implicit val messages: Messages) {

  def trustees: Option[Seq[AnswerSection]] = {
    for {
      trustees <- userAnswers.get(Trustees)
      indexed = trustees.zipWithIndex
    } yield indexed.map {
      case (trustee, index) =>

        val questions = trustee match {
          case x: TrusteeIndividual =>
            printHelpers.trusteeIndividual(userAnswers, x.name.toString, index, draftId)
          case x: TrusteeOrganisation =>
            printHelpers.trusteeOrganisation(userAnswers, x.name, index, draftId)
          case x: LeadTrusteeIndividual =>
            printHelpers.leadTrusteeIndividual(userAnswers, x.name.toString, index, draftId)
          case x: LeadTrusteeOrganisation =>
            printHelpers.leadTrusteeOrganisation(userAnswers, x.name, index, draftId)
        }

        val sectionKey = if (index == 0) Some("answersPage.section.trustees.heading") else None

        AnswerSection(
          headingKey = Some("answersPage.section.trustee.subheading"),
          rows = questions.rows,
          sectionKey = sectionKey,
          headingArgs = Seq(index + 1)
        )
    }
  }
}
