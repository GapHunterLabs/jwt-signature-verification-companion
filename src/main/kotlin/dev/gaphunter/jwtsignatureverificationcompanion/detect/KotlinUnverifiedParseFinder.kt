package dev.gaphunter.jwtsignatureverificationcompanion.detect

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dev.gaphunter.jwtsignatureverificationcompanion.model.UnverifiedParseHit
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaUnverifiedParseFinder]. */
object KotlinUnverifiedParseFinder {

    fun findAll(file: PsiFile): List<UnverifiedParseHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<UnverifiedParseHit>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                hitFor(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitFor(expression: KtDotQualifiedExpression): UnverifiedParseHit? {
        val call = expression.selectorExpression as? KtCallExpression ?: return null
        val methodName = call.calleeExpression?.text ?: return null
        if (!JwtParseSignals.isUnverifiedParseCall(methodName)) return null

        val receiver = expression.receiverExpression
        if (!JwtParseSignals.looksLikeJwtParserReceiver(receiver.text)) return null

        return UnverifiedParseHit(leafOf(call.calleeExpression!!))
    }

    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
