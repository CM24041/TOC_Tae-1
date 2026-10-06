package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.MealyTransition
import com.example.model.StateNode
import com.example.model.Transition
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlowPurple
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class MachineDisplayMode {
    MOORE,
    MEALY
}

@Composable
fun AutomatonCanvas(
    modifier: Modifier = Modifier,
    states: List<StateNode>,
    transitions: List<Transition> = emptyList(),
    mealyTransitions: List<MealyTransition> = emptyList(),
    mode: MachineDisplayMode = MachineDisplayMode.MOORE,
    initialState: String = "",
    selectedStateName: String? = null,
    highlightedTransitionIndex: Int? = null,
    activePulseState: String? = null,
    onStateClicked: (String) -> Unit = {}
) {
    // Draggable / calculated node positions: StateName -> Offset (normalized or absolute)
    val nodePositions = remember { mutableStateMapOf<String, Offset>() }

    // Pulsing halo animation for active state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (activePulseState != null) 1f else 0.4f,
        animationSpec = tween(600),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF070B14))
            .testTag("automaton_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(states) {
                    detectTapGestures { tapOffset ->
                        val nodeRadius = 38f
                        var hitState: String? = null
                        for ((name, pos) in nodePositions) {
                            val dist = (tapOffset - pos).getDistance()
                            if (dist <= nodeRadius * 1.5f) {
                                hitState = name
                                break
                            }
                        }
                        if (hitState != null) {
                            onStateClicked(hitState)
                        }
                    }
                }
                .pointerInput(states) {
                    var draggedNode: String? = null
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            val nodeRadius = 45f
                            for ((name, pos) in nodePositions) {
                                if ((startOffset - pos).getDistance() <= nodeRadius) {
                                    draggedNode = name
                                    break
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            draggedNode?.let { node ->
                                val current = nodePositions[node] ?: Offset.Zero
                                nodePositions[node] = current + dragAmount
                            }
                        },
                        onDragEnd = { draggedNode = null },
                        onDragCancel = { draggedNode = null }
                    )
                }
        ) {
            val width = size.width
            val height = size.height

            if (states.isEmpty()) {
                drawEmptyGrid(width, height)
                return@Canvas
            }

            // Compute default circular arrangement if positions are missing
            val centerX = width / 2f
            val centerY = height / 2f
            val radius = (width.coerceAtMost(height) * 0.34f).coerceAtLeast(80f)

            states.forEachIndexed { index, state ->
                if (!nodePositions.containsKey(state.name)) {
                    val angle = (2 * PI / states.size) * index - PI / 2
                    val x = centerX + radius * cos(angle).toFloat()
                    val y = centerY + radius * sin(angle).toFloat()
                    nodePositions[state.name] = Offset(x, y)
                }
            }

            // Draw futuristic subtle grid background
            drawCyberGrid(width, height)

            // Draw transitions (arrows)
            val nodeRadius = 34.dp.toPx()

            if (mode == MachineDisplayMode.MOORE) {
                // Group transitions between same pair to handle bidirectional curvatures
                transitions.forEachIndexed { idx, t ->
                    val fromPos = nodePositions[t.fromState] ?: Offset.Zero
                    val toPos = nodePositions[t.toState] ?: Offset.Zero
                    val isHighlighted = idx == highlightedTransitionIndex ||
                            (selectedStateName != null && (t.fromState == selectedStateName || t.toState == selectedStateName))

                    if (t.fromState == t.toState) {
                        drawSelfLoop(
                            center = fromPos,
                            nodeRadius = nodeRadius,
                            label = t.inputSymbol,
                            isHighlighted = isHighlighted
                        )
                    } else {
                        // Check if reverse transition exists
                        val hasReverse = transitions.any { it.fromState == t.toState && it.toState == t.fromState }
                        drawCurvedArrow(
                            from = fromPos,
                            to = toPos,
                            nodeRadius = nodeRadius,
                            label = t.inputSymbol,
                            curveOffset = if (hasReverse) 30f else 12f,
                            isHighlighted = isHighlighted
                        )
                    }
                }
            } else {
                // MEALY MODE
                mealyTransitions.forEachIndexed { idx, mt ->
                    val fromPos = nodePositions[mt.fromState] ?: Offset.Zero
                    val toPos = nodePositions[mt.toState] ?: Offset.Zero
                    val isHighlighted = idx == highlightedTransitionIndex ||
                            (selectedStateName != null && (mt.fromState == selectedStateName || mt.toState == selectedStateName))

                    val label = "${mt.inputSymbol}/${mt.outputSymbol}"

                    if (mt.fromState == mt.toState) {
                        drawSelfLoop(
                            center = fromPos,
                            nodeRadius = nodeRadius,
                            label = label,
                            isHighlighted = isHighlighted
                        )
                    } else {
                        val hasReverse = mealyTransitions.any { it.fromState == mt.toState && it.toState == mt.fromState }
                        drawCurvedArrow(
                            from = fromPos,
                            to = toPos,
                            nodeRadius = nodeRadius,
                            label = label,
                            curveOffset = if (hasReverse) 34f else 14f,
                            isHighlighted = isHighlighted
                        )
                    }
                }
            }

            // Draw State Nodes (Circles with labels)
            states.forEach { state ->
                val pos = nodePositions[state.name] ?: Offset(centerX, centerY)
                val isSelected = state.name == selectedStateName
                val isInitial = state.name == initialState
                val isPulse = state.name == activePulseState

                drawStateCircle(
                    pos = pos,
                    radius = nodeRadius,
                    state = state,
                    mode = mode,
                    isSelected = isSelected,
                    isInitial = isInitial,
                    isPulse = isPulse
                )
            }
        }
    }
}

