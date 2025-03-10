package us.cosplayapp.Cosplay

data class Cosplay(
    var uid: String = "",
    var character: String = "",
    var media: String = "",
    var mediaType: String = "",
    var progress: String = "",
    var complexity: String = "",
    var notes: String = "",
    var toDo: List<String> = listOf(""),
    var checklist: List<String> = listOf(""),
    var consList: List<String> = listOf(""),
    var referencePics: List<String> = listOf("")
)

data class CosplayWithId(
    val cosId: String,
    val cosplay: Cosplay
)