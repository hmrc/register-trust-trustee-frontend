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

package controllers.register

import base.SpecBase
import forms.TrusteeOrLeadTrusteeFormProvider
import models.core.pages.TrusteeOrLeadTrustee
import models.core.pages.TrusteeOrLeadTrustee._
import org.scalacheck.Arbitrary.arbitrary
import pages.register.TrusteeOrLeadTrusteePage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.TrusteeOrLeadTrusteeView

class TrusteeOrLeadTrusteeControllerSpec extends SpecBase with IndexValidation {

  val form = new TrusteeOrLeadTrusteeFormProvider()()

  val index = 0

  lazy val trusteeOrLeadTrusteeRoute: String = routes.TrusteeOrLeadTrusteeController.onPageLoad(index, fakeDraftId).url

  "TrusteeOrLeadTrustee Controller" must {

    "when there is no lead trustee" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, trusteeOrLeadTrusteeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteeOrLeadTrusteeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, index)(request, messages).toString

        application.stop()
      }

    }

    "when there is a lead trustee" must {

      "redirect to TrusteeIndividualOrBusiness Page for a different index to previously answered" in {

        val answers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(0), LeadTrustee)
          .success
          .value
          .set(TrusteeOrLeadTrusteePage(1), Trustee)
          .success
          .value

        val application =
          applicationBuilder(userAnswers = Some(answers)).build()

        val request = FakeRequest(GET, routes.TrusteeOrLeadTrusteeController.onPageLoad(1, fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url
      }

      "populate the view correctly on a GET when the question has previously been answered for the same trustee index" in {

        val userAnswers = emptyUserAnswers.set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeOrLeadTrusteeRoute)

        val view = application.injector.instanceOf[TrusteeOrLeadTrusteeView]

        val result = route(application, request).value

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(LeadTrustee), fakeDraftId, index)(request, messages).toString

        application.stop()
      }

    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, trusteeOrLeadTrusteeRoute)
          .withFormUrlEncodedBody(("value", LeadTrustee.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, trusteeOrLeadTrusteeRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TrusteeOrLeadTrusteeView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteeOrLeadTrusteeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteeOrLeadTrusteeRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.TrusteeOrLeadTrusteeController.onPageLoad(index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[TrusteeOrLeadTrustee],
        TrusteeOrLeadTrusteePage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.TrusteeOrLeadTrusteeController.onPageLoad(index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", LeadTrustee.toString))
      }

      validateIndex(
        arbitrary[TrusteeOrLeadTrustee],
        TrusteeOrLeadTrusteePage.apply,
        postForIndex
      )
    }
  }

}
