/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.actions

import base.SpecBase
import play.api.http.Status.SEE_OTHER
import controllers.actions.register.{RemoveIndexRequest, TrusteeRequiredAction}
import models.Status.Completed
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.Business
import models.core.pages.TrusteeOrLeadTrustee.Trustee
import models.requests.RegistrationDataRequest
import org.scalatest.concurrent.ScalaFutures
import pages.entitystatus.TrusteeStatus
import pages.register.trustees.organisation.{NamePage, UtrPage, UtrYesNoPage}
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import play.api.mvc.Result
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import viewmodels.addAnother.TrusteeViewModel

import scala.concurrent.Future

class TrusteeRequiredActionSpec extends SpecBase with ScalaFutures {

  private val index: Int = 0
  private val name: String = "Name"

  class Harness(index: Int) extends TrusteeRequiredAction(index, fakeDraftId) {
    def callRefine[A](request: RegistrationDataRequest[A]): Future[Either[Result, RemoveIndexRequest[A]]] = refine(request)
  }

  private def request(userAnswers: UserAnswers) = RegistrationDataRequest(
    fakeRequest,
    "internalId",
    userAnswers,
    AffinityGroup.Organisation,
    Enrolments(Set())
  )

  "Trustee Required Action" when {

    "no trustee found at index" must {

      "redirect to add to page" in {

        val action = new Harness(index)

        val futureResult = action.callRefine(request(emptyUserAnswers))

        whenReady(futureResult) { r =>
          val result = Future.successful(r.left.get)
          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.register.routes.AddATrusteeController.onPageLoad(fakeDraftId).url
        }
      }
    }

    "trustee found at index" must {

      "add trustee to request" in {

        val action = new Harness(index)

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
          .set(TrusteeIndividualOrBusinessPage(index), Business).success.value
          .set(NamePage(index), name).success.value
          .set(UtrYesNoPage(index), true).success.value
          .set(UtrPage(index), "utr").success.value
          .set(TrusteeStatus(index), Completed).success.value

        val futureResult = action.callRefine(request(userAnswers))

        whenReady(futureResult) { result =>
          result.right.get.trustee mustBe TrusteeViewModel(
            isLead = false,
            name = Some(name),
            `type` = Some(Business),
            status = Completed
          )
        }
      }
    }
  }
}
