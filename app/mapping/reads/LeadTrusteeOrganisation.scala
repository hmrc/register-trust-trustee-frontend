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

import mapping.registration.IdentificationMapper.buildAddress
import models.IdentificationOrgType
import models.core.pages.IndividualOrBusiness.Business
import models.core.pages.{Address, IndividualOrBusiness}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsSuccess, Reads, __}

final case class LeadTrusteeOrganisation(
  override val isLead: Boolean,
  name: String,
  utr: Option[String],
  address: Address,
  telephoneNumber: String,
  email: Option[String],
  countryOfResidence: Option[String]
) extends LeadTrustee {

  val identification: IdentificationOrgType = utr match {
    case Some(_) => IdentificationOrgType(utr, None)
    case _       => IdentificationOrgType(None, Some(buildAddress(address)))
  }

}

object LeadTrusteeOrganisation extends TrusteeReads[LeadTrusteeOrganisation] {

  override val isLeadTrustee: Boolean                     = true
  override val individualOrBusiness: IndividualOrBusiness = Business

  override def trusteeReads: Reads[LeadTrusteeOrganisation] = (
    Reads(_ => JsSuccess(isLeadTrustee)) and
      (__ \ "name").read[String] and
      yesNoReads[String]("isUKBusiness", "utr") and
      addressReads and
      (__ \ "telephoneNumber").read[String] and
      yesNoReads[String]("emailYesNo", "email") and
      (__ \ "countryOfResidence").readNullable[String]
  )(LeadTrusteeOrganisation.apply _)

}
