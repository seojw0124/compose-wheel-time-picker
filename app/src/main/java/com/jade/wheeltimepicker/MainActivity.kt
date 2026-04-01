package com.jade.wheeltimepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.jade.wheeltimepicker.ui.theme.WheelTimePickerTheme
import com.jade.wheeltimepicker.wheel.WheelTimePicker
import com.jade.wheeltimepicker.wheel.WheelTimePickerAmPmLabels
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
    val state = rememberWheelTimePickerState(
        initialHour = 10,
        initialMinute = 30,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        WheelTimePicker(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            visibleItemCount = 5,
            isFadeEdgeEnabled = true,
            isWheelEffectEnabled = true,
            amPmLabels = WheelTimePickerAmPmLabels(am = "AM", pm = "PM"),
        )

        Text(
            text = "Selected: ${state.hour12}:${state.minute.toString().padStart(2, '0')} ${if (state.isPm) "PM" else "AM"}",
            modifier = Modifier.padding(top = 20.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WheelTimePickerPreview() {
    WheelTimePickerTheme {
        WheelTimePickerDemo()
    }
}
