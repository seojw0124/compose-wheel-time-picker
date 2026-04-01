package com.jade.wheeltimepicker.wheel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

@Stable
interface WheelTimePickerState {
    var hour: Int // 0..23
    var minute: Int // 0..59
    val minuteInterval: Int
    var isPm: Boolean
    val hour12: Int

    fun updateHour12(newHour12: Int)
}

private class WheelTimePickerStateImpl(
    initialHour: Int,
    initialMinute: Int,
    override val minuteInterval: Int,
) : WheelTimePickerState {
    init {
        require(minuteInterval in 1..30 && 60 % minuteInterval == 0) {
            "minuteInterval must be a divisor of 60 and in 1..30"
        }
    }

    private var _hour by mutableIntStateOf(initialHour)
    private var _minute by mutableIntStateOf(initialMinute)

    override var hour: Int
        get() = _hour
        set(value) {
            val newValue = value.coerceIn(0, 23)
            if (_hour != newValue) {
                _hour = newValue
            }
        }

    override var minute: Int
        get() = _minute
        set(value) {
            val newValue = value.coerceIn(0, 59)
            if (_minute != newValue) {
                _minute = newValue
            }
        }

    override var isPm: Boolean
        get() = hour >= 12
        set(value) {
            val newHour =
                when {
                    value && _hour < 12 -> _hour + 12
                    !value && _hour >= 12 -> _hour - 12
                    else -> _hour
                }
            if (_hour != newHour) {
                _hour = newHour
            }
        }

    override val hour12: Int
        get() =
            when (val h = hour % 12) {
                0 -> 12
                else -> h
            }

    override fun updateHour12(newHour12: Int) {
        hour =
            when {
                isPm && newHour12 != 12 -> newHour12 + 12
                !isPm && newHour12 == 12 -> 0
                else -> newHour12
            }
    }

    companion object {
        fun Saver(): Saver<WheelTimePickerStateImpl, List<Int>> =
            Saver(
                save = { listOf(it.hour, it.minute, it.minuteInterval) },
                restore = { WheelTimePickerStateImpl(it[0], it[1], it[2]) },
            )
    }
}

@Composable
fun rememberWheelTimePickerState(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    minuteInterval: Int = WheelTimePickerDefaults.MINUTE_INTERVAL_5,
): WheelTimePickerState =
    rememberSaveable(saver = WheelTimePickerStateImpl.Saver()) {
        WheelTimePickerStateImpl(
            initialHour = initialHour,
            initialMinute = initialMinute,
            minuteInterval = minuteInterval,
        )
    }

@Immutable
data class WheelTimePickerColors(
    val selectedTextColor: Color,
    val unSelectedTextColor: Color,
    val backgroundColor: Color,
    val fadeColor: Color,
)

@Immutable
data class WheelTimePickerTextStyles(
    val textStyle: TextStyle,
)

@Immutable
data class WheelTimePickerAmPmLabels(
    val am: String,
    val pm: String,
)

@Stable
object WheelTimePickerDefaults {
    const val MINUTE_INTERVAL_1 = 1
    const val MINUTE_INTERVAL_5 = 5
    const val MINUTE_INTERVAL_10 = 10
    const val VISIBLE_ITEM_COUNT: Int = 3
    const val WHEEL_EFFECT_ROTATION_X_DEGREE: Float = 18f
    const val WHEEL_EFFECT_ALPHA_FALLOFF: Float = 0.6f
    const val UNSELECTED_ITEM_MIN_ALPHA: Float = 0.3f
    const val FADE_EDGE_STRENGTH: Float = 0.9f

    val itemVerticalPadding: Dp = 20.dp

    @Composable
    fun colors(
        selectedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        unSelectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        backgroundColor: Color = Color(0xFFFFFFFF),
        fadeColor: Color = backgroundColor,
    ): WheelTimePickerColors =
        WheelTimePickerColors(
            selectedTextColor = selectedTextColor,
            unSelectedTextColor = unSelectedTextColor,
            backgroundColor = backgroundColor,
            fadeColor = fadeColor,
        )

    @Composable
    fun textStyles(
        textStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    ): WheelTimePickerTextStyles =
        WheelTimePickerTextStyles(textStyle = textStyle)

    fun amPmLabels(locale: Locale = Locale.getDefault()): WheelTimePickerAmPmLabels =
        when (locale.language) {
            Locale.KOREAN.language -> WheelTimePickerAmPmLabels(am = "오전", pm = "오후")
            Locale.JAPANESE.language -> WheelTimePickerAmPmLabels(am = "午前", pm = "午後")
            else -> WheelTimePickerAmPmLabels(am = "AM", pm = "PM")
        }
}

