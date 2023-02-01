/*
 * Copyright 2023 HM Revenue & Customs
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

package views.register.leadtrustee.individual.mld5

import forms.CountryFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.SelectCountryViewBehaviours
import views.html.register.leadtrustee.individual.mld5.CountryOfNationalityView

class CountryOfNationalityViewSpec extends SelectCountryViewBehaviours {

  val prefix = "leadTrustee.individual.5mld.countryOfNationality"
  val index = 0
  val name: String = FullName("FirstName", None, "LastName").toString

  val form: Form[String] = new CountryFormProvider().withPrefix(prefix)

  "countryOfNationality view" must {

    val view = viewFor[CountryOfNationalityView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, fakeDraftId, index, name)(fakeRequest, messages)

    behave like normalPage(applyView(form), prefix)

    behave like pageWithTitle(applyView(form), prefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like selectCountryPage(form, applyView, prefix, name)

    behave like pageWithASubmitButton(applyView(form))
  }
}