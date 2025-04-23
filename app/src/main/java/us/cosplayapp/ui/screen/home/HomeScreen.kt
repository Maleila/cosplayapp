package us.cosplayapp.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.Con.ConWithId
import us.cosplayapp.ui.screen.cons.ConCard
import us.cosplayapp.ui.screen.cosplay.CosplayCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onNavigateToCosplayScreen: () -> Unit,
    onNavigateToConScreen: () -> Unit,
    onNavigateToCosplayDetails: (String) -> Unit,
    onNavigateToConDetails: (String) -> Unit
) {

    val cosplayListState = homeViewModel.cosList().collectAsState(
        initial = HomeViewModel.CosplayUIState.Init
    )

    val conListState = homeViewModel.conList().collectAsState(
        initial = HomeViewModel.ConUIState.Init )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth().fillMaxHeight(0.4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Cosplanner",
                    style = MaterialTheme.typography.displayLarge)
                Text(text = "100",
                    style = MaterialTheme.typography.displayLarge)
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    onNavigateToConScreen()
                },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )) {
                    Text(text = "cons",
                        style = MaterialTheme.typography.displayMedium)
                }
                Button(onClick = {
                    onNavigateToCosplayScreen()
                },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    ) {
                    Text(text = "cosplays",
                        style = MaterialTheme.typography.displayMedium)
                }
                Button(onClick = { /*TODO*/ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )) {
                    Text(text = "misc",
                        style = MaterialTheme.typography.displayMedium)
                }
            }
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "upcoming cons",
                style = MaterialTheme.typography.displayMedium)
            if (conListState.value == HomeViewModel.ConUIState.Init) {
                Text(
                    text = "loading",
                    modifier = Modifier.padding(10.dp)
                )
                LinearProgressIndicator(modifier = Modifier.width(75.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant)
            } else if (conListState.value is HomeViewModel.ConUIState.Success) {
                if ((conListState.value as HomeViewModel.ConUIState.Success).conList.isEmpty()
                ) {
                    Text(
                        text = "no cons planned! time to make some plans",
                        modifier = Modifier.padding(10.dp)
                    )
                } else {
                    val upcomingCons: List<ConWithId> = homeViewModel.getUpcomingCons((conListState.value as HomeViewModel.ConUIState.Success).conList)
                    if(upcomingCons.isEmpty()) {
                        Text(
                            text = "no cons in the next 90 days",
                            modifier = Modifier.padding(10.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth(0.92f)) {
                                items(upcomingCons) {
                                    ConCard(con = it.con,
                                        onCardClicked = { onNavigateToConDetails(it.conId) })
                                }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.05f))
            Text(text = "cosplays in progress",
                style = MaterialTheme.typography.displayMedium)
            if (cosplayListState.value == HomeViewModel.CosplayUIState.Init) {
                Text(
                    text = "loading",
                    modifier = Modifier.padding(10.dp)
                )
                LinearProgressIndicator(modifier = Modifier.width(75.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant)
            } else if (cosplayListState.value is HomeViewModel.CosplayUIState.Success) {
                if ((cosplayListState.value as HomeViewModel.CosplayUIState.Success).cosList.isEmpty()
                ) {
                    Text(
                        text = "no cosplays in progress! time to start some more",
                        modifier = Modifier.padding(10.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth(0.92f)) {
                        items((cosplayListState.value as HomeViewModel.CosplayUIState.Success).cosList) {
                            if(it.cosplay.progress == "In Progress") {
                                CosplayCard(cosplay = it,
                                    onCardClicked = { onNavigateToCosplayDetails(it.cosId) },
                                    onLongClick = {})
                            }
                        }
                    }
                }
            }
        }
    }
}