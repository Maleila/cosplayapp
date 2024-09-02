package us.cosplayapp.ui.screen.conDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId

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

    sealed interface ConUIState {
        object Init : ConUIState

        data class Success(val conList: List<ConWithId>) : ConUIState
        data class Error(val error: String?) : ConUIState
    }
}

