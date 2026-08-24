package dev.gaphunter.jwtsignatureverificationcompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinUnverifiedParseFinderTest : BasePlatformTestCase() {

    fun `test plain parse on a jwt parser is flagged`() {
        val file = myFixture.configureByText(
            "TokenService.kt",
            """
            class TokenService {
                fun read(token: String): Any {
                    return Jwts.parser().setSigningKey(key).build().parse(token)
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinUnverifiedParseFinder.findAll(file).size)
    }

    fun `test parseClaimsJws on a jwt parser is not flagged`() {
        val file = myFixture.configureByText(
            "TokenService.kt",
            """
            class TokenService {
                fun read(token: String): Any {
                    return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token)
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinUnverifiedParseFinder.findAll(file).isEmpty())
    }

    fun `test parse on an unrelated receiver is never flagged`() {
        val file = myFixture.configureByText(
            "CsvService.kt",
            """
            class CsvService {
                fun read(line: String): Any {
                    return csvFormat.parse(line)
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinUnverifiedParseFinder.findAll(file).isEmpty())
    }
}
