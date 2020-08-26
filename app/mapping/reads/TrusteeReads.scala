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

package mapping.reads

import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import models.core.pages.{Address, InternationalAddress, TrusteeOrLeadTrustee, UKAddress}
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait TrusteeReads {

  val isLeadReads: Reads[Boolean] =
    (__ \ 'trusteeOrLeadTrustee).read[TrusteeOrLeadTrustee].map[Boolean] {
      case LeadTrustee => true
      case _ => false
    }

  val addressReads: Reads[Address] =
    (__ \ 'ukAddress).read[UKAddress].widen[Address] or
      (__ \ 'internationalAddress).read[InternationalAddress].widen[Address]

  val optionalAddressReads: Reads[Option[Address]] =
    (__ \ 'ukAddress).read[UKAddress].map(Some(_: Address)) or
      (__ \ 'internationalAddress).read[InternationalAddress].map(Some(_: Address)) or
      Reads(_ => JsSuccess(None))

}
