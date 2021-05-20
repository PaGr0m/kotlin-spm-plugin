import spm.AFNetworking.*

class ExampleMacOS {
    fun hello() : String = "Hello MacOS from Kotlin"

    fun getManager() : AFHTTPSessionManager {
        return AFHTTPSessionManager.manager()
    }
}

interface ExampleInterface {
    fun hello() : String

    fun getManager() : AFHTTPSessionManager
}
