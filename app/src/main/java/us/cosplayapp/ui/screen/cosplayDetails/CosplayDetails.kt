package us.cosplayapp.ui.screen.cosplayDetails

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.storage.FirebaseStorage
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.ui.screen.cosplay.Dropdown

@Composable

fun CosplayDetails(
    id: String,
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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start) {

        if (cosplayListState.value == CosplayDetailsViewModel.CosplayDetailsUIState.Init) {
            Text(text = "loading")
        } else {
            CosplayDetails(
                cosplay = CosplayWithId(id,
                    cosplayDetailsViewModel.getCosplayById(
                        id,
                        (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                    )),
                onEditCosplay = { cosplay ->
                    showEditDialog = true
                },
                onAddCon = {
                    showAddConDialogue = true
                },
                cosplayDetailsViewModel
            )

            if(showEditDialog) {
                EditDialogue(
                    cosplay = cosplayDetailsViewModel.getCosplayById(
                        id,
                        (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                    ), //TODO maybe still not the best way to get this reference
                    id,
                    cosplayDetailsViewModel
                ) {
                    showEditDialog = false
                }
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
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CosplayDetails(cosplay: CosplayWithId,
                   onEditCosplay: (Cosplay) -> Unit = {},
                   onAddCon: () -> Unit = {},
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

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center) {
        Text(
            text = cosplay.cosplay.character,
            style = MaterialTheme.typography.titleMedium,
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
    Spacer(modifier = Modifier.fillMaxHeight(0.02f))
    Text(
        text = "Notes",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(10.dp)
    )
    Text(
        text = cosplay.cosplay.notes,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(10.dp)
    )
    Text(
        text = "Cons Appearances",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(10.dp)
    )
    FlowRow() {
        cosplay.cosplay.consList.forEach { con ->
            if(con.trim() != "") {
                Button(onClick = { /*TODO*/ },
                    modifier = Modifier.padding(5.dp)) {
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
        tint = Color.White
    )
    Spacer(modifier = Modifier.fillMaxHeight(0.02f))
    Text(
        text = "To Do",
        style = MaterialTheme.typography.bodyLarge,
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
        text = "Con Checklist",
        style = MaterialTheme.typography.bodyLarge,
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
    FlowRow() {
        cosplay.cosplay.referencePics.forEach { pic ->
            if(pic.trim() != "") {
                StoredImage(pic)
            }

        }
    }
    ImagePicker { uri ->
        cosplayDetailsViewModel.imageUri = uri
    }
    if(cosplayDetailsViewModel.imageUri != null) {
        Button(onClick = {
        cosplayDetailsViewModel.uploadImage(cosplay)
        }) {
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
                        //unless I can get the local var to listen for changes to the to-do...
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
                                textStyle = TextStyle(Color.White),
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
                                cursorBrush = SolidColor(Color.White),
                                textStyle = TextStyle(Color.White),
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
                                tint = Color.White
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
                            tint = Color.White
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
                            tint = Color.White
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
            tint = Color.White
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
        onClick = { launcher.launch("image/*") }
    ) {
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
        cursorBrush = SolidColor(Color.White),
        textStyle = TextStyle(Color.White),
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
    Dialog(onDismissRequest = {
        onDialogDismiss
    }) {
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
            }) {
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun StoredImage(uri: String) {
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//
//    FirebaseStorage.getInstance().getReference(storageReference).downloadUrl.addOnSuccessListener { uri ->
//        imageUri = uri
//    }

    //actually I think I'm sending in the downloadUrl already

    Uri.parse(uri)?.let {
        Image(
            painter = rememberAsyncImagePainter(it),
            contentDescription = "Image from Firebase Storage",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialogue(
    cosplay: Cosplay,
    characterRef: String, //TODO should change this to just passing the cosplaywithid
    cosplayDetailsViewModel: CosplayDetailsViewModel,
    onDialogDismiss: () -> Unit = {}
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
                        cosplayDetailsViewModel.editCosplay(
                            Cosplay(
                                character = character,
                                media = media,
                                mediaType = mediaType,
                                progress = progress,
                                complexity = complexity,
                                notes = notes),
                            characterRef
                        )
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
                        cosplayDetailsViewModel.deleteCosplay(
                            characterRef
                        )
                        onDialogDismiss()
                    }) {
                    Text(text = "Delete")
                }
            }
        }
    }
}