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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.riza.iluvmusix.R

@Composable
fun ErrBox(
    modifier: Modifier = Modifier,
    errMsg: String?,
    color: Color = colorResource(R.color.black),
    onRetry: () -> Unit
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Column {
            Text(
                text = errMsg?:"err",
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                color = color,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.main),
                    contentColor = colorResource(R.color.white)
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