package us.cosplayapp.ui.screen.conDetails

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import us.cosplayapp.Con.Con
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.ui.screen.cosplay.Dropdown
import us.cosplayapp.ui.screen.cosplayDetails.CheckList
import us.cosplayapp.ui.screen.cosplayDetails.CosplayDetailsViewModel
import us.cosplayapp.ui.screen.cosplayDetails.TransparentTextField
import us.cosplayapp.ui.screen.cosplayDetails.deleteConDialog

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

    var cosplayToDelete by rememberSaveable {
        mutableStateOf<String?>(null)

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
                val haptics = LocalHapticFeedback.current
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
                    onLongButtonClick = {cosplay ->
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        cosplayToDelete = cosplay
                    },
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
                if(cosplayToDelete != null) {
                    deleteCosplanDialog(
                        onDismiss = { cosplayToDelete = null},
                        onDelete = { conDetailsViewModel.deleteCosplan(mycon, cosplayToDelete!!)}
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun ConDetails(con: ConWithId,
               onEditCon: (ConWithId) -> Unit = {},
               onAddCosplan: () -> Unit = {},
               conDetailsViewModel: ConDetailsViewModel,
               onNavigateToCosplayDetails: (String) -> Unit,
               onLongButtonClick: (String) -> Unit,
                cosListState: ConDetailsViewModel.CosplayUIState) {
    Column(modifier = Modifier.padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            Text(text = con.con.name,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(10.dp),
                softWrap = true,
                textAlign = TextAlign.Center)
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
                tint = MaterialTheme.colorScheme.secondary)
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
                tint = MaterialTheme.colorScheme.secondary)
            Text(
                text = con.con.dates[0] + " - " + con.con.dates[1],
                modifier = Modifier.padding(10.dp))
        }
        Text(
            text = "Cosplans",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(10.dp)
        )
        FlowRow() {
            con.con.cosplans.forEach { cos ->
                if(cos.trim() != "") {
//                    Button(onClick = { conDetailsViewModel.getIdByCosplay(cos, (cosListState as ConDetailsViewModel.CosplayUIState.Success).cosList)
//                        ?.let { onNavigateToCosplayDetails(it) } },
//                        modifier = Modifier.padding(5.dp),
//                        colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = MaterialTheme.colorScheme.secondary,
//                        containerColor = MaterialTheme.colorScheme.surfaceVariant
//                    )) {
//                        Text(text = cos)
//                    }
                    Surface(modifier = Modifier.padding(5.dp)
                        .combinedClickable(
                            onClick = { conDetailsViewModel.getIdByCosplay(cos, (cosListState as ConDetailsViewModel.CosplayUIState.Success).cosList)
                        ?.let { onNavigateToCosplayDetails(it) } },
                            onLongClick = {
                                onLongButtonClick(cos)
                            }
                        ),
                        color = MaterialTheme.colorScheme.surfaceVariant) {
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
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.fillMaxHeight(0.02f))
        Text(
            text = "to do",
            style = MaterialTheme.typography.displayMedium,
            //modifier = Modifier.padding(10.dp)
        )
        CheckList(con,
            con.con.toDo,
            onEditItem = {cosplay, editedTodo, checked, index ->
                conDetailsViewModel.changeToDoStatus(
                    con,
                    editedTodo,
                    checked,
                    index
                )
            },
            onAddItem = { newTodo, newCheck, cosplay ->
                conDetailsViewModel.addToDoItem(newTodo, newCheck, cosplay)
            },
            onDeleteItem = { cosplay, index ->
                conDetailsViewModel.deleteToDo(cosplay, index)
            }
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
            },
                colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )) {
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun deleteCosplanDialog(onDismiss: () -> Unit, onDelete: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {

        Column(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
        ) {
            Button(modifier = Modifier.padding(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick =  {
                    onDelete()
                    onDismiss()
                }) {
                Text(text = "Delete")
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
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
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

@Composable
fun CheckList(con: ConWithId,
              items: List<String>,
              onEditItem: (ConWithId, String, Boolean, Int) -> Unit,
              onAddItem: (String, Boolean, ConWithId) -> Unit,
              onDeleteItem: (ConWithId, Int) -> Unit) {

    var showAddTodo by rememberSaveable {
        mutableStateOf(false)
    }
    var newTodo by rememberSaveable {
        mutableStateOf("")
    }

    var newCheck by rememberSaveable {
        mutableStateOf(false)
    }

    Column {
        items.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                if(item != "") {
                    var editedTodo by rememberSaveable {
                        mutableStateOf(item.substring(1))
                    }
                    var itemModifiable by rememberSaveable {
                        mutableStateOf(false)
                    }
                    Row(modifier = Modifier.fillMaxWidth(0.8f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = item[0] == '1',
                            onCheckedChange = {
                                onEditItem(con, item.substring(1), it, index)
                            }
                        )
                        //can't use the same textfield bc one needs to be the actual to-do
                        //so it updates w the database
                        //and one needs to be a local var that the user can modify
                        if(!itemModifiable) {
                            BasicTextField(
                                value = item.substring(1),
                                onValueChange = {
                                    editedTodo = it
                                },
                                enabled = false,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable {
                                        itemModifiable = true
                                        editedTodo = item.substring(1)
                                        //should also focus so you don't have to click twice
                                    },
                                textStyle = TextStyle(MaterialTheme.colorScheme.secondary),
                                decorationBox = { innerTextField ->
                                    innerTextField() // No decoration, just the text and cursor
                                })
                        }
                        if(itemModifiable) {
                            BasicTextField(
                                value = editedTodo,
                                onValueChange = {
                                    editedTodo = it
                                },
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable {
                                        itemModifiable = true
                                        //should also focus so you don't have to click twice
                                    },
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
                                textStyle = TextStyle(MaterialTheme.colorScheme.secondary),
                                decorationBox = { innerTextField ->
                                    innerTextField() // No decoration, just the text and cursor
                                })
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically) {
                        if (itemModifiable) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "edit list item",
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable {
                                        onEditItem(con, editedTodo, item[0] == '1', index)
                                        itemModifiable = false
                                    },
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "delete list item",
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    onDeleteItem(con, index)
                                },
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        if(showAddTodo) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = newCheck,
                        onCheckedChange = {
                            newCheck = it
                        }
                    )
                    TransparentTextField(value = newTodo,
                        onValueChange = {
                            newTodo = it
                        })
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "set new to-do item",
                            modifier = Modifier
                                .padding(12.dp)
                                .clickable {
                                    onAddItem(newTodo, newCheck, con)
                                    showAddTodo = false
                                    newTodo = ""
                                    newCheck = false
                                },
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

        }
        Icon(
            imageVector = Icons.Filled.AddCircleOutline,
            contentDescription = "add to-do item",
            modifier = Modifier
                .padding(12.dp)
                .clickable {
                    showAddTodo = true
                },
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}