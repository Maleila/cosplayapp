package us.cosplayapp.ui.screen.cons

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.Con.Con
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
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
            if (conListState.value == ConUploadUiState.Init) {
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

@RequiresApi(Build.VERSION_CODES.O)
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
                },
                enabled = (dateRangePickerState.selectedEndDateMillis != null)
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
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            title = {
                Text(
                    text = "Select date range",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(10.dp)
                )
            },
            showModeToggle = false,
            requestFocus = true
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialogue(
    conViewModel: ConViewModel,
    onDialogDismiss: () -> Unit = {}
) {
    //code from https://developer.android.com/develop/ui/compose/components/datepickers
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
//    val selectedDate = datePickerState.selectedDateMillis?.let {
//        convertMillisToDate(it)
//    } ?: ""

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

//Based on this version: https://medium.com/@andyphiri92/working-with-date-picker-in-jetpack-compose-3ec6c2f65a5a
@RequiresApi(Build.VERSION_CODES.O)
fun convertMillisToDate(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
    val utcDateAtStartOfDay = Instant
        .ofEpochMilli(millis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()

    val localDate = utcDateAtStartOfDay.atStartOfDay(ZoneId.systemDefault())

    return formatter.format(localDate)
}