package us.cosplayapp.ui.screen.home

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import androidx.core.net.ParseException
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.Cosplay.CosplayWithId
import java.util.Date

class HomeViewModel: ViewModel() {

    fun cosList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection("cosplays")
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val cosList = snapshot.toObjects(Cosplay::class.java)
                        var cosWithIdList = mutableListOf<CosplayWithId>()

                        cosList.forEachIndexed { index, cos ->
                            cosWithIdList.add(CosplayWithId(snapshot.documents[index].id, cos))
                        }

                        CosplayUIState.Success(
                            cosWithIdList
                        )
                    } else {
                        CosplayUIState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    //TODO I'm using these all over the place, is there a way to make these global?
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

    fun getUpcomingCons(date: String): Boolean {

        val toDayCalendar = Calendar.getInstance()
        val date1 = toDayCalendar.time

        val tomorrowCalendar: Calendar = Calendar.getInstance()
        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 30)
        val date2 = tomorrowCalendar.time

        Log.d("UPCOMING CONS", date2.toString())

        Log.d("UPCOMING CONS", date)

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        var conDate: Date? = null
        try {
            conDate = sdf.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (conDate!!.before(date2) && conDate!!.after(date1)) {
            return true
        }

        return false
    }

    sealed interface CosplayUIState {
        object Init : CosplayUIState

        data class Success(val cosList: List<CosplayWithId>) : CosplayUIState
        data class Error(val error: String?) : CosplayUIState
    }
    sealed interface ConUIState {
        object Init : ConUIState

        data class Success(val conList: List<ConWithId>) : ConUIState
        data class Error(val error: String?) : ConUIState
    }
}