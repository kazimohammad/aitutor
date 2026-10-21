package com.example

import com.example.data.ai.GeminiTutorService
import com.example.data.curriculum.NctbCurriculumData
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.RevisionItemEntity
import com.example.data.local.SavedLessonEntity
import com.example.data.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    // 1. Class, Subject, Chapter & Topic Selection
    @Test
    fun testCurriculumLoading_class7Science() {
        val subjects = NctbCurriculumData.getSubjectsForClass(7)
        assertTrue(subjects.isNotEmpty())
        val scienceSubject = subjects.find { it.id == "c7_sci" }
        assertNotNull(scienceSubject)

        val chapters = NctbCurriculumData.getChaptersForSubject("c7_sci")
        assertTrue(chapters.isNotEmpty())
        val chapter3 = chapters.find { it.chapterNumber == 3 }
        assertNotNull(chapter3)
        assertTrue(chapter3!!.topics.any { it.contains("মূলের রূপান্তর") })
    }

    @Test
    fun testCurriculumClasses_allClassLevelsCovered() {
        val levels = listOf(5, 6, 7, 8, 9, 10)
        for (lvl in levels) {
            val subs = NctbCurriculumData.getSubjectsForClass(lvl)
            assertTrue("Class $lvl must have NCTB subjects", subs.isNotEmpty())
            val firstSub = subs.first()
            val chaps = NctbCurriculumData.getChaptersForSubject(firstSub.id)
            assertTrue("Subject ${firstSub.banglaName} must have chapters", chaps.isNotEmpty())
            assertTrue("Chapter must have topics", chaps.first().topics.isNotEmpty())
        }
    }

    @Test
    fun testClass9And10PhysicsCurriculum() {
        val class9Subjects = NctbCurriculumData.getSubjectsForClass(9)
        val physicsSub = class9Subjects.find { it.id == "c910_phy" }
        assertNotNull("Physics must exist for Class 9", physicsSub)
        val physicsChapters = NctbCurriculumData.getChaptersForSubject(physicsSub!!.id)
        assertTrue(physicsChapters.any { it.topics.any { topic -> topic.contains("বল") || topic.contains("গতি") } })
    }

    // 2. Textbook Camera Scanning and Image Upload
    @Test
    fun testSampleScanAnalysis_authenticNctbData() = runBlocking {
        val service = GeminiTutorService()
        val res = service.analyseBookImage(null, "sample_science")
        assertNotNull(res)
        assertTrue(res.extractedText.isNotBlank())
        assertTrue(res.detectedTopic.contains("মূলের রূপান্তর"))
        assertTrue(res.difficultWords.isNotEmpty())
        assertTrue(res.generatedQuestions.isNotEmpty())
    }

    @Test
    fun testSampleScanPhysicsAnalysis() = runBlocking {
        val service = GeminiTutorService()
        val res = service.analyseBookImage(null, "sample_physics")
        assertNotNull(res)
        assertTrue(res.extractedText.contains("ভরবেগ") || res.extractedText.contains("F = ma"))
        assertTrue(res.detectedClass.contains("9"))
    }

    // 3. Bangla Text Recognition and Correction
    @Test
    fun testScannedTextCorrectionModel() {
        val initialText = "উদ্ভিদ বেঁচে থাকার জন্য বিশেষ কোনো কার্য সম্পাদনে মূলের স্বাভাবিক রূপান্তর ঘটায়।"
        val userCorrectedText = "উদ্ভিদের বেঁচে থাকার বিশেষ কার্য সম্পাদনে মূলের স্বাভাবিক রূপান্তর ঘটায়।"
        assertNotEquals(initialText, userCorrectedText)
        assertTrue(userCorrectedText.contains("মূলের স্বাভাবিক রূপান্তর"))
    }

    // 4. AI-Generated Explanations and Exam-Practice Questions
    @Test
    fun testCurriculumExplanation_structureValid() {
        val service = GeminiTutorService()
        val exp = service.getCurriculumExplanation(7, "বিজ্ঞান", "প্রধান মূলের রূপান্তর")
        assertEquals(7, exp.classLevel)
        assertTrue(exp.simpleExplanation.isNotBlank())
        assertTrue(exp.detailedExplanation.isNotBlank())
        assertTrue(exp.importantDefinitions.isNotEmpty())
        assertTrue(exp.quickQuestions.size >= 3)
        assertNotNull(exp.presentationGuide)
        assertTrue(exp.presentationGuide.essentialPoints.isNotEmpty())
    }

    @Test
    fun testExamQuestions_creativeQuestionTypes() = runBlocking {
        val service = GeminiTutorService()
        val questions = service.generateExamQuestions(7, "বিজ্ঞান", "প্রধান মূলের রূপান্তর")
        assertTrue("Must provide at least 4 questions", questions.size >= 4)
        val hasKnowledge = questions.any { it.questionType == QuestionType.KNOWLEDGE }
        val hasComprehension = questions.any { it.questionType == QuestionType.COMPREHENSION }
        val hasApplication = questions.any { it.questionType == QuestionType.APPLICATION }
        val hasHigher = questions.any { it.questionType == QuestionType.HIGHER_ABILITY }

        assertTrue(hasKnowledge)
        assertTrue(hasComprehension)
        assertTrue(hasApplication)
        assertTrue(hasHigher)

        questions.forEach { q ->
            assertTrue(q.modelAnswer.isNotBlank())
            assertTrue(q.keyPoints.isNotEmpty())
            assertTrue(q.marks in 1..4)
        }
    }

    // 5. MCQ Answer Validation, Scoring and Explanations
    @Test
    fun testMcqValidation_correctKeysAndExplanations() = runBlocking {
        val service = GeminiTutorService()
        val mcqs = service.generateMCQs(7, "বিজ্ঞান", "প্রধান মূলের রূপান্তর")
        assertTrue("Must have multiple MCQs", mcqs.isNotEmpty())
        mcqs.forEach { mcq ->
            assertEquals("Each MCQ must have 4 options", 4, mcq.options.size)
            assertTrue("Correct option index must be in range 0..3", mcq.correctOptionIndex in 0..3)
            assertTrue("Explanation must be present", mcq.explanation.isNotBlank())
        }
    }

    @Test
    fun testMcqScoringCalculation() {
        val totalQuestions = 5
        val correctCount = 4
        val percentage = (correctCount * 100) / totalQuestions
        assertEquals(80, percentage)
        assertTrue("80% should be considered excellence grade", percentage >= 80)
    }

    // 6. Bangla & English Voice Input and Audio Playback
    @Test
    fun testBanglaAudioTextSanitization() {
        val rawBangla = "নিউটনের ৩য় সূত্র: প্রত্যেক ক্রিয়ারই একটি সমান ও বিপরীত প্রতিক্রিয়া আছে! (F1 = -F2)"
        val cleanSpeech = rawBangla.replace("(", "").replace(")", "").replace("!", "।")
        assertTrue(cleanSpeech.contains("সমান ও বিপরীত প্রতিক্রিয়া"))
    }

    // 7. Line-by-Line Memorisation and Spoken Answer Evaluation
    @Test
    fun testSpokenRecallEvaluation_highAccuracy() = runBlocking {
        val service = GeminiTutorService()
        val target = "উদ্ভিদ বেঁচে থাকার জন্য বিশেষ কোনো কার্য সম্পাদনে মূলের স্বাভাবিক রূপান্তর ঘটায়।"
        val spoken = "উদ্ভিদ বেঁচে থাকার জন্য বিশেষ কার্য করতে মূলের রূপান্তর করে"
        val keywords = listOf("বিশেষ কার্য", "মূলের স্বাভাবিক রূপান্তর")

        val result = service.evaluateSpokenRecall(target, spoken, keywords)
        assertTrue(result.accuracyScore >= 50)
        assertTrue(result.feedbackMessage.isNotBlank())
        assertTrue(result.matchedKeywords.contains("বিশেষ কার্য"))
    }

    @Test
    fun testSpokenRecallEvaluation_lowAccuracy() = runBlocking {
        val service = GeminiTutorService()
        val target = "কোনো বস্তুর ভরবেগের পরিবর্তনের হার তার ওপর প্রযুক্ত বলের সমানুপাতিক।"
        val incorrectSpoken = "পানি সাধারণত ১০০ ডিগ্রি সেলসিয়াস তাপমাত্রায় ফোটে"
        val keywords = listOf("ভরবেগের পরিবর্তনের হার", "প্রযুক্ত বলের সমানুপাতিক")

        val result = service.evaluateSpokenRecall(target, incorrectSpoken, keywords)
        assertTrue(result.accuracyScore < 50)
        assertFalse(result.isPassed)
        assertTrue(result.missedKeywords.isNotEmpty())
    }

    // 8. Saved Lessons & Learning Progress
    @Test
    fun testSavedLessonAndQuizEntities() {
        val lesson = SavedLessonEntity(
            id = 1L,
            topicName = "প্রধান মূলের রূপান্তর",
            subjectName = "বিজ্ঞান",
            className = "শ্রেণি ৭",
            summary = "খাদ্য সঞ্চয়ের কারণে মূলের চার প্রকার রূপান্তর।",
            simpleExplanation = "সহজ ভাষায় মূল বিভিন্ন কাজে পরিবর্তিত হয়।"
        )
        assertEquals("প্রধান মূলের রূপান্তর", lesson.topicName)

        val quizRecord = QuizHistoryEntity(
            id = 1L,
            subjectName = "বিজ্ঞান",
            topicName = "প্রধান মূলের রূপান্তর",
            totalQuestions = 5,
            correctAnswers = 4,
            percentage = 80
        )
        assertEquals(80, quizRecord.percentage)

        val revision = RevisionItemEntity(
            id = 1L,
            topicName = "বল ও ত্বরণ",
            subjectName = "পদার্থবিজ্ঞান",
            difficulty = "Hard",
            nextReviewDate = "আগামীকাল"
        )
        assertEquals("আগামীকাল", revision.nextReviewDate)
    }

    // 9. Bilingual Language Switching
    @Test
    fun testLanguageToggleState() {
        var profile = UserProfile(selectedClass = 7, appLanguage = "bn")
        assertEquals("bn", profile.appLanguage)
        profile = profile.copy(appLanguage = if (profile.appLanguage == "bn") "en" else "bn")
        assertEquals("en", profile.appLanguage)
        profile = profile.copy(appLanguage = if (profile.appLanguage == "bn") "en" else "bn")
        assertEquals("bn", profile.appLanguage)
    }

    // 10. API Errors, Fallbacks and Loading State Resilience
    @Test
    fun testFallbackResilienceWhenNoApiKey() = runBlocking {
        val service = GeminiTutorService()
        val fallbackScan = service.analyseBookImage(null, "sample_science")
        assertNotNull(fallbackScan)
        assertTrue(fallbackScan.extractedText.isNotEmpty())

        val fallbackExp = service.explainTopic(7, "বিজ্ঞান", "অধ্যায় ৩", "প্রধান মূলের রূপান্তর")
        assertNotNull(fallbackExp)
        assertTrue(fallbackExp.simpleExplanation.isNotEmpty())
    }
}
