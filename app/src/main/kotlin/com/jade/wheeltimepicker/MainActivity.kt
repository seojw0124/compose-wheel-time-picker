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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
    // 1. Default Style State
    val defaultState = rememberWheelTimePickerState(initialHour = 10, initialMinute = 30)
    // 2. 3D Wheel Effect State
    val wheelEffectState = rememberWheelTimePickerState(initialHour = 21, initialMinute = 15)
    // 3. Custom Style State
    val customStyleState = rememberWheelTimePickerState(initialHour = 7, initialMinute = 45)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp) // 섹션 간 간격 확보
    ) {
        // --- Section 1: Default Style ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Default Style", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            WheelTimePicker(
                state = defaultState,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Selected: ${defaultState.hour12}:${defaultState.minute.toString().padStart(2, '0')} ${if (defaultState.isPm) "PM" else "AM"}",
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        HorizontalDivider()

        // --- Section 2: 3D Wheel Effect ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("3D Wheel Effect", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            WheelTimePicker(
                state = wheelEffectState,
                modifier = Modifier.fillMaxWidth(),
                isWheelEffectEnabled = true, // 3D 효과 활성화
                visibleItemCount = 5,
                unselectedItemMinAlpha = 0.5f,
                colors = WheelTimePickerDefaults.colors(
                    backgroundColor = Color(0xFF1F2121),
                    selectedTextColor = Color.White,
                    unSelectedTextColor = Color(0xFFB0B0B0)
                ),
                amPmLabels = WheelTimePickerAmPmLabels("AM", "PM") // AM/PM 레이블 커스터마이징
            )
            Text(
                text = "Selected: ${wheelEffectState.hour12}:${wheelEffectState.minute.toString().padStart(2, '0')} ${if (wheelEffectState.isPm) "PM" else "AM"}",
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        HorizontalDivider()

        // --- Section 3: Custom Colors & Fade ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Custom Colors & Fade", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            WheelTimePicker(
                state = customStyleState,
                modifier = Modifier.fillMaxWidth(),
                isFadeEdgeEnabled = true, // 페이드 효과 활성화
                visibleItemCount = 7,
                colors = WheelTimePickerDefaults.colors(
                    selectedTextColor = Color(0xFF6200EE), // 포인트 컬러
                    unSelectedTextColor = Color.Gray,
                    fadeColor = Color.White
                )
            )
            Text(
                text = "Selected: ${customStyleState.hour12}:${customStyleState.minute.toString().padStart(2, '0')} ${if (customStyleState.isPm) "PM" else "AM"}",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
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
