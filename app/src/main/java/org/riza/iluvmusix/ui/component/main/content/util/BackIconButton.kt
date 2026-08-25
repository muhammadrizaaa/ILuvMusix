package org.riza.iluvmusix.ui.component.main.content.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.riza.iluvmusix.R

@Composable
fun BackIconButton(
    modifier: Modifier = Modifier,
    onCLick:() -> Unit,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
){
    Icon(
        painter = painterResource(R.drawable.rounded_arrow_back_ios_new_24),
        contentDescription = null,
        tint = tint,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = {onCLick()})
    )
}