package us.cosplayapp.ui.screen.cons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.Con.Con
import us.cosplayapp.ui.screen.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConScreen(
    conViewModel: ConViewModel = viewModel(),
) {

    var showAddDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val conListState = conViewModel.conList().collectAsState(
        initial = ConUploadUiState.Init
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Cons")
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
            if (conListState.value == ConUploadUiState.Init) {
                Text(text = "loading",
                    modifier = Modifier.padding(10.dp))
            } else if (conListState.value is ConUploadUiState.Success) {
                if ((conListState.value as ConUploadUiState.Success).conList.isEmpty()
                ) {
                    Text(text = "loading or something idk",
                        modifier = Modifier.padding(10.dp))
                } else {
                    LazyColumn() {
                        items((conListState.value as ConUploadUiState.Success).conList) {
                            ConCard(con = it.con
//                                onCardClicked = { onNavigateToDetailsScreen(it.con.name)}
                                                        )
                        }
                    }
                }
            }
        }
        if (showAddDialog) {
            AddDialogue(
                conViewModel,
                { showAddDialog = false })
        }
    }
}

@Composable
fun ConCard(
    con: Con,
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
                    Text(text = con.name)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialogue(
    conViewModel: ConViewModel,
    onDialogDismiss: () -> Unit = {}
) {

    Dialog(
        onDismissRequest = onDialogDismiss
    ) {

        var name by rememberSaveable {
            mutableStateOf("")
        }

        var date by rememberSaveable {
            mutableStateOf("")
        }

        var location by rememberSaveable {
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
            Text(text = "Date", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = date,
                singleLine = true,
                onValueChange = {
                    date = it
                },
                label = { Text(text = "con date")}
            )
            Text(text = "Location", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = location,
                singleLine = true,
                onValueChange = {
                    location = it
                },
                label = { Text(text = "con location")}
            )
            Row {
                Button(modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White
                    ),
                    onClick = {
                        conViewModel.addCon(
                                name,
                                date,
                                location)
                        onDialogDismiss()
                    }) {
                    Text(text = "Add")
                }

            }
        }
    }
}