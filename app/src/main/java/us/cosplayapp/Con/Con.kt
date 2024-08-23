package us.cosplayapp.Con

data class Con(
    var uid: String = "",
    var name: String = "",
    var dates: List<String> = listOf(""),
    var location: String = ""
)

data class ConWithId(
    val conId: String,
    val con: Con
)
