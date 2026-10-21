package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.model.LessonExplanation
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiTutorScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    val explanationState by viewModel.explanationState.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val currentLength by viewModel.explanationLength.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val currentTtsSpeed by viewModel.ttsManager.currentSpeed.collectAsState()
    val chatHistory by viewModel.tutorChatHistory.collectAsState()
    val isAnsweringChat by viewModel.isAnsweringChat.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var chatInput by remember { mutableStateOf("") }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    val isBangla = profile.appLanguage == "bn"
    val tabs = listOf(
        if (isBangla) "সহজ ব্যাখ্যা" else "Simple",
        if (isBangla) "বিস্তারিত পাঠ" else "Detailed",
        if (isBangla) "সংজ্ঞা ও সূত্র" else "Formulas",
        if (isBangla) "পরীক্ষার ভুলত্রুটি" else "Mistakes",
        if (isBangla) "খাতায় কীভাবে লিখব?" else "Exam Coach"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBangla) "এআই স্টাডি টিউটর" else "AI Study Tutor",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "শ্রেণি ${profile.selectedClass} • ${viewModel.selectedSubject.value?.banglaName ?: "বিজ্ঞান"}",
                            fontSize = 12.sp,
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
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentLesson()
                            showSavedSnackbar = true
                        },
                        modifier = Modifier.testTag("save_lesson_btn")
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = "Bookmark", tint = MaterialTheme.colorScheme.primary)
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
            when (val state = explanationState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isBangla) "পাঠ প্রস্তুত করা হচ্ছে..." else "Preparing lesson...",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    val lesson = state.data

                    // Topic Header & Audio Bar
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lesson_header_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = lesson.topicName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🎯 শিখনফল: ${lesson.learningObjective}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Audio TTS Toolbar
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    if (isSpeaking) {
                                                        viewModel.ttsManager.stop()
                                                    } else {
                                                        val fullSpeech = "${lesson.topicName}। ${lesson.simpleExplanation}"
                                                        viewModel.ttsManager.speak(fullSpeech)
                                                    }
                                                },
                                                modifier = Modifier.testTag("tts_play_btn")
                                            ) {
                                                Icon(
                                                    if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                                    contentDescription = "Speak",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Text(
                                                text = if (isSpeaking)
                                                    (if (isBangla) "পড়ছে..." else "Speaking...")
                                                else
                                                    (if (isBangla) "পড়ে শোনান" else "Listen"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Speed Selector
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            listOf(0.75f, 1.0f, 1.25f).forEach { speed ->
                                                val isSelectedSpeed = currentTtsSpeed == speed
                                                FilterChip(
                                                    selected = isSelectedSpeed,
                                                    onClick = { viewModel.ttsManager.setSpeed(speed) },
                                                    label = { Text("${speed}x", fontSize = 11.sp) },
                                                    modifier = Modifier.height(32.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Explanation Length Switcher
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isBangla) "দৈর্ঘ্য:" else "Length:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            listOf("1-Min", "Short", "Detailed", "Exam").forEach { len ->
                                val label = when (len) {
                                    "1-Min" -> if (isBangla) "১-মিনিট" else "1-Min"
                                    "Short" -> if (isBangla) "সংক্ষিপ্ত" else "Short"
                                    "Detailed" -> if (isBangla) "বিস্তারিত" else "Detailed"
                                    else -> if (isBangla) "পরীক্ষার" else "Exam"
                                }
                                val isSelected = currentLength == len
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setExplanationLength(len) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    modifier = Modifier.testTag("length_chip_$len")
                                )
                            }
                        }
                    }

                    // Content Tabs
                    item {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            edgePadding = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                    modifier = Modifier.testTag("tutor_tab_$index")
                                )
                            }
                        }
                    }

                    // Tab Contents
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tutor_tab_content_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                when (selectedTab) {
                                    0 -> { // সহজ ব্যাখ্যা
                                        Text(
                                            text = "সহজ ও প্রাঞ্জল ভাষায় ব্যাখ্যা:",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = lesson.simpleExplanation,
                                            style = MaterialTheme.typography.bodyLarge,
                                            lineHeight = 24.sp
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Icon(
                                                    Icons.Default.Lightbulb,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = "বাস্তব জীবনের উদাহরণ:",
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        fontSize = 13.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = lesson.everydayExample,
                                                        fontSize = 13.sp,
                                                        lineHeight = 18.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    1 -> { // বিস্তারিত পাঠ
                                        Text(
                                            text = "শ্রেণি-উপযোগী বিস্তারিত পাঠ:",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = lesson.detailedExplanation,
                                            style = MaterialTheme.typography.bodyLarge,
                                            lineHeight = 24.sp
                                        )
                                    }
                                    2 -> { // সংজ্ঞা ও সূত্র
                                        Text(
                                            text = "গুরুত্বপূর্ণ সংজ্ঞাসমূহ:",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        lesson.importantDefinitions.forEach { def ->
                                            Text(
                                                text = "• $def",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(vertical = 4.dp),
                                                lineHeight = 20.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "সূত্র ও প্রধান তথ্য:",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        lesson.keyFactsAndFormulas.forEach { fact ->
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = fact,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 13.sp,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                    3 -> { // সাধারণ ভুলত্রুটি
                                        Text(
                                            text = "শিক্ষার্থীরা পরীক্ষায় যে ভুলগুলো করে:",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        lesson.commonMistakes.forEach { mistake ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text("⚠️ ", fontSize = 14.sp)
                                                Text(
                                                    text = mistake,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    lineHeight = 20.sp
                                                )
                                            }
                                        }
                                    }
                                    4 -> { // পরীক্ষার খাতায় কীভাবে লিখব? (Presentation Coach)
                                        val guide = lesson.presentationGuide
                                        Text(
                                            text = "পরীক্ষার খাতায় উপস্থাপনা কৌশল:",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        PresentationTipItem(
                                            icon = Icons.Default.PlayArrow,
                                            title = "উত্তর শুরু করবেন যেভাবে:",
                                            desc = guide.howToStart
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        PresentationTipItem(
                                            icon = Icons.Default.FormatListNumbered,
                                            title = "প্যারাগ্রাফ ও কাঠামো:",
                                            desc = guide.paragraphStructure
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        PresentationTipItem(
                                            icon = Icons.Default.Architecture,
                                            title = "চিত্র ও সূত্র উপস্থাপনা:",
                                            desc = guide.formulaOrDiagramTip
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        PresentationTipItem(
                                            icon = Icons.Default.DoneAll,
                                            title = "উপসংহারের লাইন:",
                                            desc = guide.concludingSentence
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3 Quick Comprehension Questions
                    item {
                        Text(
                            text = if (isBangla) "৩টি দ্রুত যাচাই প্রশ্ন ও উত্তর" else "3 Quick Comprehension Checks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        lesson.quickQuestions.forEachIndexed { i, qq ->
                            var showAnswer by remember { mutableStateOf(false) }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { showAnswer = !showAnswer },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "প্রশ্ন ${i + 1}: ${qq.question}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            if (showAnswer) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null
                                        )
                                    }
                                    if (showAnswer) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "উত্তর: ${qq.answer}",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Follow-up Chat with AI Tutor
                    item {
                        Text(
                            text = if (isBangla) "এআই শিক্ষককে যে কোনো প্রশ্ন করুন:" else "Ask Follow-up to AI Tutor:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Chat bubbles
                        chatHistory.forEach { (q, a) ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Student question bubble
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = q,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        fontSize = 13.sp
                                    )
                                }
                                // Tutor answer bubble
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "🤖 শিক্ষক: $a",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Chat input box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tutor_chat_input"),
                                placeholder = { Text(if (isBangla) "যেমন: 'একটি সহজ উদাহরণ দাও'..." else "Ask anything...") },
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        val q = chatInput.trim()
                                        chatInput = ""
                                        viewModel.askTutorFollowUp(q)
                                    }
                                },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                    .testTag("send_tutor_chat_btn")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                            }
                        }
                    }

                    // Bottom Navigation to Other Learning Modes
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.setupMemorizeSession()
                                    viewModel.navigateTo(ScreenRoute.LineByLine)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("start_memorize_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBangla) "মুখস্থ শুরু করুন" else "Memorize")
                            }

                            Button(
                                onClick = {
                                    viewModel.loadMCQs()
                                    viewModel.navigateTo(ScreenRoute.McqQuiz)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("start_mcq_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBangla) "MCQ পরীক্ষা" else "MCQ Quiz")
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = state.message, color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.loadTopicExplanation() }) {
                                    Text("পুনরায় চেষ্টা করুন")
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun PresentationTipItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
