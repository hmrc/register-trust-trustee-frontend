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

package mapping.registration

import mapping.reads.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustee, Trustees}
import models.{LeadTrusteeIndType, LeadTrusteeOrgType, LeadTrusteeType, UserAnswers}

class LeadTrusteeMapper {

  def build(userAnswers: UserAnswers): Option[LeadTrusteeType] = {
    val leadTrustee: Option[Trustee] = userAnswers.get(Trustees).getOrElse(Nil).find(_.isLead)
    leadTrustee match {
      case None => None
      case _ => leadTrustee.map(buildLeadTrusteeType)
    }
  }

  private def buildLeadTrusteeType(leadTrustee: Trustee): LeadTrusteeType = {
    leadTrustee match {
      case indLeadTrustee: LeadTrusteeIndividual => LeadTrusteeType(
        leadTrusteeInd = Some(
          LeadTrusteeIndType(
            name = indLeadTrustee.name,
            dateOfBirth = indLeadTrustee.dateOfBirth,
            phoneNumber = indLeadTrustee.telephoneNumber,
            email = indLeadTrustee.email,
            identification = indLeadTrustee.identification,
            countryOfResidence = indLeadTrustee.countryOfResidence,
            nationality = indLeadTrustee.nationality
          )
        )
      )
      case orgLeadTrustee: LeadTrusteeOrganisation => LeadTrusteeType(
        leadTrusteeOrg = Some(
          LeadTrusteeOrgType(
            name = orgLeadTrustee.name,
            phoneNumber = orgLeadTrustee.telephoneNumber,
            email = orgLeadTrustee.email,
            identification = orgLeadTrustee.identification,
            countryOfResidence = orgLeadTrustee.countryOfResidence
          )
        )
      )
    }
  }
}
