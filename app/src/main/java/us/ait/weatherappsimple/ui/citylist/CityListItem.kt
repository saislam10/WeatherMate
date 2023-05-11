package us.ait.weatherappsimple.ui.citylist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CityListItem(cityName: String, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(start = 16.dp, end = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = cityName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.h5
            )
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete"
                )
            }
        }
    }
}
