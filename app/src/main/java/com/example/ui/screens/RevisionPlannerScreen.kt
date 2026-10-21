package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionPlannerScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    val revisionItems by viewModel.revisionItems.collectAsState()
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf("Needs Practice") }

    val isBangla = profile.appLanguage == "bn"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "স্মার্ট রিভিশন ও মেমোরি প্ল্যানার" else "Smart Revision Planner",
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
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_revision_btn")
                    ) {
                        Icon(Icons.Default.AddAlarm, contentDescription = "Schedule", tint = MaterialTheme.colorScheme.primary)
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 60.dp)
        ) {
            // Spaced Repetition Science Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "বৈজ্ঞানিক ব্যবধানযুক্ত পুনরাবৃত্তি (Spaced Repetition)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "মানুষ সাধারণত পড়ার ২৪ ঘণ্টার মধ্যে ৭০% তথ্য ভুলে যায়। তাই একই দিনে, ৩ দিন পর এবং ৭ দিন পর অল্প সময়ের রিভিশন দিলে স্মৃতি আজীবন স্থায়ী হয়।",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick Add Current Topic to Revision
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "বর্তমান পাঠ: $selectedTopic",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "পড়াটি কতটুকু আয়ত্ত করতে পেরেছেন?",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.addRevisionItem("Easy") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                            ) {
                                Text("সহজ (৭ দিন)", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.addRevisionItem("Needs Practice") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                            ) {
                                Text("চর্চা দরকার (৩ দিন)", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.addRevisionItem("Difficult") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                            ) {
                                Text("কঠিন (কালকে)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Memory Techniques & Mnemonics
            item {
                Text(
                    text = "স্মৃতি ধরে রাখার বিশেষ কৌশলসমূহ:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                MemoryTechniqueCard(
                    title = "১. অ্যাক্রোনিম কৌশল (Acronyms)",
                    desc = "যেকোনো তালিকার প্রতিটি শব্দের প্রথম বর্ণ দিয়ে মজার নতুন শব্দ বানিয়ে মনে রাখুন। যেমন রংধনুর সাত রঙ: 'বেনীআসহকলা' (বেগুনী, নীল, আসমানী, সবুজ, হলুদ, কমলা, লাল)।"
                )
            }

            item {
                MemoryTechniqueCard(
                    title = "২. চাঙ্কিং পদ্ধতি (Chunking)",
                    desc = "দীর্ঘ উত্তর বা সমীকরণকে ছোট ছোট ৩-৪টি অংশে ভাগ করুন। প্রথমে প্রথম অংশটি মনে রাখুন, তারপর দ্বিতীয়টি যোগ করুন।"
                )
            }

            item {
                MemoryTechniqueCard(
                    title = "৩. কার্যকারণ শৃঙ্খল (Cause & Effect Chain)",
                    desc = "উদ্ভিদ কেন খাদ্য সঞ্চয় করে? -> প্রতিকূল ঋতুতে বাঁচার জন্য -> কীভাবে করে? -> মূলকে স্ফীত করে -> ফলে কী উৎপন্ন হয়? -> মূলা ও গাজর।"
                )
            }

            // Scheduled Revision Tasks from Room DB
            item {
                Text(
                    text = "আসন্ন রিভিশন তালিকা (${revisionItems.size} টি বিষয়):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (revisionItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("বর্তমানে কোনো রিভিশন বাকি নেই। চালিয়ে যান!", fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(revisionItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("revision_item_${item.id}"),
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
                                Text(
                                    text = item.topicName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${item.subjectName} • পুনর্মূল্যায়ন: ${item.nextReviewDate}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.completeRevision(item.id) }) {
                                Icon(Icons.Default.CheckCircleOutline, contentDescription = "Done", tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryTechniqueCard(title: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
