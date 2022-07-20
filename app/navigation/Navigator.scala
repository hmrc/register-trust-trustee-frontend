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

package navigation

import config.FrontendAppConfig
import controllers.register.routes._
import models.ReadableUserAnswers
import models.core.pages.IndividualOrBusiness
import models.core.pages.TrusteeOrLeadTrustee._
import models.registration.pages.AddATrustee
import pages._
import pages.register._
import play.api.mvc.Call
import sections.Trustees
import uk.gov.hmrc.http.HttpVerbs.GET

class Navigator {

  def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers)
              (implicit config: FrontendAppConfig): Call = routes(draftId)(config)(page)(userAnswers)

  private def routes(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse
      conditionalNavigation(draftId)

  def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case TrusteeOrLeadTrusteePage(index) => _ => TrusteeIndividualOrBusinessController.onPageLoad(index, draftId)
    case TrusteesAnswerPage => _ => AddATrusteeController.onPageLoad(draftId)
  }

  def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case TrusteeIndividualOrBusinessPage(index) => ua =>
      trusteeTypeJourney(ua, index, draftId)

    case AddATrusteeYesNoPage => ua =>
      yesNoNav(ua, AddATrusteeYesNoPage, TrusteeOrLeadTrusteeController.onPageLoad(0, draftId), registrationTaskList(draftId))

    case AddATrusteePage => ua => {
      def routeToTrusteeIndex: Call = {
        val trustees = ua.get(Trustees).getOrElse(List.empty)
        TrusteeOrLeadTrusteeController.onPageLoad(trustees.size, draftId)
      }
      ua.get(AddATrusteePage) match {
        case Some(AddATrustee.YesNow) => routeToTrusteeIndex
        case Some(AddATrustee.YesLater) | Some(AddATrustee.NoComplete) => registrationTaskList(draftId)
        case _ => controllers.routes.SessionExpiredController.onPageLoad
      }
    }
  }

  def yesNoNav(ua: ReadableUserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad)
  }

  private def registrationTaskList(draftId: String)(implicit config: FrontendAppConfig): Call = {
    Call(GET, config.registrationProgressUrl(draftId))
  }

  private def trusteeTypeJourney(userAnswers: ReadableUserAnswers, index: Int, draftId: String): Call = {
    (userAnswers.get(TrusteeOrLeadTrusteePage(index)), userAnswers.get(TrusteeIndividualOrBusinessPage(index))) match {
      case (Some(Trustee), Some(IndividualOrBusiness.Individual)) =>
        controllers.register.trustees.individual.routes.NameController.onPageLoad(index, draftId)
      case (Some(LeadTrustee), Some(IndividualOrBusiness.Individual)) =>
        controllers.register.leadtrustee.individual.routes.NameController.onPageLoad(index, draftId)
      case (Some(Trustee), Some(IndividualOrBusiness.Business)) =>
        controllers.register.trustees.organisation.routes.NameController.onPageLoad(index, draftId)
      case (Some(LeadTrustee), Some(IndividualOrBusiness.Business)) =>
        controllers.register.leadtrustee.organisation.routes.UkRegisteredYesNoController.onPageLoad(index, draftId)
      case _ =>
        controllers.routes.SessionExpiredController.onPageLoad
    }
  }
}
