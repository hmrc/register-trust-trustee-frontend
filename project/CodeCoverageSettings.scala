import sbt.Setting
import scoverage.ScoverageKeys.{coverageMinimumStmtTotal, *}

object CodeCoverageSettings {
  private val settings: Seq[Setting[?]] = Seq(
    coverageExcludedPackages := "<empty>;..*Routes.*;",
    coverageMinimumStmtTotal := 86,
    coverageFailOnMinimum := true,
  coverageHighlighting := true
  )

  def apply(): Seq[Setting[?]] = settings
}
