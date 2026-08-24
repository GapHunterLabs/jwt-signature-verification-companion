package dev.gaphunter.jwtsignatureverificationcompanion.model

import com.intellij.psi.PsiElement

/** One `Jwts.parser()...parse(token)` call site with no signature verification. */
data class UnverifiedParseHit(val callElement: PsiElement)
