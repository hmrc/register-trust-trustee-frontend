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

package views.register.trustees.individual

import forms.PassportOrIdCardFormProvider
import models.core.pages.FullName
import models.registration.pages.PassportOrIdCardDetails
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptions
import views.behaviours.QuestionViewBehaviours
import views.html.register.trustees.individual.PassportDetailsView

class PassportDetailsViewSpec extends QuestionViewBehaviours[PassportOrIdCardDetails] {

  val messageKeyPrefix = "trustee.individual.passportDetails"

  val name = FullName("First", None ,"last")

  lazy val form = injector.instanceOf[PassportOrIdCardFormProvider].apply("trustee.individual.passportDetails")

  lazy val countryOptions: Seq[InputOption] = injector.instanceOf[CountryOptions].options

  "PassportDetailsView view" must {

    val view = viewFor[PassportDetailsView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, "draftId", 0, name.toString)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    "fields" must {

      behave like pageWithPassportOrIDCardDetailsFields(
        form,
        applyView,
        messageKeyPrefix,
        controllers.register.trustees.individual.routes.PassportDetailsController.onSubmit(0, "draftId").url,
        Seq(("country", None), ("number", None)),
        "expiryDate",
        name.toString
      )
    }

    behave like pageWithASubmitButton(applyView(form))

  }
}
