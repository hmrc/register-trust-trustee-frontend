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

package controllers.register

import base.SpecBase
import forms.IndividualOrBusinessFormProvider
import models.core.pages.IndividualOrBusiness
import models.core.pages.TrusteeOrLeadTrustee._
import org.scalacheck.Arbitrary.arbitrary
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.TrusteeIndividualOrBusinessView

class TrusteeIndividualOrBusinessControllerSpec extends SpecBase with IndexValidation {

  val index = 0

  lazy val trusteeIndividualOrBusinessRoute = routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

  val formProvider = new IndividualOrBusinessFormProvider()

  "TrusteeIndividualOrBusiness Controller" must {

    "return OK and the correct view for a GET" when {

      "is lead trustee" in {

        val messageKeyPrefix = "leadTrusteeIndividualOrBusiness"

        val leadHeading = Messages(s"$messageKeyPrefix.heading")

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value

        val application = applicationBuilder(Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, index, leadHeading)(request, messages).toString

        application.stop()
      }


      "is trustee" in {

        val messageKeyPrefix = "trusteeIndividualOrBusiness"

        val heading = Messages(s"$messageKeyPrefix.heading")

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, index, heading)(request, messages).toString

        application.stop()
      }
    }

    "populate the view correctly on a GET when the question has previously been answered" when {


      "is lead trustee" in {

        val messageKeyPrefix = "leadTrusteeIndividualOrBusiness"

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.values.head).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

        val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

        val result = route(application, request).value

        val leadHeading = Messages(s"$messageKeyPrefix.heading")

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(IndividualOrBusiness.values.head), fakeDraftId, index, leadHeading)(request, messages).toString

        application.stop()
      }


      "is not lead trustee" in {

        val messageKeyPrefix = "trusteeIndividualOrBusiness"

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.values.head).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

        val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

        val result = route(application, request).value

        val heading = Messages(s"$messageKeyPrefix.heading", "")

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(IndividualOrBusiness.values.head), fakeDraftId, index, heading)(request, messages).toString

        application.stop()
      }
    }

    "redirect to TrusteeOrLeadTrustee when TrusteeOrLeadTrustee is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.TrusteeOrLeadTrusteeController.onPageLoad(index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted (lead trustee)" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to the next page when valid data is submitted (trustee)" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val messageKeyPrefix = "trusteeIndividualOrBusiness"

      val heading = Messages(s"$messageKeyPrefix.heading")

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value


      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val form = formProvider(messageKeyPrefix)

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TrusteeIndividualOrBusinessView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, heading)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteeIndividualOrBusinessRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteeIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[IndividualOrBusiness],
        TrusteeIndividualOrBusinessPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.values.head.toString))
      }

      validateIndex(
        arbitrary[IndividualOrBusiness],
        TrusteeIndividualOrBusinessPage.apply,
        postForIndex
      )
    }
  }
}