private fun DrawScope.drawCyberGrid(width: Float, height: Float) {
    val step = 40.dp.toPx()
    val gridColor = Color(0xFF131B2E).copy(alpha = 0.5f)
    var x = 0f
    while (x < width) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
        x += step
    }
    var y = 0f
    while (y < height) {
        drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
        y += step
    }
}

private fun DrawScope.drawEmptyGrid(width: Float, height: Float) {
    drawCyberGrid(width, height)
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(180, 148, 163, 184)
            textSize = 36f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        drawText("Add states above to generate the automaton diagram", width / 2f, height / 2f, paint)
    }
}

private fun DrawScope.drawStateCircle(
    pos: Offset,
    radius: Float,
    state: StateNode,
    mode: MachineDisplayMode,
    isSelected: Boolean,
    isInitial: Boolean,
    isPulse: Boolean
) {
    // Initial State Pointer Arrow (left side of initial state pointing in)
    if (isInitial) {
        val arrowStart = Offset(pos.x - radius - 38f, pos.y)
        val arrowEnd = Offset(pos.x - radius - 4f, pos.y)
        drawLine(
            color = EmeraldGreen,
            start = arrowStart,
            end = arrowEnd,
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        // arrowhead
        val arrowHeadPath = Path().apply {
            moveTo(arrowEnd.x, arrowEnd.y)
            lineTo(arrowEnd.x - 12f, arrowEnd.y - 7f)
            lineTo(arrowEnd.x - 12f, arrowEnd.y + 7f)
            close()
        }
        drawPath(arrowHeadPath, color = EmeraldGreen, style = Fill)
    }

    // Outer Glow / Halo
    if (isPulse || isSelected) {
        drawCircle(
            color = if (isPulse) EmeraldGreen.copy(alpha = 0.35f) else CyanAccent.copy(alpha = 0.3f),
            radius = radius + 14f,
            center = pos
        )
    }

    // Node background gradient
    val baseGrad = Brush.radialGradient(
        colors = when {
            isPulse -> listOf(Color(0xFF064E3B), Color(0xFF022C22))
            isSelected -> listOf(Color(0xFF2E1065), Color(0xFF1E1B4B))
            else -> listOf(Color(0xFF1E293B), Color(0xFF0F172A))
        },
        center = pos,
        radius = radius
    )
    drawCircle(brush = baseGrad, radius = radius, center = pos)

    // Border
    val borderColor = when {
        isPulse -> EmeraldGreen
        isSelected -> CyanAccent
        else -> ElectricPurple
    }
    drawCircle(
        color = borderColor,
        radius = radius,
        center = pos,
        style = Stroke(width = if (isSelected || isPulse) 3.5f else 2f)
    )

    // Text rendering:
    // In Moore mode: state.name / state.output (with divider)
    // In Mealy mode: state.name
    drawContext.canvas.nativeCanvas.apply {
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }

        if (mode == MachineDisplayMode.MOORE) {
            textPaint.textSize = 28f
            drawText(state.name, pos.x, pos.y - 4f, textPaint)

            // Small horizontal divider inside the node
            drawLine(
                color = Color(0xFF475569),
                start = Offset(pos.x - radius * 0.65f, pos.y + 6f),
                end = Offset(pos.x + radius * 0.65f, pos.y + 6f),
                strokeWidth = 1.2f
            )

            // Output label below divider
            val outPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.rgb(0, 240, 255) // Cyan
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                textSize = 24f
                isFakeBoldText = true
            }
            drawText(state.output, pos.x, pos.y + 26f, outPaint)
        } else {
            textPaint.textSize = 32f
            drawText(state.name, pos.x, pos.y + 11f, textPaint)
        }
    }
}

