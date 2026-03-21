package com.gymapp.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gymapp.data.db.dao.WeeklyVolume
import com.gymapp.data.db.entities.WorkoutSession
import com.gymapp.ui.theme.AccentGold
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundPrimary
import com.gymapp.ui.theme.BackgroundSurface
import com.gymapp.ui.theme.TextPrimary
import com.gymapp.ui.theme.TextSecondary
import com.gymapp.viewmodel.ProgressViewModel

data class RankedRecord(
    val rank: Int,
    val nameAr: String,
    val maxWeight: Float
)

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val totalSessions by viewModel.totalSessions.collectAsStateWithLifecycle()
    val totalDuration by viewModel.totalDurationMinutes.collectAsStateWithLifecycle()
    val totalVolume by viewModel.totalVolume.collectAsStateWithLifecycle()
    val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()
    val weeklyVolume by viewModel.weeklyVolume.collectAsStateWithLifecycle()

    val rankedRecords: List<RankedRecord> = personalRecords.mapIndexed { i, r ->
        RankedRecord(rank = i + 1, nameAr = r.nameAr, maxWeight = r.maxWeight)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        HeaderSection()
        Spacer(modifier = Modifier.height(16.dp))
        StatsSection(
            totalSessions = totalSessions,
            totalDuration = totalDuration,
            totalVolume = totalVolume,
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.height(20.dp))
        ChartSection(weeklyVolume = weeklyVolume)
        Spacer(modifier = Modifier.height(20.dp))
        RecordsSection(rankedRecords = rankedRecords)
        Spacer(modifier = Modifier.height(20.dp))
        SessionsSection(
            recentSessions = recentSessions,
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundSurface)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "التقدم 📈",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatsSection(
    totalSessions: Int,
    totalDuration: Int,
    totalVolume: Float,
    viewModel: ProgressViewModel
) {
    Text(
        text = "الإحصائيات الكلية",
        color = TextPrimary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatBox(
            modifier = Modifier.weight(1f),
            emoji = "🏋️",
            value = totalSessions.toString(),
            label = "جلسة",
            color = AccentRed
        )
        StatBox(
            modifier = Modifier.weight(1f),
            emoji = "⏱️",
            value = viewModel.formatHours(totalDuration),
            label = "وقت التمرين",
            color = AccentGold
        )
        StatBox(
            modifier = Modifier.weight(1f),
            emoji = "⚡",
            value = viewModel.formatVolume(totalVolume),
            label = "إجمالي الحجم",
            color = Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun ChartSection(weeklyVolume: List<WeeklyVolume>) {
    Text(
        text = "الحجم الأسبوعي",
        color = TextPrimary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (weeklyVolume.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📊", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "سجّل تمارين لرؤية الرسم البياني",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                val maxVol = weeklyVolume.maxOf { it.weekVolume }.coerceAtLeast(1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val pad = 20.dp.toPx()
                        val cw = w - pad * 2
                        val ch = h - pad * 2
                        val cnt = weeklyVolume.size
                        val bw = cw / (cnt * 2f)

                        drawLine(
                            color = Color(0xFF2A2A3A),
                            start = Offset(pad, h - pad),
                            end = Offset(w - pad, h - pad),
                            strokeWidth = 1.dp.toPx()
                        )

                        weeklyVolume.forEachIndexed { idx, data ->
                            val x = pad + idx * (bw + bw) + bw / 2
                            val bh = (data.weekVolume / maxVol) * ch
                            val top = h - pad - bh

                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        AccentRed,
                                        AccentRed.copy(alpha = 0.3f)
                                    ),
                                    startY = top,
                                    endY = h - pad
                                ),
                                topLeft = Offset(x, top),
                                size = Size(bw, bh)
                            )
                        }
                    }

                    // قيم الأعمدة كـ Text فوق Canvas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val maxVol2 = weeklyVolume.maxOf { it.weekVolume }.coerceAtLeast(1f)
                        weeklyVolume.forEach { data ->
                            val heightFraction = data.weekVolume / maxVol2
                            val topPadding = (120.dp * (1f - heightFraction))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = topPadding),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                if (data.weekVolume > 0) {
                                    Text(
                                        text = "${data.weekVolume.toInt()}",
                                        color = TextSecondary,
                                        fontSize = 9.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weeklyVolume.forEachIndexed { idx, _ ->
                        Text(
                            text = if (idx == weeklyVolume.size - 1) "هذا الأسبوع"
                            else "أسبوع ${weeklyVolume.size - idx}",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun RecordsSection(rankedRecords: List<RankedRecord>) {
    Text(
        text = "الأرقام القياسية 🏆",
        color = TextPrimary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (rankedRecords.isEmpty()) {
        EmptyBox(emoji = "🏆", message = "سجّل تمارينك لترى أرقامك القياسية")
    } else {
        rankedRecords.forEach { ranked ->
            RecordBox(ranked = ranked)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SessionsSection(
    recentSessions: List<WorkoutSession>,
    viewModel: ProgressViewModel
) {
    Text(
        text = "آخر الجلسات",
        color = TextPrimary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (recentSessions.isEmpty()) {
        EmptyBox(emoji = "📅", message = "لا توجد جلسات مسجلة بعد")
    } else {
        recentSessions.forEach { session ->
            SessionBox(session = session, viewModel = viewModel)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatBox(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecordBox(ranked: RankedRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (ranked.rank) {
                            1 -> AccentGold.copy(alpha = 0.2f)
                            2 -> Color(0xFF9E9E9E).copy(alpha = 0.2f)
                            3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                            else -> BackgroundPrimary
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (ranked.rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "${ranked.rank}"
                    },
                    fontSize = if (ranked.rank <= 3) 16.sp else 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = ranked.nameAr,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${ranked.maxWeight.toInt()} كغ",
                    color = AccentRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "أعلى وزن",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun SessionBox(
    session: WorkoutSession,
    viewModel: ProgressViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📅", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = viewModel.formatDate(session.date),
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = viewModel.formatHours(session.durationMinutes),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = viewModel.formatVolume(session.totalVolume),
                    color = AccentGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "حجم التمرين",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyBox(emoji: String, message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}