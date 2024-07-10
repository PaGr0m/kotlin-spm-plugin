import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.Foundation.NSProgress
import platform.Foundation.NSURLSessionDataTask
import spm.AFNetworking.AFHTTPSessionManager
import spm.AFNetworking.AFNetworkReachabilityStatus
import spm.AFNetworking.AFNetworkReachabilityStatusNotReachable

actual class Network actual constructor() {
    actual fun hello(): String = "Hello iOS from Kotlin"

    @OptIn(ExperimentalForeignApi::class)
    fun manager(): String = AFHTTPSessionManager.manager().toString()

    @OptIn(ExperimentalForeignApi::class)
    fun status() = AFNetworkReachabilityStatusNotReachable

    @OptIn(ExperimentalForeignApi::class)
    fun restGet(): String {
        val manager = AFHTTPSessionManager.manager()
        val url = "https://httpbin.org/get"

        var returnValue = "null"
        manager.GET(
            url,
            null,
            null,
            progress = null,
            success = { nsurlSessionDataTask: NSURLSessionDataTask?, any: Any? ->
                returnValue = "Success"
            },
            failure = { nsurlSessionDataTask: NSURLSessionDataTask?, nsError: NSError? ->
                returnValue = "Failure"
            }
        )

        println(returnValue)

        return returnValue
    }
}
