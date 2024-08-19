package us.cosplayapp.ui.screen.cosplay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.Cosplay.Cosplay

class CosplayViewModel: ViewModel() {
    companion object {
        const val COLLECTION_COSPLAYS = "cosplays"  //where to look in the database
    }

    var cosUploadUiState: CosplayUploadUiState
            by mutableStateOf(CosplayUploadUiState.Init)

    fun addCosplay(
        character: String,
        media: String,
        mediaType: String,
        complexity: String,
        progress: String,
        notes: String
    ) {
        cosUploadUiState = CosplayUploadUiState.LoadingCosplayUpload

        val myCos = Cosplay(
            character = character,
            media = media,
            mediaType = mediaType,
            progress = progress,
            complexity = complexity,
            notes = notes
        )

        val postCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_COSPLAYS)
        postCollection.add(myCos).addOnSuccessListener {
            cosUploadUiState = CosplayUploadUiState.CosplayUploadSuccess
        }.addOnFailureListener{
            cosUploadUiState = CosplayUploadUiState.Error(it.message)
        }
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

                        CosplayUploadUiState.Success(
                            cosWithIdList
                        )
                    } else {
                        CosplayUploadUiState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }
}

sealed interface CosplayUploadUiState {
    object Init : CosplayUploadUiState
    object LoadingCosplayUpload : CosplayUploadUiState

    object CosplayUploadSuccess: CosplayUploadUiState
    data class Success(val cosList: List<CosplayWithId>) : CosplayUploadUiState
    data class Error(val error: String?) : CosplayUploadUiState
}