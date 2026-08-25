package org.riza.iluvmusix.ui.component.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextButton(
    containerColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    text: String,
    isActive: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit
){
    TextButton(
        colors = ButtonDefaults.textButtonColors(
            containerColor = containerColor
        ),
        onClick = { if(isActive) onClick() }
    ) {
        if(isLoading){
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.primary
            )
        }
        else{
            Text(
                text = text,
                fontSize = 12.sp,
                color = textColor
            )
        }
    }
}