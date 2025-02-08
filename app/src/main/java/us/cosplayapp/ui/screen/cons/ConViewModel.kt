package us.cosplayapp.ui.screen.cons

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import us.cosplayapp.Con.Con
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import us.cosplayapp.Con.ConWithId

class ConViewModel: ViewModel() {

    companion object {
        const val COLLECTION_CONS = "cons"  //where to look in the database
    }

    var conUploadUiState: ConUploadUiState
            by mutableStateOf(ConUploadUiState.Init)

    fun addCon(
        name: String,
        dates: Pair<String?, String?>,
        location: String
    ) {
        conUploadUiState = ConUploadUiState.LoadingConUpload

        var newDateRange = listOf(dates.first!!, dates.second!!)

        val myCon = Con(
            name = name,
            dates = newDateRange,
            location = location
        )

        val postCollection = FirebaseFirestore.getInstance().collection(
            COLLECTION_CONS)
        postCollection.add(myCon).addOnSuccessListener {
            conUploadUiState = ConUploadUiState.ConUploadSuccess
        }.addOnFailureListener{
            conUploadUiState = ConUploadUiState.Error(it.message)
        }
    }

    fun conList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(COLLECTION_CONS)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val conList = snapshot.toObjects(Con::class.java)
                        var conWithIdList = mutableListOf<ConWithId>()

                        conList.forEachIndexed { index, con ->
                            conWithIdList.add(ConWithId(snapshot.documents[index].id, con))
                        }

                        ConUploadUiState.Success(
                            conWithIdList
                        )
                    } else {
                        ConUploadUiState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    suspend fun verifyCon(id: String): Con? {
        val snapshot =
            FirebaseFirestore.getInstance().collection(COLLECTION_CONS).document(id).get().await()
        return snapshot.toObject(Con::class.java)
    }

//  fun verifyCon(id: String) = callbackFlow {
//        val snapshot =
//            FirebaseFirestore.getInstance().collection(COLLECTION_CONS).document(id).get().await()
//        val response = snapshot.toObject(Con::class.java)
//
//        trySend(response) // emit this value through the flow
//    }

}

sealed interface ConUploadUiState {
    object Init : ConUploadUiState
    object LoadingConUpload : ConUploadUiState

    object ConUploadSuccess: ConUploadUiState
    data class Success(val conList: List<ConWithId>) : ConUploadUiState
    data class Error(val error: String?) : ConUploadUiState
}