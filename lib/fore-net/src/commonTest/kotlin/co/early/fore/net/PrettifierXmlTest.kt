package co.early.fore.net

import co.early.fore.core.delegate.Fore
import co.early.fore.core.delegate.TestDelegateDefault
import okio.Buffer
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PrettifierXmlTest {

    @BeforeTest
    fun setup(){
        Fore.setDelegate(TestDelegateDefault())
    }

    @Test
    fun `pretty print empty HTML`() {
        val input = ""
        val expected = ""
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print single tag`() {
        val input = "<html>"
        val expected = """
            <html>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print self-closing tag`() {
        val input = "<br/>"
        val expected = """
            <br/>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print single tag with content`() {
        val input = "<p>Hello, World!</p>"
        val expected = """
            <p>Hello, World!</p>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print nested tags`() {
        val input = "<html><body><p>Text</p></body></html>"
        val expected = """
            <html>
              <body>
                <p>Text</p>
              </body>
            </html>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print tags with attributes`() {
        val input = """<div class="container"><p id="intro">Welcome</p></div>"""
        val expected = """
            <div class="container">
              <p id="intro">Welcome</p>
            </div>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print complex HTML`() {
        val input = """
            <html>
            <head><title>Test</title></head>
            <body>
              <div>
                <p>Hello</p>
                <br/>
              </div>
            </body>
            </html>
        """.trimIndent()
        val expected = """
            <html>
              <head>
                <title>Test</title>
              </head>
              <body>
                <div>
                  <p>Hello</p>
                  <br/>
                </div>
              </body>
            </html>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print closing tags only`() {
        val input = "</div></p></body>"
        val expected = """
            </div>
            </p>
            </body>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print with special comments`() {
        val input = "<!-- This is a comment --><div></div>"
        val expected = """
            <!-- This is a comment -->
            <div>
            </div>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print with processing instructions`() {
        val input = "<?xml version=\"1.0\"?><data></data>"
        val expected = """
            <?xml version="1.0"?>
            <data>
            </data>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print truncated HTML`() {
        val input = """
            <html>
            <head><title>Test</title></head>
            <body>
              <div>
                <p>Hello</p>
                <br/>
              </di
        """.trimIndent()
        val expected = """
            <html>
              <head>
                <title>Test</title>
              </head>
              <body>
                <div>
                  <p>Hello</p>
                  <br/>
                </di
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print truncated HTML 2`() {
        val input = """
            <html>
            <head><title>Test</title></head>
            <body>
              <div
        """.trimIndent()
        val expected = """
            <html>
              <head>
                <title>Test</title>
              </head>
              <body>
                <div
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print XML`() {
        val input = """
            <?xml version="1.0" encoding="UTF-8"?>
            <response><status>success</status><message>Request processed successfully</message>
            <data><user><id>12345</id><username>john_doe</username>
            <email>john.doe@example.com</email><fullName>John Doe</fullName>
            <createdAt>2024-12-17T14:45:00Z</createdAt></user><transaction>
            <transactionId>98765</transactionId><amount>150.00</amount><currency>USD</currency>
            <status>completed</status><timestamp>2024-12-17T14:45:15Z</timestamp></transaction>
            </data></response>
        """.trimIndent()
        val expected = """
            <?xml version="1.0" encoding="UTF-8"?>
            <response>
              <status>success</status>
              <message>Request processed successfully</message>
              <data>
                <user>
                  <id>12345</id>
                  <username>john_doe</username>
                  <email>john.doe@example.com</email>
                  <fullName>John Doe</fullName>
                  <createdAt>2024-12-17T14:45:00Z</createdAt>
                </user>
                <transaction>
                  <transactionId>98765</transactionId>
                  <amount>150.00</amount>
                  <currency>USD</currency>
                  <status>completed</status>
                  <timestamp>2024-12-17T14:45:15Z</timestamp>
                </transaction>
              </data>
            </response>
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    private fun prettyPrint(input: String): String {
        val buffer = Buffer().writeUtf8(input)
        return buffer.xmlPrettyPrint()
    }
}
