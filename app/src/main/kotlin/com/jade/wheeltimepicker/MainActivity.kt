package com.jade.wheeltimepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jade.wheeltimepicker.ui.theme.WheelTimePickerTheme
import com.jade.wheeltimepicker.wheel.WheelTimePicker
import com.jade.wheeltimepicker.wheel.WheelTimePickerAmPmLabels
import com.jade.wheeltimepicker.wheel.WheelTimePickerDefaults
import com.jade.wheeltimepicker.wheel.rememberWheelTimePickerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheelTimePickerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WheelTimePickerDemo(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun WheelTimePickerDemo(modifier: Modifier = Modifier) {
    val defaultState = rememberWheelTimePickerState(initialHour = 10, initialMinute = 30)
    val fiveRowState = rememberWheelTimePickerState(initialHour = 21, initialMinute = 15)
    val sevenRowState = rememberWheelTimePickerState(initialHour = 7, initialMinute = 45)

    val darkPickerColors =
        WheelTimePickerDefaults.colors(
            selectedTextColor = Color.White,
            unSelectedTextColor = Color.White,
            backgroundColor = Color.Black,
            fadeColor = Color.Black,
        )

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(text = "Default")
        WheelTimePicker(
            state = defaultState,
            modifier = Modifier.fillMaxWidth(),
            amPmLabels = WheelTimePickerAmPmLabels(am = "AM", pm = "PM"),
        )
        Text(text = selectedText(defaultState.hour12, defaultState.minute, defaultState.isPm))

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "5 Rows / Dark / Wheel")
        WheelTimePicker(
            state = fiveRowState,
            modifier = Modifier.fillMaxWidth(),
            colors = darkPickerColors,
            visibleItemCount = 7,
            isWheelEffectEnabled = true,
            unselectedItemMinAlpha = 0.6f,
            amPmLabels = WheelTimePickerAmPmLabels(am = "AM", pm = "PM"),
        )
        Text(text = selectedText(fiveRowState.hour12, fiveRowState.minute, fiveRowState.isPm))

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "7 Rows / Dark / Wheel + Fade")
        WheelTimePicker(
            state = sevenRowState,
            modifier = Modifier.fillMaxWidth(),
            //colors = darkPickerColors,
            visibleItemCount = 7,
            itemVerticalPadding = 12.dp,
            //isWheelEffectEnabled = true,
            isFadeEdgeEnabled = true,
            amPmLabels = WheelTimePickerAmPmLabels(am = "AM", pm = "PM"),
        )
        Text(text = selectedText(sevenRowState.hour12, sevenRowState.minute, sevenRowState.isPm))
    }
}

private fun selectedText(hour12: Int, minute: Int, isPm: Boolean): String {
    val suffix = if (isPm) "PM" else "AM"
    return "Selected: ${hour12}:${minute.toString().padStart(2, '0')} $suffix"
}

@Preview(showBackground = true)
@Composable
private fun WheelTimePickerPreview() {
    WheelTimePickerTheme {
        WheelTimePickerDemo()
    }
}
