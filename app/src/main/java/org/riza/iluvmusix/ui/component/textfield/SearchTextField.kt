package org.riza.iluvmusix.ui.component.textfield

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import org.riza.iluvmusix.R

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValChange: (String) -> Unit,
    placeholderText: String = "Search...",
    onSearchAction: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            if (it.length < 60) {
                onValChange(it)
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = LocalContentColor.current
        ),
        // Fix cursor color matching text color in dark/light themes
        cursorBrush = SolidColor(LocalContentColor.current),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            imeAction = ImeAction.Search // Changed to Search for better UX
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onSearchAction?.invoke()
            }
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(
                        BorderStroke(
                            width = 1.dp,
                            color = LocalContentColor.current.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp, // Increased slightly for better tap targets
                            horizontal = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Render Placeholder when empty
                        if (value.isEmpty()) {
                            Text(
                                text = placeholderText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LocalContentColor.current.copy(alpha = 0.5f)
                            )
                        }
                        // Render Actual Input Text
                        innerTextField()
                    }

                    // Only show clear button when there is text typed
                    AnimatedVisibility(
                        visible = value.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_close_24),
                            contentDescription = "Clear search",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onValChange("")
                                }
                        )
                    }
                }
            }
        }
    )
}