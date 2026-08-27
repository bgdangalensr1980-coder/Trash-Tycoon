package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TopBarHUD
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreenTab
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val engine = viewModel.engine

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SleekBackground,
        topBar = {
            TopBarHUD(engine = engine)
        },
        bottomBar = {
            NavigationBar(
                containerColor = SleekCard,
                tonalElevation = 6.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                listOf(
                    ActiveScreenTab.YARD_2D to Icons.Default.PrecisionManufacturing,
                    ActiveScreenTab.CONVEYOR_EDITOR to Icons.Default.AccountTree,
                    ActiveScreenTab.GLOBAL_MARKET to Icons.Default.TrendingUp,
                    ActiveScreenTab.CONTRACTS_AUCTION to Icons.Default.Gavel,
                    ActiveScreenTab.STAFF_MGMT to Icons.Default.Groups,
                    ActiveScreenTab.ECO_COMPLIANCE to Icons.Default.Eco,
                    ActiveScreenTab.TECH_TREE to Icons.Default.Science,
                    ActiveScreenTab.ANALYTICS to Icons.Default.Analytics
                ).forEach { (tab, icon) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = when (tab) {
                                    ActiveScreenTab.YARD_2D -> "Полигон"
                                    ActiveScreenTab.CONVEYOR_EDITOR -> "Линии"
                                    ActiveScreenTab.GLOBAL_MARKET -> "Рынок"
                                    ActiveScreenTab.CONTRACTS_AUCTION -> "Тендеры"
                                    ActiveScreenTab.STAFF_MGMT -> "Кадры"
                                    ActiveScreenTab.ECO_COMPLIANCE -> "Эко"
                                    ActiveScreenTab.TECH_TREE -> "НИОКР"
                                    ActiveScreenTab.ANALYTICS -> "Аналитика"
                                },
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SleekTeal,
                            selectedTextColor = SleekTeal,
                            indicatorColor = SleekTealMuted,
                            unselectedIconColor = SleekTextLight,
                            unselectedTextColor = SleekTextLight
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ActiveScreenTab.YARD_2D -> YardSimulationView(engine = engine)
                ActiveScreenTab.CONVEYOR_EDITOR -> ConveyorEditorScreen(engine = engine)
                ActiveScreenTab.GLOBAL_MARKET -> MarketScreen(engine = engine)
                ActiveScreenTab.CONTRACTS_AUCTION -> ContractsAuctionScreen(engine = engine)
                ActiveScreenTab.STAFF_MGMT -> StaffScreen(engine = engine)
                ActiveScreenTab.ECO_COMPLIANCE -> EcoComplianceScreen(engine = engine)
                ActiveScreenTab.TECH_TREE -> TechTreeScreen(engine = engine)
                ActiveScreenTab.ANALYTICS -> AnalyticsScreen(engine = engine)
            }
        }
    }
}
