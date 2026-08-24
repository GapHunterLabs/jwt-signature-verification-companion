package dev.gaphunter.jwtsignatureverificationcompanion.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.gaphunter.jwtsignatureverificationcompanion.detect.JavaUnverifiedParseFinder
import dev.gaphunter.jwtsignatureverificationcompanion.detect.KotlinUnverifiedParseFinder
import dev.gaphunter.jwtsignatureverificationcompanion.model.UnverifiedParseHit
import dev.gaphunter.jwtsignatureverificationcompanion.review.ReviewPrompt

class UnverifiedParseLineMarkerProvider : LineMarkerProviderDescriptor(), DumbAware {

    override fun getName(): String = "JWT parsed without signature verification"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        val file = elements.firstOrNull()?.containingFile ?: return
        val hits = when (file.language.id) {
            "JAVA" -> JavaUnverifiedParseFinder.findAll(file)
            "kotlin" -> KotlinUnverifiedParseFinder.findAll(file)
            else -> emptyList()
        }
        if (hits.isEmpty()) return

        val hitsByElement = hits.associateBy { it.callElement }
        for (element in elements) {
            val hit = hitsByElement[element] ?: continue
            result.add(buildMarker(hit))

            val path = file.virtualFile?.path ?: continue
            val lineNumber = file.viewProvider.document?.getLineNumber(element.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
        }
    }

    private fun buildMarker(hit: UnverifiedParseHit): LineMarkerInfo<PsiElement> {
        val tooltip = "This parses a JWT without verifying its signature -- jjwt's plain parse() " +
            "accepts an unsigned or signature-mismatched token even when a signing key is configured; " +
            "use parseClaimsJws()/parseSignedClaims()/parsePlaintextJws() to actually verify it"
        return LineMarkerInfo(
            hit.callElement,
            hit.callElement.textRange,
            JwtIcons.RISK,
            { _: PsiElement -> tooltip },
            null,
            GutterIconRenderer.Alignment.RIGHT,
            { tooltip },
        )
    }
}
