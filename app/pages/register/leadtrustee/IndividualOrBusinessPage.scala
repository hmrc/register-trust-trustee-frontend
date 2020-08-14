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

package pages.register.leadtrustee

import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import models.core.pages.IndividualOrBusiness._
import pages.QuestionPage
import pages.register.leadtrustee.organisation._
import play.api.libs.json.JsPath
import sections.LeadTrustee

import scala.util.{Success, Try}

case object IndividualOrBusinessPage extends QuestionPage[IndividualOrBusiness] {

  override def path: JsPath = LeadTrustee.path \ toString

  override def toString: String = "individualOrBusiness"

  override def cleanup(value: Option[IndividualOrBusiness], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Individual) => userAnswers
        .remove(UkRegisteredYesNoPage)
        .flatMap(_.remove(NamePage))
        .flatMap(_.remove(UtrPage))
        .flatMap(_.remove(AddressUkYesNoPage))
        .flatMap(_.remove(UkAddressPage))
        .flatMap(_.remove(InternationalAddressPage))
        .flatMap(_.remove(EmailAddressYesNoPage))
        .flatMap(_.remove(EmailAddressPage))
        .flatMap(_.remove(TelephoneNumberPage))

      case Some(Business) =>
        Success(userAnswers)

      case _ =>
        super.cleanup(value, userAnswers)
    }
  }

}
