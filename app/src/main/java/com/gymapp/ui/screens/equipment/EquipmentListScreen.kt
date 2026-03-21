package com.gymapp.ui.screens.equipment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gymapp.data.db.entities.Equipment
import com.gymapp.ui.theme.AccentRed
import com.gymapp.ui.theme.BackgroundPrimary
import com.gymapp.ui.theme.BackgroundSurface
import com.gymapp.ui.theme.TextPrimary
import com.gymapp.ui.theme.TextSecondary
import com.gymapp.viewmodel.EquipmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentListScreen(
    onEquipmentClick: (Int) -> Unit = {},
    viewModel: EquipmentViewModel = hiltViewModel()
) {
    val equipmentList by viewModel.equipment.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundSurface)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "الأجهزة",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = {
                Text(text = "ابحث عن جهاز...", color = TextSecondary)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث",
                    tint = TextSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = BackgroundSurface,
                focusedContainerColor = BackgroundSurface,
                unfocusedContainerColor = BackgroundSurface,
                cursorColor = AccentRed,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        // Type Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.types.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { viewModel.onTypeSelected(type) },
                    label = { Text(text = type, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed,
                        selectedLabelColor = TextPrimary,
                        containerColor = BackgroundSurface,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedType == type,
                        selectedBorderColor = AccentRed,
                        borderColor = BackgroundSurface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${equipmentList.size} جهاز",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        if (equipmentList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentRed)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = equipmentList,
                    key = { it.id }
                ) { equipment ->
                    EquipmentCard(
                        equipment = equipment,
                        onClick = { onEquipmentClick(equipment.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun EquipmentCard(
    equipment: Equipment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(getTypeColor(equipment.type).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getTypeEmoji(equipment.type),
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = equipment.nameAr,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = equipment.nameEn,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                TypeBadge(
                    text = equipment.type,
                    color = getTypeColor(equipment.type)
                )
            }

            Text(
                text = "›",
                color = TextSecondary,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun TypeBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getTypeColor(type: String): Color {
    return when (type) {
        "حر" -> Color(0xFF4CAF50)
        "آلة" -> Color(0xFF2196F3)
        "كابل" -> Color(0xFFFF9800)
        "هوائي" -> Color(0xFF9C27B0)
        else -> Color(0xFF8B8FA8)
    }
}

fun getTypeEmoji(type: String): String {
    return when (type) {
        "حر" -> "🏋️"
        "آلة" -> "⚙️"
        "كابل" -> "🔗"
        "هوائي" -> "🤸"
        else -> "💪"
    }
}