package com.example.data.model

data class QuickQuestion(
    val question: String,
    val answer: String
)

data class PresentationGuide(
    val howToStart: String,
    val essentialPoints: List<String>,
    val paragraphStructure: String,
    val formulaOrDiagramTip: String,
    val concludingSentence: String,
    val commonMistakes: List<String>
)

data class LessonExplanation(
    val topicName: String,
    val subjectName: String,
    val classLevel: Int,
    val learningObjective: String,
    val simpleExplanation: String, // সহজ ব্যাখ্যা
    val detailedExplanation: String, // বিস্তারিত ব্যাখ্যা
    val everydayExample: String, // বাস্তব জীবনের সহজ উদাহরণ
    val importantDefinitions: List<String>,
    val keyFactsAndFormulas: List<String>,
    val shortSummary: String,
    val commonMistakes: List<String>,
    val quickQuestions: List<QuickQuestion>,
    val presentationGuide: PresentationGuide
)

enum class QuestionType(val banglaLabel: String, val marks: Int) {
    KNOWLEDGE("জ্ঞানমূলক (ক)", 1),
    COMPREHENSION("অনুধাবনমূলক (খ)", 2),
    APPLICATION("প্রয়োগমূলক (গ)", 3),
    HIGHER_ABILITY("উচ্চতর দক্ষতা (ঘ)", 4),
    SHORT_ANSWER("সংক্ষিপ্ত প্রশ্ন", 2),
    ONE_WORD("এক কথায় উত্তর", 1),
    TRUE_FALSE("সত্য / মিথ্যা", 1)
}

data class ExamQuestion(
    val id: String,
    val questionText: String,
    val questionType: QuestionType,
    val marks: Int,
    val difficulty: String, // Basic, Important, Challenging
    val modelAnswer: String,
    val simpleExplanation: String,
    val keyPoints: List<String>,
    val markingGuide: String
)

data class McqQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val difficulty: String = "Medium"
)

data class MemorizeLine(
    val index: Int,
    val targetLine: String,
    val banglaMeaning: String,
    val keywords: List<String>
)

data class MemorizeSession(
    val topicTitle: String,
    val lines: List<MemorizeLine>,
    val currentIndex: Int = 0,
    val completedLines: Set<Int> = emptySet()
)

data class ScanAnalysisResult(
    val extractedText: String,
    val detectedClass: String,
    val detectedSubject: String,
    val detectedChapter: String,
    val detectedTopic: String,
    val headings: List<String>,
    val difficultWords: Map<String, String>,
    val summary: String,
    val generatedQuestions: List<String>
)

data class MemoryHint(
    val techniqueName: String, // Mnemonics, Chunking, Story, Cause-and-Effect
    val description: String,
    val example: String
)
