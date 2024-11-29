package com.temi.helloworld.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.temi.helloworld.viewmodel.MainViewModelInterface
import com.temi.helloworld.viewmodel.MockMainViewModel

@Composable
fun ClickTracker(numClick: Int, onClick: () -> Unit) {
    Column (
        // modifier = Modifier.fillMaxSize(), // Make the Column fill the entire available space
        verticalArrangement = Arrangement.Center, // Center children vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally
    ){
        Text(text = "Number of clicks is $numClick")

        Button(onClick = onClick) {
            Text(text = "Button")
        }
    }
}


@Composable
fun GoLocation(locationsList: List<String>, onClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        locationsList.forEach { location: String ->
            Button(
                onClick = { onClick(location) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = location)
            }
        }

    }
}

@Composable
fun MainScreen(viewModel: MainViewModelInterface) {
    val numClicks = viewModel.numClicks.collectAsState()
    // var locations = viewModel.locations
   Column {
        ClickTracker(
            numClick = numClicks.value,
            onClick = { viewModel.onButtonClick() }
        )

        GoLocation(
            locationsList = viewModel.locations,
            onClick = { viewModel.onLocationButtonClick(it) }
            // onClick = { location: String ->
            //     viewModel.onLocationButtonClick(location)
            // }
        )
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MainScreenPreview() {
    val viewModel: MainViewModelInterface = MockMainViewModel()

    MainScreen(viewModel)
}