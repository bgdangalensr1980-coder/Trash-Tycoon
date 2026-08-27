package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameSimulationEngine
import com.example.model.StaffMember
import com.example.model.StaffRole
import com.example.ui.theme.*

@Composable
fun StaffScreen(
    engine: GameSimulationEngine,
    modifier: Modifier = Modifier
) {
    val staffList by engine.staffMembers.collectAsState()
    val money by engine.money.collectAsState()

    var showHireDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Управление Персоналом",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Text(
                        text = "Операторы, инженеры и эколог-аудиторы",
                        fontSize = 12.sp,
                        color = SleekTextMuted
                    )
                }

                Button(
                    onClick = { showHireDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekTeal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("hire_staff_open_btn")
                ) {
                    Text(text = "+ Нанять", color = SleekTextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${staffList.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SleekTeal)
                        Text(text = "Штат рабочих", fontSize = 11.sp, color = SleekTextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val totalWages = staffList.sumOf { it.salaryDaily }
                        Text(
                            text = "$${totalWages.toInt()}/день",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = SleekTextDark
                        )
                        Text(text = "Фонд оплаты", fontSize = 11.sp, color = SleekTextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val avgMorale = if (staffList.isNotEmpty()) staffList.map { it.moralePercent }.average().toInt() else 100
                        Text(text = "$avgMorale%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EcoEmerald)
                        Text(text = "Лояльность", fontSize = 11.sp, color = SleekTextMuted)
                    }
                }
            }
        }

        items(staffList) { staff ->
            StaffCard(staff = staff)
        }
    }

    if (showHireDialog) {
        AlertDialog(
            onDismissRequest = { showHireDialog = false },
            title = {
                Text(text = "Агентство Найма Персонала", fontWeight = FontWeight.Bold, color = SleekTextDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StaffRole.values().forEach { role ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SleekBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = role.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SleekTextDark)
                                    Text(text = role.perkDescription, fontSize = 10.sp, color = SleekTextMuted)
                                    Text(text = "Оклад: $${role.baseSalary.toInt()}/день", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SleekTeal)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        engine.hireStaff(role)
                                        showHireDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SleekTeal),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(text = "Нанять", fontSize = 11.sp, color = SleekTextWhite, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHireDialog = false }) {
                    Text(text = "Закрыть", color = SleekTextMuted)
                }
            },
            containerColor = SleekCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun StaffCard(staff: StaffMember) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SleekCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SleekBackground,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = staff.avatarEmoji, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = staff.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SleekTealMuted
                    ) {
                        Text(
                            text = "Lvl ${staff.level}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTealDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = staff.role.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SleekTeal
                )
                Text(
                    text = staff.role.perkDescription,
                    fontSize = 10.sp,
                    color = SleekTextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${staff.salaryDaily.toInt()}/сут",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = SleekTextDark
                )
                Text(
                    text = "Мораль ${staff.moralePercent.toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EcoEmerald
                )
            }
        }
    }
}
