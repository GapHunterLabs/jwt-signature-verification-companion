package com.acmecorp.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * Demo data for JWT Signature Verification Companion — used with
 * `./gradlew runIde` to capture the real Marketplace screenshot. Open
 * this file, the warning icon should appear on the plain `parse` call
 * inside `readLegacyToken`.
 */
public class TokenService {

    public Jws<Claims> readToken(String token) {
        // Correctly verifies the signature -- NOT flagged.
        return Jwts.parser().setSigningKey(signingKey).build().parseClaimsJws(token);
    }

    public Object readLegacyToken(String token) {
        // Plain parse() -- accepts an unsigned or mismatched-signature
        // token even though a signing key was configured. FLAGGED.
        return Jwts.parser().setSigningKey(signingKey).build().parse(token);
    }
}
