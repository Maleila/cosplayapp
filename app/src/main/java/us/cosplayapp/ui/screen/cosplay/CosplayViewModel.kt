package us.cosplayapp.ui.screen.cosplay

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.Cosplay.Cosplay

class CosplayViewModel: ViewModel() {
    companion object {
        const val COLLECTION_COSPLAYS = "cosplays"  //where to look in the database
    }

    var filterUiState: CosplayUploadUiState by mutableStateOf(CosplayUploadUiState.Init)

    var cosUploadUiState: CosplayUploadUiState
            by mutableStateOf(CosplayUploadUiState.Init)

    var mediaTypeParam by
        mutableStateOf("Any")

    var complexityParam by
        mutableStateOf("Any")

    var progressParam by
            mutableStateOf("Any")


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
            filter()
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

    //TODO will want to be able to search by media itself but that'll take a searchable dropdown so come back to to that
    private suspend fun query(): MutableList<CosplayWithId> {
        var filtered: MutableList<CosplayWithId> = mutableListOf()
        var filteredIds: MutableList<String> = mutableListOf()
        var cosList: MutableList<Cosplay> = mutableListOf()

        var q = FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).whereEqualTo("uid", "")
        if(mediaTypeParam != "Any") {
            q = q.whereEqualTo("mediaType", mediaTypeParam)
        }
        if(complexityParam != "Any") {
            q = q.whereEqualTo("complexity", complexityParam)
        }
        if(progressParam != "Any") {
            q = q.whereEqualTo("progress", progressParam)
        }

        Log.d("FILTER", mediaTypeParam + ", " + complexityParam + ", " + progressParam)

        q.get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d("SEARCH", document.id)
                filteredIds.add(document.id)
                cosList.add(document.toObject(Cosplay::class.java))
            }
            cosList.forEachIndexed { index, cos ->
                filtered.add(CosplayWithId(filteredIds[index], cos))
            }
        }
        .addOnFailureListener { exception ->
            Log.w("SEARCH", "Error getting documents: ", exception)
        }
        .await() //kinda just tacked this on here not sure about that

        return filtered
    }

    fun filter() {
        //filterUiState = CosplayUploadUiState.Init
        //^^lowkey a bad idea come back to this, pretty sure it'll crash if it errors out
        viewModelScope.launch {
            filterUiState = try {
                val filteredCosplays = query()
                CosplayUploadUiState.Success(
                    filteredCosplays
                )
            } catch (e: Exception) {
                Log.d("FILTER", e.toString())
                CosplayUploadUiState.Error(e?.message.toString())
            }
        }
    }

    fun getCosplayById(id: String, cosplays: List<CosplayWithId>): CosplayWithId {
        for(c in cosplays) {
            if(c.cosId == id) {
                Log.d("SEARCH", c.cosplay.character)
                return c
            }
        }

        return CosplayWithId( "",Cosplay("", "", "","", "", "", ""),)
    }
}

sealed interface CosplayUploadUiState {
    object Init : CosplayUploadUiState
    object LoadingCosplayUpload : CosplayUploadUiState

    object CosplayUploadSuccess: CosplayUploadUiState
    data class Success(val cosList: List<CosplayWithId>) : CosplayUploadUiState
    data class Error(val error: String?) : CosplayUploadUiState
}