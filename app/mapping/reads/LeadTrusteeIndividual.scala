/*
 * Copyright 2025 HM Revenue & Customs
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
import models.IdentificationType
import models.core.pages.IndividualOrBusiness.Individual
import models.core.pages.{Address, FullName, IndividualOrBusiness}
import models.registration.pages.DetailsChoice._
import models.registration.pages.{DetailsChoice, PassportOrIdCardDetails}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

import java.time.LocalDate

final case class LeadTrusteeIndividual(
  override val isLead: Boolean,
  name: FullName,
  dateOfBirth: LocalDate,
  nino: Option[String],
  passportOrIdCard: Option[PassportOrIdCardDetails],
  address: Address,
  telephoneNumber: String,
  email: Option[String],
  countryOfResidence: Option[String],
  nationality: Option[String]
) extends LeadTrustee {

  val identification: IdentificationType = nino match {
    case Some(_) => IdentificationType(nino, None, None)
    case _       => IdentificationType(None, buildPassport(passportOrIdCard), Some(buildAddress(address)))
  }

}

object LeadTrusteeIndividual extends TrusteeReads[LeadTrusteeIndividual] {

  override val isLeadTrustee: Boolean                     = true
  override val individualOrBusiness: IndividualOrBusiness = Individual

  override def trusteeReads: Reads[LeadTrusteeIndividual] = {

    val passportOrIdCardReads: Reads[Option[PassportOrIdCardDetails]] = (
      (__ \ "ninoYesNo").read[Boolean] and
        (__ \ "trusteeDetailsChoice").readNullable[DetailsChoice] and
        (__ \ "passportDetails").readNullable[PassportOrIdCardDetails] and
        (__ \ "idCard").readNullable[PassportOrIdCardDetails]
    )((_, _, _, _)).flatMap[Option[PassportOrIdCardDetails]] {
      case (false, Some(Passport), passport @ Some(_), None) =>
        Reads(_ => JsSuccess(passport))
      case (false, Some(IdCard), None, idCard @ Some(_))     =>
        Reads(_ => JsSuccess(idCard))
      case (true, None, None, None)                          =>
        Reads(_ => JsSuccess(None))
      case _                                                 =>
        Reads(_ => JsError("individual lead trustee passport / ID card answers are in an invalid state"))
    }

    (
      Reads(_ => JsSuccess(isLeadTrustee)) and
        (__ \ "name").read[FullName] and
        (__ \ "dateOfBirth").read[LocalDate] and
        yesNoReads[String]("ninoYesNo", "nino") and
        passportOrIdCardReads and
        addressReads and
        (__ \ "telephoneNumber").read[String] and
        yesNoReads[String]("emailAddressYesNo", "email") and
        (__ \ "countryOfResidence").readNullable[String] and
        (__ \ "nationality").readNullable[String]
    )(LeadTrusteeIndividual.apply _)

  }

}
