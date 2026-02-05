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

package controllers.register.trustees.individual

import base.SpecBase
import config.annotations.TrusteeIndividual
import controllers.register.IndexValidation
import forms.UKAddressFormProvider
import models.core.pages.{FullName, UKAddress}
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import pages.register.trustees.individual.{NamePage, UkAddressPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.trustees.individual.UkAddressView

class UkAddressControllerSpec extends SpecBase with IndexValidation {

  private val formProvider          = new UKAddressFormProvider()
  private val form: Form[UKAddress] = formProvider()
  private val index                 = 0
  private val trusteeName           = "FirstName LastName"
  private val validAnswer           = UKAddress("value 1", "value 2", Some("value 3"), Some("value 4"), "AB1 1AB")

  private lazy val trusteesUkAddressRoute: String = routes.UkAddressController.onPageLoad(index, fakeDraftId).url

  "TrusteesUkAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("FirstName", None, "LastName"))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesUkAddressRoute)

      val view = application.injector.instanceOf[UkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, trusteeName, index, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("FirstName", None, "LastName"))
        .success
        .value
        .set(UkAddressPage(index), validAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesUkAddressRoute)

      val view = application.injector.instanceOf[UkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), trusteeName, index, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("FirstName", None, "LastName"))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator]
              .qualifiedWith(classOf[TrusteeIndividual])
              .toInstance(new FakeNavigator())
          )
          .build()

      val request =
        FakeRequest(POST, trusteesUkAddressRoute)
          .withFormUrlEncodedBody(
            ("line1", "value 1"),
            ("line2", "value 2"),
            ("line3", "value 3"),
            ("line4", "town"),
            ("postcode", "AB1 1AB")
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("FirstName", None, "LastName"))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteesUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "invalid value"))

      val boundForm = form.bind(Map("line1" -> "invalid value"))

      val view = application.injector.instanceOf[UkAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, trusteeName, index, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteesUkAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteesUkAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.UkAddressController.onPageLoad(index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[UKAddress],
        UkAddressPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.UkAddressController.onPageLoad(index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(
            ("line1", "line1"),
            ("line2", "line2"),
            ("line3", "line3"),
            ("line4", "town or city"),
            ("postcode", "AB1 1AB")
          )
      }

      validateIndex(
        arbitrary[UKAddress],
        UkAddressPage.apply,
        postForIndex
      )
    }

  }

}
