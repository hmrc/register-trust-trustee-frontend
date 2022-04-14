/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.actions.register.leadtrustee.organisation

import play.api.mvc.Results.Redirect
import controllers.actions.register.UkRegisteredYesNoRequest
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import pages.register.leadtrustee.organisation.UkRegisteredYesNoPage
import play.api.i18n.MessagesApi
import play.api.mvc.{ActionRefiner, Result}

import scala.concurrent.{ExecutionContext, Future}

class UkRegisteredRequiredAction(index: Int)(implicit val executionContext: ExecutionContext)
  extends ActionRefiner[RegistrationDataRequest, UkRegisteredYesNoRequest] {

  override protected def refine[A](request: RegistrationDataRequest[A]): Future[Either[Result, UkRegisteredYesNoRequest[A]]] = {

    Future.successful(
      request.userAnswers.get(UkRegisteredYesNoPage(index)) match {
        case None =>
          Left(
            Redirect(controllers.routes.SessionExpiredController.onPageLoad())
          )
        case Some(value) =>
          Right(
            UkRegisteredYesNoRequest(
              request,
              value
            )
          )
      }
    )
  }
}

class UkRegisteredRequiredActionImpl @Inject()(implicit val executionContext: ExecutionContext, val messagesApi: MessagesApi) {
  def apply(index: Int): UkRegisteredRequiredAction = new UkRegisteredRequiredAction(index)
}
