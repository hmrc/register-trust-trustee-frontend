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

import models.core.pages.IndividualOrBusiness.Individual
import models.core.pages.{Address, FullName, UKAddress}
import models.registration.pages.DetailsChoice._
import models.registration.pages.{DetailsChoice, PassportOrIdCardDetails}
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

final case class LeadTrusteeIndividual(override val isLead: Boolean = true,
                                       name: FullName,
                                       dateOfBirth: LocalDate,
                                       nino: Option[String],
                                       passportOrIdCard: Option[PassportOrIdCardDetails],
                                       address: Address,
                                       telephoneNumber: String,
                                       email: Option[String]) extends Trustee {

  def hasUkAddress: Boolean = address.isInstanceOf[UKAddress]

}

object LeadTrusteeIndividual extends TrusteeReads {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[LeadTrusteeIndividual] = {

    val passportOrIdCardReads: Reads[Option[PassportOrIdCardDetails]] =
      ((__ \ "ninoYesNo").read[Boolean] and
        (__ \ "trusteeDetailsChoice").readNullable[DetailsChoice] and
        (__ \ "passportDetails").readNullable[PassportOrIdCardDetails] and
        (__ \ "idCard").readNullable[PassportOrIdCardDetails]
        ) ((_, _, _, _)).flatMap[Option[PassportOrIdCardDetails]] {
        case (false, Some(Passport), passport @ Some(_), None) =>
          Reads(_ => JsSuccess(passport))
        case (false, Some(IdCard), None, idCard @ Some(_)) =>
          Reads(_ => JsSuccess(idCard))
        case (true, None, None, None) =>
          Reads(_ => JsSuccess(None))
        case _  =>
          Reads(_ => JsError("individual lead trustee passport / ID card answers are in an invalid state"))
      }

    val leadTrusteeReads: Reads[LeadTrusteeIndividual] = (
      isLeadReads and
        (__ \ "name").read[FullName] and
        (__ \ "dateOfBirth").read[LocalDate] and
        yesNoReads[String]("ninoYesNo", "nino") and
        passportOrIdCardReads and
        addressReads and
        (__ \ "telephoneNumber").read[String] and
        yesNoReads[String]("emailAddressYesNo", "email")
      )(LeadTrusteeIndividual.apply _)

    (isLeadReads and
      (__ \ "individualOrBusiness").read[String]) ((_, _)).flatMap[(Boolean, String)] {
      case (isLead, individualOrBusiness) =>
        if (individualOrBusiness == Individual.toString && isLead) {
          Reads(_ => JsSuccess((isLead, individualOrBusiness)))
        } else {
          Reads(_ => JsError("lead trustee individual must not be a `business` or a normal trustee"))
        }
    }.andKeep(leadTrusteeReads)

  }
}