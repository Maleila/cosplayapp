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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
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
                cosplay = cosplayDetailsViewModel.getCosplayById(
                    id,
                    (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                ),
                onEditCosplay = { cosplay ->
                    showEditDialog = true
                }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun cosplayDetails(cosplay: Cosplay,
                   onEditCosplay: (Cosplay) -> Unit = {})
{
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center) {
        Text(
            text = cosplay.character,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(10.dp)
        )
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "edit",
            modifier = Modifier.clickable {
                onEditCosplay(cosplay)
            },
            tint = Color.Blue
        )
    }
    Spacer(modifier = Modifier.fillMaxHeight(0.02f))
    Row(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.Start) {
            Text(
                text = cosplay.media + " (" + cosplay.mediaType + ")",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(10.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End) {
            Text(
                text = cosplay.progress,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
    Text(
        text = cosplay.complexity,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(10.dp)
    )

    Text(
        text = cosplay.notes,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(10.dp)
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