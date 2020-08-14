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

package utils

import controllers.register.leadtrustee.organisation.{routes => ltoRts}
import controllers.register.trustees.organisation.{routes => toRts}
import controllers.register.trustees.individual.{routes => tiRts}
import models.Status.{Completed, InProgress}
import models.UserAnswers
import models.core.pages.IndividualOrBusiness
import models.core.pages.IndividualOrBusiness.{Business, Individual}
import play.api.i18n.Messages
import sections.Trustees
import viewmodels._
import viewmodels.addAnother.TrusteeViewModel

class AddATrusteeViewHelper(userAnswers: UserAnswers, draftId: String)(implicit messages: Messages) {

  private def render(trustee : (TrusteeViewModel, Int)) : AddRow = {

    val viewModel = trustee._1
    val index = trustee._2

    val nameOfTrustee = viewModel.name.getOrElse(messages("entities.no.name.added"))

    def renderForLead(message : String) = s"${messages("entities.lead")} $message"

    val trusteeType = viewModel.`type` match {
      case Some(k : IndividualOrBusiness) =>
        val key = messages(s"entities.trustee.$k")

        if(viewModel.isLead) renderForLead(key) else key
      case None =>
        s"${messages("entities.trustee")}"
    }

    case class ChangeLink(inProgressRoute: String, completedRoute: String)

    val changeLink: String = {
      viewModel match {
        case TrusteeViewModel(false, _, Some(Individual), InProgress) =>
          tiRts.NameController.onPageLoad(index, draftId).url
        case TrusteeViewModel(false, _, Some(Individual), Completed) =>
          controllers.routes.FeatureNotAvailableController.onPageLoad().url
        case TrusteeViewModel(false, _, Some(Business), InProgress) =>
          toRts.NameController.onPageLoad(index, draftId).url
        case TrusteeViewModel(false, _, Some(Business), Completed) =>
          toRts.CheckDetailsController.onPageLoad(index, draftId).url
        case TrusteeViewModel(true, _, Some(Individual), InProgress) =>
          controllers.routes.FeatureNotAvailableController.onPageLoad().url
        case TrusteeViewModel(true, _, Some(Individual), Completed) =>
          controllers.routes.FeatureNotAvailableController.onPageLoad().url
        case TrusteeViewModel(true, _, Some(Business), InProgress) =>
          ltoRts.UkRegisteredYesNoController.onPageLoad(index, draftId).url
        case TrusteeViewModel(true, _, Some(Business), Completed) =>
          ltoRts.CheckDetailsController.onPageLoad(index, draftId).url
        case _ => controllers.routes.FeatureNotAvailableController.onPageLoad().url
      }
    }

    val removeLink = viewModel.`type` match {
      case Some(Individual) =>
        controllers.register.trustees.individual.routes.RemoveTrusteeController.onPageLoad(index, draftId).url
      case Some(Business) =>
        controllers.register.trustees.organisation.routes.RemoveTrusteeOrgController.onPageLoad(index, draftId).url
      case _ =>
        controllers.routes.FeatureNotAvailableController.onPageLoad().url
    }

    AddRow(
      name = nameOfTrustee,
      typeLabel = trusteeType,
      changeUrl = changeLink,
      removeUrl = removeLink
    )
  }

  def rows : AddToRows = {
    val trustees = userAnswers.get(Trustees).toList.flatten.zipWithIndex

    val complete = trustees.filter(_._1.status == Completed).map(render)

    val inProgress = trustees.filter(_._1.status == InProgress).map(render)

    AddToRows(inProgress, complete)
  }

}
