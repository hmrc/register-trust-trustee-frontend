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

package controllers

import base.SpecBase
import models.UserAnswers
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import pages.register.TrusteeOrLeadTrusteePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  private val featureFlagService: FeatureFlagService = mock[FeatureFlagService]

  "Index Controller" when {

    "pre-existing user answers" must {

      "redirect to TrusteesInfoController when there are no trustees" in {
        
        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
          .build()
        
        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.routes.TrusteesInfoController.onPageLoad(fakeDraftId).url

        application.stop()
      }

      "redirect to AddATrusteeController when there are trustees" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
          .build()
        
        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.register.routes.AddATrusteeController.onPageLoad(fakeDraftId).url

        application.stop()
      }
    }

    "update value of is5mldEnabled in user answers" in {

      reset(registrationsRepository)

      val userAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
        .build()

      when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
      when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
      when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

      route(application, request).value.map { _ =>
        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

        uaCaptor.getValue.is5mldEnabled mustBe true

        application.stop()
      }
    }
  }

  "no pre-existing user answers" must {
    "instantiate new set of user answers" when {

      "5mld enabled" must {

        "add is5mldEnabled = true to user answers" in {

          reset(registrationsRepository)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
            .build()

          when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
          when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
          when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

          route(application, request).value.map { _ =>
            val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

            uaCaptor.getValue.is5mldEnabled mustBe true
            uaCaptor.getValue.draftId mustBe fakeDraftId
            uaCaptor.getValue.internalAuthId mustBe "id"

            application.stop()
          }
        }
      }

      "5mld not enabled" must {

        "add is5mldEnabled = false to user answers" in {

          reset(registrationsRepository)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
            .build()

          when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
          when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
          when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

          route(application, request).value.map { _ =>
            val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

            uaCaptor.getValue.is5mldEnabled mustBe false
            uaCaptor.getValue.draftId mustBe fakeDraftId
            uaCaptor.getValue.internalAuthId mustBe "id"

            application.stop()
          }
        }
      }
    }
  }
}
