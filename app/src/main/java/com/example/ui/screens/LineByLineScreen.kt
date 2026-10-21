package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineByLineScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    val session by viewModel.memorizeSession.collectAsState()
    val evaluation by viewModel.memorizeEvaluation.collectAsState()
    val isEvaluating by viewModel.isEvaluatingRecall.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var spokenOrTypedText by remember { mutableStateOf("") }
    var isTypingMode by remember { mutableStateOf(false) }
    var hideTargetLine by remember { mutableStateOf(false) }
    var recallSetting by remember { mutableStateOf("Key-point") } // Exact, Key-point, Own-words

    val isBangla = profile.appLanguage == "bn"

    // Speech-to-Text Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenResults = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = spokenResults?.firstOrNull() ?: ""
            if (spoken.isNotBlank()) {
                spokenOrTypedText = spoken
                viewModel.evaluateSpokenRecall(spoken)
            }
        }
    }

    var audioPermissionDenied by remember { mutableStateOf(false) }

    fun launchSpeechIntent() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "লাইনটি স্পষ্ট করে বলুন...")
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (_: Exception) {
            isTypingMode = true
        }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            audioPermissionDenied = false
            launchSpeechIntent()
        } else {
            audioPermissionDenied = true
            isTypingMode = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "এক লাইন করে মুখস্থ শিখি" else "Line-by-Line Coach",
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
                        onClick = { hideTargetLine = !hideTargetLine },
                        modifier = Modifier.testTag("toggle_hide_line_btn")
                    ) {
                        Icon(
                            if (hideTargetLine) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Hide/Show"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentSession = session
        if (currentSession == null || currentSession.lines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val currentIndex = currentSession.currentIndex
        val currentLine = currentSession.lines[currentIndex]
        val totalLines = currentSession.lines.size

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 60.dp)
        ) {
            // Topic title & Progress indicator
            item {
                Column {
                    Text(
                        text = currentSession.topicTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "লাইন ${currentIndex + 1} / $totalLines" else "Line ${currentIndex + 1} of $totalLines",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isBangla) "${currentSession.completedLines.size} টি মুখস্থ সম্পন্ন" else "${currentSession.completedLines.size} memorized",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / totalLines.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Recall setting selector chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "যাচাইয়ের ধরন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    listOf("Key-point" to "মূল পয়েন্ট", "Exact" to "হুবহু শব্দ", "Own-words" to "নিজের ভাষা").forEach { (key, label) ->
                        val isSelected = recallSetting == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { recallSetting = key },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Target Line Card (with Read Aloud & Bengali Meaning)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("target_line_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (hideTargetLine) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBangla) "লাইনটি লুকানো আছে। মনে করে বলুন!" else "Line is hidden. Recall and speak!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = currentLine.targetLine,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bangla Meaning
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "💡 সহজ অর্থ: ${currentLine.banglaMeaning}",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Listen to Line Button
                        FilledTonalButton(
                            onClick = {
                                viewModel.ttsManager.speak(currentLine.targetLine)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("tts_listen_line_btn")
                        ) {
                            Icon(
                                if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSpeaking) "থামুন" else "পড়ে শোনান")
                        }
                    }
                }
            }

            // Student Recall Input: "আমি বলব" or "আমি লিখব"
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBangla) "এবার না দেখে উত্তর দেওয়ার পালা:" else "Recall without looking:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Big Voice Button
                            Button(
                                onClick = {
                                    recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(54.dp)
                                    .testTag("speak_recall_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBangla) "আমি বলব (মাইক্রোফোন)" else "I will speak (Mic)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(
                            onClick = { isTypingMode = !isTypingMode },
                            modifier = Modifier.testTag("toggle_typing_mode_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isTypingMode) "টাইপিং বন্ধ করুন" else "কীবোর্ডে লিখে পরীক্ষা দিন")
                        }

                        if (isTypingMode) {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = spokenOrTypedText,
                                onValueChange = { spokenOrTypedText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("typed_recall_input"),
                                placeholder = { Text("লাইনটি মুখস্থ লিখুন...") },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (spokenOrTypedText.isNotBlank()) {
                                        viewModel.evaluateSpokenRecall(spokenOrTypedText)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_typed_recall_btn")
                            ) {
                                Text("উত্তর যাচাই করুন")
                            }
                        }
                    }
                }
            }

            // Semantic Evaluation Feedback Card
            evaluation?.let { eval ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("evaluation_feedback_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (eval.isPassed)
                                Color(0xFFD1FAE5) // Soft green
                            else
                                Color(0xFFFEF3C7) // Soft amber
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (eval.isPassed) "✅ দুর্দান্ত মুখস্থ হয়েছে!" else "💡 আর একটু চেষ্টা করো!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (eval.isPassed) Color(0xFF065F46) else Color(0xFF92400E)
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White
                                ) {
                                    Text(
                                        text = "${eval.accuracyScore}% নির্ভুল",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = eval.feedbackMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 20.sp
                            )

                            // Matched & Missed Keywords
                            if (eval.matchedKeywords.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "সঠিক মূল শব্দ: ${eval.matchedKeywords.joinToString(", ")}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF047857),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (eval.missedKeywords.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "অনুপস্থিত শব্দ: ${eval.missedKeywords.joinToString(", ")}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Navigation Controls: Previous / Next Line
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            spokenOrTypedText = ""
                            viewModel.prevMemorizeLine()
                        },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("prev_line_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("পূর্ববর্তী লাইন")
                    }

                    Button(
                        onClick = {
                            spokenOrTypedText = ""
                            viewModel.nextMemorizeLine()
                        },
                        enabled = currentIndex < totalLines - 1,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("next_line_btn")
                    ) {
                        Text("পরবর্তী লাইন")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }

            // Final Completed Answer Summary Card (when on last line or completed)
            if (currentIndex == totalLines - 1 || currentSession.completedLines.size >= totalLines - 1) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complete_answer_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🎉 সম্পূর্ণ উত্তরটি একসাথে রিভিশন:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            currentSession.lines.forEachIndexed { i, l ->
                                Text(
                                    text = "${i + 1}. ${l.targetLine}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    lineHeight = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val completeText = currentSession.lines.joinToString(" ") { it.targetLine }
                                    viewModel.ttsManager.speak(completeText)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("সম্পূর্ণ উত্তরটি একবার শুনুন")
                            }
                        }
                    }
                }
            }
        }
    }
}
