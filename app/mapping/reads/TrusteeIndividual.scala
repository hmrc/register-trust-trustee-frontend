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

import models.core.pages.{Address, FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import play.api.libs.json._

final case class TrusteeIndividual(override val isLead : Boolean,
                                   name: FullName,
                                   dateOfBirth: Option[LocalDate],
                                   nino: Option[String],
                                   address: Option[Address],
                                   passport: Option[PassportOrIdCardDetails],
                                   idCard: Option[PassportOrIdCardDetails]) extends Trustee {

  def passportOrId: Option[PassportOrIdCardDetails] = if (passport.isDefined) passport else idCard

}

object TrusteeIndividual {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[TrusteeIndividual] = {

    println("???????")

    val addressReads: Reads[Option[Address]] =
      (__ \ 'ukAddress).read[UKAddress].map(Some(_: Address)) or
        (__ \ 'internationalAddress).read[InternationalAddress].map(Some(_: Address)) or
        Reads(_ => JsSuccess(None))

    val trusteeReads: Reads[TrusteeIndividual] = {
      (
        (__ \ "name").read[FullName] and
          (__ \ "dateOfBirth").readNullable[LocalDate] and
          (__ \ "nino").readNullable[String] and
          addressReads and
          (__ \ "passportDetails").readNullable[PassportOrIdCardDetails] and
          (__ \ "idCard").readNullable[PassportOrIdCardDetails]
        )((name, dateOfBirth, nino, address, passportDetails, idCardDetails) =>
        TrusteeIndividual(isLead = false, name, dateOfBirth, nino, address, passportDetails, idCardDetails))
    }

    ((__ \ "isThisLeadTrustee").read[Boolean] and
      (__ \ "individualOrBusiness").read[IndividualOrBusiness]) ((_, _)).flatMap[(Boolean, IndividualOrBusiness)] {
      case (isLead, individualOrBusiness) =>
        if (individualOrBusiness == IndividualOrBusiness.Individual && !isLead) {
          Reads(_ => JsSuccess((isLead, individualOrBusiness)))
        } else {
          println(individualOrBusiness)
          println(isLead)
          Reads(_ => JsError("trustee individual must not be a `business` or a `lead`"))
        }
    }.andKeep(trusteeReads)

  }
}
