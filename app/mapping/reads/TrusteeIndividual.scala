/*
 * Copyright 2022 HM Revenue & Customs
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

import mapping.registration.IdentificationMapper.{buildAddress, buildPassport}
import models.{IdentificationType, YesNoDontKnow}
import models.core.pages.IndividualOrBusiness.Individual
import models.core.pages.{Address, FullName, IndividualOrBusiness}
import models.registration.pages.PassportOrIdCardDetails
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate

final case class TrusteeIndividual(override val isLead: Boolean,
                                   name: FullName,
                                   dateOfBirth: Option[LocalDate],
                                   nino: Option[String],
                                   address: Option[Address],
                                   passportOrIdCard: Option[PassportOrIdCardDetails],
                                   countryOfResidence: Option[String],
                                   nationality: Option[String],
                                   mentalCapacityYesNo: Option[YesNoDontKnow]) extends Trustee {

  val identification: Option[IdentificationType] = (nino, passportOrIdCard, address) match {
    case (None, None, None) => None
    case _ => Some(IdentificationType(nino, buildPassport(passportOrIdCard), buildAddress(address)))
  }
}

object TrusteeIndividual extends TrusteeReads[TrusteeIndividual] {

  override val isLeadTrustee: Boolean = false
  override val individualOrBusiness: IndividualOrBusiness = Individual

  override def trusteeReads: Reads[TrusteeIndividual] = {

    val passportOrIdCardReads: Reads[Option[PassportOrIdCardDetails]] = (
      (__ \ "passportDetailsYesNo").readNullable[Boolean] and
        (__ \ "passportDetails").readNullable[PassportOrIdCardDetails] and
        (__ \ "idCardDetailsYesNo").readNullable[Boolean] and
        (__ \ "idCard").readNullable[PassportOrIdCardDetails]
      )((_, _, _, _)).flatMap[Option[PassportOrIdCardDetails]] {
      case (Some(true), passport @ Some(_), None, None) =>
        Reads(_ => JsSuccess(passport))
      case (Some(false), None, Some(true), idCard @ Some(_)) =>
        Reads(_ => JsSuccess(idCard))
      case (Some(false), None, Some(false), None) | (None, None, None, None) =>
        Reads(_ => JsSuccess(None))
      case _ =>
        Reads(_ => JsError("individual trustee passport / ID card answers are in an invalid state"))
    }

    (
      Reads(_ => JsSuccess(isLeadTrustee)) and
        (__ \ "name").read[FullName] and
        yesNoReads[LocalDate]("dateOfBirthYesNo", "dateOfBirth") and
        yesNoReads[String]("ninoYesNo", "nino") and
        optionalAddressReads("ninoYesNo") and
        passportOrIdCardReads and
        (__ \ "countryOfResidence").readNullable[String] and
        (__ \ "nationality").readNullable[String] and
        readMentalCapacity
      )(TrusteeIndividual.apply _)

  }

}
