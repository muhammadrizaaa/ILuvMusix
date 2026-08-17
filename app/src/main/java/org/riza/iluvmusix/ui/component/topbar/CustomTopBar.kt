package org.riza.iluvmusix.ui.component.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.riza.iluvmusix.R

data class NavigationIconTopBar(
    val icon: Painter,
    val onCLick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    titleText: String? = null,
    titleIcon: Painter? = null,
    navIcon: NavigationIconTopBar?
){
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = colorResource(R.color.white),
            titleContentColor = colorResource(R.color.white),
            actionIconContentColor = colorResource(R.color.white)
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if(titleIcon != null){
                    Icon(
                        painter = titleIcon,
                        tint = null,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
                if(titleText != null){
                    Text(
                        text = titleText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700
                    )
                }
            }
        },
        navigationIcon = {
            if (navIcon != null){
                Box(
                    modifier = Modifier.wrapContentSize().clip(CircleShape).clickable(onClick = navIcon.onCLick),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = navIcon.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).padding(4.dp)
                    )
                }
            }
        }
    )
}