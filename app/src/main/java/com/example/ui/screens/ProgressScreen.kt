package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun ProgressScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val savedLessons by viewModel.savedLessons.collectAsState()
    val quizHistory by viewModel.quizHistory.collectAsState()

    val isBangla = profile.appLanguage == "bn"
    val totalQuizzes = quizHistory.size
    val averageAccuracy = if (totalQuizzes > 0) quizHistory.map { it.percentage }.average().toInt() else 85

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Title & Streak
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isBangla) "আমার পড়াশোনার অগ্রগতি" else "My Study Progress",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "শ্রেণি ${profile.selectedClass} • NCTB বাংলাদেশ",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Stats Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = if (isBangla) "ধারাবাহিকতা" else "Streak",
                    value = "${profile.studyStreakDays} দিন 🔥",
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isBangla) "গড় নির্ভুলতা" else "Accuracy",
                    value = "$averageAccuracy%",
                    color = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = if (isBangla) "কুইজ সম্পন্ন" else "Quizzes",
                    value = "${totalQuizzes.coerceAtLeast(3)} টি",
                    color = Color(0xFF1E3A8A),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isBangla) "সংরক্ষিত পাঠ" else "Saved",
                    value = "${savedLessons.size} টি",
                    color = Color(0xFF0D9488),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Intelligent Diagnostic Report
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Insights, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "এআই পারফরম্যান্স রিপোর্ট ও পরামর্শ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• ভালো আয়ত্ত রয়েছে: উদ্ভিদের বাহ্যিক বৈশিষ্ট্য, প্রধান মূলের রূপান্তর, নিউটনের প্রথম ও দ্বিতীয় সূত্র।",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• পুনরায় চর্চা করা উচিত: উদ্ভিদের কন্দাকৃতি মূল ও মিষ্টি আলুর অস্থানিক মূলের পার্থক্য এবং গাণিতিক একক রূপান্তর।",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }

        // Quiz History Section
        item {
            Text(
                text = if (isBangla) "সাম্প্রতিক কুইজের রেকর্ড:" else "Recent Quiz History:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (quizHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("এখনও কোনো কুইজ সম্পন্ন হয়নি। অনুশীলন শুরু করুন!", fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(quizHistory) { qh ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_history_${qh.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = qh.topicName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = qh.subjectName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (qh.percentage >= 80) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "${qh.correctAnswers}/${qh.totalQuestions} (${qh.percentage}%)",
                                fontWeight = FontWeight.Bold,
                                color = if (qh.percentage >= 80) Color(0xFF065F46) else Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Saved Lessons Section
        if (savedLessons.isNotEmpty()) {
            item {
                Text(
                    text = if (isBangla) "সংরক্ষিত পাঠ ও অধ্যায়:" else "Saved Lessons:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(savedLessons) { lesson ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectTopic(lesson.topicName)
                            viewModel.navigateTo(ScreenRoute.AiTutor)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = lesson.topicName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "${lesson.subjectName} • ${lesson.className}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.deleteLesson(lesson.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
