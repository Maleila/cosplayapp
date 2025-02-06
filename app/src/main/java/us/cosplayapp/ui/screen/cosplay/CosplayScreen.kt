package us.cosplayapp.ui.screen.cosplay

import android.net.Uri
import android.widget.Spinner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.storage.FirebaseStorage
import us.cosplayapp.Cosplay.Cosplay
import us.cosplayapp.Cosplay.CosplayWithId
import us.cosplayapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosplayScreen(
    cosplayViewModel: CosplayViewModel = viewModel(),
    onNavigateToDetailsScreen: (String) -> Unit,
) {

    var showAddDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showFilterDialogue by rememberSaveable {
         mutableStateOf(false)
    }

    val cosplayListState = cosplayViewModel.cosList().collectAsState(
        initial = CosplayUploadUiState.Init
    )

    LaunchedEffect(true) {
        cosplayViewModel.filter()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Cosplays",
                    style = MaterialTheme.typography.displayMedium)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                },
                containerColor = Color.Gray,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                )
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            if (cosplayViewModel.filterUiState == CosplayUploadUiState.Init ||
                cosplayViewModel.filterUiState == CosplayUploadUiState.LoadingCosplayUpload
            ) {
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
            } else if (cosplayViewModel.filterUiState is CosplayUploadUiState.Success) {
                if ((cosplayViewModel.filterUiState as CosplayUploadUiState.Success).cosList.isEmpty()
                ) { Column() {
                    Button(onClick = {
                        //TODO should extract this into a reset method
                        cosplayViewModel.mediaTypeParam = "Any"
                        cosplayViewModel.complexityParam = "Any"
                        cosplayViewModel.progressParam = "Any"
                        cosplayViewModel.filter()
                    }) {
                        Text(text = "Clear")
                    }
                    Text(
                        text = "no results :(",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                } else {
                    Button(onClick = {
                        showFilterDialogue = true
                    },
                        modifier = Modifier.padding(10.dp)) {
                        Text(text = "Filter")
                    }
                    LazyColumn() {
                        items((cosplayViewModel.filterUiState as CosplayUploadUiState.Success).cosList) {
                            CosplayCard(cosplay = it.cosplay,
                                onCardClicked = { onNavigateToDetailsScreen(it.cosId) }
                            )
                        }
                    }
                }
            }
        }
    }
        if (showAddDialog) {
            AddDialogue(
                cosplayViewModel,
                { showAddDialog = false })
        }

        if(showFilterDialogue) {
            FilterDialogue(
                cosplayViewModel,
                {showFilterDialogue = false}
            )
        }
    }

