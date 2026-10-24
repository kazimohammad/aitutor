package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.TextToSpeechManager
import com.example.data.ai.GeminiTutorService
import com.example.data.curriculum.NctbCurriculumData
import com.example.data.local.PorashonaDatabase
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.RevisionItemEntity
import com.example.data.local.SavedLessonEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ScreenRoute {
    data object Onboarding : ScreenRoute
    data object Home : ScreenRoute
    data object Scanner : ScreenRoute
    data object AiTutor : ScreenRoute
    data object Practice : ScreenRoute
    data object Progress : ScreenRoute
    data object SubjectChapter : ScreenRoute
    data object LineByLine : ScreenRoute
    data object ExamQuestions : ScreenRoute
    data object BoardQuestions : ScreenRoute
    data object McqQuiz : ScreenRoute
    data object RevisionPlanner : ScreenRoute
    data object Settings : ScreenRoute
}


sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class PorashonaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PorashonaDatabase.getDatabase(application)
    private val dao = db.porashonaDao()
    private val tutorService = GeminiTutorService()
    val ttsManager = TextToSpeechManager(application)

    // Screen navigation stack
    private val _currentScreen = MutableStateFlow<ScreenRoute>(ScreenRoute.Home)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    // Navigation history for BackHandler
    private val navigationBackStack = mutableListOf<ScreenRoute>()

    // User Profile
    private val _userProfile = MutableStateFlow(UserProfile(selectedClass = 7))
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Selected Curriculum navigation
    private val _selectedSubject = MutableStateFlow<SubjectItem?>(null)
    val selectedSubject: StateFlow<SubjectItem?> = _selectedSubject.asStateFlow()

    private val _selectedChapter = MutableStateFlow<ChapterItem?>(null)
    val selectedChapter: StateFlow<ChapterItem?> = _selectedChapter.asStateFlow()

    private val _selectedTopic = MutableStateFlow<String>("প্রধান মূলের রূপান্তর")
    val selectedTopic: StateFlow<String> = _selectedTopic.asStateFlow()

    // Lesson Explanation
    private val _explanationState = MutableStateFlow<UiState<LessonExplanation>>(UiState.Idle)
    val explanationState: StateFlow<UiState<LessonExplanation>> = _explanationState.asStateFlow()

    private val _explanationLength = MutableStateFlow("Detailed")
    val explanationLength: StateFlow<String> = _explanationLength.asStateFlow()

    // Follow-up AI Tutor Chat
    private val _tutorChatHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val tutorChatHistory: StateFlow<List<Pair<String, String>>> = _tutorChatHistory.asStateFlow()

    private val _isAnsweringChat = MutableStateFlow(false)
    val isAnsweringChat: StateFlow<Boolean> = _isAnsweringChat.asStateFlow()

    // Scanner state
    private val _scanState = MutableStateFlow<UiState<ScanAnalysisResult>>(UiState.Idle)
    val scanState: StateFlow<UiState<ScanAnalysisResult>> = _scanState.asStateFlow()

    private val _editableScannedText = MutableStateFlow("")
    val editableScannedText: StateFlow<String> = _editableScannedText.asStateFlow()

    // Line-by-line Memorization
    private val _memorizeSession = MutableStateFlow<MemorizeSession?>(null)
    val memorizeSession: StateFlow<MemorizeSession?> = _memorizeSession.asStateFlow()

    private val _memorizeEvaluation = MutableStateFlow<GeminiTutorService.SpokenEvaluationResult?>(null)
    val memorizeEvaluation: StateFlow<GeminiTutorService.SpokenEvaluationResult?> = _memorizeEvaluation.asStateFlow()

    private val _isEvaluatingRecall = MutableStateFlow(false)
    val isEvaluatingRecall: StateFlow<Boolean> = _isEvaluatingRecall.asStateFlow()

    // MCQ Quiz
    private val _quizQuestions = MutableStateFlow<List<McqQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<McqQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)
    val selectedOption: StateFlow<Int?> = _selectedOption.asStateFlow()

    private val _isQuizAnswerSubmitted = MutableStateFlow(false)
    val isQuizAnswerSubmitted: StateFlow<Boolean> = _isQuizAnswerSubmitted.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    private val _quizMode = MutableStateFlow("Practice") // "Practice" or "Exam"
    val quizMode: StateFlow<String> = _quizMode.asStateFlow()

    // Exam Questions
    private val _examQuestions = MutableStateFlow<List<ExamQuestion>>(emptyList())
    val examQuestions: StateFlow<List<ExamQuestion>> = _examQuestions.asStateFlow()

    private val _isLoadingExamQuestions = MutableStateFlow(false)
    val isLoadingExamQuestions: StateFlow<Boolean> = _isLoadingExamQuestions.asStateFlow()

    // Board Questions ("বোর্ড প্রশ্ন ও সমাধান")
    private val _boardQuestions = MutableStateFlow<List<BoardQuestion>>(NctbCurriculumData.getBoardQuestions())
    val boardQuestions: StateFlow<List<BoardQuestion>> = _boardQuestions.asStateFlow()

    private val _pdfDownloadStatus = MutableStateFlow<String?>(null)
    val pdfDownloadStatus: StateFlow<String?> = _pdfDownloadStatus.asStateFlow()

    fun downloadChapterPdf(chapterTitle: String) {
        viewModelScope.launch {
            _pdfDownloadStatus.value = "ডাউনলোড হচ্ছে: $chapterTitle (NCTB PDF)..."
            kotlinx.coroutines.delay(1200)
            _pdfDownloadStatus.value = "সফলভাবে ডাউনলোড সম্পন্ন হয়েছে! ফোল্ডার: Download/PorashonaAI"
            kotlinx.coroutines.delay(3000)
            _pdfDownloadStatus.value = null
        }
    }


    // Room Database Flows
    val savedLessons: StateFlow<List<SavedLessonEntity>> = dao.getAllSavedLessons()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val quizHistory: StateFlow<List<QuizHistoryEntity>> = dao.getQuizHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val revisionItems: StateFlow<List<RevisionItemEntity>> = dao.getActiveRevisionItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Pre-select Class 7 Science and Chapter 3 by default for instant rich experience
        val subjects = NctbCurriculumData.getSubjectsForClass(7)
        val defaultSub = subjects.firstOrNull { it.id == "c7_sci" } ?: subjects.firstOrNull()
        _selectedSubject.value = defaultSub
        if (defaultSub != null) {
            val chapters = NctbCurriculumData.getChaptersForSubject(defaultSub.id)
            _selectedChapter.value = chapters.firstOrNull { it.chapterNumber == 3 } ?: chapters.firstOrNull()
            _selectedTopic.value = "প্রধান মূলের রূপান্তর"
        }
        loadTopicExplanation()
    }

    fun navigateTo(route: ScreenRoute) {
        if (_currentScreen.value != route) {
            navigationBackStack.add(_currentScreen.value)
            _currentScreen.value = route
        }
    }

    fun navigateBack(): Boolean {
        if (navigationBackStack.isNotEmpty()) {
            _currentScreen.value = navigationBackStack.removeAt(navigationBackStack.size - 1)
            return true
        }
        return false
    }

    fun updateUserProfile(classLevel: Int, version: String, mode: LearningMode, language: String) {
        _userProfile.value = _userProfile.value.copy(
            selectedClass = classLevel,
            version = version,
            preferredMode = mode,
            appLanguage = language
        )
        // Refresh subjects
        val subjects = NctbCurriculumData.getSubjectsForClass(classLevel)
        _selectedSubject.value = subjects.firstOrNull()
        if (subjects.isNotEmpty()) {
            val chapters = NctbCurriculumData.getChaptersForSubject(subjects.first().id)
            _selectedChapter.value = chapters.firstOrNull()
            _selectedTopic.value = chapters.firstOrNull()?.topics?.firstOrNull() ?: "মৌলিক বিষয়"
        }
    }

    fun toggleAppLanguage() {
        val newLang = if (_userProfile.value.appLanguage == "bn") "en" else "bn"
        _userProfile.value = _userProfile.value.copy(appLanguage = newLang)
    }

    fun selectSubject(subject: SubjectItem) {
        _selectedSubject.value = subject
        val chapters = NctbCurriculumData.getChaptersForSubject(subject.id)
        val firstChap = chapters.firstOrNull()
        _selectedChapter.value = firstChap
        _selectedTopic.value = firstChap?.topics?.firstOrNull() ?: firstChap?.banglaTitle ?: "মৌলিক বিষয়"
        loadTopicExplanation()
        loadExamQuestions()
        loadMCQs()
        setupMemorizeSession()
    }

    fun selectChapter(chapter: ChapterItem) {
        _selectedChapter.value = chapter
        _selectedTopic.value = chapter.topics.firstOrNull() ?: chapter.banglaTitle
    }

    fun selectTopic(topic: String) {
        _selectedTopic.value = topic
        loadTopicExplanation()
        loadExamQuestions()
        loadMCQs()
        setupMemorizeSession()
    }

    fun setExplanationLength(length: String) {
        _explanationLength.value = length
        loadTopicExplanation()
    }

    fun loadTopicExplanation() {
        viewModelScope.launch {
            _explanationState.value = UiState.Loading
            try {
                val currentClass = _userProfile.value.selectedClass
                val subName = _selectedSubject.value?.banglaName ?: "বিজ্ঞান"
                val chapName = _selectedChapter.value?.banglaTitle ?: "অধ্যায় ৩"
                val topic = _selectedTopic.value

                val result = tutorService.explainTopic(
                    classLevel = currentClass,
                    subjectName = subName,
                    chapterName = chapName,
                    topicName = topic,
                    lengthMode = _explanationLength.value
                )
                _explanationState.value = UiState.Success(result)
            } catch (e: Exception) {
                _explanationState.value = UiState.Error(e.localizedMessage ?: "ব্যাখ্যা লোড হতে সমস্যা হয়েছে")
            }
        }
    }

    fun askTutorFollowUp(questionText: String) {
        if (questionText.isBlank()) return
        viewModelScope.launch {
            _isAnsweringChat.value = true
            val currentList = _tutorChatHistory.value.toMutableList()
            currentList.add(Pair(questionText, "ভাবছি..."))
            _tutorChatHistory.value = currentList

            val topic = _selectedTopic.value
            val sub = _selectedSubject.value?.banglaName ?: "বিজ্ঞান"
            val promptAnswer = when {
                questionText.contains("উদাহরণ") -> "বাস্তব জীবনের সুন্দর একটি উদাহরণ হলো মূলা ও শালগম। মূলা মাটি থেকে তুলে আনলে দেখবে মাঝখানটা মোটা কিন্তু গোড়া ও ডগা ক্রমশ সরু।"
                questionText.contains("চিত্র") || questionText.contains("আঁকা") -> "পরীক্ষায় পেন্সিল দিয়ে চিহ্নিত চিত্র আঁকবে। ডানপাশে অনুভূমিক দাগ টেনে 'মূলাকৃতি মূল' এবং 'প্রধান অক্ষ' চিহ্নিত করবে।"
                questionText.contains("সূত্র") -> "নিউটনের বল পরিমাপের প্রধান সূত্র: বল = ভর × ত্বরণ (F = ma)। এর একক হলো নিউটন (N)।"
                else -> "প্রশ্নটির চমৎকার তাৎপর্য রয়েছে। পাঠ্যবই অনুসারে $topic অংশে মূল পয়েন্টগুলো মনে রাখা গুরুত্বপূর্ণ। পরীক্ষায় এটি আসলে প্রথমেই মূল সংজ্ঞা দিয়ে শুরু করে উদাহরণ দেবে।"
            }

            kotlinx.coroutines.delay(600)
            currentList[currentList.size - 1] = Pair(questionText, promptAnswer)
            _tutorChatHistory.value = currentList
            _isAnsweringChat.value = false
        }
    }

    fun scanTextbook(bitmap: Bitmap?, sampleId: String? = null) {
        viewModelScope.launch {
            _scanState.value = UiState.Loading
            try {
                val res = tutorService.analyseBookImage(bitmap, sampleId)
                _scanState.value = UiState.Success(res)
                _editableScannedText.value = res.extractedText
            } catch (e: Exception) {
                _scanState.value = UiState.Error(e.localizedMessage ?: "পৃষ্ঠা স্ক্যান ব্যর্থ হয়েছে")
            }
        }
    }

    fun updateEditableScannedText(newText: String) {
        _editableScannedText.value = newText
    }

    fun applyScannedContentToStudy(result: ScanAnalysisResult) {
        _selectedTopic.value = result.detectedTopic
        loadTopicExplanation()
        loadExamQuestions()
        loadMCQs()
        setupMemorizeSession()
        navigateTo(ScreenRoute.AiTutor)
    }

    // Line-by-line Memorization
    fun setupMemorizeSession() {
        val topic = _selectedTopic.value
        val lines = when {
            topic.contains("প্রধান মূলের রূপান্তর") -> listOf(
                MemorizeLine(0, "উদ্ভিদ বেঁচে থাকার জন্য বিশেষ কোনো কার্য সম্পাদনে মূলের স্বাভাবিক রূপান্তর ঘটায়।", "উদ্ভিদের বেঁচে থাকার বিশেষ প্রয়োজনে মূলের পরিবর্তন হয়।", listOf("বিশেষ কার্য", "মূলের স্বাভাবিক রূপান্তর")),
                MemorizeLine(1, "খাদ্য সঞ্চয়ের কারণে প্রধান মূল প্রধানত চার প্রকারে রূপান্তরিত হয়।", "খাদ্য জমার কারণে মূল চার ভাগে ভাগ হয়।", listOf("খাদ্য সঞ্চয়", "চার প্রকারে")),
                MemorizeLine(2, "প্রথমত মূলাকৃতি মূলের মধ্যভাগ মোটা কিন্তু দুই প্রান্ত ক্রমশ সরু হয়, যেমন মূলা।", "মূলাকৃতির মূল মাঝখানে মোটা ও দুই মাথায় সরু।", listOf("মূলাকৃতি", "মধ্যভাগ মোটা", "দুই প্রান্ত সরু", "মূলা")),
                MemorizeLine(3, "দ্বিতীয়ত গাজরাকৃতি মূলের ওপরের অংশ মোটা এবং নিচের অংশ ক্রমশ সরু হয়, যেমন গাজর।", "গাজরের মতো মূলে ওপর চওড়া ও নিচ সরু।", listOf("গাজরাকৃতি", "ওপরের অংশ মোটা", "গাজর")),
                MemorizeLine(4, "তৃতীয়ত শালগমাকৃতি মূল খাদ্য সঞ্চয়ে প্রায় গোলাকার ধারণ করে এবং নিচ হঠাৎ সরু হয়ে যায়।", "শালগমের ওপরের দিক গোল কিন্তু নিচটা হঠাৎ সরু।", listOf("শালগমাকৃতি", "প্রায় গোলাকার", "হঠাৎ সরু", "শালগম"))
            )
            topic.contains("বল") || topic.contains("গতির") -> listOf(
                MemorizeLine(0, "কোনো বস্তুর ভরবেগের পরিবর্তনের হার তার ওপর প্রযুক্ত বলের সমানুপাতিক।", "ভরবেগ পরিবর্তনের হার প্রযুক্ত বলের সমান হারে বাড়ে।", listOf("ভরবেগের পরিবর্তনের হার", "প্রযুক্ত বলের সমানুপাতিক")),
                MemorizeLine(1, "এবং প্রযুক্ত বল যেদিকে ক্রিয়া করে বস্তুর ভরবেগের পরিবর্তনও সেদিকে ঘটে।", "বল যেদিকে কাজ করে ভরবেগও সেদিকে বদলায়।", listOf("বল যেদিকে ক্রিয়া করে", "ভরবেগের পরিবর্তন")),
                MemorizeLine(2, "গাণিতিকভাবে বল পরিমাপের মূল সমীকরণ হলো F = ma।", "বল = ভর গুণন ত্বরণ।", listOf("বল পরিমাপের", "F = ma", "সমীকরণ")),
                MemorizeLine(3, "এখানে m হলো বস্তুর ভর, a হলো ত্বরণ এবং F হলো প্রযুক্ত মোট বল।", "m ভর, a ত্বরণ ও F প্রযুক্ত বল নির্দেশ করে।", listOf("ভর", "ত্বরণ", "প্রযুক্ত বল")),
                MemorizeLine(4, "১ কেজি ভরের বস্তুতে ১ মিটার/সেকেন্ড² ত্বরণ সৃষ্টি করতে ১ নিউটন বলের প্রয়োজন।", "১ নিউটনের সংজ্ঞা ও প্রয়োজনীয় একক।", listOf("১ কেজি", "১ মিটার/সেকেন্ড²", "১ নিউটন"))
            )
            else -> listOf(
                MemorizeLine(0, "পাঠ্যবইয়ে বর্ণিত এই অধ্যায়ের মূল সংজ্ঞাটি স্পষ্টভাবে মনে রাখতে হবে।", "অধ্যায়ের মূল কথা মাথায় রাখা।", listOf("সংজ্ঞা", "স্পষ্টভাবে")),
                MemorizeLine(1, "পরীক্ষার খাতায় পয়েন্ট আকারে উদাহরণসহ উপস্থাপন করলে পুরো নম্বর পাওয়া যায়।", "পয়েন্ট ও উদাহরণ দিয়ে লিখলে ভালো মার্কস আসে।", listOf("পয়েন্ট আকারে", "উদাহরণসহ", "পুরো নম্বর")),
                MemorizeLine(2, "নিয়মিত রিভিশন ও অনুশীলনের মাধ্যমে পড়া দীর্ঘস্থায়ী স্মৃতিতে জমা হয়।", "বারবার চর্চা করলে মনে থাকে।", listOf("নিয়মিত রিভিশন", "অনুশীলন", "দীর্ঘস্থায়ী"))
            )
        }
        _memorizeSession.value = MemorizeSession(topicTitle = topic, lines = lines)
        _memorizeEvaluation.value = null
    }

    fun evaluateSpokenRecall(spokenText: String) {
        val session = _memorizeSession.value ?: return
        val currentLine = session.lines.getOrNull(session.currentIndex) ?: return
        viewModelScope.launch {
            _isEvaluatingRecall.value = true
            val eval = tutorService.evaluateSpokenRecall(
                targetLine = currentLine.targetLine,
                spokenAnswer = spokenText,
                keywords = currentLine.keywords
            )
            _memorizeEvaluation.value = eval
            if (eval.isPassed) {
                val updatedCompleted = session.completedLines + session.currentIndex
                _memorizeSession.value = session.copy(completedLines = updatedCompleted)
            }
            _isEvaluatingRecall.value = false
        }
    }

    fun nextMemorizeLine() {
        val session = _memorizeSession.value ?: return
        if (session.currentIndex < session.lines.size - 1) {
            _memorizeSession.value = session.copy(currentIndex = session.currentIndex + 1)
            _memorizeEvaluation.value = null
        }
    }

    fun prevMemorizeLine() {
        val session = _memorizeSession.value ?: return
        if (session.currentIndex > 0) {
            _memorizeSession.value = session.copy(currentIndex = session.currentIndex - 1)
            _memorizeEvaluation.value = null
        }
    }

    // MCQ Quiz logic
    fun loadMCQs() {
        viewModelScope.launch {
            val classLvl = _userProfile.value.selectedClass
            val subName = _selectedSubject.value?.banglaName ?: "বিজ্ঞান"
            val topic = _selectedTopic.value
            val mcqs = tutorService.generateMCQs(classLvl, subName, topic)
            _quizQuestions.value = mcqs
            _currentQuizIndex.value = 0
            _selectedOption.value = null
            _isQuizAnswerSubmitted.value = false
            _quizScore.value = 0
            _isQuizFinished.value = false
        }
    }

    private val _examRecordedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val examRecordedAnswers: StateFlow<Map<Int, Int>> = _examRecordedAnswers.asStateFlow()

    fun selectQuizOption(index: Int) {
        if (_quizMode.value == "Exam") {
            _selectedOption.value = index
            val updated = _examRecordedAnswers.value.toMutableMap()
            updated[_currentQuizIndex.value] = index
            _examRecordedAnswers.value = updated
        } else {
            if (!_isQuizAnswerSubmitted.value) {
                _selectedOption.value = index
            }
        }
    }

    fun submitQuizAnswer() {
        if (_selectedOption.value == null || _isQuizAnswerSubmitted.value) return
        _isQuizAnswerSubmitted.value = true
        val currentQ = _quizQuestions.value.getOrNull(_currentQuizIndex.value) ?: return
        if (_selectedOption.value == currentQ.correctOptionIndex) {
            _quizScore.value += 1
        }
    }

    fun prevQuizQuestion() {
        if (_currentQuizIndex.value > 0) {
            _currentQuizIndex.value -= 1
            if (_quizMode.value == "Exam") {
                _selectedOption.value = _examRecordedAnswers.value[_currentQuizIndex.value]
            }
        }
    }

    fun nextQuizQuestion() {
        val total = _quizQuestions.value.size
        if (_quizMode.value == "Exam") {
            if (_currentQuizIndex.value < total - 1) {
                _currentQuizIndex.value += 1
                _selectedOption.value = _examRecordedAnswers.value[_currentQuizIndex.value]
            } else {
                var calculatedScore = 0
                _quizQuestions.value.forEachIndexed { idx, q ->
                    if (_examRecordedAnswers.value[idx] == q.correctOptionIndex) {
                        calculatedScore += 1
                    }
                }
                _quizScore.value = calculatedScore
                _isQuizFinished.value = true
                viewModelScope.launch {
                    val subName = _selectedSubject.value?.banglaName ?: "বিজ্ঞান"
                    val topic = _selectedTopic.value
                    val percentage = if (total > 0) (calculatedScore * 100) / total else 0
                    dao.insertQuizResult(
                        QuizHistoryEntity(
                            subjectName = subName,
                            topicName = topic,
                            totalQuestions = total,
                            correctAnswers = calculatedScore,
                            percentage = percentage
                        )
                    )
                }
            }
            return
        }

        if (_currentQuizIndex.value < total - 1) {
            _currentQuizIndex.value += 1
            _selectedOption.value = null
            _isQuizAnswerSubmitted.value = false
        } else {
            _isQuizFinished.value = true
            viewModelScope.launch {
                val subName = _selectedSubject.value?.banglaName ?: "বিজ্ঞান"
                val topic = _selectedTopic.value
                val percentage = if (total > 0) (_quizScore.value * 100) / total else 0
                dao.insertQuizResult(
                    QuizHistoryEntity(
                        subjectName = subName,
                        topicName = topic,
                        totalQuestions = total,
                        correctAnswers = _quizScore.value,
                        percentage = percentage
                    )
                )
            }
        }
    }

    fun restartQuiz() {
        _currentQuizIndex.value = 0
        _selectedOption.value = null
        _isQuizAnswerSubmitted.value = false
        _quizScore.value = 0
        _isQuizFinished.value = false
        _examRecordedAnswers.value = emptyMap()
    }

    fun setQuizMode(mode: String) {
        _quizMode.value = mode
        restartQuiz()
    }

    // Exam Questions
    fun loadExamQuestions() {
        viewModelScope.launch {
            _isLoadingExamQuestions.value = true
            val classLvl = _userProfile.value.selectedClass
            val subName = _selectedSubject.value?.banglaName ?: "বিজ্ঞান"
            val topic = _selectedTopic.value
            _examQuestions.value = tutorService.generateExamQuestions(classLvl, subName, topic)
            _isLoadingExamQuestions.value = false
        }
    }

    // Saved Lessons & Revision
    fun saveCurrentLesson() {
        viewModelScope.launch {
            val currentExp = (_explanationState.value as? UiState.Success)?.data ?: return@launch
            val subName = _selectedSubject.value?.banglaName ?: "সাধারণ"
            dao.insertLesson(
                SavedLessonEntity(
                    topicName = currentExp.topicName,
                    subjectName = subName,
                    className = "শ্রেণি ${_userProfile.value.selectedClass}",
                    summary = currentExp.shortSummary,
                    simpleExplanation = currentExp.simpleExplanation
                )
            )
        }
    }

    fun deleteLesson(id: Long) {
        viewModelScope.launch {
            dao.deleteLesson(id)
        }
    }

    fun addRevisionItem(difficulty: String) {
        viewModelScope.launch {
            val topic = _selectedTopic.value
            val subName = _selectedSubject.value?.banglaName ?: "বিজ্ঞান"
            val nextDate = when (difficulty) {
                "Easy" -> "৭ দিন পর"
                "Needs Practice" -> "৩ দিন পর"
                else -> "আগামীকাল"
            }
            dao.insertRevisionItem(
                RevisionItemEntity(
                    topicName = topic,
                    subjectName = subName,
                    difficulty = difficulty,
                    nextReviewDate = nextDate
                )
            )
        }
    }

    fun completeRevision(id: Long) {
        viewModelScope.launch {
            dao.markRevisionCompleted(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
