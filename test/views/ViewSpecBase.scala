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

package views

import base.SpecBase
import models.UserAnswers
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.Assertion
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.AffinityGroup

import scala.reflect.ClassTag

trait ViewSpecBase extends SpecBase {

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  def assertContainsQuestionAnswerPair(doc: Document, questionText: String, answerText: String): Assertion = {
    val question = doc.getElementsMatchingOwnText(questionText)
    val answer = question.next.text
    assert(answer == answerText, "\n\nquestion: " + questionText + " answer: " + answerText + " was not rendered on the page.\n")
  }

  def assertEqualsMessage(doc: Document, cssSelector: String, expectedMessageKey: String, args: Any*): Assertion =
    assertEqualsValue(doc, cssSelector, ViewUtils.breadcrumbTitle(messages(expectedMessageKey, args: _*)))

  def assertEqualsValue(doc: Document, cssSelector: String, expectedValue: String): Assertion = {
    val elements = doc.select(cssSelector)

    if (elements.isEmpty) throw new IllegalArgumentException(s"CSS Selector $cssSelector wasn't rendered.")

    //<p class="govuk-body"> HTML elements are rendered out with a carriage return on some pages, so discount for comparison
    assert(elements.first().html().replace("\n", "") == expectedValue)
  }

  def assertPageTitleEqualsMessage(doc: Document, expectedMessageKey: String, args: Any*): Assertion = {
    val headers = doc.getElementsByTag("h1")

    val expected = messages(expectedMessageKey, args: _*).replaceAll("&nbsp;", " ")

    headers.size mustBe 1
    headers.first.text.replaceAll("\u00a0", " ") mustBe expected
  }

  def assertPageTitleWithCaptionEqualsMessage(doc: Document,
                                              expectedMessageKey: String,
                                              captionParam: String,
                                              args: Any*): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.size mustBe 1

    val expectedCaption = messages(s"$expectedMessageKey.caption", captionParam)

    val expectedHeading = messages(s"$expectedMessageKey.heading", args:_*)

    val expected = s"$expectedCaption $expectedHeading"
      .replaceAll("&nbsp;", " ")

    val actual = headers
      .first
      .text
      .replaceAll("\u00a0", " ")

