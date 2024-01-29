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

package mapping.reads

import mapping.registration.IdentificationMapper.buildAddress
import models.IdentificationOrgType
import models.core.pages.IndividualOrBusiness.Business
import models.core.pages.{Address, IndividualOrBusiness}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsSuccess, Reads, __}

final case class TrusteeOrganisation(override val isLead: Boolean,
                                     name: String,
                                     utr: Option[String],
                                     address: Option[Address],
                                     countryOfResidence: Option[String]) extends Trustee {

  val identification: Option[IdentificationOrgType] = (utr, address) match {
    case (None, None) => None
    case _ => Some(IdentificationOrgType(utr, buildAddress(address)))
  }
}

object TrusteeOrganisation extends TrusteeReads[TrusteeOrganisation] {

  override val isLeadTrustee: Boolean = false
  override val individualOrBusiness: IndividualOrBusiness = Business

  override def trusteeReads: Reads[TrusteeOrganisation] = (
    Reads(_ => JsSuccess(isLeadTrustee)) and
      (__ \ "name").read[String] and
      yesNoReads[String]("utrYesNo", "utr") and
      optionalAddressReads("utrYesNo") and
      (__ \ "countryOfResidence").readNullable[String]
    )(TrusteeOrganisation.apply _)

}
