import spm.AFNetworking.*

class ExampleMacOS : ExampleInterface {
    override fun hello() : String = "Hello from MacOS"

    override fun getManager() : AFHTTPSessionManager {
        return AFHTTPSessionManager.manager()
    }
}

interface ExampleInterface {
    fun hello() : String

    fun getManager() : AFHTTPSessionManager
}
