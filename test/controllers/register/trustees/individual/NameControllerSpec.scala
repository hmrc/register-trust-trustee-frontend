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

package controllers.register.trustees.individual

import base.SpecBase
import config.annotations.TrusteeIndividual
import controllers.register.IndexValidation
import forms.NameFormProvider
import models.core.pages.FullName
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import pages.register.trustees.individual.NamePage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.trustees.individual.NameView

class NameControllerSpec extends SpecBase with IndexValidation {

  private val formProvider = new NameFormProvider()

  private val index = 0

  private lazy val trusteesNameRoute: String = routes.NameController.onPageLoad(index, fakeDraftId).url

  "TrusteesName Controller" must {

    "return Ok and the correct view for a GET" when {

      "is trustee" in {

        val messageKeyPrefix = "trustee.individual.name"

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, trusteesNameRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[NameView]

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, index)(request, messages).toString

        application.stop()
      }

    }

    "return a Bad Request and errors when invalid data is submitted" when {

      "is trustee" in {

        val messageKeyPrefix = "trustee.individual.name"

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, trusteesNameRoute)
            .withFormUrlEncodedBody(("value", ""))

        val form = formProvider(messageKeyPrefix)

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[NameView]

        val result = route(application, request).value


        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, index)(request, messages).toString

        application.stop()

      }

    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val name = FullName("first name", Some("middle name"), "last name")

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesNameRoute)

      val view = application.injector.instanceOf[NameView]

      val result = route(application, request).value

      val form = formProvider("trustee.individual.name")

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(name), fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page on a POST valid data is submitted" in {

      val name = FullName("First name", Some("Middle name"), "Last name")

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator]
              .qualifiedWith(classOf[TrusteeIndividual])
              .toInstance(new FakeNavigator())
          ).build()

      val request =
        FakeRequest(POST, trusteesNameRoute)
          .withFormUrlEncodedBody(("firstName", "First"), ("middleName", "Middle"), ("lastName", "Last"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()

    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteesNameRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteesNameRoute)
          .withFormUrlEncodedBody(("firstName", "First"), ("middleName", "Middle"), ("lastName", "Last"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = controllers.register.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[FullName],
        NamePage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          controllers.register.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("firstName", "First"), ("middleName", "Middle"), ("lastName", "Last"))
      }

      validateIndex(
        arbitrary[FullName],
        NamePage.apply,
        postForIndex
      )
    }
  }
}
