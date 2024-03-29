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

package pages.register.leadtrustee.individual

import models.UserAnswers
import models.registration.pages.DetailsChoice
import models.registration.pages.DetailsChoice._
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.Trustees

import scala.util.Try

final case class TrusteeDetailsChoicePage(index : Int) extends QuestionPage[DetailsChoice] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "trusteeDetailsChoice"

  override def cleanup(value: Option[DetailsChoice], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Passport) =>
        userAnswers.remove(IDCardDetailsPage(index))
      case Some(IdCard) =>
        userAnswers.remove(PassportDetailsPage(index))
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }
}
