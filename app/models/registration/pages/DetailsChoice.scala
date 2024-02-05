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

package models.registration.pages

import models.{Enumerable, WithName}
import viewmodels.RadioOption

sealed trait DetailsChoice

object DetailsChoice extends Enumerable.Implicits {

  case object IdCard extends WithName("idCard") with DetailsChoice

  case object Passport extends WithName("passport") with DetailsChoice

  val values: List[DetailsChoice] = List(
    Passport, IdCard
  )

  def options: List[RadioOption] = values.map {
    value => {
      val prefix: String =  "leadTrustee.individual.5mld.trusteeDetailsChoice"
      RadioOption(prefix, value.toString)
    }
  }

  implicit val enumerable: Enumerable[DetailsChoice] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
