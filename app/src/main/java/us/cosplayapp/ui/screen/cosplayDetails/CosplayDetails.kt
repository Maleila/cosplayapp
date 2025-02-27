package us.cosplayapp.ui.screen.cosplayDetails

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.ui.screen.cosplay.Dropdown
import androidx.compose.ui.input.pointer.pointerInput

@Composable

fun CosplayDetails(
    id: String,
    onNavigateToDetailsScreen: (String) -> Unit,
    onDeleteCosplay: () -> Unit,
    cosplayDetailsViewModel: CosplayDetailsViewModel = viewModel())
{

    val cosplayListState = cosplayDetailsViewModel.cosList().collectAsState(
        initial = CosplayDetailsViewModel.CosplayDetailsUIState.Init
    )

    val conListState = cosplayDetailsViewModel.conList().collectAsState(
        initial = CosplayDetailsViewModel.ConUIState.Init )

    var showEditDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showAddConDialogue by rememberSaveable {
        mutableStateOf(false)
    }

    var activeUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start) {

        if (cosplayListState.value == CosplayDetailsViewModel.CosplayDetailsUIState.Init) {
            Text(text = "loading")
        } else {

            var mycos = CosplayWithId(id,
                cosplayDetailsViewModel.getCosplayById(
                    id,
                    (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                ))
            CosplayDetails(
                mycos,
                onEditCosplay = { cosplay ->
                    showEditDialog = true
                },
                onAddCon = {
                    showAddConDialogue = true
                },
                onImageClicked = {  photo ->
                    activeUri = photo
                },
                onNavigateToConDetails = onNavigateToDetailsScreen,
                conListState.value,
                cosplayDetailsViewModel
            )

            if(showEditDialog) {
                EditDialogue(
                    mycos,
                    cosplayDetailsViewModel,
                    onDeleteCosplay = {id ->
                        cosplayDetailsViewModel.deleteCosplay(
                            id
                        )
                        onDeleteCosplay()
                    },
                    onDialogDismiss = { showEditDialog = false }
                )
            }

            if(showAddConDialogue) {
                AddConDialogue(
                    cosplay = CosplayWithId(id,
                        cosplayDetailsViewModel.getCosplayById(
                            id,
                            (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                        )),
                    (conListState.value as CosplayDetailsViewModel.ConUIState.Success).conList,
                    cosplayDetailsViewModel = cosplayDetailsViewModel,
                    {
                        showAddConDialogue = false
                    })
            }
        }
    }

    if (cosplayListState.value == CosplayDetailsViewModel.CosplayDetailsUIState.Init) {
        Text(text = "loading")
    } else {
        if (activeUri != null) {
            FullScreenImage(activeUri!!) { activeUri = null }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CosplayDetails(cosplay: CosplayWithId,
                   onEditCosplay: (Cosplay) -> Unit = {},
                   onAddCon: () -> Unit = {},
                   onImageClicked: (Uri) -> Unit,
                   onNavigateToConDetails: (String) -> Unit,
                   conListState: CosplayDetailsViewModel.ConUIState,
                   cosplayDetailsViewModel: CosplayDetailsViewModel)
{
    var charTextColor: Color = when (cosplay.cosplay.progress) {
        "Not started" -> {
            Color.Red
        }
        "In Progress" -> {
            Color(0xFFFF9800)
        }
        else -> {
            Color.Green
        }
    }

    Row(modifier = Modifier.fillMaxSize().padding(70.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top) {
        Text(
            text = cosplay.cosplay.character,
            style = MaterialTheme.typography.displayLarge,
            //modifier = Modifier.padding(60.dp),
            color = charTextColor
        )
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "edit",
            modifier = Modifier.clickable {
                onEditCosplay(cosplay.cosplay)
            },
            tint = MaterialTheme.colorScheme.secondary
        )
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(0.6f),
            horizontalArrangement = Arrangement.Start) {
            Text(
                text = cosplay.cosplay.media + " (" + cosplay.cosplay.mediaType + ")",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(10.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End) {
            Text(
                text = cosplay.cosplay.progress,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Speed,
            contentDescription = "complexity",
            tint = MaterialTheme.colorScheme.secondary)
        Text(
            text = cosplay.cosplay.complexity,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(10.dp)
        )
    }

    Spacer(modifier = Modifier.fillMaxHeight(0.02f))
    Text(
        text = "notes",
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.padding(10.dp)
    )
    Text(
        text = cosplay.cosplay.notes,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(10.dp)
    )
    Text(
        text = "con appearances",
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.padding(10.dp)
    )
    FlowRow() {
        cosplay.cosplay.consList.forEach { con ->
            if(con.trim() != "") {
                Button(onClick = { cosplayDetailsViewModel.getIdByCon(con, (conListState as CosplayDetailsViewModel.ConUIState.Success).conList)
                    ?.let { onNavigateToConDetails(it) }  },
                    modifier = Modifier.padding(5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )){
                    Text(text = con)
                }
            }

        }
    }
    Icon(
        imageVector = Icons.Filled.AddCircleOutline,
        contentDescription = "add con",
        modifier = Modifier
            .padding(12.dp)
            .clickable {
                onAddCon()
            },
        tint = MaterialTheme.colorScheme.secondary
    )
    Spacer(modifier = Modifier.fillMaxHeight(0.02f))
    Text(
        text = "to do",
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.padding(10.dp)
    )
    CheckList(cosplay,
        cosplay.cosplay.toDo,
              onEditItem = {cosplay, editedTodo, checked, index ->
                  cosplayDetailsViewModel.changeToDoStatus(
                      cosplay,
                      editedTodo,
                      checked,
                      index
                  )
              },
              onAddItem = { newTodo, newCheck, cosplay ->
                  cosplayDetailsViewModel.addToDoItem(newTodo, newCheck, cosplay)
              },
              onDeleteItem = { cosplay, index ->
                cosplayDetailsViewModel.deleteToDo(cosplay, index)
                }
    )
    Spacer(modifier = Modifier.fillMaxHeight(0.02f))
    Text(
        text = "con checklist",
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.padding(10.dp)
    )
    CheckList(cosplay,
        cosplay.cosplay.checklist,
        onEditItem = {cosplay, editedItem, checked, index ->
            cosplayDetailsViewModel.changeChecklistItemStatus(
                cosplay,
                editedItem,
                checked,
                index
            )
        },
        onAddItem = { newItem, newCheck, cosplay ->
            cosplayDetailsViewModel.addChecklistItem(newItem, newCheck, cosplay)
        },
        onDeleteItem = { cosplay, index ->
            cosplayDetailsViewModel.deleteChecklistItem(cosplay, index)
        }
    )
    Text(text = "gallery",
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.padding(10.dp))
    FlowRow() {//idk if flowrow is ideal for this - maybe lazygrid?
        cosplay.cosplay.referencePics.forEach { pic ->
            if(pic.trim() != "") {
                StoredImage(pic, onImageClicked)
            }

        }
    }
    ImagePicker { uri ->
        cosplayDetailsViewModel.imageUri = uri
    }
    if(cosplayDetailsViewModel.imageUri != null) {
        Button(onClick = {
        cosplayDetailsViewModel.uploadImage(cosplay)
        },
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )) {
            Text(text = "add photo")
        }
    }
}

@Composable
fun CheckList(cosplay: CosplayWithId,
              items: List<String>,
              onEditItem: (CosplayWithId, String, Boolean, Int) -> Unit,
              onAddItem: (String, Boolean, CosplayWithId) -> Unit,
              onDeleteItem: (CosplayWithId, Int) -> Unit) {

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
                                onEditItem(cosplay, item.substring(1), it, index)
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
                                        onEditItem(cosplay, editedTodo, item[0] == '1', index)
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
                                    onDeleteItem(cosplay, index)
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
                                    onAddItem(newTodo, newCheck, cosplay)
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

//code from https://medium.com/@emmanuelmuturia/how-to-add-and-retrieve-images-from-firebase-storage-using-jetpack-compose-dedda31ff66d
@Composable
fun ImagePicker(onImageSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let { onImageSelected(it) } }
    )

    Button(
        onClick = { launcher.launch("image/*") },
    colors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.secondary,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )){
        Text("Select Image")
    }
}

@Composable
fun TransparentTextField(
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
            .padding(5.dp)
            .focusRequester(focusRequester),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
        textStyle = TextStyle(MaterialTheme.colorScheme.secondary),
        decorationBox = { innerTextField ->
            innerTextField() // No decoration, just the text and cursor
        }
    )
}

@Composable
fun AddConDialogue(
    cosplay: CosplayWithId,
    cons: List<ConWithId>,
    cosplayDetailsViewModel: CosplayDetailsViewModel,
    onDialogDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest =
        onDialogDismiss
    ) {
        var conList by rememberSaveable {
            mutableStateOf(cosplayDetailsViewModel.getConsList(cons))
        }

        var con by rememberSaveable {
            mutableStateOf(conList[0])
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
            Dropdown(
                list = conList,
                preselected = conList[0],
                onSelectionChanged = {
                    con = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            Button(onClick = {
                cosplayDetailsViewModel.addCon(con, cosplay)
                onDialogDismiss()
            },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),) {
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun StoredImage(uri: String, onClick: (Uri) -> Unit) {
    Uri.parse(uri)?.let {
        Image(
            painter = rememberAsyncImagePainter(it),
            contentDescription = "Image from Firebase Storage",
            modifier = Modifier
                .height(200.dp)
                .padding(5.dp)
                .clickable {
                    onClick(it)
                }
        )
    }
}

@Composable
fun FullScreenImage(uri: Uri, onDismiss: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scrim(onDismiss, Modifier.fillMaxSize())
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "Full-screen image",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun Scrim(onDismiss: () -> Unit, modifier: Modifier) {
    Box(modifier = modifier
        .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
        .onKeyEvent {
            if (it.key == Key.Escape) {
                onDismiss()
                true
            } else {
                false
            }
        }
        .background(Color.DarkGray.copy(alpha = 0.75f))) {
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialogue(
    cosplay: CosplayWithId,
    cosplayDetailsViewModel: CosplayDetailsViewModel,
    onDialogDismiss: () -> Unit = {},
    onDeleteCosplay: (String) -> Unit
) {

    Dialog(
        onDismissRequest = onDialogDismiss
    ) {

        var character by rememberSaveable {
            mutableStateOf(cosplay.cosplay.character)
        }

        var media by rememberSaveable {
            mutableStateOf(cosplay.cosplay.media)
        }

        //TODO can't remember why this isn't a pre-defined list...?
        var mediaType by rememberSaveable {
            mutableStateOf(cosplay.cosplay.mediaType)
        }

        var progress by rememberSaveable {
            mutableStateOf(cosplay.cosplay.progress)
        }

        var complexity by rememberSaveable {
            mutableStateOf(cosplay.cosplay.complexity)
        }

        var notes by rememberSaveable {
            mutableStateOf(cosplay.cosplay.notes)
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
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = {
                        cosplayDetailsViewModel.editCosplay(
                            Cosplay(
                                character = character,
                                media = media,
                                mediaType = mediaType,
                                progress = progress,
                                complexity = complexity,
                                notes = notes),
                            cosplay.cosId
                        )
                        onDialogDismiss()
                    }) {
                    Text(text = "Save")
                }
                Button(modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = Color.Red
                    ),
                    onClick = {
                        onDialogDismiss()
                        onDeleteCosplay(cosplay.cosId)
                    }) {
                    Text(text = "Delete")
                }
            }
        }
    }
}