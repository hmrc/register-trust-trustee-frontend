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

import config.FrontendAppConfig
import models.AddressType
import models.core.pages.{Address, InternationalAddress, UKAddress}
import utils.Constants.GB

import javax.inject.Inject

class AddressMapper @Inject()(implicit val config: FrontendAppConfig) {

  private def buildUkAddress(address: Option[UKAddress]): Option[AddressType] = {
    address.map { x =>
      buildUkAddress(x)
    }
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

  private def buildInternationalAddress(address: Option[InternationalAddress]): Option[AddressType] = {
    address.map { x =>
      buildInternationalAddress(x)
    }
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

  def buildOptional(address: Address): Option[AddressType] = {
    address match {
      case a: UKAddress =>
        buildUkAddress(Some(a))
      case a: InternationalAddress =>
        buildInternationalAddress(Some(a))
    }
  }

  def build(address: Address): AddressType = {
    address match {
      case a: UKAddress =>
        buildUkAddress(a)
      case a: InternationalAddress =>
        buildInternationalAddress(a)
    }
  }

  def build(ukOrInternationalAddress: Option[Address]): Option[AddressType] = {
    ukOrInternationalAddress flatMap {
      case ukAddress: UKAddress => buildUkAddress(Some(ukAddress))
      case international: InternationalAddress => buildInternationalAddress(Some(international))
    }
  }

}
