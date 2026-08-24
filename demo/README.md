# Demo data — JWT Signature Verification Companion

For capturing the real Marketplace screenshot:

1. `./gradlew runIde`
2. Open `demo/src/main/java/com/acmecorp/auth/TokenService.java` as a
   scratch/standalone file (or drop it into any sandbox project) inside
   the sandbox IDE.
3. The `Jwts.parser()...parse(token)` call inside `readLegacyToken`
   shows the gutter warning icon — hover it for the tooltip. The
   `readToken` method's `parseClaimsJws` call stays clean, for
   contrast.
4. Enter Full Screen (`View > Appearance > Enter Full Screen`), capture
   with `Win+Shift+S`, save directly to `docs/screenshots/` in this
   repo.
