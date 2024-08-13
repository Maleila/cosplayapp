package us.cosplayapp.Con

data class Con(
    var uid: String = "",
    var name: String = "",
    var date: String = "",
    var location: String = ""
)

data class ConWithId(
    val conId: String,
    val con: Con
)
