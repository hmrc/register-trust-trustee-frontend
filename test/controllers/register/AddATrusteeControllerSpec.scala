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
import controllers.register.routes.RemoveIndexController
import controllers.register.trustees.individual.routes.CheckDetailsController
import forms.{AddATrusteeFormProvider, YesNoFormProvider}
import models.Status.Completed
import models.UserAnswers
import models.core.pages.TrusteeOrLeadTrustee.Trustee
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.AddATrustee
import pages.entitystatus.TrusteeStatus
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import pages.register.trustees.individual.NamePage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import views.html.register.{AddATrusteeView, AddATrusteeYesNoView}

class AddATrusteeControllerSpec extends SpecBase {

  private lazy val getRoute : String = routes.AddATrusteeController.onPageLoad(fakeDraftId).url
  private lazy val submitAnotherRoute : String = routes.AddATrusteeController.submitAnother(fakeDraftId).url
  private lazy val submitYesNoRoute : String = routes.AddATrusteeController.submitOne(fakeDraftId).url

  private def changeLink(index: Int): String = CheckDetailsController.onPageLoad(index, fakeDraftId).url
  private def removeLink(index: Int): String = RemoveIndexController.onPageLoad(index, fakeDraftId).url

  private val addTrusteeForm = new AddATrusteeFormProvider()()
  private val yesNoForm: Form[Boolean] = new YesNoFormProvider().withPrefix("addATrusteeYesNo")

  private lazy val trustee = List(
    AddRow("First 0 Last 0", typeLabel = "Trustee Individual", changeLink(0), removeLink(0)),
    AddRow("First 1 Last 1", typeLabel = "Trustee Individual", changeLink(1), removeLink(1))
  )

  private val userAnswersWithTrusteesComplete: UserAnswers = emptyUserAnswers
    .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
    .set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
    .set(NamePage(0), FullName("First 0", None, "Last 0")).success.value
    .set(TrusteeStatus(0), Completed).success.value

    .set(TrusteeOrLeadTrusteePage(1), Trustee).success.value
    .set(TrusteeIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
    .set(NamePage(1), FullName("First 1", None, "Last 1")).success.value
    .set(TrusteeStatus(1), Completed).success.value

  "AddATrustee Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "no trustees" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(yesNoForm, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = yesNoForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }
    }

    "there are trustees" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(addTrusteeForm, fakeDraftId, Nil, trustee, isLeadTrusteeDefined = false, heading = "You have added 2 trustees")(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addTrusteeForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(
            boundForm,
            fakeDraftId,
            Nil,
            trustee,
            isLeadTrusteeDefined = false,
            heading = "You have added 2 trustees"
          )(fakeRequest, messages).toString

        application.stop()
      }

    }

  }
}
