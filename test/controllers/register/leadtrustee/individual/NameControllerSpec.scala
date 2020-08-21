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

package controllers.register.leadtrustee.individual

import base.SpecBase
import config.annotations.LeadTrusteeIndividual
import controllers.register.IndexValidation
import forms.trustees.TrusteesNameFormProvider
import models.core.pages.FullName
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import pages.register.leadtrustee.individual.TrusteesNamePage
import pages.register.trustees._
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.leadtrustee.individual.NameView

class NameControllerSpec extends SpecBase with IndexValidation {

  val formProvider = new TrusteesNameFormProvider()

  val index = 0

  val name = FullName("first name", Some("middle name"), "last name")

  val messageKeyPrefix = "leadTrustee.individual.name"

  lazy val trusteesNameRoute: String = routes.NameController.onPageLoad(index, fakeDraftId).url

  "Name Controller" must {

    "return Ok and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, trusteesNameRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[NameView]

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, index)(fakeRequest, messages).toString

        application.stop()

    }

    "return a Bad Request and errors when invalid data is submitted" in {

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
          view(boundForm, fakeDraftId, index)(fakeRequest, messages).toString

        application.stop()

      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteesNamePage(index), name).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteesNameRoute)

        val view = application.injector.instanceOf[NameView]

        val result = route(application, request).value

        val form = formProvider(messageKeyPrefix)

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form.fill(name), fakeDraftId, index)(fakeRequest, messages).toString

        application.stop()
      }


      "redirect to the next page on a POST" when {

        "valid data is submitted" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteesNamePage(index), name).success.value

          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(
                bind[Navigator].qualifiedWith(classOf[LeadTrusteeIndividual]).toInstance(new FakeNavigator())
              )
              .build()

          val request =
            FakeRequest(POST, trusteesNameRoute)
              .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

          application.stop()
        }
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, trusteesNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, trusteesNameRoute)
            .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "for a GET" must {

        def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
          val route = controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

          FakeRequest(GET, route)
        }

        validateIndex(
          arbitrary[FullName],
          TrusteesNamePage.apply,
          getForIndex
        )

      }

      "for a POST" must {
        def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

          val route =
            controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

          FakeRequest(POST, route)
            .withFormUrlEncodedBody(("firstName", "first"), ("middleName", "middle"), ("lastName", "last"))
        }

        validateIndex(
          arbitrary[FullName],
          TrusteesNamePage.apply,
          postForIndex
        )
      }
    }
}
