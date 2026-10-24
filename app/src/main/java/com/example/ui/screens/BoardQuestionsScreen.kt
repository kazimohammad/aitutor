package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BoardQuestion
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardQuestionsScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    val boardQuestions by viewModel.boardQuestions.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val isBangla = profile.appLanguage == "bn"

    var selectedBoard by remember { mutableStateOf("সকল") }
    var selectedYear by remember { mutableStateOf("সকল") }

    val boards = listOf("সকল", "ঢাকা বোর্ড", "রাজশাহী বোর্ড", "কুমিল্লা বোর্ড", "যশোর বোর্ড", "চট্টগ্রাম বোর্ড", "সিলেট বোর্ড", "দিনাজপুর বোর্ড", "এআই বোর্ড প্র্যাকটিস")
    val years = listOf("সকল", "২০২৩", "২০২২", "২০২৪")

    val filteredQuestions = boardQuestions.filter { q ->
        val matchesBoard = selectedBoard == "সকল" || q.boardName.contains(selectedBoard)
        val matchesYear = selectedYear == "সকল" || q.year.toString() == selectedYear
        matchesBoard && matchesYear
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "বোর্ড প্রশ্ন ও সমাধান" else "Board Questions & Solutions",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBangla) "৯টি শিক্ষা বোর্ডের বিগত প্রশ্ন ও এআই প্র্যাকটিস" else "Verified past board & AI practice",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(ScreenRoute.Home)
                        },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 60.dp)
        ) {
            // Accuracy Notice Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "বোর্ড প্রশ্ন সত্যতা নীতি:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "সত্যিকারের বোর্ড প্রশ্নগুলো সবুজ ব্যাজে চিহ্নিত। এআই-জেনারেটেড প্র্যাকটিস প্রশ্নগুলো আলাদাভাবে হলুদ ব্যাজে চিহ্নিত করা হয়েছে।",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Board Filter Chips
            item {
                Text(
                    text = if (isBangla) "শিক্ষা বোর্ড নির্বাচন:" else "Select Education Board:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(boards) { board ->
                        val isSelected = selectedBoard == board
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBoard = board },
                            label = { Text(board, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("board_chip_$board")
                        )
                    }
                }
            }

            // Year Filter Chips
            item {
                Text(
                    text = if (isBangla) "সাল নির্বাচন করুন:" else "Select Year:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(years) { yr ->
                        val isSelected = selectedYear == yr
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedYear = yr },
                            label = { Text(yr, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("year_chip_$yr")
                        )
                    }
                }
            }

            if (filteredQuestions.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("এই ফিল্টারে কোনো বোর্ড প্রশ্ন পাওয়া যায়নি। অন্য বোর্ড বা সাল নির্বাচন করুন।")
                        }
                    }
                }
            } else {
                items(filteredQuestions) { bq ->
                    BoardQuestionCard(bq = bq, onSpeak = {
                        viewModel.ttsManager.speak("${bq.questionText}। ${bq.modelAnswer}")
                    })
                }
            }
        }
    }
}

@Composable
fun BoardQuestionCard(
    bq: BoardQuestion,
    onSpeak: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("board_question_card_${bq.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Provenance Badge
                Surface(
                    color = if (bq.isVerifiedBoardQuestion) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (bq.isVerifiedBoardQuestion) Icons.Default.Verified else Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = if (bq.isVerifiedBoardQuestion) Color(0xFF047857) else Color(0xFFD97706),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (bq.isVerifiedBoardQuestion) "সত্যিকারের বোর্ড প্রশ্ন" else "এআই বোর্ড প্র্যাকটিস",
                            color = if (bq.isVerifiedBoardQuestion) Color(0xFF047857) else Color(0xFFD97706),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${bq.boardName} (${bq.year})",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subject & Chapter info
            Text(
                text = "${bq.subjectName} • ${bq.chapterName} (শ্রেণি ${bq.classLevel})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (!bq.stimulus.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = bq.stimulus,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(10.dp),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = bq.questionText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.testTag("board_toggle_answer_${bq.id}")
                ) {
                    Text(if (isExpanded) "উত্তর লুকান" else "উত্তর দেখুন")
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }

                IconButton(onClick = onSpeak) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Read Aloud", tint = MaterialTheme.colorScheme.primary)
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "আদর্শ সমাধান ও ব্যাখ্যা:",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bq.modelAnswer,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "উৎস তথ্য: ${bq.sourceReference}",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