    actual mustBe expected
  }

  def assertPageTitleWithSectionSubheading(doc: Document,
                                              expectedMessageKey: String,
                                              captionParam: String,
                                              args: Any*): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.size mustBe 1

    val expectedCaption = s"${messages(s"$expectedMessageKey.caption.hidden")} ${messages(s"$expectedMessageKey.caption", captionParam)}"

    val expectedHeading = messages(s"$expectedMessageKey.heading", args:_*)

    val expected = s"$expectedCaption $expectedHeading"
      .replaceAll("&nbsp;", " ")

    val actual = headers
      .first
      .text
      .replaceAll("\u00a0", " ")

    actual mustBe expected
  }

  def assertContainsText(doc: Document, text: String): Assertion =
    assert(doc.toString.contains(text), "\n\ntext " + text + " was not rendered on the page.\n")

  def assertDoesNotContainText(doc: Document, text: String): Assertion =
    assert(!doc.toString.contains(text), "\n\ntext " + text + " was rendered on the page.\n")

  def assertContainsMessages(doc: Document, expectedMessageKeys: String*): Unit = {
    for (key <- expectedMessageKeys) assertContainsText(doc, messages(key))
  }

  def assertAttributeValueForElement(element: Element, attribute: String, attributeValue: String): Assertion = {
    assert(element.attr(attribute) == attributeValue)
  }

  def assertElementHasAttribute(element: Element, attribute: String): Assertion = {
    assert(element.hasAttr(attribute))
  }

  def assertElementDoesNotHaveAttribute(element: Element, attribute: String): Assertion = {
    assert(!element.hasAttr(attribute))
  }

  def assertRenderedById(doc: Document, id: String): Assertion = {
    assert(doc.getElementById(id) != null, "\n\nElement " + id + " was not rendered on the page.\n")
  }

  def assertNotRenderedById(doc: Document, id: String): Assertion = {
    assert(doc.getElementById(id) == null, "\n\nElement " + id + " was rendered on the page.\n")
  }

  def assertRenderedByClass(doc: Document, cssClass: String): Assertion =
    assert(doc.getElementsByClass(cssClass) != null, "\n\nElement " + cssClass + " was not rendered on the page.\n")

  def assertNotRenderedByClass(doc: Document, className: String): Assertion = {
    assert(doc.getElementsByClass(className).isEmpty, "\n\nElement " + className + " was rendered on the page.\n")
  }

  def assertElementNotPresent(doc: Document, elementTag: String): Assertion = {
    assert(doc.getElementsByTag(elementTag).isEmpty, s"\n\nElement $elementTag was rendered on the page.\n")
  }

  def assertRenderedByCssSelector(doc: Document, cssSelector: String): Assertion = {
    assert(!doc.select(cssSelector).isEmpty, "Element " + cssSelector + " was not rendered on the page.")
  }

  def assertNotRenderedByCssSelector(doc: Document, cssSelector: String): Assertion = {
    assert(doc.select(cssSelector).isEmpty, "\n\nElement " + cssSelector + " was rendered on the page.\n")
  }

  def assertContainsLabel(doc: Document, forElement: String, expectedText: String, expectedHintText: Option[String] = None): Any = {
    val labels = doc.getElementsByAttributeValue("for", forElement)
    assert(labels.size == 1, s"\n\nLabel for $forElement was not rendered on the page.")
    val label = labels.first

    assert(label.text().contains(expectedText), s"\n\nLabel for $forElement was not $expectedText")

    assertContainsHint(doc, forElement, expectedHintText)
  }

  def assertContainsHint(doc: Document, forElement: String, expectedHintText: Option[String]): Any = {
    if (expectedHintText.isDefined) {
      assert(doc.getElementsByClass("govuk-hint").first.text == expectedHintText.get,
        s"\n\nLabel for $forElement did not contain hint text $expectedHintText")
    }
  }

  def assertContainsRegProgressLink(doc: Document): Any = {
    assertRenderedById(doc, "reg-progress")
  }

  def assertDoesNotContainRegProgressLink(doc: Document): Any = {
    assertNotRenderedById(doc, "reg-progress")
  }

  def assertContainsClass(element: Element, className: String): Any = {
    assert(element.getElementsByClass(className).size() > 0, s"\n\nPage did not contain element with class $className")
  }

  def assertElementHasClass(element: Element, id: String, expectedClass: String): Assertion = {
    assert(element.getElementById(id).hasClass(expectedClass), s"\n\nElement $id does not have class $expectedClass")
  }

  def assertContainsRadioButton(doc: Document, id: String, name: String, value: String, isChecked: Boolean): Assertion = {
    assertRenderedById(doc, id)
    val radio = doc.getElementById(id)
    assert(radio.attr("name") == name, s"\n\nElement $id does not have name $name")
    assert(radio.attr("value") == value, s"\n\nElement $id does not have value $value")
    if (isChecked) {
      assert(radio.hasAttr("checked"), s"\n\nElement $id is not checked")
    } else {
      assert(!radio.hasAttr("checked"), s"\n\nElement $id is checked")
    }
  }

  def assertContainsTextForId(doc: Document, id: String, expectedText: String): Assertion = {
    assert(doc.getElementById(id).text() == expectedText, s"\n\nElement $id does not have text $expectedText")
  }

  def viewFor[A](data: Option[UserAnswers])(implicit tag: ClassTag[A]): A = {
    val application = applicationBuilder(data).build()
    val view = application.injector.instanceOf[A]
    application.stop()
    view
  }

  def viewForAgent[A](data: Option[UserAnswers])(implicit tag: ClassTag[A]): A = {
    val application = applicationBuilder(data, affinityGroup = AffinityGroup.Agent).build()
    val view = application.injector.instanceOf[A]
    application.stop()
    view
  }

}
