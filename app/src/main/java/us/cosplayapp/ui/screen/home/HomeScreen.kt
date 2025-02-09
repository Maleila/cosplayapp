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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Cosplay",
                    style = MaterialTheme.typography.displayLarge)
                Text(text = "Planner",
                    style = MaterialTheme.typography.displayLarge)
                Text(text = "100",
                    style = MaterialTheme.typography.displaySmall)
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    onNavigateToConScreen()
                }) {
                    Text(text = "Cons")
                }
                Button(onClick = {
                    onNavigateToCosplayScreen()
                }) {
                    Text(text = "Cosplays")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Misc")
                }
            }
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Upcoming cons")
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
                        text = "no upcoming cons! time to make some plans",
                        modifier = Modifier.padding(10.dp)
                    )
                } else {
                    LazyColumn() {
                        val upcomingCons: List<ConWithId> = homeViewModel.getUpcomingCons((conListState.value as HomeViewModel.ConUIState.Success).conList)
                        if(upcomingCons.isEmpty()) {
//                            Text(text = "No cons in the next 90 days",
//                                modifier = Modifier.padding(10.dp))
                        } else {
                            items(upcomingCons) {
                                ConCard(con = it.con,
                                    onCardClicked = { onNavigateToConDetails(it.conId) })
                            }
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.02f))
            Text(text = "Cosplays in progress")
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
                    LazyColumn() {
                        items((cosplayListState.value as HomeViewModel.CosplayUIState.Success).cosList) {
                            if(it.cosplay.progress == "In Progress") {
                                CosplayCard(cosplay = it.cosplay,
                                    onCardClicked = { onNavigateToCosplayDetails(it.cosId) })
                            }
                        }
                    }
                }
            }
        }
    }
}