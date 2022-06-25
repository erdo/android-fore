package co.early.fore.net.testhelpers

import java.io.IOException

/**
 *
 * @param <R> The class type expected to be passed back as the result
</R> */
class StubbedServiceDefinition<R> {
    val httpCode: Int
    val resourceFileName: String
    val mimeType: String
    val ioException: IOException?
    val expectedResult: R?

    constructor(ioException: IOException, expectedResult: R) {
        httpCode = 0
        resourceFileName = "none"
        mimeType = "application/json"
        this.ioException = ioException
        this.expectedResult = expectedResult
    }

    constructor(httpCode: Int, resourceFileName: String, expectedResult: R) : this(
        httpCode,
        resourceFileName,
        "application/json",
        expectedResult
    ) {
    }

    @JvmOverloads
    constructor(
        httpCode: Int,
        resourceFileName: String,
        mimeType: String = "application/json",
        expectedResult: R? = null
    ) {
        this.httpCode = httpCode
        this.resourceFileName = resourceFileName
        this.mimeType = mimeType
        ioException = null
        this.expectedResult = expectedResult
    }

    fun successfullyConnected(): Boolean {
        return ioException == null
    }
}
