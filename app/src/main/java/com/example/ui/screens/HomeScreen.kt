package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun HomeScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    val savedLessons by viewModel.savedLessons.collectAsState()
    val revisionItems by viewModel.revisionItems.collectAsState()
    val pdfStatus by viewModel.pdfDownloadStatus.collectAsState()

    val isBangla = profile.appLanguage == "bn"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Welcoming Greeting & Class Badge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("greeting_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) "স্বাগতম, প্রিয় শিক্ষার্থী! 📚" else "Welcome, Student! 📚",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla)
                                "শ্রেণি ${profile.selectedClass} • ${profile.version} মাধ্যম"
                            else
                                "Class ${profile.selectedClass} • ${profile.version} Version",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Streak Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.testTag("streak_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "${profile.studyStreakDays} দিন" else "${profile.studyStreakDays} Days",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Hero Study Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_study_hero),
                        contentDescription = "Porashona Study Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.Black.copy(alpha = 0.35f)
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isBangla) "জাতীয় শিক্ষাক্রম (NCTB)" else "NCTB Bangladesh",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBangla) "সহজে বুঝো, এক লাইনে মুখস্থ করো!" else "Understand clearly, memorize line-by-line!",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Quick Primary Action Buttons
        item {
            Text(
                text = if (isBangla) "তাত্ক্ষণিক শিক্ষাসভা" else "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Scan Book Button
                Button(
                    onClick = { viewModel.navigateTo(ScreenRoute.Scanner) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("scan_book_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "বই স্ক্যান" else "Scan Book",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Ask AI Question Button
                Button(
                    onClick = { viewModel.navigateTo(ScreenRoute.AiTutor) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("ask_question_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "প্রশ্ন করুন" else "Ask Tutor",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Continue Learning Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(ScreenRoute.AiTutor) }
                    .testTag("continue_learning_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = selectedSubject?.banglaName ?: "বিজ্ঞান",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = if (isBangla) "পড়া চালিয়ে যান →" else "Continue →",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = selectedTopic,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { 0.65f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        // Learning Mode Grid (Memorize, Exam Questions, MCQ, Revision)
        item {
            Text(
                text = if (isBangla) "শেখার মাধ্যমসমূহ" else "Learning Modes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard(
                    title = if (isBangla) "এক লাইন করে মুখস্থ" else "Line-by-Line",
                    subtitle = if (isBangla) "উচ্চারণ ও নির্ভুলতা" else "Spoken recall",
                    icon = Icons.Default.Mic,
                    color = Color(0xFF059669),
                    modifier = Modifier.weight(1f),
                    testTag = "mode_memorize_card"
                ) {
                    viewModel.setupMemorizeSession()
                    viewModel.navigateTo(ScreenRoute.LineByLine)
                }

                FeatureCard(
                    title = if (isBangla) "MCQ কুইজ" else "MCQ Quiz",
                    subtitle = if (isBangla) "তাৎক্ষণিক ব্যাখ্যা" else "Instant test",
                    icon = Icons.Default.Quiz,
                    color = Color(0xFF1E3A8A),
                    modifier = Modifier.weight(1f),
                    testTag = "mode_mcq_card"
                ) {
                    viewModel.loadMCQs()
                    viewModel.navigateTo(ScreenRoute.McqQuiz)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard(
                    title = if (isBangla) "পরীক্ষার খাতা কোচ" else "Exam Coach",
                    subtitle = if (isBangla) "খাতায় কীভাবে লিখব?" else "Presentation guide",
                    icon = Icons.Default.EditNote,
                    color = Color(0xFF0D9488),
                    modifier = Modifier.weight(1f),
                    testTag = "mode_exam_card"
                ) {
                    viewModel.loadExamQuestions()
                    viewModel.navigateTo(ScreenRoute.ExamQuestions)
                }

                FeatureCard(
                    title = if (isBangla) "রিভিশন রিমাইন্ডার" else "Revision Plan",
                    subtitle = if (isBangla) "স্মৃতি ধরে রাখার ছক" else "Spaced memory",
                    icon = Icons.Default.Schedule,
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f),
                    testTag = "mode_revision_card"
                ) {
                    viewModel.navigateTo(ScreenRoute.RevisionPlanner)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard(
                    title = if (isBangla) "বোর্ড প্রশ্ন ও সমাধান" else "Board Questions",
                    subtitle = if (isBangla) "৯টি শিক্ষা বোর্ডের বিগত প্রশ্ন" else "Verified past papers",
                    icon = Icons.Default.LibraryBooks,
                    color = Color(0xFF4338CA),
                    modifier = Modifier.weight(1f),
                    testTag = "mode_board_questions_card"
                ) {
                    viewModel.navigateTo(ScreenRoute.BoardQuestions)
                }

                FeatureCard(
                    title = if (isBangla) "পাঠ্যবই পিডিএফ ডাউনলোড" else "Download PDF",
                    subtitle = if (isBangla) "অফলাইন এনসিটিবি বই" else "Offline NCTB books",
                    icon = Icons.Default.PictureAsPdf,
                    color = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f),
                    testTag = "mode_pdf_download_card"
                ) {
                    viewModel.downloadChapterPdf(selectedTopic)
                }
            }

            if (pdfStatus != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = pdfStatus ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }


        // Browse Subjects Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "পাঠ্যবই ও বিষয়সমূহ" else "Subjects & Chapters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.SubjectChapter) },
                    modifier = Modifier.testTag("view_all_subjects_button")
                ) {
                    Text(if (isBangla) "সব দেখুন" else "View All")
                }
            }
        }

        // Saved Lessons or Quick Tips
        if (savedLessons.isNotEmpty()) {
            item {
                Text(
                    text = if (isBangla) "সংরক্ষিত পাঠসমূহ" else "Saved Lessons",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(savedLessons.take(3)) { lesson ->
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
                            Text(
                                text = lesson.topicName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${lesson.subjectName} • ${lesson.className}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.deleteLesson(lesson.id) }) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
