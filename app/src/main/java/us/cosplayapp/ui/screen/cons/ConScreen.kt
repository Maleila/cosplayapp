package us.cosplayapp.ui.screen.cons

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.ui.screen.home.HomeViewModel

@Composable
fun ConScreen(
    ViewModel: ConViewModel = viewModel(),
) {
    Text(text = "cons list here")
}