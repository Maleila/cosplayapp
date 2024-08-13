package us.cosplayapp.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    ViewModel: HomeViewModel = viewModel(),
    onNavigateToCosplayScreen: () -> Unit,
    onNavigateToConScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Cosplay app yippee")
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
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
        Column(modifier = Modifier.padding(it)) {
            Text(text = "hello")
        }
    }
}