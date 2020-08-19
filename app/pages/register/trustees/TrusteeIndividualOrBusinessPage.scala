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

package pages.register.trustees

import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import models.core.pages.IndividualOrBusiness._
import pages.QuestionPage
import pages.register.leadtrustee.{organisation => ltorg}
import pages.register.trustees.{individual => tind, organisation => torg}
import play.api.libs.json.JsPath
import sections.Trustees

import scala.util.Try

final case class TrusteeIndividualOrBusinessPage(index : Int) extends QuestionPage[IndividualOrBusiness] {

  override def path: JsPath = Trustees.path \ index \ toString

  override def toString: String = "individualOrBusiness"

  override def cleanup(value: Option[IndividualOrBusiness], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Business) =>
        userAnswers.remove(tind.TrusteesDateOfBirthPage(index))
          .flatMap(_.remove(tind.TrusteeAUKCitizenPage(index)))
          .flatMap(_.remove(tind.NinoPage(index)))
          .flatMap(_.remove(tind.AddressUkYesNoPage(index)))
          .flatMap(_.remove(tind.UkAddressPage(index)))
          .flatMap(_.remove(tind.InternationalAddressPage(index)))
        // TODO - lead trustee individual pages

      case Some(Individual) =>
        userAnswers
          .remove(ltorg.UkRegisteredYesNoPage(index))
          .flatMap(_.remove(ltorg.NamePage(index)))
          .flatMap(_.remove(ltorg.UtrPage(index)))
          .flatMap(_.remove(ltorg.AddressUkYesNoPage(index)))
          .flatMap(_.remove(ltorg.UkAddressPage(index)))
          .flatMap(_.remove(ltorg.InternationalAddressPage(index)))
          .flatMap(_.remove(ltorg.EmailAddressYesNoPage(index)))
          .flatMap(_.remove(ltorg.EmailAddressPage(index)))
          .flatMap(_.remove(ltorg.TelephoneNumberPage(index)))

          .flatMap(_.remove(torg.NamePage(index)))
          .flatMap(_.remove(torg.UtrYesNoPage(index)))
          .flatMap(_.remove(torg.UtrPage(index)))
          .flatMap(_.remove(torg.AddressYesNoPage(index)))
          .flatMap(_.remove(torg.AddressUkYesNoPage(index)))
          .flatMap(_.remove(torg.UkAddressPage(index)))
          .flatMap(_.remove(torg.InternationalAddressPage(index)))

      case _ => super.cleanup(value, userAnswers)
    }
  }

}
