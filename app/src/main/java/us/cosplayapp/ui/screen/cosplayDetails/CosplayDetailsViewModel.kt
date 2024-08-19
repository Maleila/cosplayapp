package us.cosplayapp.ui.screen.cosplayDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.ui.screen.cosplay.CosplayViewModel

class CosplayDetailsViewModel : ViewModel(

) {
    companion object {
        const val COLLECTION_COSPLAYS = "cosplays"  //where to look in the database
    }

    fun cosList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val cosList = snapshot.toObjects(Cosplay::class.java)
                        var cosWithIdList = mutableListOf<CosplayWithId>()

                        cosList.forEachIndexed { index, cos ->
                            cosWithIdList.add(CosplayWithId(snapshot.documents[index].id, cos))
                        }

                        CosplayDetailsUIState.Success(
                            cosWithIdList
                        )
                    } else {
                        CosplayDetailsUIState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

//    fun getCosplayByName(character: String, cosplays: List<CosplayWithId>): Cosplay {
//        for(c in cosplays) {
//            if(c.cosplay.character == character) {
//                return c.cosplay
//            }
//        }
//
//        return Cosplay("", "", "","", "", "", "")
//    }

    fun getCosplayById(id: String, cosplays: List<CosplayWithId>): Cosplay {
        for(c in cosplays) {
            if(c.cosId == id) {
                return c.cosplay
            }
        }

        return Cosplay("", "", "","", "", "", "")
    }

    fun editCosplay(newCosplay: Cosplay, characterId: String, cosplays: List<CosplayWithId>) {
        //TODO not sure this is the best way to do it

//        Log.d("COSPLAY", character)
//        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS)
//            .whereEqualTo("character", newCosplay.character)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.d("COSPLAY", "Found the character!")
//                    Log.d("COSPLAY", "${document.id} => ${document.data}")
//                    cosRef = document.id
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("COSPLAY", "Error getting documents: ", exception)
//            }

        Log.d("COSPLAY", characterId)

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(characterId)
            .update(mapOf(
                "character" to newCosplay.character,
                "media" to newCosplay.media,
                "mediaType" to newCosplay.mediaType,
                "progress" to newCosplay.progress,
                "complexity" to newCosplay.complexity))
            .addOnSuccessListener { Log.d("tag", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("Error updating document", e) }
    }

    sealed interface CosplayDetailsUIState {
        object Init : CosplayDetailsUIState

        data class Success(val cosList: List<CosplayWithId>) : CosplayDetailsUIState
        data class Error(val error: String?) : CosplayDetailsUIState
    }
}