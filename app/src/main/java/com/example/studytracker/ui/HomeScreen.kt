package com.example.studytracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.YearMonth

@Composable
fun HomeScreen(
    state: HomeUiState,
    onHoursSubmit: (String) -> Unit
) {
    var inputHours by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0D1122), Color(0xFF05070F), Color(0xFF020308))
                )
            )
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ATUALIZADO AS ${state.updatedAt}",
                color = Color(0xFF86829A),
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Tracker de ",
                    color = Color(0xFFDDDDDD),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Estudos",
                    color = Color(0xFFE4C774),
                    style = MaterialTheme.typography.headlineMedium,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressOrb(
                    orbSize = 126.dp,
                    value = state.todayHours.toFloat().coerceIn(0f, 12f) / 12f,
                    centerValue = "%.1f".format(state.todayHours),
                    subtitle = "HOJE",
                    glowColor = Color(0xFFE8C97A)
                )

                val currentMonth = YearMonth.now()
                ProgressOrb(
                    orbSize = 178.dp,
                    value = state.monthProgress,
                    centerValue = "%.1f".format(state.monthHours),
                    subtitle = "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                    glowColor = Color(0xFFE8C97A)
                )

                ProgressOrb(
                    orbSize = 126.dp,
                    value = state.weekProgress,
                    centerValue = "%.1f".format(state.weekHours),
                    subtitle = "ESTA SEMANA",
                    glowColor = Color(0xFF7AC9EA)
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            StatsBar(
                sessions = state.sessionsThisMonth,
                streak = state.currentStreakDays,
                remaining = (state.weekGoal - state.weekHours).coerceAtLeast(0.0),
                consistency = state.consistency
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = inputHours,
                    onValueChange = { inputHours = it },
                    label = { Text("Horas") },
                    modifier = Modifier.width(180.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = {
                    onHoursSubmit(inputHours)
                    inputHours = ""
                }) {
                    Text("Salvar")
                }
            }

            AnimatedVisibility(visible = state.weekHours > 0) {
                Text(
                    text = "Meta semanal: %.1fh / %.1fh".format(state.weekHours, state.weekGoal),
                    color = Color(0xFF88E1B5),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun ProgressOrb(
    orbSize: Dp,
    value: Float,
    centerValue: String,
    subtitle: String,
    glowColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(orbSize)) {
            Canvas(
                modifier = matchParentSize()
                    .blur(8.dp)
            ) {
                drawCircle(color = glowColor.copy(alpha = 0.12f))
            }

            Canvas(modifier = matchParentSize()) {
                val stroke = 8.dp.toPx()
                drawArc(
                    color = Color.White.copy(alpha = 0.08f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = stroke)
                )
                drawArc(
                    color = glowColor,
                    startAngle = -90f,
                    sweepAngle = 360f * value,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawCircle(color = Color.White.copy(alpha = 0.03f), radius = size.minDimension / 2.9f)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = centerValue,
                    color = Color(0xFFDFDFDF),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "HORAS",
                    color = Color(0xFF8A849D),
                    letterSpacing = 2.sp,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            color = Color(0xFF8A849D),
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatsBar(
    sessions: Int,
    streak: Int,
    remaining: Double,
    consistency: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x1FFFFFFF)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("$sessions", "SESSOES NO MES", Color(0xFFE9C56C))
            StatItem("$streak", "DIAS DE SEQUENCIA", Color(0xFF73C5EA))
            StatItem("%.1fh".format(remaining), "FALTAM P/ META", Color(0xFFDF7097))
            StatItem("$consistency%", "APROVEITAMENTO", Color(0xFF87D46F))
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color(0xFF8A849D), fontSize = 10.sp, letterSpacing = 1.5.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Canvas(modifier = Modifier.size(width = 88.dp, height = 3.dp)) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(color.copy(alpha = 0.12f), color, color.copy(alpha = 0.12f)),
                    start = Offset.Zero,
                    end = Offset(size.width, 0f)
                ),
                size = Size(size.width, size.height)
            )
        }
    }
}
