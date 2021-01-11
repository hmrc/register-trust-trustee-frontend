/*
 * Copyright 2021 HM Revenue & Customs
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

import models.core.pages.IndividualOrBusiness.Business
import models.core.pages.{Address, UKAddress}
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.leadtrustee.organisation.{EmailAddressPage, EmailAddressYesNoPage, NamePage, TelephoneNumberPage, UkRegisteredYesNoPage, UtrPage}
import pages.register.leadtrustee.organisation.nonTaxable.CountryOfResidencePage
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

final case class LeadTrusteeOrganisation(override val isLead: Boolean = true,
                                         name: String,
                                         utr: Option[String],
                                         address: Address,
                                         telephoneNumber: String,
                                         email: Option[String],
                                         countryOfResidence: Option[String]) extends Trustee {

  def hasUkAddress: Boolean = address.isInstanceOf[UKAddress]
}

object LeadTrusteeOrganisation extends TrusteeReads {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[LeadTrusteeOrganisation] = {

    val leadTrusteeReads: Reads[LeadTrusteeOrganisation] = (
      isLeadReads and
        (__ \ NamePage.toString).read[String] and
        yesNoReads[String](UkRegisteredYesNoPage.toString, UtrPage.toString) and
        addressReads and
        (__ \ TelephoneNumberPage.toString).read[String] and
        yesNoReads[String](EmailAddressYesNoPage.toString, EmailAddressPage.toString) and
        (__ \ CountryOfResidencePage.toString).readNullable[String]
      )(LeadTrusteeOrganisation.apply _)

    (isLeadReads and
      (__ \ TrusteeIndividualOrBusinessPage.toString).read[String]) ((_, _)).flatMap[(Boolean, String)] {
      case (isLead, individualOrBusiness) =>
        if (individualOrBusiness == Business.toString && isLead) {
          Reads(_ => JsSuccess((isLead, individualOrBusiness)))
        } else {
          Reads(_ => JsError("lead trustee organisation must not be an `individual` or a normal trustee"))
        }
    }.andKeep(leadTrusteeReads)

  }
}