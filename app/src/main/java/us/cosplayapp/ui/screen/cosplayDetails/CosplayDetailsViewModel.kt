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

    fun changeToDoStatus(cosplay: CosplayWithId, toDo: String, checked: Boolean, index: Int) {
        var newTodo: String
        if(checked) {
            newTodo = "1" + toDo
        } else {
            newTodo = "0" + toDo
        }

        var newTodos: MutableList<String>
                = cosplay.cosplay.toDo.toMutableList()
        newTodos[index] = newTodo

        Log.d("TODO", "updating to " + newTodo)

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(cosplay.cosId)
            .update(mapOf(
                "toDo" to newTodos))
            .addOnSuccessListener { Log.d("tag", "todo item successfully updated!") }
            .addOnFailureListener { e -> Log.w("Error updating todo item", e) }
    }

    fun addToDoItem(toDo: String, checked: Boolean, cosplay: CosplayWithId) {
        var newTodo: String
        if(checked) {
            newTodo = "1" + toDo
        } else {
            newTodo = "0" + toDo
        }

        var newTodos: MutableList<String>
                = cosplay.cosplay.toDo.toMutableList()
        newTodos.add(newTodo)

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(cosplay.cosId)
            .update(mapOf(
                "toDo" to newTodos))
            .addOnSuccessListener { Log.d("tag", "todo item successfully added!") }
            .addOnFailureListener { e -> Log.w("Error adding todo item", e) }
    }

    fun getCosplayById(id: String, cosplays: List<CosplayWithId>): Cosplay {
        for(c in cosplays) {
            if(c.cosId == id) {
                return c.cosplay
            }
        }

        return Cosplay("", "", "","", "", "", "", listOf(""))
    }

    fun editCosplay(newCosplay: Cosplay, characterId: String, cosplays: List<CosplayWithId>) {
        //TODO not sure this is the best way to do it

        Log.d("COSPLAY", characterId)

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(characterId)
            .update(mapOf(
                "character" to newCosplay.character,
                "media" to newCosplay.media,
                "mediaType" to newCosplay.mediaType,
                "progress" to newCosplay.progress,
                "complexity" to newCosplay.complexity,
                "notes" to newCosplay.notes))
            .addOnSuccessListener { Log.d("tag", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("Error updating document", e) }
    }

    sealed interface CosplayDetailsUIState {
        object Init : CosplayDetailsUIState

        data class Success(val cosList: List<CosplayWithId>) : CosplayDetailsUIState
        data class Error(val error: String?) : CosplayDetailsUIState
    }
}