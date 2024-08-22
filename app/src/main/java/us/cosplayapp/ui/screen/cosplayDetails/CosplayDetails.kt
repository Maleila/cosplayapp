package us.cosplayapp.ui.screen.cosplayDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.ui.screen.cosplay.CosplayViewModel
import us.cosplayapp.ui.screen.cosplay.Dropdown

@Composable

fun CosplayDetails(
    id: String,
    cosplayDetailsViewModel: CosplayDetailsViewModel = viewModel())
{

    val cosplayListState = cosplayDetailsViewModel.cosList().collectAsState(
        initial = CosplayDetailsViewModel.CosplayDetailsUIState.Init
    )

    var showEditDialog by rememberSaveable {
    mutableStateOf(false)
}

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start) {

        if (cosplayListState.value == CosplayDetailsViewModel.CosplayDetailsUIState.Init) {
            Text(text = "loading")
        } else {
            cosplayDetails(
                cosplay = CosplayWithId(id,
                    cosplayDetailsViewModel.getCosplayById(
                        id,
                        (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                    )),
                onEditCosplay = { cosplay ->
                    showEditDialog = true
                },
                cosplayDetailsViewModel
            )

            if(showEditDialog) {
                editDialogue(
                    cosplay = cosplayDetailsViewModel.getCosplayById(
                        id,
                        (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                    ), //TODO maybe still not the best way to get this reference
                    id,
                    cosplayDetailsViewModel,
                    onDialogDismiss = {
                        showEditDialog = false
                    },
                    (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun cosplayDetails(cosplay: CosplayWithId,
                   onEditCosplay: (Cosplay) -> Unit = {},
                   cosplayDetailsViewModel: CosplayDetailsViewModel)
{
    var charTextColor: Color
    charTextColor = if(cosplay.cosplay.progress == "Not started") {
        Color.Red
    } else if(cosplay.cosplay.progress == "In Progress") {
        Color(0xFFFF9800)
    } else {
        Color.Green
    }

    var showAddTodo by rememberSaveable {
        mutableStateOf(false)
    }
    var newTodo by rememberSaveable {
        mutableStateOf("")
    }

    var newCheck by rememberSaveable {
        mutableStateOf(false)
    }

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center) {
        Text(
            text = cosplay.cosplay.character,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp),
            color = charTextColor
        )
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "edit",
            modifier = Modifier.clickable {
                onEditCosplay(cosplay.cosplay)
            },
            tint = Color.White
        )
    }
    Spacer(modifier = Modifier.fillMaxHeight(0.02f))
    Row(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.Start) {
            Text(
                text = cosplay.cosplay.media + " (" + cosplay.cosplay.mediaType + ")",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(10.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End) {
            Text(
                text = cosplay.cosplay.progress,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
    Text(
        text = cosplay.cosplay.complexity,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(10.dp)
    )
    Text(
        text = cosplay.cosplay.notes,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(10.dp)
    )
    Column {
        Text(
            text = "To Do",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
        )
        cosplay.cosplay.toDo.forEachIndexed { index, todo ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(todo != "") {
                    var editedTodo by rememberSaveable {
                        mutableStateOf(todo.substring(1))
                    }
                    var itemModifiable by rememberSaveable {
                        mutableStateOf(false)
                    }

                    Checkbox(
                        checked = todo.get(0) == '1',
                        onCheckedChange = {
                            cosplayDetailsViewModel.changeToDoStatus(
                                cosplay,
                                todo.substring(1),
                                it,
                                index)}
                    )
                    BasicTextField(
                        value = editedTodo,
                        onValueChange = {
                            editedTodo = it
                        },
                        enabled = itemModifiable,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                itemModifiable = true
                                //should also focus so you don't have to click twice
                            },
                        cursorBrush = SolidColor(Color.White),
                        textStyle = TextStyle(Color.White),
                        decorationBox = { innerTextField ->
                            innerTextField() // No decoration, just the text and cursor
                    })
                    if(itemModifiable) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "edit to-do item",
                            modifier = Modifier
                                .padding(12.dp)
                                .clickable {
                                    cosplayDetailsViewModel.changeToDoStatus(cosplay, editedTodo, todo.get(0) == '1', index)
                                    itemModifiable = false
                                },
                            tint = Color.White
                        )
                    }
                }
            }
        }
        if(showAddTodo) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = newCheck,
                    onCheckedChange = {
                        newCheck = it}
                )
                transparentTextfield(value = newTodo,
                    onValueChange = {
                        newTodo = it
                })
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "set new to-do item",
                    modifier = Modifier
                        .padding(12.dp)
                        .clickable {
                            cosplayDetailsViewModel.addToDoItem(newTodo, newCheck, cosplay)
                            showAddTodo = false
                            newTodo = ""
                            newCheck = false
                        },
                    tint = Color.White
                )
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
            tint = Color.White
        )
    }
}

@Composable
fun transparentTextfield(
    value: String,
    onValueChange: (String) -> Unit
) {

    // Create a FocusRequester
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        // Request focus as soon as the composable is visible
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .padding(10.dp)
            .focusRequester(focusRequester),
        cursorBrush = SolidColor(Color.White),
        textStyle = TextStyle(Color.White),
        decorationBox = { innerTextField ->
            innerTextField() // No decoration, just the text and cursor
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editDialogue(
    cosplay: Cosplay,
    characterRef: String,
    cosplayDetailsViewModel: CosplayDetailsViewModel,
    onDialogDismiss: () -> Unit = {},
    cosplayListWithId: List<CosplayWithId>
) {
    //code from https://developer.android.com/develop/ui/compose/components/datepickers

    Dialog(
        onDismissRequest = onDialogDismiss
    ) {

        var character by rememberSaveable {
            mutableStateOf(cosplay.character)
        }

        var media by rememberSaveable {
            mutableStateOf(cosplay.media)
        }

        //TODO can't remember why this isn't a pre-defined list...?
        var mediaType by rememberSaveable {
            mutableStateOf(cosplay.mediaType)
        }

        var progress by rememberSaveable {
            mutableStateOf(cosplay.progress)
        }

        var complexity by rememberSaveable {
            mutableStateOf(cosplay.complexity)
        }

        var notes by rememberSaveable {
            mutableStateOf(cosplay.notes)
        }

        Column(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
        ) {
            Text(text = "Character", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = character,
                singleLine = true,
                onValueChange = {
                    character = it
                },
                label = { Text(text = "character name")}
            )
            Text(text = "Media", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = media,
                singleLine = true,
                onValueChange = {
                    media = it
                },
                label = { Text(text = "media")}
            )
            Text(text = "Media type", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                listOf("Anime", "Movie", "Show", "Podcast", "Book", "Other"),
                preselected = mediaType,
                onSelectionChanged = {
                    mediaType = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            //code from https://developer.android.com/develop/ui/compose/components/datepickers
            Text(text = "Complexity", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                listOf("Simple", "Medium", "Complicated"),
                preselected = complexity,
                onSelectionChanged = {
                    complexity = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            Text(text = "Progress", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                listOf("Not started", "In Progress", "Completed"),
                preselected = progress,
                onSelectionChanged = {
                    progress = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            Text(text = "Notes", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = notes,
                onValueChange = {
                    notes = it
                },
                label = { Text(text = "notes")}
            )
            Row {
                Button(modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White
                    ),
                    onClick = {
                        cosplayDetailsViewModel.editCosplay(Cosplay(
                            character = character,
                            media = media,
                            mediaType = mediaType,
                            progress = progress,
                            complexity = complexity,
                            notes = notes),
                            characterRef,
                            cosplayListWithId)
                        onDialogDismiss()
                    }) {
                    Text(text = "Save")
                }

            }
        }
    }
}