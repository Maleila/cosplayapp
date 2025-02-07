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

    fun getUpcomingCons(cons: List<ConWithId>): List<ConWithId> {
        var upcomingCons = mutableListOf<ConWithId>()
        cons.forEach {
            if(checkConDate(it.con.dates.first())) {
                upcomingCons.add(it)
            }
        }
        return upcomingCons
    }

    fun checkConDate(date: String): Boolean {
    //inspired by answers from https://stackoverflow.com/questions/10774871/best-way-to-compare-dates-in-android
        val toDayCalendar = Calendar.getInstance()
        val today = toDayCalendar.time

        val tomorrowCalendar: Calendar = Calendar.getInstance()
        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 90)
        val window = tomorrowCalendar.time

        Log.d("UPCOMING CONS", window.toString())

        Log.d("UPCOMING CONS", date)

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        var conDate: Date? = null
        try {
            conDate = sdf.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (conDate!!.before(window) && conDate!!.after(today)) {
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