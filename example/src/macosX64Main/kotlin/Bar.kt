import spm.AFNetworking.AFNetworkReachabilityStatus
import spm.Alamofire.SWIFT_TYPEDEFS

fun main() {
    println(AFNetworkReachabilityStatus.MAX_VALUE)
    println(SWIFT_TYPEDEFS)

    val d = firstFunction()
    val s = secondFunction()

    println(d)
    println(s)
}

fun firstFunction(): Double {
    return 3.14
}

fun secondFunction(): String = "Hello world"