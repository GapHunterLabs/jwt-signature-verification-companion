package dev.gaphunter.jwtsignatureverificationcompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethodCallExpression
import dev.gaphunter.jwtsignatureverificationcompanion.model.UnverifiedParseHit

/**
 * Finds `Jwts.parser()...parse(token)` call chains (jjwt) -- the
 * `parse` method accepts a JWT whose signature is empty or doesn't
 * match, even when a signing key has been configured on the parser.
 * jjwt's own issue tracker and CodeQL's "Missing JWT signature check"
 * query both document this: the caller must use `parseClaimsJws`,
 * `parseSignedClaims`, or `parsePlaintextJws` to actually verify the
 * signature.
 *
 * **v0.1 scope, stated honestly:** matches by simple text/name only,
 * walking up the call chain looking for "jwt" + "parser"/"parserBuilder"
 * -- doesn't resolve the real jjwt classpath type, so a differently
 * named parser with an unrelated `.parse(...)` method is a possible
 * (rare) false positive, and an aliased/wrapped jjwt parser call isn't
 * covered.
 */
object JavaUnverifiedParseFinder {

    fun findAll(file: PsiFile): List<UnverifiedParseHit> {
        val hits = mutableListOf<UnverifiedParseHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                hitFor(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitFor(call: PsiMethodCallExpression): UnverifiedParseHit? {
        val methodName = call.methodExpression.referenceName ?: return null
        if (!JwtParseSignals.isUnverifiedParseCall(methodName)) return null

        val qualifier = call.methodExpression.qualifierExpression ?: return null
        if (!JwtParseSignals.looksLikeJwtParserReceiver(qualifier.text)) return null

        return UnverifiedParseHit(leafOf(call.methodExpression))
    }

    /** Descends to a real leaf PSI element -- LineMarkerInfo must never anchor on a composite node (SDK_GOTCHAS.md SS20). */
    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
