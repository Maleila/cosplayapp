package us.cosplayapp.ui.screen.cosplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import us.cosplayapp.ui.screen.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosplayScreen(
    ViewModel: CosplayViewModel = viewModel(),
) {
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
                    //showSearchDialog = true
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

        }
    }
}