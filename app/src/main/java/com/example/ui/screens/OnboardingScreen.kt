package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.curriculum.NctbCurriculumData
import com.example.data.model.LearningMode
import com.example.ui.viewmodel.PorashonaViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun OnboardingScreen(
    viewModel: PorashonaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedClass by remember { mutableIntStateOf(7) }
    var selectedVersion by remember { mutableStateOf("Bangla") }
    var selectedMode by remember { mutableStateOf(LearningMode.READ) }
    var selectedLanguage by remember { mutableStateOf("bn") }

    val isBangla = selectedLanguage == "bn"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
    ) {
        // App Header & Logo
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_porashona_logo),
                            contentDescription = "Porashona Logo",
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "পড়াশোনা এআই (Porashona AI)",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "স্মার্ট স্টাডি টিউটর • NCTB বাংলাদেশ (শ্রেণি ৫ - ১০)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Select Class Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "১. আপনার শ্রেণি নির্বাচন করুন:" else "1. Select your Class:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 6, 7).forEach { lvl ->
                            val isSelected = selectedClass == lvl
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedClass = lvl },
                                label = { Text("শ্রেণি $lvl", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_class_$lvl")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(8, 9, 10).forEach { lvl ->
                            val isSelected = selectedClass == lvl
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedClass = lvl },
                                label = { Text("শ্রেণি $lvl", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_class_$lvl")
                            )
                        }
                    }
                }
            }
        }

        // Select Curriculum Version
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "২. মাধ্যম নির্বাচন করুন:" else "2. Select Version:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = selectedVersion == "Bangla",
                            onClick = { selectedVersion = "Bangla" },
                            label = { Text("বাংলা মাধ্যম", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedVersion == "English",
                            onClick = { selectedVersion = "English" },
                            label = { Text("English Version", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Preferred Learning Mode
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "৩. পছন্দের পড়ার ধরন:" else "3. Preferred Learning Mode:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LearningMode.values().forEach { mode ->
                        val isSelected = selectedMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else Color.Transparent
                                )
                                .padding(vertical = 8.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSelected, onClick = { selectedMode = mode })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBangla) mode.banglaTitle else mode.englishTitle,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Start Learning Button
        item {
            Button(
                onClick = {
                    viewModel.updateUserProfile(
                        classLevel = selectedClass,
                        version = selectedVersion,
                        mode = selectedMode,
                        language = selectedLanguage
                    )
                    viewModel.navigateTo(ScreenRoute.Home)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_learning_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (isBangla) "পড়াশোনা শুরু করুন 🚀" else "Start Learning 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
