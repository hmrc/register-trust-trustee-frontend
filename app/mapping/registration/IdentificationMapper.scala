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

package mapping.registration

import models.core.pages.{Address, InternationalAddress, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import models.{AddressType, PassportType}
import utils.Constants.GB

object IdentificationMapper {

  def buildAddress(address: Address): AddressType = {
    address match {
      case a: UKAddress => buildUkAddress(a)
      case a: InternationalAddress => buildInternationalAddress(a)
    }
  }

  def buildAddress(address: Option[Address]): Option[AddressType] = {
    address.map(buildAddress)
  }

  private def buildUkAddress(address: UKAddress): AddressType = {
    AddressType(
      line1 = address.line1,
      line2 = address.line2,
      line3 = address.line3,
      line4 = address.line4,
      postCode = Some(address.postcode),
      country = GB
    )
  }

  private def buildInternationalAddress(address: InternationalAddress): AddressType = {
    AddressType(
      line1 = address.line1,
      line2 = address.line2,
      line3 = address.line3,
      line4 = None,
      postCode = None,
      country = address.country
    )
  }

  def buildPassport(passportOrIdCardDetails: Option[PassportOrIdCardDetails]): Option[PassportType] = {
    passportOrIdCardDetails map { passportOrIdCardDetails =>
      PassportType(passportOrIdCardDetails.cardNumber, passportOrIdCardDetails.expiryDate, passportOrIdCardDetails.country)
    }
  }

}
