package us.cosplayapp.Con

data class Con(
    var uid: String = "",
    var name: String = "",
    var dates: List<String> = listOf(""),
    var location: String = "",
    var cosplans: List<String> = listOf("")
)

data class ConWithId(
    val conId: String,
    val con: Con
)
