package us.cosplayapp.ui.screen.conDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.ui.screen.cosplayDetails.CosplayDetailsViewModel

class ConDetailsViewModel: ViewModel() {

    companion object {
        const val COLLECTION_CONS = "cons"  //where to look in the database
    }

    fun conList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(COLLECTION_CONS)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val consList = snapshot.toObjects(Con::class.java)
                        var consWithIdList = mutableListOf<ConWithId>()

                        consList.forEachIndexed { index, con ->
                            consWithIdList.add(ConWithId(snapshot.documents[index].id, con))
                        }

                        ConUIState.Success(
                            consWithIdList
                        )
                    } else {
                        ConUIState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun getConById(id: String, cons: List<ConWithId>): Con {
        for(c in cons) {
            if(c.conId == id) {
                Log.d("CON", c.con.name)
                return c.con
            }
        }

        return Con("", "", listOf(""),"")
    }

    fun editCon(newCon: ConWithId) {
        Log.d("EDITCON", newCon.conId)

        FirebaseFirestore.getInstance().collection(COLLECTION_CONS).document(newCon.conId)
            .update(mapOf(
                "name" to newCon.con.name,
                "dates" to newCon.con.dates,
                "location" to newCon.con.location))
            .addOnSuccessListener { Log.d("tag", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("Error updating document", e) }
    }

    fun deleteCon(id: String) {
        FirebaseFirestore.getInstance().collection(COLLECTION_CONS).document(id)
            .delete()
            .addOnSuccessListener { Log.d("DELETE", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("DELETE", "Error deleting document", e) }
    }

    sealed interface ConUIState {
        object Init : ConUIState

        data class Success(val conList: List<ConWithId>) : ConUIState
        data class Error(val error: String?) : ConUIState
    }
}

