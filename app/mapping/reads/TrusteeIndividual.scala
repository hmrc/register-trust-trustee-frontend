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

package mapping.reads

import java.time.LocalDate

import models.core.pages.{Address, FullName, IndividualOrBusiness}
import models.registration.pages.PassportOrIdCardDetails
import play.api.libs.json._

final case class TrusteeIndividual(override val isLead: Boolean,
                                   name: FullName,
                                   dateOfBirth: Option[LocalDate],
                                   nino: Option[String],
                                   address: Option[Address],
                                   passport: Option[PassportOrIdCardDetails],
                                   idCard: Option[PassportOrIdCardDetails]) extends Trustee {

  def passportOrId: Option[PassportOrIdCardDetails] = if (passport.isDefined) passport else idCard

}

object TrusteeIndividual extends TrusteeReads {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[TrusteeIndividual] = {

    val idCardDetailsReads: Reads[Option[PassportOrIdCardDetails]] =
      ((__ \ "passportDetailsYesNo").readNullable[Boolean] and
        (__ \ "idCardDetailsYesNo").readNullable[Boolean] and
        (__ \ "idCard").readNullable[PassportOrIdCardDetails]) ((_, _, _)).flatMap[Option[PassportOrIdCardDetails]] {
        case (Some(false), Some(true), idCardDetails @ Some(_)) => Reads(_ => JsSuccess(idCardDetails))
        case (Some(true), None, None) | (Some(false), Some(false), None) | (None, None, None) => Reads(_ => JsSuccess(None))
        case _ => Reads(_ => JsError("ID card answers are in an invalid state"))
      }

    val trusteeReads: Reads[TrusteeIndividual] = {
      (
        (__ \ "name").read[FullName] and
          yesNoReads[LocalDate]("dateOfBirthYesNo", "dateOfBirth") and
          yesNoReads[String]("ninoYesNo", "nino") and
          optionalAddressReads("ninoYesNo") and
          yesNoReads[PassportOrIdCardDetails]("passportDetailsYesNo", "passportDetails") and
          idCardDetailsReads
        )((name, dateOfBirth, nino, address, passportDetails, idCardDetails) =>
        TrusteeIndividual(isLead = false, name, dateOfBirth, nino, address, passportDetails, idCardDetails))
    }

    (isLeadReads and
      (__ \ "individualOrBusiness").read[IndividualOrBusiness]) ((_, _)).flatMap[(Boolean, IndividualOrBusiness)] {
      case (isLead, individualOrBusiness) =>
        if (individualOrBusiness == IndividualOrBusiness.Individual && !isLead) {
          Reads(_ => JsSuccess((isLead, individualOrBusiness)))
        } else {
          Reads(_ => JsError("trustee individual must not be a `business` or a `lead`"))
        }
    }.andKeep(trusteeReads)

  }
}
