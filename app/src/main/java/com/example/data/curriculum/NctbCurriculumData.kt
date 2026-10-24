package com.example.data.curriculum

import com.example.data.model.ChapterItem
import com.example.data.model.ClassLevel
import com.example.data.model.SubjectItem

object NctbCurriculumData {

    val classLevels = listOf(
        ClassLevel(5, "পঞ্চম শ্রেণি", "Class 5", "পিইসি/সমাপনী স্তরের মৌলিক ভিত্তি ও প্রাথমিক শিক্ষা"),
        ClassLevel(6, "ষষ্ঠ শ্রেণি", "Class 6", "মাধ্যমিক স্তরে প্রবেশ ও সমন্বিত শিখন অভিজ্ঞতা"),
        ClassLevel(7, "সপ্তম শ্রেণি", "Class 7", "বিজ্ঞান, গণিত ও বাস্তব জীবনের প্রায়োগিক শিক্ষা"),
        ClassLevel(8, "অষ্টম শ্রেণি", "Class 8", "জুনিয়র স্কুল স্তর, মৌলিক বিজ্ঞান ও প্যাটার্ন গণিত"),
        ClassLevel(9, "নবম শ্রেণি", "Class 9", "এসএসসি স্তর ও বিভাগীয় বিষয়সমূহ (বিজ্ঞান/মানবিক/ব্যবসায়)"),
        ClassLevel(10, "দশম শ্রেণি", "Class 10", "এসএসসি পরীক্ষার চূড়ান্ত প্রস্তুতি ও মডেল টেস্ট")
    )

    fun getSubjectsForClass(classLevel: Int): List<SubjectItem> {
        return when (classLevel) {
            5 -> listOf(
                SubjectItem("c5_sci", "প্রাথমিক বিজ্ঞান", "Elementary Science", "science", 5, 14),
                SubjectItem("c5_math", "প্রাথমিক গণিত", "Elementary Mathematics", "calculate", 5, 14),
                SubjectItem("c5_ban", "বাংলা", "Bangla", "menu_book", 5, 20),
                SubjectItem("c5_eng", "English For Today", "English", "language", 5, 25),
                SubjectItem("c5_bgs", "বাংলাদেশ ও বিশ্বপরিচয়", "Bangladesh & Global Studies", "public", 5, 12),
                SubjectItem("c5_rel", "ইসলাম ও নৈতিক শিক্ষা", "Islam & Moral Education", "verified", 5, 5)
            )
            6 -> listOf(
                SubjectItem("c6_sci", "বিজ্ঞান", "General Science", "science", 6, 14),
                SubjectItem("c6_math", "গণিত", "Mathematics", "calculate", 6, 10),
                SubjectItem("c6_ban", "বাংলা (চারুপাঠ)", "Bangla", "menu_book", 6, 15),
                SubjectItem("c6_eng", "English For Today", "English", "language", 6, 20),
                SubjectItem("c6_bgs", "বাংলাদেশ ও বিশ্বপরিচয়", "Bangladesh & Global Studies", "public", 6, 13),
                SubjectItem("c6_ict", "তথ্য ও যোগাযোগ প্রযুক্তি", "ICT", "computer", 6, 5)
            )
            7 -> listOf(
                SubjectItem("c7_sci", "বিজ্ঞান", "Science", "science", 7, 14),
                SubjectItem("c7_math", "গণিত", "Mathematics", "calculate", 7, 11),
                SubjectItem("c7_ban", "বাংলা (সপ্তবর্ণা)", "Bangla", "menu_book", 7, 16),
                SubjectItem("c7_eng", "English For Today", "English", "language", 7, 18),
                SubjectItem("c7_bgs", "বাংলাদেশ ও বিশ্বপরিচয়", "Bangladesh & Global Studies", "public", 7, 13),
                SubjectItem("c7_ict", "তথ্য ও যোগাযোগ প্রযুক্তি", "ICT", "computer", 7, 5)
            )
            8 -> listOf(
                SubjectItem("c8_sci", "বিজ্ঞান", "Science", "science", 8, 14),
                SubjectItem("c8_math", "গণিত", "Mathematics", "calculate", 8, 11),
                SubjectItem("c8_ban", "বাংলা (সাহিত্য কণিকা)", "Bangla", "menu_book", 8, 18),
                SubjectItem("c8_eng", "English For Today", "English", "language", 8, 20),
                SubjectItem("c8_bgs", "বাংলাদেশ ও বিশ্বপরিচয়", "Bangladesh & Global Studies", "public", 8, 14),
                SubjectItem("c8_ict", "তথ্য ও যোগাযোগ প্রযুক্তি", "ICT", "computer", 8, 5)
            )
            9, 10 -> listOf(
                SubjectItem("c910_phy", "পদার্থবিজ্ঞান", "Physics", "bolt", classLevel, 14),
                SubjectItem("c910_chem", "রসায়ন", "Chemistry", "science", classLevel, 12),
                SubjectItem("c910_bio", "জীববিজ্ঞান", "Biology", "eco", classLevel, 14),
                SubjectItem("c910_gen_sci", "সাধারণ বিজ্ঞান", "General Science", "psychology", classLevel, 14),
                SubjectItem("c910_math", "সাধারণ গণিত", "General Mathematics", "calculate", classLevel, 17),
                SubjectItem("c910_high_math", "উচ্চতর গণিত", "Higher Mathematics", "functions", classLevel, 14),
                SubjectItem("c910_ban", "বাংলা সাহিত্য ও ব্যাকরণ", "Bangla", "menu_book", classLevel, 24),
                SubjectItem("c910_eng", "English 1st & 2nd Paper", "English", "language", classLevel, 20),
                SubjectItem("c910_bgs", "বাংলাদেশ ও বিশ্বপরিচয়", "Bangladesh & Global Studies", "public", classLevel, 16),
                SubjectItem("c910_ict", "তথ্য ও যোগাযোগ প্রযুক্তি", "ICT", "computer", classLevel, 6)
            )
            else -> emptyList()
        }
    }

