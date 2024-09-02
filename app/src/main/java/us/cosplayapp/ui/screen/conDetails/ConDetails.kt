package us.cosplayapp.ui.screen.conDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.Con.ConWithId

@Composable
fun ConDetails(
    id: String,
    conDetailsViewModel: ConDetailsViewModel = viewModel()
) {

    val conListState = conDetailsViewModel.conList().collectAsState(
        initial = ConDetailsViewModel.ConUIState.Init )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start) {

        if (conListState.value == ConDetailsViewModel.ConUIState.Init) {
            Text(text = "loading")
        } else {
            ConDetails(
                con = ConWithId(id,
                    conDetailsViewModel.getConById(
                        id,
                        (conListState.value as ConDetailsViewModel.ConUIState.Success).conList
                    )),
//                onEditCosplay = { cosplay ->
//                    showEditDialog = true
//                },
//                onAddCon = {
//                    showAddConDialogue = true
//                },
                conDetailsViewModel
            )

//            if(showEditDialog) {
//                EditDialogue(
//                    cosplay = cosplayDetailsViewModel.getCosplayById(
//                        id,
//                        (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
//                    ), //TODO maybe still not the best way to get this reference
//                    id,
//                    cosplayDetailsViewModel
//                ) {
//                    showEditDialog = false
//                }
//            }
        }
    }
}

@Composable
fun ConDetails(con: ConWithId,
//               onEditCosplay: (Cosplay) -> Unit = {},
//               onAddCon: () -> Unit = {},
               conDetailsViewModel: ConDetailsViewModel) {
    Column() {
        Text(text = con.con.name)
    }
}