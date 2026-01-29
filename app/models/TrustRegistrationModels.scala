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

package models

import models.core.pages.FullName
import play.api.libs.json._

import java.time.LocalDate

/**
 * Trust Registration API Schema - definitions models below
 */

case class TrusteeType(trusteeInd: Option[TrusteeIndividualType] = None, trusteeOrg: Option[TrusteeOrgType] = None)

object TrusteeType {
  implicit val trusteeTypeFormat: Format[TrusteeType] = Json.format[TrusteeType]
}

case class TrusteeOrgType(
  name: String,
  phoneNumber: Option[String] = None,
  email: Option[String] = None,
  identification: Option[IdentificationOrgType],
  countryOfResidence: Option[String]
)

object TrusteeOrgType {
  implicit val trusteeOrgTypeFormat: Format[TrusteeOrgType] = Json.format[TrusteeOrgType]
}

case class TrusteeIndividualType(
  name: FullName,
  dateOfBirth: Option[LocalDate],
  phoneNumber: Option[String],
  identification: Option[IdentificationType],
  countryOfResidence: Option[String],
  nationality: Option[String],
  legallyIncapable: Option[Boolean]
)

object TrusteeIndividualType {
  implicit val trusteeIndividualTypeFormat: Format[TrusteeIndividualType] = Json.format[TrusteeIndividualType]
}

case class LeadTrusteeIndType(
  name: FullName,
  dateOfBirth: LocalDate,
  phoneNumber: String,
  email: Option[String] = None,
  identification: IdentificationType,
  countryOfResidence: Option[String],
  nationality: Option[String]
)

object LeadTrusteeIndType {
  implicit val leadTrusteeIndTypeFormat: Format[LeadTrusteeIndType] = Json.format[LeadTrusteeIndType]
}

case class LeadTrusteeOrgType(
  name: String,
  phoneNumber: String,
  email: Option[String] = None,
  identification: IdentificationOrgType,
  countryOfResidence: Option[String]
)

object LeadTrusteeOrgType {
  implicit val leadTrusteeOrgTypeFormat: Format[LeadTrusteeOrgType] = Json.format[LeadTrusteeOrgType]
}

case class LeadTrusteeType(
  leadTrusteeInd: Option[LeadTrusteeIndType] = None,
  leadTrusteeOrg: Option[LeadTrusteeOrgType] = None
)

object LeadTrusteeType {
  implicit val leadTrusteeTypeReads: Format[LeadTrusteeType] = Json.format[LeadTrusteeType]
}

case class IdentificationOrgType(utr: Option[String], address: Option[AddressType])

object IdentificationOrgType {
  implicit val trustBeneficiaryIdentificationFormat: Format[IdentificationOrgType] = Json.format[IdentificationOrgType]
}

case class IdentificationType(nino: Option[String], passport: Option[PassportType], address: Option[AddressType])

object IdentificationType {
  implicit val identificationTypeFormat: Format[IdentificationType] = Json.format[IdentificationType]
}

case class PassportType(number: String, expirationDate: LocalDate, countryOfIssue: String)

object PassportType {
  implicit val passportTypeFormat: Format[PassportType] = Json.format[PassportType]
}

case class AddressType(
  line1: String,
  line2: String,
  line3: Option[String],
  line4: Option[String],
  postCode: Option[String],
  country: String
)

object AddressType {
  implicit val addressTypeFormat: Format[AddressType] = Json.format[AddressType]
}
