package us.cosplayapp.ui.screen.cosplay

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.ui.screen.home.HomeViewModel

@Composable
fun CosplayScreen(
    ViewModel: CosplayViewModel = viewModel(),
) {
    Column {
        Text(text = "cosplay list here")
    }
}