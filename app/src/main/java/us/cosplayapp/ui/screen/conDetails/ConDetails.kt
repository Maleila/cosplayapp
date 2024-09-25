package us.cosplayapp.ui.screen.conDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.ui.screen.cosplay.Dropdown
import us.cosplayapp.ui.screen.cosplayDetails.CosplayDetailsViewModel

@Composable
fun ConDetails(
    id: String,
    conDetailsViewModel: ConDetailsViewModel = viewModel()
) {

    val conListState = conDetailsViewModel.conList().collectAsState(
        initial = ConDetailsViewModel.ConUIState.Init )

    var showEditDialog by rememberSaveable {
        mutableStateOf(false)
    }

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
                onEditCon = { con ->
                    showEditDialog = true
                },
//                onAddCon = {
//                    showAddConDialogue = true
//                },
                conDetailsViewModel
            )

            if(showEditDialog) {
                EditDialogue(
                    con = ConWithId(id,
                        conDetailsViewModel.getConById(
                            id,
                            (conListState.value as ConDetailsViewModel.ConUIState.Success).conList
                        )), //TODO maybe still not the best way to get this reference
                    conDetailsViewModel
                ) {
                    showEditDialog = false
                }
            }
        }
    }
}

@Composable
fun ConDetails(con: ConWithId,
               onEditCon: (ConWithId) -> Unit = {},
//               onAddCon: () -> Unit = {},
               conDetailsViewModel: ConDetailsViewModel) {
    Column(modifier = Modifier.padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            Text(text = con.con.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp))
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "edit",
                modifier = Modifier.clickable {
                    onEditCon(con)
                },
                tint = Color.White
            )
        }
        Text(text = con.con.location)
        Text(text = con.con.dates[0] + " - " + con.con.dates[1])
    }
}

@Composable
fun EditDialogue(
    con: ConWithId,
    conDetailsViewModel: ConDetailsViewModel,
    onDialogDismiss: () -> Unit = {}
) {
    //code from https://developer.android.com/develop/ui/compose/components/datepickers

    Dialog(
        onDismissRequest = onDialogDismiss
    ) {

        var name by rememberSaveable {
            mutableStateOf(con.con.name)
        }

        var dates by rememberSaveable {
            mutableStateOf(con.con.dates)
        }

        //TODO can't remember why this isn't a pre-defined list...?
        var location by rememberSaveable {
            mutableStateOf(con.con.location)
        }

        Column(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
        ) {
            Text(text = "Con", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                singleLine = true,
                onValueChange = {
                    name = it
                },
                label = { Text(text = "con name")}
            )
            Text(text = "Location", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = location,
                singleLine = true,
                onValueChange = {
                    location = it
                },
                label = { Text(text = "location")}
            )
//            Text(text = "Notes", modifier = Modifier.padding(horizontal = 10.dp))
//            OutlinedTextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = notes,
//                onValueChange = {
//                    notes = it
//                },
//                label = { Text(text = "notes")}
//            )
            Row {
                Button(modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White
                    ),
                    onClick = {
                        conDetailsViewModel.editCon(
                            ConWithId(con.conId,
                                Con(name = name,
                                    dates = con.con.dates,
                                    location = location)
                        ))
                        onDialogDismiss()
                    }) {
                    Text(text = "Save")
                }

            }
        }
    }
}