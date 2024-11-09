package us.cosplayapp.ui.screen.cosplayDetails

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.Cosplay.CosplayWithId

class CosplayDetailsViewModel : ViewModel(

) {
    companion object {
        const val COLLECTION_COSPLAYS = "cosplays"  //where to look in the database
    }

    var imageUri by mutableStateOf<Uri?>(null)

    fun uploadImage(id: String) {

        imageUri?.let { uri ->
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images").child("ace books.jpg")
            Log.d("IMAGE", imageRef.toString())
            Log.d("IMAGE", uri!!.toString())
            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                // Image upload successful
                //Toast.makeText(LocalContext.current,"Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                Log.d("IMAGE", "image uploaded!")
            }.addOnFailureListener { e ->
                // Image upload failed
                //Toast.makeText(application, "Image upload failed: $e", Toast.LENGTH_SHORT).show()
                Log.d("IMAGE", "image upload issue")
            }
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

    fun conList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection("cons")
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

    fun getConsList(cons: List<ConWithId>): List<String> {
        var consList = mutableListOf<String>()

        cons.forEach {
            consList.add(it.con.name)
        }

        return consList
    }

    fun addCon(con: String, cosplay: CosplayWithId) {
        var newConsList: MutableList<String>
                = cosplay.cosplay.consList.toMutableList()
        newConsList.add(con)
        //not a great fix cause this only removes this after someone clicks the add button
        if(newConsList[0] == "") {
            newConsList.removeAt(0)
        }

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(cosplay.cosId)
            .update(mapOf(
                "consList" to newConsList))
            .addOnSuccessListener { Log.d("tag", "con successfully added!") }
            .addOnFailureListener { e -> Log.w("Error adding con", e) }
    }

    fun changeToDoStatus(cosplay: CosplayWithId, toDo: String, checked: Boolean, index: Int) {
        var newTodo: String = if(checked) {
            "1$toDo"
        } else {
            "0$toDo"
        }

        var newTodos: MutableList<String>
                = cosplay.cosplay.toDo.toMutableList()
        newTodos[index] = newTodo

        Log.d("TODO", "updating to $newTodo")

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(cosplay.cosId)
            .update(mapOf(
                "toDo" to newTodos))
            .addOnSuccessListener { Log.d("tag", "todo item successfully updated!") }
            .addOnFailureListener { e -> Log.w("Error updating todo item", e) }
    }

    fun changeChecklistItemStatus(cosplay: CosplayWithId, item: String, checked: Boolean, index: Int) {
        var newItem: String = if(checked) {
            "1$item"
        } else {
            "0$item"
        }

        var newChecklist: MutableList<String>
                = cosplay.cosplay.checklist.toMutableList()
        newChecklist[index] = newItem

        Log.d("CHECKLIST", "updating to $newItem")

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(cosplay.cosId)
            .update(mapOf(
                "checklist" to newChecklist))
            .addOnSuccessListener { Log.d("tag", "checklist item successfully updated!") }
            .addOnFailureListener { e -> Log.w("Error updating checklist item", e) }
    }

    fun addToDoItem(toDo: String, checked: Boolean, cosplay: CosplayWithId) {
        var newTodo: String = if(checked) {
            "1$toDo"
        } else {
            "0$toDo"
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

    fun addChecklistItem(item: String, checked: Boolean, cosplay: CosplayWithId) {
        var newItem: String = if(checked) {
            "1$item"
        } else {
            "0$item"
        }

        var newItems: MutableList<String>
                = cosplay.cosplay.checklist.toMutableList()
        newItems.add(newItem)

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(cosplay.cosId)
            .update(mapOf(
                "checklist" to newItems))
            .addOnSuccessListener { Log.d("tag", "todo item successfully added!") }
            .addOnFailureListener { e -> Log.w("Error adding todo item", e) }
    }
    fun deleteToDo(cosplay: CosplayWithId, index: Int) {
        var newTodos = cosplay.cosplay.toDo.toMutableList()
        Log.d("DELETE", "list pre-delete:")
        newTodos.forEachIndexed { index, item ->
            Log.d("DELETE", "$index: $item")
        }

        Log.d("DELETE", "deleting item #$index")
        Log.d("DELETE", newTodos.removeAt(index))

        Log.d("DELETE", "new list:")
        newTodos.forEachIndexed { index, item ->
            Log.d("DELETE", "$index: $item")
        }

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(cosplay.cosId)
            .update(mapOf(
                "toDo" to newTodos))
            .addOnSuccessListener { Log.d("DELETE", "todo item successfully removed!") }
            .addOnFailureListener { e -> Log.w("Error removing todo item", e) }
    }

    //TODO these functions for the 2 checklists could probably be optimized for less duplicate code
    fun deleteChecklistItem(cosplay: CosplayWithId, index: Int) {
        var newItems = cosplay.cosplay.checklist.toMutableList()

        newItems.removeAt(index)

        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(cosplay.cosId)
            .update(mapOf(
                "checklist" to newItems))
            .addOnSuccessListener { Log.d("DELETE", "todo item successfully removed!") }
            .addOnFailureListener { e -> Log.w("Error removing todo item", e) }
    }

    fun getCosplayById(id: String, cosplays: List<CosplayWithId>): Cosplay {
        for(c in cosplays) {
            if(c.cosId == id) {
                return c.cosplay
            }
        }

        return Cosplay("", "", "","", "", "", "", listOf(""))
    }

    fun editCosplay(newCosplay: Cosplay, characterId: String) {
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

    fun deleteCosplay(id: String) {
        FirebaseFirestore.getInstance().collection(COLLECTION_COSPLAYS).document(id)
            .delete()
            .addOnSuccessListener { Log.d("DELETE", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("DELETE", "Error deleting document", e) }
    }

    sealed interface CosplayDetailsUIState {
        object Init : CosplayDetailsUIState

        data class Success(val cosList: List<CosplayWithId>) : CosplayDetailsUIState
        data class Error(val error: String?) : CosplayDetailsUIState
    }
    sealed interface ConUIState {
        object Init : ConUIState

        data class Success(val conList: List<ConWithId>) : ConUIState
        data class Error(val error: String?) : ConUIState
    }
}