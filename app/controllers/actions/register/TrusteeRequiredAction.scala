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

package controllers.actions.register

import play.api.mvc.Results.Redirect
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import play.api.Logging
import play.api.mvc.{ActionRefiner, Result}
import sections.Trustee

import scala.concurrent.{ExecutionContext, Future}

class TrusteeRequiredAction(index: Int, draftId: String)(implicit val executionContext: ExecutionContext)
    extends ActionRefiner[RegistrationDataRequest, RemoveIndexRequest] with Logging {

  override protected def refine[A](request: RegistrationDataRequest[A]): Future[Either[Result, RemoveIndexRequest[A]]] =
    Future.successful(
      request.userAnswers.get(Trustee(index)) match {
        case Some(trustee) =>
          Right(RemoveIndexRequest(request, trustee))
        case _             =>
          logger.info(s"Unable to remove trustee. Did not find trustee at index $index")
          Left(Redirect(controllers.register.routes.AddATrusteeController.onPageLoad(draftId)))
      }
    )

}

class TrusteeRequiredActionImpl @Inject() (implicit val executionContext: ExecutionContext) {
  def apply(index: Int, draftId: String): TrusteeRequiredAction = new TrusteeRequiredAction(index, draftId)
}
