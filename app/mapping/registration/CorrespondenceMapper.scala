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

package mapping.registration

import javax.inject.Inject
import mapping.reads.{LeadTrusteeIndividual, LeadTrusteeOrganisation, Trustees}
import models.{RegistrationSubmission, UserAnswers}
import play.api.Logger
import play.api.libs.json.{JsBoolean, JsString, Json}

class CorrespondenceMapper @Inject()(addressMapper: AddressMapper) {

  def build(userAnswers: UserAnswers): List[RegistrationSubmission.MappedPiece] = {
    val result = userAnswers.get(Trustees).getOrElse(Nil) match {
      case Nil => None
      case list =>
        list.find(_.isLead).map {
          case lti: LeadTrusteeIndividual =>
            val address = addressMapper.build(lti.address)
            List(
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(!lti.hasUkAddress)),
              RegistrationSubmission.MappedPiece("correspondence/address", Json.toJson(address)),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString(lti.telephoneNumber))
            )
          case lto: LeadTrusteeOrganisation =>
            val address = addressMapper.build(lto.address)
            List(
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(!lto.hasUkAddress)),
              RegistrationSubmission.MappedPiece("correspondence/address", Json.toJson(address)),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString(lto.telephoneNumber))
            )
          case _ =>
            Logger.info(s"[CorrespondenceMapper][build] unable to create correspondence due to unexpected lead trustee type")
            List.empty
        }
    }

    result match {
      case Some(list) => list
      case None => List.empty
    }
  }
}