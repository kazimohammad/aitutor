package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScanAnalysisResult
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScannerScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    val context = LocalContext.current
    val scanState by viewModel.scanState.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val editableText by viewModel.editableScannedText.collectAsState()

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedSampleKey by remember { mutableStateOf<String?>("sample_science") }
    var isEditingText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (scanState is UiState.Idle) {
            viewModel.scanTextbook(null, "sample_science")
        }
    }

    val isBangla = profile.appLanguage == "bn"

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    selectedBitmap = bmp
                    viewModel.scanTextbook(bmp, null)
                }
            } catch (_: Exception) {}
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        if (bmp != null) {
            selectedBitmap = bmp
            viewModel.scanTextbook(bmp, null)
        }
    }

    var cameraPermissionDenied by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraPermissionDenied = false
            cameraLauncher.launch(null)
        } else {
            cameraPermissionDenied = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "স্মার্ট বই স্ক্যানার" else "Smart Book Scanner",
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
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 60.dp)
        ) {
            // Scanner Instructions Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DocumentScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isBangla)
                                "পাঠ্যবইয়ের যেকোনো পৃষ্ঠা স্ক্যান করুন অথবা নিচের নমুনা পৃষ্ঠা বেছে নিন। এআই সাথে সাথে ব্যাখ্যা ও প্রশ্ন তৈরি করবে!"
                            else
                                "Scan any textbook page or select a sample page below. AI will extract text and generate study materials!",
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Capture / Upload Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("camera_capture_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBangla) "ক্যামেরা চালু" else "Take Photo")
                    }

                    OutlinedButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("gallery_upload_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBangla) "ছবি আপলোড" else "Upload")
                    }
                }
                if (cameraPermissionDenied) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla)
                                    "ক্যামেরা ব্যবহারের অনুমতি দেওয়া হয়নি। অনুগ্রহ করে অনুমতি দিন অথবা ছবি আপলোড বা নিচের নমুনা পৃষ্ঠা বেছে নিন।"
                                else
                                    "Camera permission not granted. Please grant permission, upload photo, or use sample pages.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Built-in authentic NCTB sample pages for instant demonstration
            item {
                Text(
                    text = if (isBangla) "অথবা তাৎক্ষণিক নমুনা বই বেছে নিন:" else "Or try a sample NCTB page:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedSampleKey = "sample_science"
                                viewModel.scanTextbook(null, "sample_science")
                            }
                            .testTag("sample_science_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (selectedSampleKey == "sample_science")
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "সপ্তম শ্রেণি বিজ্ঞান",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "অধ্যায় ৩: মূলের রূপান্তর",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedSampleKey = "sample_physics"
                                viewModel.scanTextbook(null, "sample_physics")
                            }
                            .testTag("sample_physics_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (selectedSampleKey == "sample_physics")
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "নবম-দশম পদার্থবিজ্ঞান",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "অধ্যায় ৩: বল (F = ma)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Image Preview (if camera or upload selected)
            if (selectedBitmap != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = "Scanned Page",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Scanning Status / Analysis Results
            when (val state = scanState) {
                is UiState.Loading -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isBangla)
                                        "পৃষ্ঠাটি এআই দিয়ে বিশ্লেষণ করা হচ্ছে..."
                                    else
                                        "Analyzing page with Gemini AI...",
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBangla) "লেখা, চিত্র ও সমীকরণ আলাদা করা হচ্ছে" else "Extracting text, diagrams & equations",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                is UiState.Success -> {
                    val result = state.data

                    // Recognition Overview Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scan_result_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${result.detectedClass} • ${result.detectedSubject}",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.ttsManager.speak(result.extractedText)
                                        },
                                        modifier = Modifier.testTag("tts_read_scanned_btn")
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Read Aloud", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = result.detectedTopic,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Difficult Words Chips
                                if (result.difficultWords.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isBangla) "কঠিন শব্দের অর্থ:" else "Vocabulary:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    result.difficultWords.forEach { (word, meaning) ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "• $word: ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = meaning,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Extracted Text with Edit & Correction Feature
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isBangla) "বই থেকে পঠিত লেখা (OCR):" else "Extracted Textbook Text:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(
                                        onClick = { isEditingText = !isEditingText },
                                        modifier = Modifier.testTag("toggle_edit_text_btn")
                                    ) {
                                        Text(if (isEditingText) "সম্পন্ন" else "সংশোধন করুন")
                                    }
                                }

                                if (isEditingText) {
                                    OutlinedTextField(
                                        value = editableText,
                                        onValueChange = { viewModel.updateEditableScannedText(it) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 120.dp)
                                            .testTag("editable_ocr_text_field"),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else {
                                    Text(
                                        text = editableText.ifEmpty { result.extractedText },
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    // Action Buttons from Scanned Page
                    item {
                        Text(
                            text = if (isBangla) "এই পৃষ্ঠা নিয়ে কী করতে চান?" else "Next Actions:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.applyScannedContentToStudy(result) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("explain_scanned_page_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) "এই পৃষ্ঠা সম্পূর্ণ ব্যাখ্যা করুন" else "Explain this page in detail",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.selectTopic(result.detectedTopic)
                                    viewModel.setupMemorizeSession()
                                    viewModel.navigateTo(ScreenRoute.LineByLine)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("memorize_from_scan_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBangla) "এক লাইনে মুখস্থ" else "Memorize")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.selectTopic(result.detectedTopic)
                                    viewModel.loadMCQs()
                                    viewModel.navigateTo(ScreenRoute.McqQuiz)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mcq_from_scan_btn"),
                                shape = RoundedCornerShape(12.dp)
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
                                Text(
                                    text = if (isBangla) "স্ক্যানিংয়ে ত্রুটি হয়েছে" else "Scanning Error",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = state.message,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.scanTextbook(null, selectedSampleKey) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(if (isBangla) "আবার চেষ্টা করুন" else "Retry")
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
