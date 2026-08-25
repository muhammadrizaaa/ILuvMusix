package org.riza.iluvmusix.ui.component.state.err

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ErrBox(
    modifier: Modifier = Modifier,
    errMsg: String?,
    onRetry: () -> Unit
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Column {
            Text(
                text = errMsg?:"err",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center
                )
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Retry",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}