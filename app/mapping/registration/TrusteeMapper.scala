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
import mapping.reads.{Trustee, TrusteeIndividual, TrusteeOrganisation, Trustees}
import models.UserAnswers

class TrusteeMapper @Inject()(addressMapper: AddressMapper,
                              passportOrIdCardMapper: PassportOrIdCardMapper) extends Mapping[List[TrusteeType]] {

  override def build(userAnswers: UserAnswers): Option[List[TrusteeType]] = {
    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(List.empty[Trustee])
    val trusteesList: List[Trustee] = trustees.filter(!_.isLead)
    trusteesList match {
      case Nil => None
      case list =>
        Some(list.map { trustee =>
          getTrusteeType(trustee)
        })
    }
  }

  private def getTrusteeType(trustee: Trustee): TrusteeType = {
    trustee match {
      case indTrustee: TrusteeIndividual =>
        TrusteeType(
          trusteeInd = Some(
            TrusteeIndividualType(
              name = indTrustee.name,
              dateOfBirth = indTrustee.dateOfBirth,
              phoneNumber = None,
              identification = identificationMap(indTrustee),
              countryOfResidence = indTrustee.countryOfResidence,
              nationality = indTrustee.nationality,
              legallyIncapable = indTrustee.mentalCapacityYesNo.map(!_)
            )
          ),
          trusteeOrg = None
        )
      case orgTrustee: TrusteeOrganisation =>
        TrusteeType(
          trusteeInd = None,
          trusteeOrg = Some(
            TrusteeOrgType(
              name = orgTrustee.name,
              phoneNumber = None,
              email = None,
              identification = identificationMap(orgTrustee)
            )
          )
        )
    }
  }


  private def identificationMap(trustee: TrusteeIndividual): Option[IdentificationType] = {
    val identificationType = IdentificationType(
      trustee.nino,
      passportOrIdCardMapper.build(trustee.passportOrIdCard),
      addressMapper.build(trustee.address)
    )

    identificationType match {
      case IdentificationType(None, None, None) => None
      case _ => Some(identificationType)
    }
  }

  private def identificationMap(trustee: TrusteeOrganisation): Option[IdentificationOrgType] = {
    (trustee.utr, addressMapper.build(trustee.address)) match {
      case (None, None) => None
      case (utr, address) => Some(IdentificationOrgType(utr, address))
    }
  }
}
