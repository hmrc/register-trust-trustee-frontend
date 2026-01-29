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

package mapping.reads

import models.YesNoDontKnow
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import models.core.pages._
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.reflect.{ClassTag, classTag}

abstract class TrusteeReads[A: ClassTag] {

  val isLeadTrustee: Boolean
  val individualOrBusiness: IndividualOrBusiness

  implicit lazy val reads: Reads[A] = (
    isLeadReads and
      (__ \ "individualOrBusiness").read[IndividualOrBusiness]
  )((_, _))
    .flatMap[(Boolean, IndividualOrBusiness)] { case (a, b) =>
      if (a == isLeadTrustee && b == individualOrBusiness) {
        Reads(_ => JsSuccess((isLeadTrustee, individualOrBusiness)))
      } else {
        Reads(_ =>
          JsError(
            s"${classTag[A].runtimeClass.getSimpleName} must not have values isLeadTrustee = $a and individualOrBusiness = $b"
          )
        )
      }
    }
    .andKeep(trusteeReads)

  def trusteeReads: Reads[A]

  def isLeadReads: Reads[Boolean] =
    (__ \ Symbol("trusteeOrLeadTrustee")).read[TrusteeOrLeadTrustee].map[Boolean] {
      case LeadTrustee => true
      case _           => false
    }

  def addressReads: Reads[Address] =
    (__ \ Symbol("ukAddress")).read[UKAddress].widen[Address] or
      (__ \ Symbol("internationalAddress")).read[InternationalAddress].widen[Address]

  def optionalAddressReads(yesNoPath: String): Reads[Option[Address]] = {

    val addressReads: Reads[Option[Address]] =
      (__ \ Symbol("ukAddress")).read[UKAddress].map(Some(_: Address)) or
        (__ \ Symbol("internationalAddress")).read[InternationalAddress].map(Some(_: Address)) or
        Reads(_ => JsSuccess(None))

    ((__ \ yesNoPath).readNullable[Boolean] and
      (__ \ Symbol("addressYesNo")).readNullable[Boolean] and
      (__ \ Symbol("addressUKYesNo")).readNullable[Boolean] and
      addressReads)((_, _, _, _)).flatMap[Option[Address]] {
      case (Some(false), Some(true), Some(_), address @ Some(_))                                              =>
        Reads(_ => JsSuccess(address))
      case (Some(false), Some(false), None, None) | (Some(true), None, None, None) | (None, None, None, None) =>
        Reads(_ => JsSuccess(None))
      case _                                                                                                  =>
        Reads(_ => JsError("address answers are in an invalid state"))
    }
  }

  def yesNoReads[T](yesNoPath: String, valuePath: String)(implicit reads: Reads[T]): Reads[Option[T]] =
    ((__ \ yesNoPath).readNullable[Boolean] and
      (__ \ valuePath).readNullable[T])((_, _)).flatMap[Option[T]] {
      case (Some(true), x @ Some(_))          =>
        Reads(_ => JsSuccess(x))
      case (Some(false), None) | (None, None) =>
        Reads(_ => JsSuccess(None))
      case _                                  =>
        Reads(_ => JsError(s"answers at $yesNoPath and $valuePath are in an invalid state"))
    }

  def readMentalCapacity: Reads[Option[YesNoDontKnow]] =
    (__ \ Symbol("mentalCapacityYesNo"))
      .readNullable[Boolean]
      .flatMap[Option[YesNoDontKnow]] { x: Option[Boolean] =>
        Reads(_ => JsSuccess(YesNoDontKnow.fromBoolean(x)))
      }
      .orElse {
        (__ \ Symbol("mentalCapacityYesNo")).readNullable[YesNoDontKnow]
      }

}
