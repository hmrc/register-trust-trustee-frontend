/*
 * Copyright 2023 HM Revenue & Customs
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

import javax.inject.Inject
import models.UserAnswers
import play.api.i18n.Messages
import viewmodels.AnswerSection

class PrintHelpers @Inject()(trusteeIndividualPrintHelper: TrusteeIndividualPrintHelper,
                             trusteeBusinessPrintHelper: TrusteeOrganisationPrintHelper,
                             leadTrusteeIndividualPrintHelper: LeadTrusteeIndividualPrintHelper,
                             leadTrusteeBusinessPrintHelper: LeadTrusteeOrganisationPrintHelper
                            ) {

  def trusteeIndividual(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                       (implicit messages: Messages): AnswerSection =
    trusteeIndividualPrintHelper.checkDetailsSection(userAnswers, name, index, draftId)

  def trusteeOrganisation(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                         (implicit messages: Messages): AnswerSection =
    trusteeBusinessPrintHelper.checkDetailsSection(userAnswers, name, index, draftId)

  def leadTrusteeIndividual(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                           (implicit messages: Messages): AnswerSection =
    leadTrusteeIndividualPrintHelper.checkDetailsSection(userAnswers, name, index, draftId)

  def leadTrusteeOrganisation(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                             (implicit messages: Messages): AnswerSection =
    leadTrusteeBusinessPrintHelper.checkDetailsSection(userAnswers, name, index, draftId)

}
