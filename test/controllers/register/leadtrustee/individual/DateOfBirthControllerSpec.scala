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

package controllers.register.leadtrustee.individual

import base.SpecBase
import config.annotations.LeadTrusteeIndividual
import controllers.register.IndexValidation
import forms.DateFormProvider
import models.UserAnswers
import models.core.pages.FullName
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.register.leadtrustee.individual.{MatchedYesNoPage, TrusteesDateOfBirthPage, TrusteesNamePage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.leadtrustee.individual.DateOfBirthView

import java.time.{LocalDate, ZoneOffset}

class DateOfBirthControllerSpec extends SpecBase with MockitoSugar with IndexValidation {

  val messagePrefix = "leadTrustee.individual.dateOfBirth"
  val formProvider = new DateFormProvider(frontendAppConfig)
  val validAnswer: LocalDate = LocalDate.now(ZoneOffset.UTC)
  val index = 0
  val name: FullName = FullName("FirstName", None, "LastName")

  lazy val trusteesDateOfBirthRoute: String = routes.DateOfBirthController.onPageLoad(index, fakeDraftId).url

  "DateOfBirth Controller" when {

    val form = formProvider.withConfig(messagePrefix, matchingLeadTrustee = true)
    val baseUserAnswers: UserAnswers = emptyUserAnswers
      .set(TrusteesNamePage(index), name).success.value

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request = FakeRequest(GET, trusteesDateOfBirthRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DateOfBirthView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, name.toString, readOnly = false)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when lead trustee matched" in {

      val userAnswers = baseUserAnswers
        .set(MatchedYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesDateOfBirthRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DateOfBirthView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, name.toString, readOnly = true)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseUserAnswers
        .set(TrusteesDateOfBirthPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteesDateOfBirthRoute)

      val view = application.injector.instanceOf[DateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, index, name.toString, readOnly = false)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(baseUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[LeadTrusteeIndividual]).toInstance(new FakeNavigator())
          )
          .build()

      val request =
        FakeRequest(POST, trusteesDateOfBirthRoute)
          .withFormUrlEncodedBody(
            "value.day" -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year" -> validAnswer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseUserAnswers)).build()

      val request =
        FakeRequest(POST, trusteesDateOfBirthRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[DateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, name.toString, readOnly = false)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteesDateOfBirthRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteesDateOfBirthRoute)
          .withFormUrlEncodedBody(
            "value.day" -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year" -> validAnswer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }


    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        val route = routes.DateOfBirthController.onPageLoad(index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        Gen.const(validAnswer),
        TrusteesDateOfBirthPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          routes.DateOfBirthController.onPageLoad(index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(
            "value.day" -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year" -> validAnswer.getYear.toString
          )
      }

      validateIndex(
        Gen.const(validAnswer),
        TrusteesDateOfBirthPage.apply,
        postForIndex
      )
    }
  }
}
