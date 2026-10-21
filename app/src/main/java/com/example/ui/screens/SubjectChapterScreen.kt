package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.NctbCurriculumData
import com.example.data.model.ChapterItem
import com.example.data.model.SubjectItem
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectChapterScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    val profile by viewModel.userProfile.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var customTopicInput by remember { mutableStateOf("") }
    var showCustomTopicDialog by remember { mutableStateOf(false) }

    val isBangla = profile.appLanguage == "bn"
    val subjects = NctbCurriculumData.getSubjectsForClass(profile.selectedClass)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "পাঠ্যবই ও অধ্যায়সমূহ" else "Subjects & Chapters",
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
                    IconButton(
                        onClick = { showCustomTopicDialog = true },
                        modifier = Modifier.testTag("add_custom_topic_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add custom topic")
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
        ) {
            // Class Selector Chips (5 to 10)
            item {
                Text(
                    text = if (isBangla) "শ্রেণি নির্বাচন করুন:" else "Select Class:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(NctbCurriculumData.classLevels) { classLevel ->
                        val isSelected = classLevel.level == profile.selectedClass
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.updateUserProfile(
                                    classLevel = classLevel.level,
                                    version = profile.version,
                                    mode = profile.preferredMode,
                                    language = profile.appLanguage
                                )
                            },
                            label = {
                                Text(
                                    text = if (isBangla) classLevel.banglaName else classLevel.englishName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("class_chip_${classLevel.level}")
                        )
                    }
                }
            }

            // Subject Selector Horizontal Chips
            item {
                Text(
                    text = if (isBangla) "বিষয় নির্বাচন করুন:" else "Select Subject:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjects) { sub ->
                        val isSubSelected = sub.id == selectedSubject?.id
                        ElevatedFilterChip(
                            selected = isSubSelected,
                            onClick = { viewModel.selectSubject(sub) },
                            label = {
                                Text(
                                    text = if (isBangla) sub.banglaName else sub.englishName,
                                    fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("subject_chip_${sub.id}")
                        )
                    }
                }
            }

            // Search Bar for Chapters & Topics
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_chapter_input"),
                    placeholder = {
                        Text(if (isBangla) "অধ্যায় বা বিষয়বস্তু খুঁজুন..." else "Search chapters or topics...")
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Chapters List
            val currentSub = selectedSubject ?: subjects.firstOrNull()
            val chapters = if (currentSub != null) {
                NctbCurriculumData.getChaptersForSubject(currentSub.id).filter {
                    it.banglaTitle.contains(searchQuery, ignoreCase = true) ||
                            it.englishTitle.contains(searchQuery, ignoreCase = true) ||
                            it.topics.any { t -> t.contains(searchQuery, ignoreCase = true) }
                }
            } else emptyList()

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "${currentSub?.banglaName ?: ""} এর অধ্যায়সমূহ" else "Chapters of ${currentSub?.englishName ?: ""}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${chapters.size} টি অধ্যায়",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(chapters) { chapter ->
                ChapterCard(
                    chapter = chapter,
                    isSelected = chapter.id == selectedChapter?.id,
                    isBangla = isBangla,
                    onSelectTopic = { topic ->
                        viewModel.selectChapter(chapter)
                        viewModel.selectTopic(topic)
                        viewModel.navigateTo(ScreenRoute.AiTutor)
                    }
                )
            }

            // Custom topic banner if not found in official list
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCustomTopicDialog = true }
                        .testTag("custom_topic_prompt_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBangla) "আপনার কাঙ্ক্ষিত অধ্যায় বা বিষয় খুঁজে পাননি?" else "Can't find your specific topic?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBangla) "এখানে চাপ দিয়ে নিজে লিখুন অথবা বই স্ক্যান করুন" else "Tap here to type any topic or scan book",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCustomTopicDialog) {
        AlertDialog(
            onDismissRequest = { showCustomTopicDialog = false },
            title = {
                Text(if (isBangla) "পড়ার বিষয় লিখুন" else "Enter Study Topic")
            },
            text = {
                Column {
                    Text(
                        text = if (isBangla)
                            "যেকোনো পাঠ্যবই বা অধ্যায়ের নাম লিখুন। এআই শিক্ষক তা বিশ্লেষণ করবে।"
                        else
                            "Type any textbook topic or chapter name to study.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customTopicInput,
                        onValueChange = { customTopicInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_topic_field"),
                        placeholder = { Text(if (isBangla) "যেমন: সালোকসংশ্লেষণ বা প্যাটার্ন" else "e.g. Photosynthesis") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTopicInput.isNotBlank()) {
                            viewModel.selectTopic(customTopicInput.trim())
                            showCustomTopicDialog = false
                            viewModel.navigateTo(ScreenRoute.AiTutor)
                        }
                    },
                    modifier = Modifier.testTag("submit_custom_topic_btn")
                ) {
                    Text(if (isBangla) "শিখতে শুরু করুন" else "Start Learning")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomTopicDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun ChapterCard(
    chapter: ChapterItem,
    isSelected: Boolean,
    isBangla: Boolean,
    onSelectTopic: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chapter_card_${chapter.chapterNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "${chapter.chapterNumber}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isBangla) chapter.banglaTitle else chapter.englishTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Topics list inside this chapter
            Text(
                text = if (isBangla) "গুরুত্বপূর্ণ বিষয়বস্তু:" else "Key Topics:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))

            chapter.topics.forEach { topic ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectTopic(topic) }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isBangla) "পড়ুন →" else "Study →",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
