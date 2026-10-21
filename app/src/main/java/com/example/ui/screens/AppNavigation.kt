package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

data class BottomNavItem(
    val route: ScreenRoute,
    val banglaTitle: String,
    val englishTitle: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val isBangla = profile.appLanguage == "bn"

    val bottomNavItems = listOf(
        BottomNavItem(
            route = ScreenRoute.Home,
            banglaTitle = "হোম",
            englishTitle = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = ScreenRoute.Scanner,
            banglaTitle = "বই স্ক্যান",
            englishTitle = "Scan",
            selectedIcon = Icons.Filled.DocumentScanner,
            unselectedIcon = Icons.Outlined.DocumentScanner
        ),
        BottomNavItem(
            route = ScreenRoute.AiTutor,
            banglaTitle = "টিউটর",
            englishTitle = "Tutor",
            selectedIcon = Icons.Filled.AutoAwesome,
            unselectedIcon = Icons.Outlined.AutoAwesome
        ),
        BottomNavItem(
            route = ScreenRoute.SubjectChapter,
            banglaTitle = "পাঠ্যবই",
            englishTitle = "Subjects",
            selectedIcon = Icons.Filled.MenuBook,
            unselectedIcon = Icons.Outlined.MenuBook
        ),
        BottomNavItem(
            route = ScreenRoute.Progress,
            banglaTitle = "অগ্রগতি",
            englishTitle = "Progress",
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart
        )
    )

    // Check if bottom bar should be visible
    val isBottomBarVisible = currentScreen in listOf(
        ScreenRoute.Home,
        ScreenRoute.Scanner,
        ScreenRoute.AiTutor,
        ScreenRoute.SubjectChapter,
        ScreenRoute.Progress
    )

    Scaffold(
        topBar = {
            if (currentScreen != ScreenRoute.Onboarding) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_porashona_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "পড়াশোনা এআই" else "Porashona AI",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isBangla) "স্মার্ট স্টাডি টিউটর" else "Smart Study Tutor",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // Language Toggle Chip
                        AssistChip(
                            onClick = { viewModel.toggleAppLanguage() },
                            label = { Text(if (isBangla) "বাংলা" else "ENG", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            modifier = Modifier.testTag("top_language_toggle")
                        )

                        // Settings Icon
                        IconButton(
                            onClick = { viewModel.navigateTo(ScreenRoute.Settings) },
                            modifier = Modifier.testTag("top_settings_button")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentScreen == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(item.route) },
                            icon = {
                                Icon(
                                    if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = if (isBangla) item.banglaTitle else item.englishTitle
                                )
                            },
                            label = {
                                Text(
                                    text = if (isBangla) item.banglaTitle else item.englishTitle,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("bottom_tab_${item.englishTitle.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                ScreenRoute.Onboarding -> OnboardingScreen(viewModel = viewModel)
                ScreenRoute.Home -> HomeScreen(viewModel = viewModel)
                ScreenRoute.Scanner -> BookScannerScreen(viewModel = viewModel)
                ScreenRoute.AiTutor -> AiTutorScreen(viewModel = viewModel)
                ScreenRoute.SubjectChapter -> SubjectChapterScreen(viewModel = viewModel)
                ScreenRoute.LineByLine -> LineByLineScreen(viewModel = viewModel)
                ScreenRoute.ExamQuestions -> ExamQuestionsScreen(viewModel = viewModel)
                ScreenRoute.McqQuiz -> McqQuizScreen(viewModel = viewModel)
                ScreenRoute.RevisionPlanner -> RevisionPlannerScreen(viewModel = viewModel)
                ScreenRoute.Progress -> ProgressScreen(viewModel = viewModel)
                ScreenRoute.Practice -> {
                    // Practice hub: redirects to McqQuiz
                    LaunchedEffect(Unit) {
                        viewModel.loadMCQs()
                        viewModel.navigateTo(ScreenRoute.McqQuiz)
                    }
                }
                ScreenRoute.Settings -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
