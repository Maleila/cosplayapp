package us.cosplayapp.ui.screen.cosplay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
                Text(text = "Cosplays")
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
            //if (cosplayListState.value == CosplayUploadUiState.Init) {
            if (cosplayViewModel.filterUiState == CosplayUploadUiState.Init ||
                cosplayViewModel.filterUiState == CosplayUploadUiState.LoadingCosplayUpload
            ) {
                Text(
                    text = "loading",
                    modifier = Modifier.padding(10.dp)
                )
                //} else if (cosplayListState.value is CosplayUploadUiState.Success) {
            } else if (cosplayViewModel.filterUiState is CosplayUploadUiState.Success) {
                //if ((cosplayListState.value as CosplayUploadUiState.Success).cosList.isEmpty()
                if ((cosplayViewModel.filterUiState as CosplayUploadUiState.Success).cosList.isEmpty()
                ) {
                    Text(
                        text = "no results :(",
                        modifier = Modifier.padding(10.dp)
                    )
                } else {
                    Button(onClick = {
                        showFilterDialogue = true
                    }) {
                        Text(text = "Filter")
                    }
                    LazyColumn() {
                        //items((cosplayListState.value as CosplayUploadUiState.Success).cosList) {
                        items((cosplayViewModel.filterUiState as CosplayUploadUiState.Success).cosList) {
                            CosplayCard(cosplay = it.cosplay,
                                onCardClicked = { onNavigateToDetailsScreen(it.cosId) }
                            )
                        }
                    }
                }
            }

//            when (cosplayViewModel.filterUiState) {
//                is CosplayUploadUiState.Init -> Text(text = "loading")
//                is CosplayUploadUiState.LoadingCosplayUpload -> Text(text = "more loading")
//                is CosplayUploadUiState.Success ->
//                LazyColumn() {
//                    //items((cosplayListState.value as CosplayUploadUiState.Success).cosList) {
//                    items((cosplayViewModel.filterUiState as CosplayUploadUiState.Success).cosList) {
//                        CosplayCard(cosplay = it.cosplay,
//                            onCardClicked = { onNavigateToDetailsScreen(it.cosId)}
//                        )
//                    }
//                }
//                is CosplayUploadUiState.Error -> Text(text = "error!")
//                is CosplayUploadUiState.CosplayUploadSuccess -> Text(text = "this one is redundant I think")
//            }
//            }
//        Button(onClick = {
//                            showFilterDialogue = true
//                        }) {
//                            Text(text = "Filter")
//                        }
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
                    Text(text = cosplay.character)
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
    //code from https://developer.android.com/develop/ui/compose/components/datepickers

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
            mutableStateOf("")
        }

        var progress by rememberSaveable {
            mutableStateOf("")
        }

        var complexity by rememberSaveable {
            mutableStateOf("")
        }

        var notes by rememberSaveable {
            mutableStateOf("")
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
                        cosplayViewModel.addCosplay(
                            character,
                            media,
                            mediaType,
                            complexity,
                            progress,
                            notes)
                        onDialogDismiss()
                    }) {
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
