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

import javax.inject.Inject
import mapping.Mapping
import mapping.reads.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustee, Trustees}
import models.UserAnswers

class LeadTrusteeMapper @Inject()(addressMapper: AddressMapper,
                                  passportOrIdCardMapper: PassportOrIdCardMapper
                                 ) extends Mapping[LeadTrusteeType] {

  override def build(userAnswers: UserAnswers): Option[LeadTrusteeType] = {
    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(List.empty[Trustee])
    trustees match {
      case Nil => None
      case list =>
        list
          .find(_.isLead)
          .map(buildLeadTrusteeType)
    }
  }

  private def buildLeadTrusteeType(leadTrustee: Trustee): LeadTrusteeType = {
    leadTrustee match {
      case indLeadTrustee: LeadTrusteeIndividual => buildLeadTrusteeIndividual(indLeadTrustee)
      case orgLeadTrustee: LeadTrusteeOrganisation => buildLeadTrusteeBusiness(orgLeadTrustee)
    }
  }

  private def buildLeadTrusteeIndividual(leadTrustee: LeadTrusteeIndividual) = {

    val identification = if(leadTrustee.nino.isDefined) {
      IdentificationType(nino = leadTrustee.nino, passport = None, address = None)
    } else {
      IdentificationType(nino = None,
        passport = passportOrIdCardMapper.build(leadTrustee.passportOrIdCard),
        address = addressMapper.buildOptional(leadTrustee.address)
      )
    }

    LeadTrusteeType(
      leadTrusteeInd = Some(
        LeadTrusteeIndType(
          name = leadTrustee.name,
          dateOfBirth = leadTrustee.dateOfBirth,
          phoneNumber = leadTrustee.telephoneNumber,
          email = leadTrustee.email,
          identification = identification
        )
      ),
      leadTrusteeOrg = None
    )
  }

  private def buildLeadTrusteeBusiness(leadTrustee: LeadTrusteeOrganisation) = {

    val identification = if(leadTrustee.utr.isDefined) {
      IdentificationOrgType(
        utr = leadTrustee.utr,
        address = None
      )
    } else {
      IdentificationOrgType(
        utr = None,
        address = addressMapper.buildOptional(leadTrustee.address)
      )
    }

      LeadTrusteeType(
        leadTrusteeInd = None,
        leadTrusteeOrg = Some(
          LeadTrusteeOrgType(
            name = leadTrustee.name,
            phoneNumber = leadTrustee.telephoneNumber,
            email = leadTrustee.email,
            identification = identification,
            countryOfResidence = leadTrustee.countryOfResidence
          )
        )
      )
  }
}
