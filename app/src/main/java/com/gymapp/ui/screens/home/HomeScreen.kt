package com.gymapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gymapp.ui.theme.AccentGold
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundPrimary
import com.gymapp.ui.theme.BackgroundSurface
import com.gymapp.ui.theme.TextPrimary
import com.gymapp.ui.theme.TextSecondary
import com.gymapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToExercises: () -> Unit = {},
    onNavigateToEquipment: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val totalExercises by viewModel.totalExercises.collectAsStateWithLifecycle()
    val totalEquipment by viewModel.totalEquipment.collectAsStateWithLifecycle()
    val weeklySessions by viewModel.weeklySessionsCount.collectAsStateWithLifecycle()
    val (dayName, dateString) = remember { viewModel.getCurrentDayAndDate() }
    val greeting = remember { viewModel.getGreeting() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // ═══ Header ═══
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BackgroundSurface,
                                BackgroundPrimary
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Text(
                        text = greeting,
                        color = AccentGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "مرحباً بك في GymApp",
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentRed.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = dayName,
                                color = AccentRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dateString,
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // ═══ Stats Section ═══
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "إحصائياتك",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🏋️",
                        value = totalExercises.toString(),
                        label = "تمرين",
                        color = AccentRed
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        emoji = "⚙️",
                        value = totalEquipment.toString(),
                        label = "جهاز",
                        color = Color(0xFF2196F3)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        emoji = "📅",
                        value = weeklySessions.toString(),
                        label = "هذا الأسبوع",
                        color = AccentGold
                    )
                }
            }
        }

        // ═══ Quick Actions ═══
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "وصول سريع",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Start Workout — Big Button
                StartWorkoutButton(
                    onClick = onNavigateToWorkout
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Browse Exercises + Equipment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        emoji = "💪",
                        title = "تصفح التمارين",
                        subtitle = "$totalExercises تمرين متاح",
                        color = AccentRed,
                        onClick = onNavigateToExercises
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        emoji = "🔧",
                        title = "دليل الأجهزة",
                        subtitle = "$totalEquipment جهاز موثق",
                        color = Color(0xFF2196F3),
                        onClick = onNavigateToEquipment
                    )
                }
            }
        }

        // ═══ Motivation Card ═══
        item {
            Spacer(modifier = Modifier.height(24.dp))
            MotivationCard()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ═══════════════ Composables ═══════════════

@Composable
fun StatCard(
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
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun StartWorkoutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(AccentRed, Color(0xFFFF6B6B))
                )
            )
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ابدأ تمريني",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "سجّل تمرينك الآن",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
            Text(text = "🏃", fontSize = 36.sp)
        }
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun MotivationCard() {
    val quotes = listOf(
        Pair("الانضباط يبني الإمبراطوريات 👑", "استمر حتى تصل"),
        Pair("كل تمرين يقربك من هدفك 🎯", "لا تتوقف الآن"),
        Pair("الألم مؤقت، الفخر دائم 💪", "أنت أقوى مما تعتقد"),
        Pair("النجاح يبدأ بخطوة واحدة 🚀", "ابدأ الآن")
    )
    val quote = remember {
        quotes[System.currentTimeMillis().toInt() % quotes.size]
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1A1A35),
                        BackgroundSurface
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = quote.first,
                color = AccentGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quote.second,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}