package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.LearningMode
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    val profile by viewModel.userProfile.collectAsState()
    val isBangla = profile.appLanguage == "bn"

    var showApiKeyInfoDialog by remember { mutableStateOf(false) }
    var showClassDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "সেটিংস ও প্রোফাইল" else "Settings & Profile",
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 60.dp)
        ) {
            // Language & Class Preference Section
            item {
                Text(
                    text = if (isBangla) "শিক্ষার্থী ও ভাষা পছন্দ" else "Student & Language",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Language Toggle Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleAppLanguage() }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isBangla) "অ্যাপের ভাষা (Language)" else "App Language",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (isBangla) "বর্তমান: বাংলা" else "Current: English",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = profile.appLanguage == "en",
                                onCheckedChange = { viewModel.toggleAppLanguage() }
                            )
                        }

                        HorizontalDivider()

                        // Change Class Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClassDialog = true }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isBangla) "বর্তমান শ্রেণি" else "Current Class",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "শ্রেণি ${profile.selectedClass} (${profile.version})",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }

            // AI Studio & Gemini Configuration
            item {
                Text(
                    text = if (isBangla) "এআই ও শিক্ষাক্রম উৎস" else "AI & Curriculum",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showApiKeyInfoDialog = true }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Gemini AI সংযোগ ও কনফিগারেশন", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "AI Studio Secrets / gemini-3.8-flash",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "জাতীয় শিক্ষাক্রম (NCTB) মানদণ্ড", fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "শ্রেণি ৫ থেকে ১০ এর যাচাইকৃত পাঠ্যবই কাঠামো",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Child Safety & Privacy Policy
            item {
                Text(
                    text = if (isBangla) "শিশু সুরক্ষা ও গোপনীয়তা" else "Child Safety & Privacy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPrivacyDialog = true }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "শিশুবান্ধব সুরক্ষা নীতিমালা", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "কোনো ব্যক্তিগত বা সংবেদনশীল তথ্য সংরক্ষণ করা হয় না",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
        }
    }

    // Class Selection Dialog
    if (showClassDialog) {
        AlertDialog(
            onDismissRequest = { showClassDialog = false },
            title = { Text("শ্রেণি পরিবর্তন করুন") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    NctbCurriculumData.classLevels.forEach { lvl ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.updateUserProfile(
                                        classLevel = lvl.level,
                                        version = profile.version,
                                        mode = profile.preferredMode,
                                        language = profile.appLanguage
                                    )
                                    showClassDialog = false
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = profile.selectedClass == lvl.level,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${lvl.banglaName} (${lvl.englishName})")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showClassDialog = false }) { Text("বন্ধ করুন") }
            }
        )
    }

    // API Key Instructions Dialog
    if (showApiKeyInfoDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyInfoDialog = false },
            title = { Text("Gemini AI API নির্দেশনা") },
            text = {
                Text(
                    text = "Porashona AI অ্যাপটি Gemini 3.8 Flash মডেলের মাধ্যমে বইয়ের পাতা বিশ্লেষণ ও ব্যক্তিগত শিক্ষক সেবা প্রদান করে।\n\nAI Studio প্ল্যাটফর্মের Secrets প্যানেল থেকে GEMINI_API_KEY সহজেই যুক্ত করা যায়।\n\nইন্টারনেট বা কি না থাকলেও অ্যাপটিতে সম্পূর্ণ অফলাইন NCTB শিক্ষাক্রমের বিস্তারিত তথ্য সংরক্ষিত আছে, ফলে শিক্ষার্থীরা সব সময় নিশ্চিন্তে পড়তে পারবে।",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(onClick = { showApiKeyInfoDialog = false }) { Text("বুঝেছি") }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("শিশু সুরক্ষা ও নিরাপত্তা") },
            text = {
                Text(
                    text = "১. কোনো ব্যক্তিগত ঠিকানা, ফোন নম্বর বা সংবেদনশীল তথ্য সংগ্রহ করা হয় না।\n২. ক্যামেরা ও মাইক্রোফোন ব্যবহারের অনুমতি কেবল বই স্ক্যান ও উচ্চারণ অনুশীলনের সময় ব্যবহারকারীর অনুমতি সাপেক্ষে কাজ করে।\n৩. পড়ার যাবতীয় অগ্রগতি ও কুইজের রেকর্ড সম্পূর্ণ ডিভাইসের লোকাল মেমরিতে সুরক্ষিত থাকে।",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) { Text("ঠিক আছে") }
            }
        )
    }
}
