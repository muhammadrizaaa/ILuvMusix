package org.riza.iluvmusix.ui.component.state.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.riza.iluvmusix.R

@Composable
fun LoadingBox(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    strokeWidth: Dp = 2.dp,
    color: Color = colorResource(R.color.white),
    trackColor: Color = colorResource(R.color.main)
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(
            modifier = Modifier
                .size(size),
            strokeWidth = strokeWidth,
            color = color,
            trackColor = trackColor
        )
    }
}