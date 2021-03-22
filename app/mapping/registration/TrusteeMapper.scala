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

import mapping.reads.{Trustee, TrusteeIndividual, TrusteeOrganisation, Trustees}
import models.{TrusteeIndividualType, TrusteeOrgType, TrusteeType, UserAnswers}

class TrusteeMapper {

  def build(userAnswers: UserAnswers): Option[List[TrusteeType]] = {
    val trustees: List[Trustee] = userAnswers.get(Trustees).getOrElse(Nil).filter(!_.isLead)
    trustees match {
      case Nil => None
      case _ => Some(trustees.map(buildTrusteeType))
    }
  }

  private def buildTrusteeType(trustee: Trustee): TrusteeType = {
    trustee match {
      case indTrustee: TrusteeIndividual => TrusteeType(
        trusteeInd = Some(
          TrusteeIndividualType(
            name = indTrustee.name,
            dateOfBirth = indTrustee.dateOfBirth,
            phoneNumber = None,
            identification = indTrustee.identification,
            countryOfResidence = indTrustee.countryOfResidence,
            nationality = indTrustee.nationality,
            legallyIncapable = indTrustee.mentalCapacityYesNo.map(!_)
          )
        )
      )
      case orgTrustee: TrusteeOrganisation => TrusteeType(
        trusteeOrg = Some(
          TrusteeOrgType(
            name = orgTrustee.name,
            phoneNumber = None,
            email = None,
            identification = orgTrustee.identification,
            countryOfResidence = orgTrustee.countryOfResidence
          )
        )
      )
    }
  }
}
