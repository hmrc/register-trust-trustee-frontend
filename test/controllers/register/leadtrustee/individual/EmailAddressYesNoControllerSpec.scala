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
import controllers.register.IndexValidation
import forms.YesNoFormProvider
import models.core.pages.FullName
import org.scalacheck.Arbitrary.arbitrary
import pages.register.leadtrustee.individual.{EmailAddressYesNoPage, TrusteesNamePage}
import pages.register.trustees.IsThisLeadTrusteePage
import pages.register.trustees.individual.TrusteeAUKCitizenPage
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.leadtrustee.individual.EmailAddressYesNoView


class EmailAddressYesNoControllerSpec extends SpecBase with IndexValidation {

  def onwardRoute = Call("GET", "/foo")

  val messagePrefix = "leadTrustee.individual.emailAddressYesNo"
  val formProvider = new YesNoFormProvider()
  val form = formProvider.withPrefix(messagePrefix)

  val index = 0
  val trusteeName = FullName("FirstName", None, "LastName").toString

  lazy val emailAddressYesNoRoute = routes.EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url

  "emailAddressYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, emailAddressYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[EmailAddressYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value
        .set(EmailAddressYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, emailAddressYesNoRoute)

      val view = application.injector.instanceOf[EmailAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), fakeDraftId, index, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to NamePage when TrusteesName is not answered" in {
      val userAnswers = emptyUserAnswers
        .set(EmailAddressYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, emailAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.NameController.onPageLoad(index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, emailAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))


      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, emailAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[EmailAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, emailAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, emailAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.NinoYesNoController.onPageLoad(index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[Boolean],
        TrusteeAUKCitizenPage.apply,
        getForIndex
      )

    }

    "for a POST" must {

      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.NinoYesNoController.onPageLoad(index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", "true"))
      }

      validateIndex(
        arbitrary[Boolean],
        TrusteeAUKCitizenPage.apply,
        postForIndex
      )
    }
  }
}