package us.cosplayapp.ui.screen.cosplayDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.Cosplay.Cosplay

@Composable

fun CosplayDetails(
    character: String,
    cosplayDetailsViewModel: CosplayDetailsViewModel = viewModel())
{

    val cosplayListState = cosplayDetailsViewModel.cosList().collectAsState(
        initial = CosplayDetailsViewModel.CosplayDetailsUIState.Init
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start) {

        if (cosplayListState.value == CosplayDetailsViewModel.CosplayDetailsUIState.Init) {
            Text(text = "loading")
        } else {
            cosplayDetails(
                cosplay = cosplayDetailsViewModel.getCosplayByName(
                    character,
                    (cosplayListState.value as CosplayDetailsViewModel.CosplayDetailsUIState.Success).cosList
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun cosplayDetails(cosplay: Cosplay) {
    Text(
        text = cosplay.character,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(10.dp)
    )
    Spacer(modifier = Modifier.fillMaxHeight(0.04f))
    Text(
        text = cosplay.media + " (" + cosplay.mediaType + ")",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(10.dp)
    )
    Text(
        text = cosplay.progress,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(10.dp)
    )
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