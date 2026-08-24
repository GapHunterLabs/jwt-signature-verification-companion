package dev.gaphunter.jwtsignatureverificationcompanion.detect

/**
 * jjwt (io.jsonwebtoken) is the confirmed target for v0.1: its own
 * issue tracker and CodeQL's "Missing JWT signature check" query
 * document the real footgun -- `Jwts.parser()...parse(token)` accepts
 * an unsigned or signature-mismatched JWT, while `parseClaimsJws`/
 * `parseSignedClaims`/`parsePlaintextJws` correctly verify it.
 */
object JwtParseSignals {
    private val PARSER_BUILDER_NAMES = listOf("parser", "parserbuilder")
    private val UNVERIFIED_PARSE_METHOD = "parse"
    private val VERIFIED_PARSE_METHODS = listOf(
        "parseclaimsjws",
        "parsesignedclaims",
        "parseplaintextjws",
        "parsesignedcontent",
        "parsecontent",
    )

    fun looksLikeJwtParserReceiver(chainText: String): Boolean {
        val lower = chainText.lowercase()
        return PARSER_BUILDER_NAMES.any { lower.contains(it) } && lower.contains("jwt")
    }

    fun isUnverifiedParseCall(methodName: String): Boolean =
        methodName.equals(UNVERIFIED_PARSE_METHOD, ignoreCase = true)

    fun isVerifiedParseCall(methodName: String): Boolean =
        VERIFIED_PARSE_METHODS.any { it.equals(methodName, ignoreCase = true) }
}
