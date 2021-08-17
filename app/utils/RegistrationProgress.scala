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

package utils

import models.Status.{Completed, InProgress}
import models.registration.pages.AddATrustee
import models.{Status, UserAnswers}
import pages.register.AddATrusteePage
import play.api.Logging
import sections.Trustees

class RegistrationProgress extends Logging {

  def trusteesStatus(userAnswers: UserAnswers): Option[Status] = {

    userAnswers.get(Trustees) match {
      case Some(Nil) =>
        logger.info(s"[trusteesStatus] no trustees to determine a status")
        None
      case Some(trustees) =>
        val noMoreToAdd = userAnswers.get(AddATrusteePage).contains(AddATrustee.NoComplete)
        val hasLeadTrustee = trustees.exists(_.isLead)
        val isComplete = !trustees.exists(_.status == InProgress) && noMoreToAdd && hasLeadTrustee

        if (isComplete) {
          logger.info(s"[trusteesStatus] trustee status is completed")
          Some(Completed)
        } else {
          logger.info(s"[trusteesStatus] trustee status is in progress")
          Some(InProgress)
        }
      case None =>
        logger.info(s"[trusteesStatus] no trustees to determine a status")
        None
    }
  }

}
