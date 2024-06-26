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

package controllers

import base.SpecBase
import connectors.SubmissionDraftConnector
import models.TaskStatus.InProgress
import models.UserAnswers
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import org.mockito.{ArgumentCaptor, Mockito}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.TrusteeOrLeadTrusteePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustsStoreService
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with ScalaCheckPropertyChecks {

  private val trustsStoreService: TrustsStoreService = Mockito.mock(classOf[TrustsStoreService])
  private val submissionDraftConnector: SubmissionDraftConnector = Mockito.mock(classOf[SubmissionDraftConnector])

  private val utr: String = "1234567890"

  def beforeTest(isTaxable: Boolean = true, utr: Option[String] = None): Unit = {
    reset(registrationsRepository)
    when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))

    reset(trustsStoreService)
    when(trustsStoreService.updateTaskStatus(any(), any())(any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

    reset(submissionDraftConnector)
    when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(isTaxable))
    when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(utr))
  }

  "Index Controller" when {

    "pre-existing user answers" must {

      "redirect to TrusteesInfoController when there are no trustees" in {

        beforeTest()

        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsStoreService].toInstance(trustsStoreService),
            bind[SubmissionDraftConnector].toInstance(submissionDraftConnector)
          ).build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.routes.TrusteesInfoController.onPageLoad(fakeDraftId).url

        verify(trustsStoreService).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

        application.stop()
      }

      "redirect to AddATrusteeController when there are trustees" in {

        beforeTest()

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsStoreService].toInstance(trustsStoreService),
            bind[SubmissionDraftConnector].toInstance(submissionDraftConnector)
          ).build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.routes.AddATrusteeController.onPageLoad(fakeDraftId).url

        verify(trustsStoreService).updateTaskStatus(any(), eqTo(InProgress))(any(), any())

        application.stop()
      }
    }

    "update value of isTaxable and utr in user answers" in {

      beforeTest(utr = Some(utr))

      val userAnswers = emptyUserAnswers.copy(isTaxable = false, existingTrustUtr = None)

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[TrustsStoreService].toInstance(trustsStoreService),
          bind[SubmissionDraftConnector].toInstance(submissionDraftConnector)
        ).build()

      when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

      route(application, request).value.map { _ =>
        val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

        uaCaptor.getValue.isTaxable mustBe true
        uaCaptor.getValue.existingTrustUtr.get mustBe utr

        application.stop()
      }
    }
  }

  "no pre-existing user answers" must {
    "instantiate new set of user answers" in {

      forAll(arbitrary[Boolean], arbitrary[Option[String]]) {
        (isTrustTaxable, utr) =>
          beforeTest(isTaxable = isTrustTaxable, utr = utr)

          val application = applicationBuilder(userAnswers = None)
            .overrides(
              bind[TrustsStoreService].toInstance(trustsStoreService),
              bind[SubmissionDraftConnector].toInstance(submissionDraftConnector)
            ).build()

          when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

          route(application, request).value.map { _ =>
            val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

            uaCaptor.getValue.isTaxable mustBe isTrustTaxable
            uaCaptor.getValue.existingTrustUtr mustBe utr
            uaCaptor.getValue.draftId mustBe fakeDraftId
            uaCaptor.getValue.internalAuthId mustBe "id"

            application.stop()
          }
      }
    }
  }
}