    fun getChaptersForSubject(subjectId: String): List<ChapterItem> {
        return when (subjectId) {
            "c7_sci" -> listOf(
                ChapterItem("c7_sci_1", subjectId, 1, "নিম্নশ্রেণির জীব", "Lower Organisms", listOf("ভাইরাস ও ব্যাকটেরিয়ার বৈশিষ্ট্য", "অ্যামিবা ও অ্যান্টামিবা", "স্বাস্থ্যঝুঁকি ও প্রতিকার")),
                ChapterItem("c7_sci_2", subjectId, 2, "উদ্ভিদ ও প্রাণীর কোষীয় সংগঠন", "Cellular Organization", listOf("উদ্ভিদ কোষ ও প্রাণী কোষের তুলনা", "প্লাস্টিড ও এর কাজ", "মাইটোকন্ড্রিয়া - কোষের শক্তিঘর")),
                ChapterItem("c7_sci_3", subjectId, 3, "উদ্ভিদের বাহ্যিক বৈশিষ্ট্য", "External Morphology of Plants", listOf("প্রধান মূলের রূপান্তর", "রূপান্তরিত কাণ্ড", "পাতা ও বিভিন্ন রূপ")),
                ChapterItem("c7_sci_4", subjectId, 4, "শ্বসন", "Respiration", listOf("উদ্ভিদের গ্যাসীয় বিনিময়", "মানব শ্বসনতন্ত্রের গঠন", "ফুসফুস ও অ্যালভিওলাই")),
                ChapterItem("c7_sci_5", subjectId, 5, "পরিপাকতন্ত্র এবং রক্ত সংবহনতন্ত্র", "Digestive and Circulatory Systems", listOf("পরিপাক প্রক্রিয়া ও খাদ্যনালী", "রক্তরস ও রক্তকণিকা", "হৃৎপিণ্ডের গঠন ও কাজ")),
                ChapterItem("c7_sci_6", subjectId, 6, "পদার্থের গঠন", "Structure of Matter", listOf("পরমাণু ও অণু", "মৌলিক ও যৌগিক পদার্থ", "রাসায়নিক সংকেত ও প্রতীক")),
                ChapterItem("c7_sci_7", subjectId, 7, "শক্তির ব্যবহার", "Use of Energy", listOf("কাজ, ক্ষমতা ও শক্তি", "শক্তির রূপান্তর ও সংরক্ষণশীলতা", "নবায়নযোগ্য শক্তি")),
                ChapterItem("c7_sci_8", subjectId, 8, "শব্দের কথা", "Sound and Acoustics", listOf("শব্দ কীভাবে উৎপন্ন হয়", "শব্দের বেগ ও সঞ্চালন", "শ্রাব্যতার সীমা ও শব্দদূষণ"))
            )
            "c8_sci" -> listOf(
                ChapterItem("c8_sci_1", subjectId, 1, "প্রাণিজগতের শ্রেণিবিন্যাস", "Classification of Animals", listOf("শ্রেণিবিন্যাসের প্রয়োজনীয়তা", "অমেরুদণ্ডী প্রাণীর পর্বসমূহ", "কর্ডাটা ও মেরুদণ্ডী শ্রেণি")),
                ChapterItem("c8_sci_2", subjectId, 2, "জীবের বৃদ্ধি ও বংশগতি", "Growth and Heredity", listOf("অ্যামাইটোসিস ও মাইটোসিস", "মিয়োসিস কোষ বিভাজন", "ডিএনএ ও ক্রোমোজোম")),
                ChapterItem("c8_sci_3", subjectId, 3, "ব্যাপন, অভিস্রবণ ও প্রস্বেদন", "Diffusion and Osmosis", listOf("ব্যাপন ও এর তাৎপর্য", "অভিস্রবণ পরীক্ষা", "উদ্ভিদে প্রস্বেদন প্রক্রিয়া")),
                ChapterItem("c8_sci_4", subjectId, 4, "উদ্ভিদে বংশবৃদ্ধি", "Plant Reproduction", listOf("পরাগায়নের প্রকারভেদ", "নিষেক প্রক্রিয়া", "ফল ও বীজ গঠন")),
                ChapterItem("c8_sci_6", subjectId, 6, "পরমাণুর গঠন", "Structure of Atom", listOf("ইলেকট্রন, প্রোটন ও নিউট্রন", "পারমাণবিক সংখ্যা ও ভরসংখ্যা", "আইসোটোপ ও এর ব্যবহার")),
                ChapterItem("c8_sci_8", subjectId, 8, "রাসায়নিক বিক্রিয়া", "Chemical Reactions", listOf("সংযোজন ও বিয়োজন বিক্রিয়া", "প্রতিস্থাপন ও দহন বিক্রিয়া", "তড়িৎ বিশ্লেষণ")),
                ChapterItem("c8_sci_10", subjectId, 10, "অম্ল, ক্ষারক ও লবণ", "Acids, Bases and Salts", listOf("অ্যাসিডের বৈশিষ্ট্য ও লিটমাস পরীক্ষা", "ক্ষারক ও ক্ষার", "নিরপেক্ষকরণ ও পিএইচ"))
            )
            "c8_math" -> listOf(
                ChapterItem("c8_math_1", subjectId, 1, "প্যাটার্ন", "Patterns", listOf("স্বাভাবিক সংখ্যার প্যাটার্ন", "ফিবোনাক্কি ধারা", "বীজগণিতীয় রাশি নির্ণয়")),
                ChapterItem("c8_math_2", subjectId, 2, "মুনাফা", "Profit and Interest", listOf("সরল মুনাফা (I = Prn)", "চক্রবৃদ্ধি মুনাফা (C = P(1+r)^n)", "মুনাফা-আসল সমস্যা")),
                ChapterItem("c8_math_3", subjectId, 3, "পরিমাপ", "Measurement", listOf("দৈর্ঘ্য, ক্ষেত্রফল ও আয়তন", "মেট্রিক পদ্ধতি ও রূপান্তর", "পুকুরের পাড় ও চৌবাচ্চা সংক্রান্ত")),
                ChapterItem("c8_math_4", subjectId, 4, "বীজগণিতীয় সূত্রাবলী ও প্রয়োগ", "Algebraic Formulae", listOf("বর্গের সূত্রাবলী", "ঘন সংবলিত সূত্রাবলী", "উৎপাদকে বিশ্লেষণ"))
            )
            "c910_phy" -> listOf(
                ChapterItem("c910_phy_1", subjectId, 1, "ভৌত রাশি ও পরিমাপ", "Physical Quantities and Measurement", listOf("মৌলিক ও লব্ধ রাশি", "স্লাইড ক্যালিপার্স ও স্ক্রু গজ", "পরিমাপের ত্রুটি ও যথার্থতা")),
                ChapterItem("c910_phy_2", subjectId, 2, "গতি", "Motion", listOf("স্কেলার ও ভেক্টর রাশি", "গতির সমীকরণসমূহ (v=u+at, s=ut+0.5at²)", "পরন্ত বস্তুর সূত্রাবলী")),
                ChapterItem("c910_phy_3", subjectId, 3, "বল", "Force and Laws of Motion", listOf("নিউটনের গতির ১ম, ২য় ও ৩য় সূত্র", "ভরবেগ ও ভরবেগের সংরক্ষণ সূত্র", "ঘর্ষণ বল ও এর প্রকার")),
                ChapterItem("c910_phy_4", subjectId, 4, "কাজ, ক্ষমতা ও শক্তি", "Work, Power and Energy", listOf("কাজের সংজ্ঞা ও প্রকারভেদ (W=Fs)", "গতিশক্তি ও বিভব শক্তি", "কর্মদক্ষতা (Efficiency)")),
                ChapterItem("c910_phy_5", subjectId, 5, "পদার্থের অবস্থা ও চাপ", "State of Matter and Pressure", listOf("চাপ ও ঘনত্ব (P = hρg)", "প্যাসকেলের সূত্র ও হাইড্রোলিক প্রেস", "আর্কিমিডিসের নীতি ও প্লবতা"))
            )
            "c910_chem" -> listOf(
                ChapterItem("c910_chem_1", subjectId, 1, "রসায়নের ধারণা", "Concepts of Chemistry", listOf("রসায়নের পরিধি ও ক্ষেত্র", "রাসায়নিক দ্রব্যের ঝুঁকি চিহ্ন", "গবেষণাগারে সতর্কতা")),
                ChapterItem("c910_chem_2", subjectId, 2, "পদার্থের অবস্থা", "States of Matter", listOf("কণার গতিতত্ত্ব", "ব্যাপন ও নিঃসরণ", "গলন, স্ফুটন ও ঊর্ধ্বপাতন")),
                ChapterItem("c910_chem_3", subjectId, 3, "পদার্থের গঠন", "Structure of Matter", listOf("রাদারফোর্ড ও বোর পরমাণু মডেল", "ইলেকট্রন বিন্যাস ও নীতি", "আইসোটোপ ও তেজস্ক্রিয়তা")),
                ChapterItem("c910_chem_4", subjectId, 4, "পর্যায় সারণি", "Periodic Table", listOf("পর্যায় সারণির বৈশিষ্ট্য", "পর্যায়বৃত্ত ধর্ম (আয়নিকরণ শক্তি, তড়িৎ ঋণাত্মকতা)", "গ্রুপ ও মৌলের সক্রিয়তা"))
            )
            "c910_bio" -> listOf(
                ChapterItem("c910_bio_1", subjectId, 1, "জীবন পাঠ", "Lessons on Life", listOf("জীববিজ্ঞানের শাখাসমূহ", "দ্বিপদ নামকরণ পদ্ধতি", "জীবজগতের ৫ রাজ্য")),
                ChapterItem("c910_bio_2", subjectId, 2, "জীব কোষ ও টিস্যু", "Living Cells and Tissues", listOf("উদ্ভিদ ও প্রাণী কোষের অঙ্গাণু", "ভাজক টিস্যু ও স্থায়ী টিস্যু", "অঙ্গ ও তন্ত্র")),
                ChapterItem("c910_bio_4", subjectId, 4, "জীবনীশক্তি", "Bioenergetics", listOf("সালোকসংশ্লেষণ আলোক ও অন্ধকার পর্যায়", "সবাত ও অবাত শ্বসন", "এটিপি (ATP) - জৈব মুদ্রা"))
            )
            else -> listOf(
                ChapterItem("${subjectId}_1", subjectId, 1, "প্রথম অধ্যায়: মৌলিক ধারণা ও ভিত্তি", "Chapter 1: Foundations", listOf("সূচনা ও পটভূমি", "মূল নিয়মাবলী ও তত্ত্ব", "বোর্ড পরীক্ষায় আসার মতো পয়েন্ট")),
                ChapterItem("${subjectId}_2", subjectId, 2, "দ্বিতীয় অধ্যায়: তত্ত্ব ও প্রয়োগ", "Chapter 2: Theory & Practice", listOf("সংজ্ঞা ও ব্যাখ্যা", "বাস্তব জীবনের উদাহরণ", "সমস্যা সমাধান")),
                ChapterItem("${subjectId}_3", subjectId, 3, "তৃতীয় অধ্যায়: কাঠামো ও পরীক্ষণ", "Chapter 3: Structure & Analysis", listOf("চিত্র ও বিশ্লেষণ", "গুরুত্বপূর্ণ প্রশ্নাবলী", "সংক্ষিপ্ত প্রশ্নোত্তর"))
            )
        }
    }

