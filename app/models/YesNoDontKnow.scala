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

package models

import play.api.libs.json.{JsBoolean, JsError, JsNull, JsPath, JsString, JsSuccess, Reads, Writes}

sealed trait YesNoDontKnow

object YesNoDontKnow {

  case object Yes extends YesNoDontKnow

  case object No extends YesNoDontKnow

  case object DontKnow extends  YesNoDontKnow

  val values: Set[YesNoDontKnow] = Set(
    Yes, No, DontKnow
  )

  implicit val enumerable: Enumerable[YesNoDontKnow] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  implicit val reads: Reads[YesNoDontKnow] = {
    (JsPath \ "mentalCapacityYesNo").readNullable[Boolean] map {
      case Some(true) => Yes
      case Some(false) => No
      case None  => DontKnow
    }
  }

  implicit val writes: Writes[YesNoDontKnow] = Writes(value => JsString(value.toString))
}