@Composable
fun WheelTimePicker(
    state: WheelTimePickerState,
    modifier: Modifier = Modifier,
    colors: WheelTimePickerColors = WheelTimePickerDefaults.colors(),
    styles: WheelTimePickerTextStyles = WheelTimePickerDefaults.textStyles(),
    amPmLabels: WheelTimePickerAmPmLabels = WheelTimePickerDefaults.amPmLabels(),
    itemVerticalPadding: Dp = WheelTimePickerDefaults.itemVerticalPadding,
    visibleItemCount: Int = WheelTimePickerDefaults.VISIBLE_ITEM_COUNT,
    isFadeEdgeEnabled: Boolean = false,
    isWheelEffectEnabled: Boolean = false,
    wheelEffectRotationXDegree: Float = WheelTimePickerDefaults.WHEEL_EFFECT_ROTATION_X_DEGREE,
    unselectedItemMinAlpha: Float = WheelTimePickerDefaults.UNSELECTED_ITEM_MIN_ALPHA,
    fadeEdgeStrength: Float = WheelTimePickerDefaults.FADE_EDGE_STRENGTH,
) {
    require(visibleItemCount >= 3 && visibleItemCount % 2 == 1) {
        "visibleItemCount must be odd and >= 3"
    }
    require(wheelEffectRotationXDegree >= 0f) {
        "wheelEffectRotationXDegree must be >= 0"
    }
    require(unselectedItemMinAlpha in 0f..1f) {
        "unselectedItemMinAlpha must be in 0..1"
    }
    require(fadeEdgeStrength in 0f..1f) {
        "fadeEdgeStrength must be in 0..1"
    }

    val density = LocalDensity.current
    val itemHeight =
        remember(styles.textStyle, itemVerticalPadding) {
            with(density) {
                styles.textStyle.fontSize.toDp() + (itemVerticalPadding * 2)
            }
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(colors.backgroundColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AmPmColumn(
            isPm = state.isPm,
            labels = amPmLabels,
            onAmPmChange = { state.isPm = it },
            itemHeight = itemHeight,
            visibleItemCount = visibleItemCount,
            textStyle = styles.textStyle,
            colors = colors,
            isFadeEdgeEnabled = isFadeEdgeEnabled,
            isWheelEffectEnabled = isWheelEffectEnabled,
            wheelEffectRotationXDegree = wheelEffectRotationXDegree,
            unselectedItemMinAlpha = unselectedItemMinAlpha,
            fadeEdgeStrength = fadeEdgeStrength,
            modifier = Modifier.weight(1f),
        )
        HourColumn(
            hour12 = state.hour12,
            onHourChange = { newHour12 ->
                val oldHour12 = state.hour12
                if ((oldHour12 == 11 && newHour12 == 12) || (oldHour12 == 12 && newHour12 == 11)) {
                    state.isPm = !state.isPm
                }
                state.updateHour12(newHour12)
            },
            itemHeight = itemHeight,
            visibleItemCount = visibleItemCount,
            textStyle = styles.textStyle,
            colors = colors,
            isFadeEdgeEnabled = isFadeEdgeEnabled,
            isWheelEffectEnabled = isWheelEffectEnabled,
            wheelEffectRotationXDegree = wheelEffectRotationXDegree,
            unselectedItemMinAlpha = unselectedItemMinAlpha,
            fadeEdgeStrength = fadeEdgeStrength,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = ":",
            style = styles.textStyle,
            color = colors.selectedTextColor,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        MinuteColumn(
            minute = state.minute,
            minuteInterval = state.minuteInterval,
            onMinuteChange = { state.minute = it },
            itemHeight = itemHeight,
            visibleItemCount = visibleItemCount,
            textStyle = styles.textStyle,
            colors = colors,
            isFadeEdgeEnabled = isFadeEdgeEnabled,
            isWheelEffectEnabled = isWheelEffectEnabled,
            wheelEffectRotationXDegree = wheelEffectRotationXDegree,
            unselectedItemMinAlpha = unselectedItemMinAlpha,
            fadeEdgeStrength = fadeEdgeStrength,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AmPmColumn(
    isPm: Boolean,
    labels: WheelTimePickerAmPmLabels,
    itemHeight: Dp,
    visibleItemCount: Int,
    textStyle: TextStyle,
    colors: WheelTimePickerColors,
    onAmPmChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isFadeEdgeEnabled: Boolean = false,
    isWheelEffectEnabled: Boolean = false,
    wheelEffectRotationXDegree: Float = WheelTimePickerDefaults.WHEEL_EFFECT_ROTATION_X_DEGREE,
    unselectedItemMinAlpha: Float = WheelTimePickerDefaults.UNSELECTED_ITEM_MIN_ALPHA,
    fadeEdgeStrength: Float = WheelTimePickerDefaults.FADE_EDGE_STRENGTH,
) {
    val amPmItems = remember(labels) { listOf(labels.am, labels.pm) }.toImmutableList()

    BasicScrollableColumn(
        items = amPmItems,
        initialIndex = if (isPm) 1 else 0,
        externalSelectedIndex = if (isPm) 1 else 0,
        onValueChange = { onAmPmChange(it == 1) },
        itemHeight = itemHeight,
        visibleItemCount = visibleItemCount,
        textStyle = textStyle,
        colors = colors,
        isInfinite = false,
        isFadeEdgeEnabled = isFadeEdgeEnabled,
        isWheelEffectEnabled = isWheelEffectEnabled,
        wheelEffectRotationXDegree = wheelEffectRotationXDegree,
        unselectedItemMinAlpha = unselectedItemMinAlpha,
        fadeEdgeStrength = fadeEdgeStrength,
        modifier = modifier,
    )
}

@Composable
private fun HourColumn(
    hour12: Int,
    onHourChange: (Int) -> Unit,
    itemHeight: Dp,
    visibleItemCount: Int,
    textStyle: TextStyle,
    colors: WheelTimePickerColors,
    isFadeEdgeEnabled: Boolean,
    isWheelEffectEnabled: Boolean,
    wheelEffectRotationXDegree: Float,
    unselectedItemMinAlpha: Float,
    fadeEdgeStrength: Float,
    modifier: Modifier = Modifier,
) {
    BasicScrollableColumn(
        items = (1..12).map { it.toString() }.toImmutableList(),
        initialIndex = hour12 - 1,
        externalSelectedIndex = hour12 - 1,
        onValueChange = { onHourChange(it + 1) },
        itemHeight = itemHeight,
        visibleItemCount = visibleItemCount,
        textStyle = textStyle,
        colors = colors,
        isInfinite = true,
        isFadeEdgeEnabled = isFadeEdgeEnabled,
        isWheelEffectEnabled = isWheelEffectEnabled,
        wheelEffectRotationXDegree = wheelEffectRotationXDegree,
        unselectedItemMinAlpha = unselectedItemMinAlpha,
        fadeEdgeStrength = fadeEdgeStrength,
        modifier = modifier,
    )
}

@Composable
private fun MinuteColumn(
    minute: Int,
    minuteInterval: Int,
    onMinuteChange: (Int) -> Unit,
    itemHeight: Dp,
    visibleItemCount: Int,
    textStyle: TextStyle,
    colors: WheelTimePickerColors,
    isFadeEdgeEnabled: Boolean,
    isWheelEffectEnabled: Boolean,
    wheelEffectRotationXDegree: Float,
    unselectedItemMinAlpha: Float,
    fadeEdgeStrength: Float,
    modifier: Modifier = Modifier,
) {
    val items =
        remember(minuteInterval) {
            (0 until 60 step minuteInterval).map { it.toString().padStart(2, '0') }
        }.toImmutableList()
    val currentIndex = (minute / minuteInterval).coerceIn(0, items.size - 1)

    BasicScrollableColumn(
        items = items,
        initialIndex = currentIndex,
        externalSelectedIndex = currentIndex,
        onValueChange = { onMinuteChange(it * minuteInterval) },
        itemHeight = itemHeight,
        visibleItemCount = visibleItemCount,
        textStyle = textStyle,
        colors = colors,
        isInfinite = true,
        isFadeEdgeEnabled = isFadeEdgeEnabled,
        isWheelEffectEnabled = isWheelEffectEnabled,
        wheelEffectRotationXDegree = wheelEffectRotationXDegree,
        unselectedItemMinAlpha = unselectedItemMinAlpha,
        fadeEdgeStrength = fadeEdgeStrength,
        modifier = modifier,
    )
}

@Composable
private fun BasicScrollableColumn(
    items: ImmutableList<String>,
    initialIndex: Int,
    externalSelectedIndex: Int,
    onValueChange: (Int) -> Unit,
    itemHeight: Dp,
    visibleItemCount: Int,
    textStyle: TextStyle,
    colors: WheelTimePickerColors,
    isFadeEdgeEnabled: Boolean,
    isWheelEffectEnabled: Boolean,
    wheelEffectRotationXDegree: Float,
    unselectedItemMinAlpha: Float,
    fadeEdgeStrength: Float,
    modifier: Modifier = Modifier,
    isInfinite: Boolean = true,
) {
    val itemCount = items.size
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val repeatCount = if (isInfinite) 1000 else 1
    val startOffset = if (isInfinite) (repeatCount / 2) * itemCount else 0

    val listState =
        rememberLazyListState(
            initialFirstVisibleItemIndex = startOffset + initialIndex,
        )
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val currentIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) return@derivedStateOf initialIndex

            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val centerItem =
                layoutInfo.visibleItemsInfo.minByOrNull {
                    abs((it.offset + it.size / 2) - viewportCenter)
                }
            (centerItem?.index ?: 0) % itemCount
        }
    }

    LaunchedEffect(externalSelectedIndex) {
        if (!listState.isScrollInProgress && currentIndex != externalSelectedIndex) {
            val currentPosition = listState.firstVisibleItemIndex
            val currentRealIndex = currentPosition % itemCount
            val diff = externalSelectedIndex - currentRealIndex
            val scrollTarget =
                if (isInfinite) {
                    val adjustedDiff =
                        when {
                            abs(diff) <= itemCount / 2 -> diff
                            diff > 0 -> diff - itemCount
                            else -> diff + itemCount
                        }
                    currentPosition + adjustedDiff
                } else {
                    externalSelectedIndex
                }
            listState.animateScrollToItem(scrollTarget)
        }
    }

    var lastVibratedIndex by remember { mutableIntStateOf(-1) }
    val latestOnValueChange by rememberUpdatedState(onValueChange)

    LaunchedEffect(listState) {
        snapshotFlow { currentIndex }
            .collect { index ->
                if (lastVibratedIndex != index) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    lastVibratedIndex = index
                    latestOnValueChange(index)
                }
            }
    }

    val sideItems = visibleItemCount / 2

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(itemHeight * visibleItemCount)
                .then(
                    if (isFadeEdgeEnabled) {
                        Modifier.fadeEdge(colors.fadeColor, fadeEdgeStrength)
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            flingBehavior = snapBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * sideItems),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                count = itemCount * repeatCount,
                key = { index -> "${index}_${items[index % itemCount]}" },
            ) { index ->
                val realIndex = index % itemCount
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .graphicsLayer {
                                val layoutInfo = listState.layoutInfo
                                val itemInfo =
                                    layoutInfo.visibleItemsInfo.find { it.index == index }
                                if (itemInfo != null) {
                                    val viewportCenter =
                                        (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                                    val itemCenter = itemInfo.offset + itemInfo.size / 2f
                                    val distanceRatio =
                                        (itemCenter - viewportCenter) / itemInfo.size
                                    val distanceAbs = abs(distanceRatio)

                                    if (isWheelEffectEnabled) {
                                        rotationX = distanceRatio * -wheelEffectRotationXDegree
                                        val scale = 1f - (distanceAbs * 0.15f)
                                        scaleX = scale
                                        scaleY = scale
                                        alpha =
                                            (1f - (distanceAbs * WheelTimePickerDefaults.WHEEL_EFFECT_ALPHA_FALLOFF))
                                                .coerceIn(unselectedItemMinAlpha, 1f)
                                    } else {
                                        alpha = (1f - distanceAbs).coerceIn(unselectedItemMinAlpha, 1f)
                                    }
                                } else {
                                    alpha = unselectedItemMinAlpha
                                }
                            }
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) {
                                scope.launch {
                                    val currentFirstIndex = listState.firstVisibleItemIndex
                                    val currentRealIndex = currentFirstIndex % itemCount
                                    var diff = realIndex - currentRealIndex
                                    if (isInfinite && abs(diff) > itemCount / 2) {
                                        diff = if (diff > 0) diff - itemCount else diff + itemCount
                                    }
                                    listState.animateScrollToItem(currentFirstIndex + diff)
                                }
                            },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = items[realIndex],
                        style = textStyle,
                        color =
                            if (realIndex == currentIndex) {
                                colors.selectedTextColor
                            } else {
                                colors.unSelectedTextColor
                            },
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun Modifier.fadeEdge(
    color: Color,
    strength: Float = WheelTimePickerDefaults.FADE_EDGE_STRENGTH,
): Modifier {
    val fadeEdgeBrushColor =
        remember(color, strength) {
            arrayOf(
                0f to color.copy(alpha = strength),
                0.25f to color.copy(alpha = strength * 0.25f),
                0.5f to Color.Transparent,
                0.75f to color.copy(alpha = strength * 0.25f),
                1f to color.copy(alpha = strength),
            )
        }

    return this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithCache {
            val brush =
                Brush.verticalGradient(
                    colorStops = fadeEdgeBrushColor,
                    startY = 0f,
                    endY = size.height,
                )
            onDrawWithContent {
                drawContent()
                drawRect(
                    brush = brush,
                    blendMode = BlendMode.DstOut,
                )
            }
        }
}
