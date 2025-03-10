package us.cosplayapp.ui.screen.conDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.ui.screen.cosplay.Dropdown

@Composable
fun ConDetails(
    id: String,
    conDetailsViewModel: ConDetailsViewModel = viewModel(),
    onNavigateToCosplayDetails: (String) -> Unit,
    onDeleteCon: () -> Unit
) {

    val conListState = conDetailsViewModel.conList().collectAsState(
        initial = ConDetailsViewModel.ConUIState.Init )

    val cosListState = conDetailsViewModel.cosList().collectAsState(
        initial = ConDetailsViewModel.CosplayUIState.Init)

    var showEditDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var isDeleted by rememberSaveable {
        mutableStateOf(false)
    }

    var showAddCosplanDialogue by rememberSaveable {
        mutableStateOf(false)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start) {

        if (conListState.value == ConDetailsViewModel.ConUIState.Init) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = "loading",
                    modifier = Modifier.padding(10.dp)
                )
                LinearProgressIndicator(modifier = Modifier.width(75.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant)
            }
        } else {
            if(!isDeleted) {
                var mycon = ConWithId(
                    id,
                    conDetailsViewModel.getConById(
                        id,
                        (conListState.value as ConDetailsViewModel.ConUIState.Success).conList
                    )
                )

                ConDetails(
                    con = mycon,
                    onEditCon = {
                        showEditDialog = true
                    },
                    onAddCosplan = {
                        showAddCosplanDialogue = true
                    },
                    conDetailsViewModel,
                    onNavigateToCosplayDetails,
                    cosListState.value
                )

                if (showEditDialog) {
                    EditDialogue(
                        con = mycon,
                        conDetailsViewModel,
                        onDialogDismiss = {
                            showEditDialog = false
                        },
                        onDelete = { id ->
                            isDeleted = true
                            conDetailsViewModel.deleteCon(
                                id
                            )
                            onDeleteCon()
                        }
                    )
                }
                if (showAddCosplanDialogue) {
                    AddCosplanDialogue(
                        (cosListState.value as ConDetailsViewModel.CosplayUIState.Success).cosList,
                        con = mycon,
                        conDetailsViewModel = conDetailsViewModel,
                        onDialogDismiss = {
                            showAddCosplanDialogue = false
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConDetails(con: ConWithId,
               onEditCon: (ConWithId) -> Unit = {},
               onAddCosplan: () -> Unit = {},
               conDetailsViewModel: ConDetailsViewModel,
               onNavigateToCosplayDetails: (String) -> Unit,
                cosListState: ConDetailsViewModel.CosplayUIState) {
    Column(modifier = Modifier.padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            Text(text = con.con.name,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(10.dp))
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "edit",
                modifier = Modifier.clickable {
                    onEditCon(con)
                },
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(imageVector = Icons.Filled.LocationOn,
                contentDescription = "location marker",
                modifier = Modifier.clickable {
                    onEditCon(con)
                },
                tint = Color.White)
            Text(
                text = con.con.location,
                modifier = Modifier.padding(10.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.CalendarMonth,
                contentDescription = "date",
                modifier = Modifier.clickable {
                    onEditCon(con)
                },
                tint = Color.White)
            Text(
                text = con.con.dates[0] + " - " + con.con.dates[1],
                modifier = Modifier.padding(10.dp))
        }
        Text(
            text = "Cosplans",
            style = MaterialTheme.typography.bodyLarge,
            //modifier = Modifier.padding(10.dp)
        )
        FlowRow() {
            con.con.cosplans.forEach { cos ->
                if(cos.trim() != "") {
                    Button(onClick = { conDetailsViewModel.getIdByCosplay(cos, (cosListState as ConDetailsViewModel.CosplayUIState.Success).cosList)
                        ?.let { onNavigateToCosplayDetails(it) } },
                        modifier = Modifier.padding(5.dp)) {
                        Text(text = cos)
                    }
                }

            }
        }
        Icon(
            imageVector = Icons.Filled.AddCircleOutline,
            contentDescription = "add cos",
            modifier = Modifier
                .padding(12.dp)
                .clickable {
                    onAddCosplan()
                },
            tint = Color.White
        )
    }
}

@Composable
fun AddCosplanDialogue(
    cosplays: List<CosplayWithId>,
    con: ConWithId,
    conDetailsViewModel: ConDetailsViewModel,
    onDialogDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = {
        onDialogDismiss()
    }) {
        var cosList by rememberSaveable {
            mutableStateOf(conDetailsViewModel.getCosplaysList(cosplays))
        }

        var cos by rememberSaveable {
            mutableStateOf(cosList[0])
        }
        Column(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
        ) {
            Text(text = "Cosplay", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                list = cosList,
                preselected = cosList[0],
                onSelectionChanged = {
                    cos = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            Button(onClick = {
                conDetailsViewModel.addCosplan(con, cos)
                onDialogDismiss()
            }) {
                Text(text = "Add")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialogue(
    con: ConWithId,
    conDetailsViewModel: ConDetailsViewModel,
    onDialogDismiss: () -> Unit = {},
    onDelete: (String) -> Unit
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
                Button(modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Red
                    ),
                    onClick = {
                        onDialogDismiss()
                        onDelete(con.conId)
                    }) {
                    Text(text = "Delete")
                }

            }
        }
    }
}