package co.early.fore.net

import co.early.fore.DelegateTestSynchronousCopy
import co.early.fore.core.delegate.Fore
import okio.Buffer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PrettifierJsonTest {

    private lateinit var delegate: DelegateTestSynchronousCopy

    @BeforeTest
    fun setup() {

        // make the code run synchronously, reroute Log.x to
        // System.out.println() so we see it in the test log
        delegate = DelegateTestSynchronousCopy()
        Fore.setDelegate(delegate)
    }

    @AfterTest
    fun cleanup() {
        delegate.cleanup()
    }

    @Test
    fun `pretty print simple JSON object`() {
        val input = """{"key":"value"}"""
        val expected = """
            {
              "key":"value"
            }
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print JSON list of objects`() {
        val input = """[{"name":"orange","isCitrus":true,"tastyPercentScore":43}]"""
        val expected = """
            [
              {
                "name":"orange",
                "isCitrus":true,
                "tastyPercentScore":43
              }
            ]
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print already formatted JSON`() {
        val input = """[
              {
                "name":"orange",
                "isCitrus":true,
                "tastyPercentScore":43
              }
            ]"""
        val expected = """
            [
              {
                "name":"orange",
                "isCitrus":true,
                "tastyPercentScore":43
              }
            ]
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print JSON with nested objects`() {
        val input = """{"key1":{"key2":{"key3":"value"}}}"""
        val expected = """
            {
              "key1":{
                "key2":{
                  "key3":"value"
                }
              }
            }
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print JSON with arrays`() {
        val input = """{"array":[1,2,3]}"""
        val expected = """
            {
              "array":[
                1,
                2,
                3
              ]
            }
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print JSON with mixed structures`() {
        val input = """{"array":[1,{"key":"value"},3],"object":{"nestedKey":"nestedValue"}}"""
        val expected = """
            {
              "array":[
                1,
                {
                  "key":"value"
                },
                3
              ],
              "object":{
                "nestedKey":"nestedValue"
              }
            }
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print invalid JSON structure`() {
        val input = """{key: "value"}"""
        val expected = """
            {
              key:"value"
            }
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print empty input`() {
        val input = ""
        val expected = ""
        assertEquals(expected, prettyPrint(input))
    }

    @Test
    fun `pretty print truncated JSON`() {
        val input = """{"array":[1,{"key":"value"},3],"obj"""
        val expected = """
            {
              "array":[
                1,
                {
                  "key":"value"
                },
                3
              ],
              "obj
        """.trimIndent()
        assertEquals(expected, prettyPrint(input))
    }

    private fun prettyPrint(json: String): String {
        val buffer = Buffer().writeUtf8(json)
        return buffer.jsonPrettyPrint()
    }
}
