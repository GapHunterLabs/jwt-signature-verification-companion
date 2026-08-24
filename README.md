# JWT Signature Verification Companion

Warning icon on a jjwt `Jwts.parser()...parse(token)` call chain — jjwt's
own `parse` method accepts a JWT whose signature is empty or doesn't
match, even when a signing key has been configured on the parser. This
is a documented real vulnerability class: CodeQL ships a dedicated
"Missing JWT signature check" query for exactly this pattern, and
jjwt's own issue tracker confirms it. The fix is easy to miss but
simple — use `parseClaimsJws()`, `parseSignedClaims()`, or
`parsePlaintextJws()` instead, which actually verify the signature.

## Why it exists

`Jwts.parser().setSigningKey(key).build().parse(token)` compiles fine,
runs fine, and looks like it verifies the token — a signing key was
even configured. It doesn't. An attacker can hand this code an
unsigned or mismatched-signature JWT and it gets accepted, exactly the
"algorithm confusion" / signature-bypass class of bug this plugin
exists to catch before it ships.

## Why built this way

- **100% static text/PSI analysis** — matches the call chain by simple
  text (a "jwt" + "parser"/"parserBuilder" receiver, plus a bare
  `parse(...)` call), so it works whether the real jjwt jar is on the
  classpath or not. Java and Kotlin.

## v0.1 scope — stated honestly, not exhaustively

Targets jjwt specifically (the most common Java JWT library) and
matches by simple text, not real type resolution — an unrelated
`.parse(...)` method on a variable that happens to be named/called
like a JWT parser is a possible (rare) false positive, and an aliased
or wrapped jjwt parser call isn't covered.

## Usage

Open any Java/Kotlin file that parses JWTs with jjwt. A `Jwts.parser()`
chain ending in the plain `parse(...)` call shows a warning icon;
`parseClaimsJws`/`parseSignedClaims`/`parsePlaintextJws` stay clean.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
