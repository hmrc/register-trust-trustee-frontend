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

package pages.register

import models.UserAnswers
import models.core.pages.TrusteeOrLeadTrustee
import models.core.pages.TrusteeOrLeadTrustee._
import pages.QuestionPage
import pages.entitystatus.TrusteeStatus
import play.api.libs.json.JsPath
import sections.Trustees

import scala.util.Try

final case class TrusteeOrLeadTrusteePage(index: Int) extends QuestionPage[TrusteeOrLeadTrustee] with Cleanup {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "trusteeOrLeadTrustee"

  override def cleanup(value: Option[TrusteeOrLeadTrustee], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(LeadTrustee) =>
        userAnswers
          .remove(TrusteeIndividualOrBusinessPage(index))
          .flatMap(_.remove(TrusteeStatus(index)))
          .flatMap(ua => removeTrusteeIndividual(ua, index))
          .flatMap(ua => removeTrusteeBusiness(ua, index))

      case Some(Trustee) =>
        userAnswers
          .remove(TrusteeIndividualOrBusinessPage(index))
          .flatMap(_.remove(TrusteeStatus(index)))
          .flatMap(ua => removeLeadTrusteeIndividual(ua, index))
          .flatMap(ua => removeLeadTrusteeBusiness(ua, index))

      case _ =>
        super.cleanup(value, userAnswers)
    }

}
