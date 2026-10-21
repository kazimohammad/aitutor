package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun McqQuizScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    val questions by viewModel.quizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val isSubmitted by viewModel.isQuizAnswerSubmitted.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val isFinished by viewModel.isQuizFinished.collectAsState()
    val quizMode by viewModel.quizMode.collectAsState()
    val examAnswers by viewModel.examRecordedAnswers.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val isBangla = profile.appLanguage == "bn"
    val isExamMode = quizMode == "Exam"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "MCQ অনুশীলন ইঞ্জিন" else "MCQ Practice Engine",
                        fontWeight = FontWeight.Bold
                    )
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
                },
                actions = {
                    // Mode Toggle: Practice vs Exam
                    FilterChip(
                        selected = quizMode == "Practice",
                        onClick = {
                            viewModel.setQuizMode(if (quizMode == "Practice") "Exam" else "Practice")
                        },
                        label = {
                            Text(if (quizMode == "Practice") "অনুশীলন মোড" else "পরীক্ষা মোড", fontSize = 11.sp)
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("quiz_mode_toggle")
                    )
                }
            )
        }
    ) { paddingValues ->
        if (questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("MCQ লোড হচ্ছে...")
                }
            }
            return@Scaffold
        }

        if (isFinished) {
            val total = questions.size
            val percentage = if (total > 0) (score * 100) / total else 0

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_result_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = if (percentage >= 80) "🏆" else if (percentage >= 50) "🌟" else "📚", fontSize = 44.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (percentage >= 80) "চমৎকার প্রস্তুতি!" else if (percentage >= 50) "বেশ ভালো হয়েছে!" else "আরও অনুশীলন প্রয়োজন!",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "আপনার প্রাপ্ত স্কোর: $score / $total ($percentage%)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "পরামর্শ ও রিভিশন:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (percentage >= 80)
                                            "আপনার অধ্যায়ের মৌলিক ধারণা পরিষ্কার। এবার সৃজনশীল প্রশ্ন ও প্রয়োগমূলক সমস্যা অনুশীলন করুন।"
                                        else
                                            "ভুল হওয়া প্রশ্নগুলোর ব্যাখ্যা ভালো করে পড়ে নিন এবং 'এক লাইন করে মুখস্থ' মোডে মূল পয়েন্টগুলো পুনরায় ঝালাই করুন।",
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.restartQuiz() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("retry_quiz_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("আবার পরীক্ষা দিন")
                                }

                                Button(
                                    onClick = { viewModel.navigateTo(ScreenRoute.Home) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("finish_quiz_home_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("হোমে ফিরুন")
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "প্রশ্নোত্তর পর্যালোচনা (Review):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                itemsIndexed(questions) { qIndex, q ->
                    val userAns = if (isExamMode) examAnswers[qIndex] else if (qIndex == currentIndex) selectedOption else null
                    val isCorrect = userAns == q.correctOptionIndex

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "প্রশ্ন ${qIndex + 1}: ${q.question}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (isCorrect) "✅ সঠিক" else "❌ ভুল",
                                    color = if (isCorrect) Color(0xFF059669) else Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "সঠিক উত্তর: ${q.options.getOrElse(q.correctOptionIndex) { "" }}",
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF059669),
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ব্যাখ্যা: ${q.explanation}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
            return@Scaffold
        }

        val currentQ = questions[currentIndex]
        val total = questions.size

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 60.dp)
        ) {
            // Header Progress
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "প্রশ্ন ${currentIndex + 1} / $total" else "Question ${currentIndex + 1} of $total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = if (isExamMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isExamMode) "পরীক্ষা মোড" else "অনুশীলন (স্কোর: $score)",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / total.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Question Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mcq_question_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "কঠিনতা: ${currentQ.difficulty}",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentQ.question,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // 4 Answer Options
            items(currentQ.options.size) { optIndex ->
                val optionText = currentQ.options[optIndex]
                val isSelected = selectedOption == optIndex
                val isCorrect = optIndex == currentQ.correctOptionIndex

                val optionBg = when {
                    !isExamMode && isSubmitted && isCorrect -> Color(0xFFD1FAE5) // Green
                    !isExamMode && isSubmitted && isSelected && !isCorrect -> Color(0xFFFEE2E2) // Red
                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    else -> MaterialTheme.colorScheme.surface
                }

                val optionBorder = when {
                    !isExamMode && isSubmitted && isCorrect -> Color(0xFF059669)
                    !isExamMode && isSubmitted && isSelected && !isCorrect -> Color(0xFFDC2626)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, optionBorder, RoundedCornerShape(14.dp))
                        .clickable(enabled = if (isExamMode) true else !isSubmitted) {
                            viewModel.selectQuizOption(optIndex)
                        }
                        .testTag("mcq_option_$optIndex"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = optionBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = listOf("ক", "খ", "গ", "ঘ").getOrElse(optIndex) { "${optIndex + 1}" },
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )

                        if (!isExamMode && isSubmitted) {
                            if (isCorrect) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                            } else if (isSelected) {
                                Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }

            // Explanation Card (Only shown in Practice Mode when answer submitted)
            if (!isExamMode && isSubmitted) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mcq_explanation_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "সঠিক উত্তরের ব্যাখ্যা:",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQ.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Controls
            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (isExamMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.prevQuizQuestion() },
                            enabled = currentIndex > 0,
                            modifier = Modifier.testTag("prev_exam_question_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("পূর্ববর্তী")
                        }

                        Button(
                            onClick = { viewModel.nextQuizQuestion() },
                            enabled = selectedOption != null,
                            modifier = Modifier.testTag("next_exam_question_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (currentIndex < total - 1) "পরবর্তী প্রশ্ন →" else "পরীক্ষা জমা দিন 🏁",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    if (!isSubmitted) {
                        Button(
                            onClick = { viewModel.submitQuizAnswer() },
                            enabled = selectedOption != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_mcq_answer_btn"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(if (isBangla) "উত্তর জমা দিন" else "Submit Answer", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.nextQuizQuestion() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("next_mcq_question_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(
                                text = if (currentIndex < total - 1)
                                    (if (isBangla) "পরবর্তী প্রশ্ন →" else "Next Question →")
                                else
                                    (if (isBangla) "ফলাফল দেখুন 🏁" else "View Results 🏁"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
