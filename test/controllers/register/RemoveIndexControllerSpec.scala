/*
 * Copyright 2026 HM Revenue & Customs
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
import forms.YesNoFormProvider
import models.Status._
import models.UserAnswers
import models.core.pages.IndividualOrBusiness._
import models.core.pages.{FullName, TrusteeOrLeadTrustee}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import pages.entitystatus.TrusteeStatus
import pages.register.leadtrustee.{individual => ltind, organisation => ltorg}
import pages.register.trustees.{individual => tind, organisation => torg}
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import sections.Trustee
import views.html.RemoveIndexView

import scala.concurrent.Future

class RemoveIndexControllerSpec extends SpecBase with IndexValidation {

  private val formProvider                        = new YesNoFormProvider()
  private def form(prefix: String): Form[Boolean] = formProvider.withPrefix(prefix)

  private val index                  = 0
  private val fakeName               = "Test"
  private val fakeFullName: FullName = FullName("John", None, "Doe")
  private val defaultTrusteeName     = "the trustee"
  private val defaultLeadTrusteeName = "the lead trustee"
  private val trusteePrefix          = "removeTrusteeYesNo"
  private val leadPrefix             = "removeLeadTrusteeYesNo"

  private lazy val removeRoute: String = routes.RemoveIndexController.onPageLoad(index, fakeDraftId).url

  "RemoveIndex Controller" must {

    "return OK and the correct view for a GET" when {

      "trustee but type unknown" in {

        val prefix = trusteePrefix

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeStatus(index), InProgress)
          .success
          .value
          .set(TrusteeOrLeadTrusteePage(index), TrusteeOrLeadTrustee.Trustee)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, removeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(prefix), fakeDraftId, index, defaultTrusteeName, prefix)(request, messages).toString

        application.stop()
      }

      "lead trustee but type unknown" in {

        val prefix = leadPrefix

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeStatus(index), InProgress)
          .success
          .value
          .set(TrusteeOrLeadTrusteePage(index), TrusteeOrLeadTrustee.LeadTrustee)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, removeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(prefix), fakeDraftId, index, defaultLeadTrusteeName, prefix)(request, messages).toString

        application.stop()
      }

      "trustee ind" when {

        val prefix = trusteePrefix

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeStatus(index), InProgress)
          .success
          .value
          .set(TrusteeOrLeadTrusteePage(index), TrusteeOrLeadTrustee.Trustee)
          .success
          .value
          .set(TrusteeIndividualOrBusinessPage(index), Individual)
          .success
          .value

        "has no name" in {

          val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

          val request = FakeRequest(GET, removeRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveIndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(prefix), fakeDraftId, index, defaultTrusteeName, prefix)(request, messages).toString

          application.stop()
        }

        "has name" in {

          val userAnswers = baseAnswers
            .set(tind.NamePage(index), fakeFullName)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, removeRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveIndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(prefix), fakeDraftId, index, fakeFullName.toString, prefix)(request, messages).toString

          application.stop()
        }
      }

      "trustee org" when {

        val prefix = trusteePrefix

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeStatus(index), InProgress)
          .success
          .value
          .set(TrusteeOrLeadTrusteePage(index), TrusteeOrLeadTrustee.Trustee)
          .success
          .value
          .set(TrusteeIndividualOrBusinessPage(index), Business)
          .success
          .value

        "has no name" in {

          val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

          val request = FakeRequest(GET, removeRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveIndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(prefix), fakeDraftId, index, defaultTrusteeName, prefix)(request, messages).toString

          application.stop()
        }

        "has name" in {

          val userAnswers = baseAnswers
            .set(torg.NamePage(index), fakeName)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, removeRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveIndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(prefix), fakeDraftId, index, fakeName, prefix)(request, messages).toString

          application.stop()
        }
      }

      "lead trustee ind" when {

        val prefix = leadPrefix

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeStatus(index), InProgress)
          .success
          .value
          .set(TrusteeOrLeadTrusteePage(index), TrusteeOrLeadTrustee.LeadTrustee)
          .success
          .value
          .set(TrusteeIndividualOrBusinessPage(index), Individual)
          .success
          .value

        "has no name" in {

          val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

          val request = FakeRequest(GET, removeRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveIndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(prefix), fakeDraftId, index, defaultLeadTrusteeName, prefix)(request, messages).toString

          application.stop()
        }

        "has name" in {

          val userAnswers = baseAnswers
            .set(ltind.TrusteesNamePage(index), fakeFullName)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, removeRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveIndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(prefix), fakeDraftId, index, fakeFullName.toString, prefix)(request, messages).toString

          application.stop()
        }
      }

      "lead trustee org" when {

        val prefix = leadPrefix

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeStatus(index), InProgress)
          .success
          .value
          .set(TrusteeOrLeadTrusteePage(index), TrusteeOrLeadTrustee.LeadTrustee)
          .success
          .value
          .set(TrusteeIndividualOrBusinessPage(index), Business)
          .success
          .value

        "has no name" in {

          val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

          val request = FakeRequest(GET, removeRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveIndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(prefix), fakeDraftId, index, defaultLeadTrusteeName, prefix)(request, messages).toString

          application.stop()
        }

        "has name" in {

          val userAnswers = baseAnswers
            .set(ltorg.NamePage(index), fakeName)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, removeRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[RemoveIndexView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(prefix), fakeDraftId, index, fakeName, prefix)(request, messages).toString

          application.stop()
        }
      }
    }

    "redirect to add to page" when {

      lazy val addToPageRoute: String = routes.AddATrusteeController.onPageLoad(fakeDraftId).url

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(TrusteeStatus(index), Completed)
        .success
        .value
        .set(TrusteeOrLeadTrusteePage(index), TrusteeOrLeadTrustee.Trustee)
        .success
        .value
        .set(TrusteeIndividualOrBusinessPage(index), Business)
        .success
        .value
        .set(torg.NamePage(index), fakeName)
        .success
        .value
        .set(torg.UtrYesNoPage(index), true)
        .success
        .value
        .set(torg.UtrPage(index), "utr")
        .success
        .value

      "YES is submitted and trustee is removed" in {

        reset(registrationsRepository)
        when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))

        val application =
          applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(POST, removeRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual addToPageRoute

        val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository, times(1)).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(Trustee(index)) mustNot be(defined)

        application.stop()
      }

      "NO is submitted and trustee is not removed" in {

        reset(registrationsRepository)
        when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))

        val application =
          applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(POST, removeRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual addToPageRoute

        val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository, times(0)).set(uaCaptor.capture)(any(), any())

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val prefix = trusteePrefix

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(TrusteeStatus(index), InProgress)
        .success
        .value
        .set(TrusteeOrLeadTrusteePage(index), TrusteeOrLeadTrustee.Trustee)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, removeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form(prefix).bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[RemoveIndexView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, defaultTrusteeName, prefix)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, removeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, removeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
