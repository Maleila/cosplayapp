package us.cosplayapp.ui.screen.cosplayDetails

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.ui.screen.cosplay.CosplayUploadUiState
import us.cosplayapp.ui.screen.cosplay.CosplayViewModel

class CosplayDetailsViewModel : ViewModel(

) {
    companion object {
        const val COLLECTION_COSPLAYS = "cosplays"  //where to look in the database
    }

    fun cosList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(CosplayViewModel.COLLECTION_COSPLAYS)
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

    fun getCosplayByName(character: String, cosplays: List<CosplayWithId>): Cosplay {
        for(c in cosplays) {
            if(c.cosplay.character == character) {
                return c.cosplay
            }
        }

        return Cosplay("", "", "","", "", "", "")
    }

    sealed interface CosplayDetailsUIState {
        object Init : CosplayDetailsUIState

        data class Success(val cosList: List<CosplayWithId>) : CosplayDetailsUIState
        data class Error(val error: String?) : CosplayDetailsUIState
    }
}