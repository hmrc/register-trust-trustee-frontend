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

package controllers

import base.SpecBase
import models.UserAnswers
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import pages.register.TrusteeOrLeadTrusteePage
import play.api.test.FakeRequest
import play.api.test.Helpers._

class IndexControllerSpec extends SpecBase {

  "Index Controller" when {

    "there are no trustees" must {

      "redirect to TrusteesInfoController" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.routes.TrusteesInfoController.onPageLoad(fakeDraftId).url

        application.stop()
      }
    }

    "there are trustees" must {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value

      "redirect to AddATrusteeController" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.routes.TrusteesInfoController.onPageLoad(fakeDraftId).url

        application.stop()
      }
    }

  }
}
