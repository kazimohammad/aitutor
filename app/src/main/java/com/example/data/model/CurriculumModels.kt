package com.example.data.model

data class ClassLevel(
    val level: Int,
    val banglaName: String,
    val englishName: String,
    val description: String
)

enum class CurriculumVersion(val banglaLabel: String, val englishLabel: String) {
    BANGLA_VERSION("বাংলা মাধ্যম", "Bangla Version"),
    ENGLISH_VERSION("ইংরেজি ভার্সন", "English Version")
}

data class SubjectItem(
    val id: String,
    val banglaName: String,
    val englishName: String,
    val iconName: String,
    val classLevel: Int,
    val chaptersCount: Int
)

data class ChapterItem(
    val id: String,
    val subjectId: String,
    val chapterNumber: Int,
    val banglaTitle: String,
    val englishTitle: String,
    val topics: List<String>
)

enum class LearningMode(val banglaTitle: String, val englishTitle: String, val icon: String) {
    READ("পড়ুন ও বুঝুন", "Read & Understand", "book"),
    LISTEN("শুনে শিখুন", "Listen (Audio)", "headphones"),
    SPEAK("মুখস্থ বলুন", "Speak & Memorize", "mic"),
    PRACTICE("অনুশীলন ও কুইজ", "Practice & Quiz", "quiz")
}

data class UserProfile(
    val selectedClass: Int = 8,
    val curriculum: String = "NCTB Bangladesh",
    val version: String = "Bangla",
    val appLanguage: String = "bn", // "bn" or "en"
    val preferredMode: LearningMode = LearningMode.READ,
    val studyStreakDays: Int = 3,
    val xpPoints: Int = 180
)