@Composable
fun CosplayCard(
    cosplay: Cosplay,
    onCardClicked: () -> Unit = {}
) {

//    val charTextModifier = Modifier
//        .then(if(cosplay.progress == "Not started") Modifier.background(Color.Red)
//        else if(cosplay.progress == "In Progress") Modifier.background(Color.Yellow)
//        else Modifier.background((Color.Green)))

    var charTextColor: Color
    charTextColor = if(cosplay.progress == "Not started") {
        Color.Red
    } else if(cosplay.progress == "In Progress") {
        Color(0xFFFF9800)
    } else {
        Color.Green
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier
            .padding(5.dp)
            .clickable { onCardClicked() }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = cosplay.character,
                        color = charTextColor)
                    Text(text = cosplay.media)
                    Text(text = cosplay.complexity)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialogue(
    cosplayViewModel: CosplayViewModel,
    onDialogDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDialogDismiss
    ) {

        var character by rememberSaveable {
            mutableStateOf("")
        }

        var media by rememberSaveable {
            mutableStateOf("")
        }

        //TODO can't remember why this isn't a pre-defined list...?
        var mediaType by rememberSaveable {
            mutableStateOf("Other")
        }

        var progress by rememberSaveable {
            mutableStateOf("Not started")
        }

        var complexity by rememberSaveable {
            mutableStateOf("Medium")
        }

        var notes by rememberSaveable {
            mutableStateOf("")
        }

        var characterErrorState by rememberSaveable {
            mutableStateOf(
                false
            ) }

        var mediaErrorState by rememberSaveable {
            mutableStateOf(false)
        }

        var characterErrorText by rememberSaveable {
            mutableStateOf("")
        }

        var mediaErrorText by rememberSaveable{
            mutableStateOf("")
        }

        fun validateCharacter(text: String) {
            if (text.trim() == "") {
                characterErrorState = true
                characterErrorText = "Enter a character name"
            } else {
                characterErrorState = false
            }
        }

        fun validateMedia(text: String) {
            if (text.trim() == "") {
                mediaErrorState = true
                mediaErrorText = "Enter the name of the media"
            } else {
                mediaErrorState = false
            }
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
                trailingIcon = {
                    if (characterErrorState) {
                        Icon(
                            Icons.Filled.Warning, "name error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                onValueChange = {
                    character = it
                    validateCharacter(it)
                },
                label = { Text(text = "character name")}
            )
            if (characterErrorState) {
                Text(
                    text = characterErrorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Text(text = "Media", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = media,
                singleLine = true,
                trailingIcon = {
                    if (mediaErrorState) {
                        Icon(
                            Icons.Filled.Warning, "media error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                onValueChange = {
                    media = it
                    validateMedia(it)
                },
                label = { Text(text = "media")}
            )
            if (mediaErrorState) {
                Text(
                    text = mediaErrorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Text(text = "Media type", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                listOf("Anime", "Movie", "Show", "Podcast", "Book", "Other"),
                preselected = "Other",
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
                preselected = "Medium",
                onSelectionChanged = {
                    complexity = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            Text(text = "Progress", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                listOf("Not started", "In Progress", "Completed"),
                preselected = "Not Started",
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
                //singleLine = true,
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
                        validateCharacter(character)
                        validateMedia(media)
                        if(!characterErrorState && !mediaErrorState) {
                        cosplayViewModel.addCosplay(
                            character,
                            media,
                            mediaType,
                            complexity,
                            progress,
                            notes)
                        onDialogDismiss()
                    } }) {
                    Text(text = "Add")
                }

            }
        }
    }
}

@Composable
fun FilterDialogue(
    cosplayViewModel: CosplayViewModel,
    onDialogDismiss: () -> Unit = {}
) {
    var mediaType by rememberSaveable {
        mutableStateOf("Any")
    }

    var progress by rememberSaveable {
        mutableStateOf("Any")
    }

    var complexity by rememberSaveable {
        mutableStateOf("Any")
    }

    Dialog(
        onDismissRequest = onDialogDismiss
    ) {
        Column(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
        ) {
            Text(text = "Media type", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                listOf("Anime", "Movie", "Show", "Podcast", "Book", "Other", "Any"),
                preselected = mediaType,
                onSelectionChanged = {
                    mediaType = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            //code from https://developer.android.com/develop/ui/compose/components/datepickers
            Text(text = "Complexity", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                listOf("Simple", "Medium", "Complicated", "Any"),
                preselected = complexity,
                onSelectionChanged = {
                    complexity = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            Text(text = "Progress", modifier = Modifier.padding(horizontal = 10.dp))
            Dropdown(
                listOf("Not started", "In Progress", "Completed", "Any"),
                preselected = progress,
                onSelectionChanged = {
                    progress = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            Row {
                Button(modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White
                    ),
                    onClick = {
                        cosplayViewModel.mediaTypeParam = mediaType
                        cosplayViewModel.complexityParam = complexity
                        cosplayViewModel.progressParam = progress
                        cosplayViewModel.filter()
                        onDialogDismiss()
                    }) {
                    Text(text = "Filter")
                }

            }
        }
    }
}

//code from mobile fall 23
@Composable
fun Dropdown(
    list: List<String>,
    preselected: String,
    onSelectionChanged: (myData: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) }
    OutlinedCard(
        modifier = modifier.clickable {
            expanded = !expanded
        }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selected,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Icon(Icons.Outlined.ArrowDropDown, null, modifier = Modifier.padding(8.dp))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth()
            ) {
                list.forEach { listEntry ->
                    DropdownMenuItem(
                        onClick = {
                            selected = listEntry
                            expanded = false
                            onSelectionChanged(selected)
                        },
                        text = {
                            Text(
                                text = listEntry,
                                modifier = Modifier
                            )
                        },
                    )
                }
            }
        }
    }
}
