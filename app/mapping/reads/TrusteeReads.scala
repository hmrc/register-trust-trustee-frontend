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

  private val ukOrInternationalAddressReads: Reads[Option[Address]] =
    (__ \ 'ukAddress).read[UKAddress].map(Some(_: Address)) or
      (__ \ 'internationalAddress).read[InternationalAddress].map(Some(_: Address)) or
      Reads(_ => JsSuccess(None))

  def optionalAddressReads(yesNoPath: String): Reads[Option[Address]] =
    ((__ \ yesNoPath).readNullable[Boolean] and
      (__ \ 'addressYesNo).readNullable[Boolean] and
      (__ \ 'addressUKYesNo).readNullable[Boolean] and
      ukOrInternationalAddressReads) ((_, _, _, _)).flatMap[Option[Address]] {
      case (Some(false), Some(true), Some(_), address @ Some(_)) => Reads(_ => JsSuccess(address))
      case (Some(false), Some(false), None, None) | (Some(true), None, None, None) | (None, None, None, None) => Reads(_ => JsSuccess(None))
      case _ => Reads(_ => JsError("trustee address answers in invalid state"))
    }

  def yesNoReads[T](yesNoPath: String, valuePath: String)(implicit reads: Reads[T]): Reads[Option[T]] =
    ((__ \ yesNoPath).readNullable[Boolean] and
      (__ \ valuePath).readNullable[T]) ((_, _)).flatMap[Option[T]] {
      case (Some(true), x @ Some(_)) => Reads(_ => JsSuccess(x))
      case (Some(false), None) | (None, None) => Reads(_ => JsSuccess(None))
      case _ => Reads(_ => JsError(s"answers at $yesNoPath and $valuePath are in an invalid state"))
    }

}
