package dev.gaphunter.jwtsignatureverificationcompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaUnverifiedParseFinderTest : BasePlatformTestCase() {

    fun `test plain parse on a jwt parser is flagged`() {
        val file = myFixture.configureByText(
            "TokenService.java",
            """
            class TokenService {
                Object read(String token) {
                    return Jwts.parser().setSigningKey(key).build().parse(token);
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaUnverifiedParseFinder.findAll(file).size)
    }

    fun `test parseClaimsJws on a jwt parser is not flagged`() {
        val file = myFixture.configureByText(
            "TokenService.java",
            """
            class TokenService {
                Object read(String token) {
                    return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaUnverifiedParseFinder.findAll(file).isEmpty())
    }

    fun `test parse on an unrelated receiver is never flagged`() {
        val file = myFixture.configureByText(
            "CsvService.java",
            """
            class CsvService {
                Object read(String line) {
                    return csvFormat.parse(line);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaUnverifiedParseFinder.findAll(file).isEmpty())
    }
}