private fun DrawScope.drawCurvedArrow(
    from: Offset,
    to: Offset,
    nodeRadius: Float,
    label: String,
    curveOffset: Float,
    isHighlighted: Boolean
) {
    val dx = to.x - from.x
    val dy = to.y - from.y
    val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)

    // Unit vector
    val ux = dx / dist
    val uy = dy / dist

    // Normal vector perpendicular to line
    val nx = -uy
    val ny = ux

    // Start and end clipped at circle boundaries
    val start = Offset(from.x + ux * nodeRadius, from.y + uy * nodeRadius)
    val end = Offset(to.x - ux * nodeRadius, to.y - uy * nodeRadius)

    // Control point for quadratic bezier curve
    val mid = Offset((start.x + end.x) / 2f, (start.y + end.y) / 2f)
    val control = Offset(mid.x + nx * curveOffset, mid.y + ny * curveOffset)

    val path = Path().apply {
        moveTo(start.x, start.y)
        quadraticTo(control.x, control.y, end.x, end.y)
    }

    val arrowColor = if (isHighlighted) CyanAccent else Color(0xFF818CF8)
    drawPath(
        path = path,
        color = arrowColor,
        style = Stroke(
            width = if (isHighlighted) 3.5f else 2.2f,
            cap = StrokeCap.Round
        )
    )

    // Calculate tangent angle at the end for arrowhead
    val tEndTangent = Offset(end.x - control.x, end.y - control.y)
    val angle = atan2(tEndTangent.y, tEndTangent.x)
    val arrowHeadLen = 14f

    val p1 = Offset(
        end.x - arrowHeadLen * cos(angle - PI / 6).toFloat(),
        end.y - arrowHeadLen * sin(angle - PI / 6).toFloat()
    )
    val p2 = Offset(
        end.x - arrowHeadLen * cos(angle + PI / 6).toFloat(),
        end.y - arrowHeadLen * sin(angle + PI / 6).toFloat()
    )

    val headPath = Path().apply {
        moveTo(end.x, end.y)
        lineTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        close()
    }
    drawPath(headPath, color = arrowColor, style = Fill)

    // Label on curve (near control point)
    val labelPos = Offset(control.x + nx * 12f, control.y + ny * 12f)
    drawLabelPill(labelPos, label, isHighlighted)
}

private fun DrawScope.drawSelfLoop(
    center: Offset,
    nodeRadius: Float,
    label: String,
    isHighlighted: Boolean
) {
    // Loop over the top of the node
    val loopHeight = 44f
    val start = Offset(center.x - nodeRadius * 0.45f, center.y - nodeRadius)
    val end = Offset(center.x + nodeRadius * 0.45f, center.y - nodeRadius)
    val c1 = Offset(start.x - 24f, start.y - loopHeight)
    val c2 = Offset(end.x + 24f, end.y - loopHeight)

    val path = Path().apply {
        moveTo(start.x, start.y)
        cubicTo(c1.x, c1.y, c2.x, c2.y, end.x, end.y)
    }

    val arrowColor = if (isHighlighted) CyanAccent else Color(0xFF818CF8)
    drawPath(
        path = path,
        color = arrowColor,
        style = Stroke(
            width = if (isHighlighted) 3.5f else 2.2f,
            cap = StrokeCap.Round
        )
    )

    // Arrowhead on loop end pointing into the node
    val headPath = Path().apply {
        moveTo(end.x, end.y)
        lineTo(end.x + 6f, end.y - 12f)
        lineTo(end.x + 14f, end.y - 5f)
        close()
    }
    drawPath(headPath, color = arrowColor, style = Fill)

    // Label placed above the loop apex
    val labelPos = Offset(center.x, center.y - nodeRadius - loopHeight - 10f)
    drawLabelPill(labelPos, label, isHighlighted)
}

private fun DrawScope.drawLabelPill(pos: Offset, text: String, isHighlighted: Boolean) {
    val pillW = (text.length * 12f + 20f).coerceAtLeast(32f)
    val pillH = 22f

    // Background badge
    drawRoundRect(
        color = if (isHighlighted) Color(0xFF0F172A) else Color(0xFF1E293B),
        topLeft = Offset(pos.x - pillW / 2f, pos.y - pillH / 2f),
        size = androidx.compose.ui.geometry.Size(pillW, pillH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
    )
    drawRoundRect(
        color = if (isHighlighted) CyanAccent else Color(0xFF475569),
        topLeft = Offset(pos.x - pillW / 2f, pos.y - pillH / 2f),
        size = androidx.compose.ui.geometry.Size(pillW, pillH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
        style = Stroke(width = 1.2f)
    )

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = if (isHighlighted) android.graphics.Color.rgb(0, 240, 255) else android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 22f
            isAntiAlias = true
            isFakeBoldText = true
        }
        drawText(text, pos.x, pos.y + 7f, paint)
    }
}
