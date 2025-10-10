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

package viewmodels.addAnother

import mapping.reads._
import models.Status
import models.Status.{Completed, InProgress}
import models.core.pages.{FullName, IndividualOrBusiness, TrusteeOrLeadTrustee}
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class TrusteeViewModel(isLead: Boolean,
                                  name: Option[String],
                                  `type`: Option[IndividualOrBusiness],
                                  status: Status) {

  def isComplete: Boolean = status == Status.Completed
}

object TrusteeViewModel {

  implicit class OptionString(s: String) {
    def toOption: Option[String] = if(s.isEmpty) None else Some(s)
  }

  val nameReads: Reads[Option[String]] =
    (__ \ "name").read[FullName].map(_.toString.toOption) orElse
      (__ \ "name").readNullable[String]

  val isLeadReads: Reads[Boolean] =
    (__ \ "trusteeOrLeadTrustee").readWithDefault[TrusteeOrLeadTrustee](TrusteeOrLeadTrustee.Trustee).map[Boolean] {
      case TrusteeOrLeadTrustee.LeadTrustee => true
      case _ => false
    }

  val statusReads: Reads[Status] =
    ((__ \ "status").readWithDefault[Status](InProgress) and
      Trustee.optionalReads
      )(
      (status, trustee) => {
        (status, trustee) match {
          case (Completed, Some(_)) => Completed
          case _ => InProgress
        }
      }
    )

  implicit lazy val reads: Reads[TrusteeViewModel] =
    (isLeadReads and
      (__ \ "individualOrBusiness").readNullable[IndividualOrBusiness] and
      nameReads and
      statusReads
      )(
      (isLead, individualOrBusiness, name, status) => {
        TrusteeViewModel(isLead, name, individualOrBusiness, status)
      }
    )

}