    fun getBoardQuestions(): List<com.example.data.model.BoardQuestion> {
        return listOf(
            com.example.data.model.BoardQuestion(
                id = "bq_dhaka_2023_sci",
                boardName = "ঢাকা বোর্ড",
                year = 2023,
                classLevel = 8,
                subjectName = "বিজ্ঞান",
                chapterName = "পরমাণুর গঠন",
                questionType = com.example.data.model.QuestionType.APPLICATION,
                stimulus = "উদ্দীপক: এক্স পরমাণুর ভরসংখ্যা ২৩ এবং প্রোটন সংখ্যা ১১। ওয়াই পরমাণুর ভরসংখ্যা ৩৫ এবং ইলেকট্রন সংখ্যা ১৭।",
                questionText = "উদ্দীপকের পরমাণু দুটির ইলেকট্রন বিন্যাস দেখাও এবং তাদের রাসায়নিক সক্রিয়তা তুলনা করো।",
                modelAnswer = "এক্স (Na) এর ইলেকট্রন বিন্যাস ২, ৮, ১ এবং ওয়াই (Cl) এর ইলেকট্রন বিন্যাস ২, ৮, ৭। সোডিয়াম একটি ইলেকট্রন ত্যাগ করে এবং ক্লোরিন একটি ইলেকট্রন গ্রহণ করে নিষ্ক্রিয় গ্যাসের অর্জন করতে চায়, যা এদের আয়নিক বন্ধন গঠনে উৎসাহিত করে।",
                isVerifiedBoardQuestion = true,
                sourceReference = "ঢাকা বোর্ড ২০২৩ - বিজ্ঞান (প্রশ্ন নং ৩)"
            ),
            com.example.data.model.BoardQuestion(
                id = "bq_rajshahi_2022_math",
                boardName = "রাজশাহী বোর্ড",
                year = 2022,
                classLevel = 8,
                subjectName = "গণিত",
                chapterName = "প্যাটার্ন",
                questionType = com.example.data.model.QuestionType.HIGHER_ABILITY,
                stimulus = "উদ্দীপক: ৫, ১৩, ২১, ২৯, ... একটি সংখ্যা প্যাটার্ন।",
                questionText = "প্যাটার্নটির পরবর্তী দুটি সংখ্যা নির্ণয় করো এবং দেখাও যে প্রথম ৩০টি পদের সমষ্টি একটি নির্দিষ্ট বীজগণিতীয় নিয়ম মেনে চলে।",
                modelAnswer = "প্যাটার্নটির প্রতি পদে পার্থক্য ৮। সুতরাং বীজগণিতীয় রাশিটি হলো (৮n - ৩)। ৩০তম পদ = ৮(৩০) - ৩ = ২৩৭। সমষ্টি সূত্র ব্যবহার করে মোট সমষ্টি হবে ৩৬১৫।",
                isVerifiedBoardQuestion = true,
                sourceReference = "রাজশাহী বোর্ড ২০২২ - গণিত (প্রশ্ন নং ১)"
            ),
            com.example.data.model.BoardQuestion(
                id = "bq_cumilla_2023_phy",
                boardName = "কুমিল্লা বোর্ড",
                year = 2023,
                classLevel = 9,
                subjectName = "পদার্থবিজ্ঞান",
                chapterName = "গতি",
                questionType = com.example.data.model.QuestionType.APPLICATION,
                stimulus = "উদ্দীপক: একটি গাড়ি স্থির অবস্থান থেকে যাত্রা শুরু করে ৪ সেকেণ্ডে ২০ মি/সে বেগ অর্জন করল।",
                questionText = "গাড়িটির ত্বরণ নির্ণয় করো এবং প্রথম ৪ সেকেণ্ডে অতিক্রান্ত দূরত্ব হিসাব করো।",
                modelAnswer = "ত্বরণ a = (v - u) / t = (20 - 0) / 4 = 5 m/s²। অতিক্রান্ত দূরত্ব s = ut + 0.5at² = 0 + 0.5 * 5 * (4)² = 40 মিটার।",
                isVerifiedBoardQuestion = true,
                sourceReference = "কুমিল্লা বোর্ড ২০২৩ - পদার্থবিজ্ঞান (প্রশ্ন নং ২)"
            ),
            com.example.data.model.BoardQuestion(
                id = "bq_jashore_2023_chem",
                boardName = "যশোর বোর্ড",
                year = 2023,
                classLevel = 9,
                subjectName = "রসায়ন",
                chapterName = "পর্যায় সারণি",
                questionType = com.example.data.model.QuestionType.COMPREHENSION,
                stimulus = null,
                questionText = "পর্যায় সারণিতে একই গ্রুপের ওপর থেকে নিচের দিকে মৌলের ধাতব ধর্ম বৃদ্ধি পায় কেন? ব্যাখ্যা করো।",
                modelAnswer = "গ্রুপের ওপর থেকে নিচে নামলে নতুন শক্তিস্তর যুক্ত হয়, ফলে পরমাণুর আকার বৃদ্ধি পায়। নিউক্লিয়াস ও যোজ্যতা ইলেকট্রনের দূরত্ব বাড়ায় ইলেকট্রন সহজে অপসারিত হয়, তাই ধাতব ধর্ম বাড়ে।",
                isVerifiedBoardQuestion = true,
                sourceReference = "যশোর বোর্ড ২০২৩ - রসায়ন (প্রশ্ন নং ৪)"
            ),
            com.example.data.model.BoardQuestion(
                id = "bq_ai_practice_1",
                boardName = "এআই বোর্ড প্র্যাকটিস (ঢাকা বোর্ড স্ট্যান্ডার্ড)",
                year = 2024,
                classLevel = 8,
                subjectName = "বিজ্ঞান",
                chapterName = "ব্যাপন, অভিস্রবণ ও প্রস্বেদন",
                questionType = com.example.data.model.QuestionType.HIGHER_ABILITY,
                stimulus = "উদ্দীপক: শিক্ষক ক্লাসে দুটি আলুর টুকরো নিয়ে একটিতে লবণের ঘন দ্রবণ এবং অন্যটিতে বিশুদ্ধ পানি দিয়ে পরীক্ষা দেখালেন।",
                questionText = "উদ্দীপকের কোন প্রক্রিয়ায় আলুর খন্ডে পানির পরিবর্তন ঘটেছে? বিশ্লেষণ করো।",
                modelAnswer = "এটি অভিস্রবণ প্রক্রিয়া। অর্ধভেদ্য পর্দার মাধ্যমে কম ঘন দ্রবণ থেকে বেশি ঘন দ্রবণের দিকে দ্রাবক অণুর প্রবেশ ঘটে। লবণের প্রভাবে আলুর কোষরসের ভেতর থেকে পানি বাইরে চলে আসে।",
                isVerifiedBoardQuestion = false,
                sourceReference = "AI Board Practice Generator (NCTB Aligned)"
            ),
            com.example.data.model.BoardQuestion(
                id = "bq_chattogram_2022_ban",
                boardName = "চট্টগ্রাম বোর্ড",
                year = 2022,
                classLevel = 8,
                subjectName = "বাংলা",
                chapterName = "সাহিত্য কণিকা",
                questionType = com.example.data.model.QuestionType.KNOWLEDGE,
                stimulus = null,
                questionText = "পড়াশোনায় একাগ্রতা অর্জনের প্রধান উপায় কী?",
                modelAnswer = "নিয়মিত চর্চা, রুটিন মেনে চলা এবং পাঠ্যবই মনোযোগ সহকারে পড়া।",
                isVerifiedBoardQuestion = true,
                sourceReference = "চট্টগ্রাম বোর্ড ২০২২ - বাংলা (প্রশ্ন নং ১)"
            )
        )
    }
}

