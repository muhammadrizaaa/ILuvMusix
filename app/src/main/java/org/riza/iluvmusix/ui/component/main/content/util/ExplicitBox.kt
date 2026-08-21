package org.riza.iluvmusix.ui.component.main.content.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.riza.iluvmusix.R

@Composable
fun ExplicitBox(
    size: TextUnit = 12.sp,
    contentColor: Color = colorResource(R.color.main).copy(alpha = 0.8f),
    bgColor: Color = colorResource(R.color.white)
){
    Box(
        modifier = Modifier.wrapContentSize()
            .background(
                color = contentColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ){
        Text(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
            text = "E",
            fontWeight = FontWeight.W900,
            fontSize = size,
            color = bgColor
        )
    }
}