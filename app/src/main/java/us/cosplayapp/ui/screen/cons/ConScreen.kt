package us.cosplayapp.ui.screen.cons

import android.icu.text.SimpleDateFormat
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.Con.Con
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConScreen(
    conViewModel: ConViewModel = viewModel(),
    onNavigateToDetailsScreen: (String) -> Unit
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
                Text(text = "Cons",
                    style = MaterialTheme.typography.titleLarge)
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
                    Text(text = "no cons yet! add some :)",
                        modifier = Modifier.padding(10.dp))
                } else {
                    LazyColumn() {
                        items((conListState.value as ConUploadUiState.Success).conList) {
                            ConCard(con = it.con,
                                onCardClicked = { onNavigateToDetailsScreen(it.conId)}
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
                    Text(text = con.dates[0] + " - " + con.dates[1])
                    Text(text = con.location)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConDatesPicker(
    onDateRangeSelected: (Pair<String?, String?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            convertMillisToDate(dateRangePickerState.selectedStartDateMillis!!),
                            convertMillisToDate(dateRangePickerState.selectedEndDateMillis!!)
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialogue(
    conViewModel: ConViewModel,
    onDialogDismiss: () -> Unit = {}
) {
    //code from https://developer.android.com/develop/ui/compose/components/datepickers
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    var selectedDates by rememberSaveable {
        mutableStateOf<Pair<String?, String?>>(Pair("", ""))
    }

    Dialog(
        onDismissRequest = onDialogDismiss
    ) {

        var name by rememberSaveable {
            mutableStateOf("")
        }

        var location by rememberSaveable {
            mutableStateOf("")
        }

        var nameErrorState by rememberSaveable {
            mutableStateOf(false)
        }

        var locationErrorState by rememberSaveable {
            mutableStateOf(false)
        }

        var nameErrorText by rememberSaveable {
            mutableStateOf("")
        }

        var locationErrorText by rememberSaveable {
            mutableStateOf("")
        }

        fun validateName(text: String) {
            if (text.trim() == "") {
                nameErrorState = true
                nameErrorText = "Enter a con"
            } else {
                nameErrorState = false
            }
    }

        fun validateLocation(text: String){
            if (text.trim() == "") {
                locationErrorState = true
                locationErrorText = "Enter a location"
            } else {
                locationErrorState = false
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
            Text(text = "Con", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                singleLine = true,
                trailingIcon = {
                    if (nameErrorState) {
                        Icon(
                            Icons.Filled.Warning, "name error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                onValueChange = {
                    name = it
                    validateName(it)
                },
                label = { Text(text = "con name")}
            )
            if (nameErrorState) {
                Text(
                    text = nameErrorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Text(text = "Date", modifier = Modifier.padding(horizontal = 10.dp))
            //code from https://developer.android.com/develop/ui/compose/components/datepickers
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
                value = selectedDates.first + " - " + selectedDates.second,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                })
            if (showDatePicker) {
                ConDatesPicker(
                    onDateRangeSelected = {
                                          selectedDates = it
                    },
                    onDismiss = { showDatePicker = false })
//                Popup(
//                    onDismissRequest = { showDatePicker = false },
//                    alignment = Alignment.TopStart
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .offset(y = 64.dp)
//                            .shadow(elevation = 4.dp)
//                            .background(MaterialTheme.colorScheme.surface)
//                            .padding(16.dp)
//                    ) {
//                        DatePicker(
//                            state = datePickerState,
//                            showModeToggle = false
//                        )
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.End
//                        ) {
//                            Button(onClick =
//                            { showDatePicker = false }) {
//                                Text(text = "Select")
//                            }
//                        }
//
//                    }
//                }
            }
            Text(text = "Location", modifier = Modifier.padding(horizontal = 10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = location,
                singleLine = true,
                trailingIcon = {
                    if (locationErrorState) {
                        Icon(
                            Icons.Filled.Warning, "location error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                onValueChange = {
                    location = it
                    validateLocation(it)
                },
                label = { Text(text = "con location")}
            )
            if (locationErrorState) {
                Text(
                    text = locationErrorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Row {
                Button(modifier = Modifier.padding(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White
                    ),
                    onClick = {
                        validateName(name)
                        validateLocation(location)
                        if(!locationErrorState && !nameErrorState) {
                            conViewModel.addCon(
                                name,
                                selectedDates,
                                location)
                            onDialogDismiss()
                        }
                    }) {
                    Text(text = "Add")
                }

            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}