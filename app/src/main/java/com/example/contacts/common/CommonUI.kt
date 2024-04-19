package com.example.contacts.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.contacts.ui.theme.dimen0
import com.example.contacts.ui.theme.mediumTextStyleSize16
import com.example.contacts.ui.theme.mediumTextStyleSize18
import com.example.contacts.ui.theme.mediumTextStyleSize24
import com.example.contacts.ui.theme.primaryTextColor

@Composable
fun CommonText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    textDecoration: TextDecoration = TextDecoration.None,
    textColor: Color = primaryTextColor,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimen0),
            verticalAlignment = Alignment.CenterVertically
        ) {

         Text(
                modifier = modifier,
                textAlign = textAlign,
                text = text,
                textDecoration = textDecoration,
                color = textColor,
                style = style,
                overflow = overflow,
                maxLines = maxLines
            )
        }
    }

@Composable
fun CommonHeader(
    text: String,
    image: Painter,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Image(
            painter = image,
            contentDescription = "Back",
            modifier = Modifier
                .clickable(onClick = onBackClick)
                .padding(end = 16.dp)
                .size(34.dp)
        )
        CommonText(
            modifier = Modifier.weight(1f),
            text = text,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.mediumTextStyleSize24
        )
    }
}

@Composable
fun CommonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textStyle: TextStyle = TextStyle.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        textStyle = textStyle,
        cursorBrush = SolidColor(Color.Black),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                if (value.isEmpty()) {
                    CommonText(
                        text = placeholder,
                        textColor = primaryTextColor,
                        style = MaterialTheme.typography.mediumTextStyleSize16,
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun CommonButton(
    modifier: Modifier = Modifier,
    contentColor: Color,
    backgroundColor: Color,
    txt: String,
    textStyle: TextStyle = MaterialTheme.typography.mediumTextStyleSize18,
    enabled:Boolean = true,
    onClick: () -> Unit
) {

    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        CommonText(textColor = contentColor, text = txt, style = textStyle, modifier = Modifier)
    }
}
