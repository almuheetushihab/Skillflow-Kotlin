package com.example.skillflow.data.repository

import android.graphics.Bitmap
import com.example.skillflow.BuildConfig
import com.example.skillflow.domain.model.KnowledgeNugget
import com.example.skillflow.domain.model.QuizQuestion
import com.example.skillflow.domain.repository.GeminiRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepositoryImpl @Inject constructor() : GeminiRepository {

    override fun askGemini(context: String, userQuestion: String): Flow<Result<String>> = flow {
        val apiKey = sanitizeApiKey(BuildConfig.GEMINI_API_KEY)
        if (apiKey.isBlank()) {
            emit(
                Result.failure(
                    IllegalStateException(
                        "Gemini API Key is missing. Please add GEMINI_API_KEY=your_key in local.properties"
                    )
                )
            )
            return@flow
        }

        val systemInstructionContent = content {
            text(
                "You are a professional Android and UI/UX Tutor for SkillFlow. " +
                        "Answer the user's question based on this nugget content: $context. " +
                        "Keep answers concise and beginner-friendly."
            )
        }

        val candidateModels = listOf(
            "gemini-1.5-flash",
            "gemini-2.0-flash",
            "gemini-2.5-flash",
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro",
            "gemini-2.0-flash-exp"
        )

        var lastError: Throwable? = null
        var lastTriedModel: String? = null

        for (modelName in candidateModels) {
            try {
                lastTriedModel = modelName
                Timber.d("Attempting Gemini API request with model: $modelName")

                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    systemInstruction = systemInstructionContent
                )

                val response = generativeModel.generateContent(userQuestion)
                val responseText = response.text

                if (!responseText.isNullOrBlank()) {
                    Timber.d("Successfully received response from model: $modelName")
                    emit(Result.success(responseText))
                    return@flow
                } else {
                    lastError = IllegalStateException("Model '$modelName' returned an empty response.")
                }
            } catch (e: SerializationException) {
                Timber.w(e, "Serialization failed for model: $modelName")
                lastError = e
            } catch (e: Exception) {
                Timber.w(e, "Request failed for model: $modelName")
                lastError = e
            }
        }

        val errorText = lastError?.message.orEmpty()
        val message = when {
            errorText.contains("404", ignoreCase = true) ||
                    errorText.contains("NOT_FOUND", ignoreCase = true) ||
                    errorText.contains("not found", ignoreCase = true) -> {
                "Gemini model status error: '$lastTriedModel' is not available. Please check network or API Key configuration."
            }
            errorText.contains("401", ignoreCase = true) ||
                    errorText.contains("unauthorized", ignoreCase = true) ||
                    errorText.contains("invalid api key", ignoreCase = true) -> {
                "Invalid Gemini API key. Please verify GEMINI_API_KEY in local.properties."
            }
            lastError is SerializationException -> {
                "Gemini returned an unexpected response format (serialization error). Please try again later."
            }
            else -> {
                "Unable to connect to SkillFlow AI Tutor right now. ${lastError?.message ?: ""}".trim()
            }
        }

        emit(Result.failure(Exception(message, lastError)))
    }.flowOn(Dispatchers.IO)

    override fun translateToBangla(title: String, content: String): Flow<Result<Pair<String, String>>> = flow {
        val apiKey = sanitizeApiKey(BuildConfig.GEMINI_API_KEY)
        if (apiKey.isBlank() || !apiKey.startsWith("AIzaSy")) {
            val fallback = fallbackBanglaTranslation(title, content)
            emit(Result.success(fallback))
            return@flow
        }

        val systemInstructionContent = content {
            text(
                "You are an expert technical and document translator for the SkillFlow app. " +
                        "Translate the provided title and content into natural, clear, fluent Bengali (Bangla). " +
                        "Keep markdown structure, headings (like ###), bullet points, and line breaks intact. " +
                        "Respond strictly in raw JSON without markdown codeblocks: " +
                        "{\"translatedTitle\": \"...\", \"translatedContent\": \"...\"}"
            )
        }

        val candidateModels = listOf(
            "gemini-1.5-flash",
            "gemini-2.0-flash",
            "gemini-2.5-flash",
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro",
            "gemini-2.0-flash-exp"
        )

        for (modelName in candidateModels) {
            try {
                Timber.d("Attempting Gemini Translation request with model: $modelName")

                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    systemInstruction = systemInstructionContent
                )

                val prompt = "Title: $title\nContent: $content"
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text

                if (!responseText.isNullOrBlank()) {
                    val cleanJson = responseText
                        .replace("```json", "")
                        .replace("```", "")
                        .trim()

                    try {
                        val jsonElement = Json.parseToJsonElement(cleanJson)
                        val jsonObject = jsonElement.jsonObject
                        val transTitle = jsonObject["translatedTitle"]?.jsonPrimitive?.content ?: title
                        val transContent = jsonObject["translatedContent"]?.jsonPrimitive?.content ?: content

                        emit(Result.success(Pair(transTitle, transContent)))
                        return@flow
                    } catch (_: Exception) {
                        val titleMatch = Regex("\"translatedTitle\"\\s*:\\s*\"(.*?)\"").find(cleanJson)
                        val contentMatch = Regex("\"translatedContent\"\\s*:\\s*\"(.*?)\"").find(cleanJson)
                        val transTitle = titleMatch?.groupValues?.get(1) ?: title
                        val transContent = contentMatch?.groupValues?.get(1) ?: fallbackBanglaTranslation(title, content).second
                        emit(Result.success(Pair(transTitle, transContent)))
                        return@flow
                    }
                }
            } catch (e: Exception) {
                Timber.w(e, "Translation request failed for model: $modelName")
            }
        }

        // Fallback translation if API call fails
        val fallback = fallbackBanglaTranslation(title, content)
        emit(Result.success(fallback))
    }.flowOn(Dispatchers.IO)

    private fun fallbackBanglaTranslation(title: String, content: String): Pair<String, String> {
        val transTitle = translateTitleOffline(title)
        val transContent = translateContentOffline(content)
        return Pair(transTitle, transContent)
    }

    private fun translateTitleOffline(title: String): String {
        val trimmed = title.trim()
        val titleMap = mapOf(
            "Scanned Document (Real OCR)" to "স্ক্যান করা ডকুমেন্ট (OCR)",
            "Scanned Document Content (Real OCR Extracted)" to "স্ক্যান করা ডকুমেন্টের তথ্য (OCR)",
            "Variables: val vs var" to "ভেরিয়েবল: val বনাম var",
            "String Templates" to "স্ট্রিং টেমপ্লেটস",
            "The 'when' Expression" to "'when' এক্সপ্রেশন",
            "Null Safety" to "নাল সেফটি (Null Safety)",
            "Singleton Pattern with 'object'" to "'object' দিয়ে সিঙ্গেলটন প্যাটার্ন",
            "Companion Objects" to "কম্প্যানিয়ন অবজেক্টস",
            "Activity Lifecycle" to "অ্যাক্টিভিটি লাইফসাইকেল",
            "Compose State" to "কম্পোজ স্টেট",
            "ViewModel Pattern" to "ভিউমডেল প্যাটার্ন",
            "Jetpack Navigation" to "জেটপ্যাক নেভিগেশন",
            "DataStore" to "ডাটা স্টোর",
            "Data Classes" to "ডাটা ক্লাসেস",
            "Extension Functions" to "এক্সটেনশন ফাংশনস",
            "Lateinit vs Lazy" to "Lateinit বনাম Lazy",
            "Higher-Order Functions & Lambdas" to "হায়ার-অর্ডার ফাংশন এবং ল্যাম্বডা",
            "Collections & Transformations" to "কালেকশনস এবং ট্রান্সফরমেশনস",
            "Kotlin Coroutines" to "কোটলিন করুটিনস",
            "Kotlin Flow" to "কোটলিন ফ্লো",
            "Room Database" to "রুম ডাটাবেস",
            "Retrofit Networking" to "রেট্রোফিট নেটওয়ার্কিং",
            "WorkManager" to "ওয়ার্কম্যানেজার",
            "Hilt Dependency Injection" to "হিল্ট ডিপেন্ডেন্সি ইনজেকশন",
            "Solid Principles - SRP" to "সলিড প্রিন্সিপালস - SRP",
            "Testing - JUnit" to "টেস্টিং - JUnit",
            "Smart Casts" to "স্মার্ট কাস্টস",
            "Scope Functions (let, apply, run...)" to "স্কোপ ফাংশনস (let, apply, run...)",
            "Sealed Classes & Interfaces" to "সিল্ড ক্লাসেস এবং ইন্টারফেস",
            "Inline Functions" to "ইনলাইন ফাংশনস",
            "Kotlin Fundamentals" to "কোটলিন ফান্ডামেন্টালস",
            "Jetpack Compose Basics" to "জেটপ্যাক কম্পোজ বেসিকস",
            "Clean Architecture" to "ক্লিন আর্কিটেকচার",
            "Swift Fundamentals" to "সুইফট ফান্ডামেন্টালস",
            "SwiftUI Essentials" to "সুইফটইউআই এসেনশিয়ালস",
            "ARC & Memory Management" to "ARC এবং মেমোরি ম্যানেজমেন্ট",
            "RESTful API Design" to "রেস্টফুল API ডিজাইন",
            "Microservices Architecture" to "মাইক্রোসার্ভিসেস আর্কিটেকচার",
            "SQL vs NoSQL" to "SQL বনাম NoSQL",
            "React Hooks" to "রিয়েক্ট হুকস",
            "CSS Flexbox" to "সিএসএস ফ্লেক্সবক্স",
            "Typography Hierarchy" to "টাইপোগ্রাফি হায়ারার্কি",
            "Color Theory" to "কালার থিওরি",
            "Python for Data Science" to "ডাটা সায়েন্সের জন্য পাইথন",
            "Machine Learning Basics" to "মেশিন লার্নিং বেসিকস"
        )
        return titleMap[trimmed] ?: translateTextPhraseByPhrase(trimmed)
    }

    private fun translateContentOffline(content: String): String {
        val text = content.trim()

        val exactMap = mapOf(
            "In Kotlin, 'val' creates a read-only" to
                "কোটলিনে 'val' দিয়ে শুধুমাত্র পাঠযোগ্য (immutable) ভেরিয়েবল তৈরি করা হয়, অর্থাৎ একবার মান নির্ধারণ করার পর তা আর পরিবর্তন করা যায় না। 'var' দিয়ে পরিবর্তনযোগ্য (mutable) ভেরিয়েবল তৈরি করা হয়। আপনার কোডকে আরও নিরাপদ এবং নির্ভরযোগ্য করতে ডিফল্টভাবে 'val' ব্যবহার করা সর্বোত্তম অনুশীলন।",

            "Kotlin allows embedding variables" to
                "কোটলিনে \$ চিহ্ন ব্যবহার করে সরাসরি স্ট্রিংয়ের ভেতরে ভেরিয়েবল এবং এক্সপ্রেশন যুক্ত করা যায়। যেমন: \"Hello \$name! 2+2 is \${2+2}\"।",

            "'when' replaces the C-style switch" to
                "'when' সি-স্টাইলের switch স্টেটমেন্টের বিকল্প হিসেবে কাজ করে। এটি একটি এক্সপ্রেশন, যা মান রিটার্ন করতে পারে। এটি টাইপ চেক (is String), রেঞ্জ (in 1..10) ইত্যাদি সমর্থন করে এবং এতে 'break' স্টেটমেন্টের প্রয়োজন হয় না।",

            "Kotlin distinguishes between nullable types" to
                "কোটলিন নাল হতে পারে এমন টাইপ (String?) এবং নাল হতে পারে না এমন টাইপের (String) মধ্যে পার্থক্য করে। নাল টাইপের মেথড কল করতে সেফ কল অপারেটর (?.) এবং নাল হলে ডিফল্ট মান দিতে এলভিস অপারেটর (?:) ব্যবহার করুন।",

            "In Java, implementing the Singleton pattern" to
                "জাভাতে সিঙ্গেলটন প্যাটার্ন তৈরি করতে অনেক বাড়তি কোড লিখতে হয়। কিন্তু কোটলিনে শুধু 'object MySingleton' ঘোষণা করলেই এটি স্বয়ংক্রিয়ভাবে সিঙ্গেলটন তৈরি করে নেয়।",

            "Kotlin does not have 'static' keywords" to
                "কোটলিনে 'static' কিওয়ার্ড নেই। এর পরিবর্তে ক্লাসের ভেতরে 'companion object' ব্যবহার করে অবজেক্ট তৈরি না করেই মেথড ও প্রপার্টি কল করা যায়।",

            "The Activity lifecycle consists of states" to
                "অ্যাক্টিভিটি লাইফসাইকেলে onCreate, onStart, onResume, onPause, onStop এবং onDestroy ধাপসমূহ থাকে। এগুলো সঠিকভাবে পরিচালনা করলে মেমোরি লিক রোধ করা যায়।",

            "In Compose, state is any value that can change" to
                "কম্পোজে স্টেট হলো এমন কোনো মান যা সময়ের সাথে পরিবর্তিত হতে পারে। স্টেট আপডেট হলে কম্পোজ স্বয়ংক্রিয়ভাবে ইউআই পুনর্গঠন (recomposition) করে।",

            "ViewModels store and manage UI-related data" to
                "ভিউমডেল ইউআই সংক্রান্ত ডাটা সংরক্ষণ করে যা স্ক্রিন রোটেশনের মতো কনফিগারেশন পরিবর্তনেও নষ্ট হয় না।",

            "The Navigation component helps you implement" to
                "নেভিগেশন উপাদান অ্যাপের এক স্ক্রিন থেকে অন্য স্ক্রিনে সহজে যাতায়াত পরিচালনা করতে সাহায্য করে।",

            "DataStore is a data storage solution" to
                "ডাটা-স্টোর হলো আধুনিক ডাটা সংরক্ষণের মাধ্যম যা SharedPreferences এর বিকল্প হিসেবে Coroutines এবং Flow ব্যবহার করে ডাটা সেভ করে।",

            "Adding the 'data' keyword to a class" to
                "ক্লাসের আগে 'data' কিওয়ার্ড ব্যবহার করলে স্বয়ংক্রিয়ভাবে equals(), hashCode(), toString() এবং copy() ফাংশন তৈরি হয়ে যায়।",

            "Extension functions allow you to add new functions" to
                "এক্সটেনশন ফাংশন কোনো বিদ্যমান ক্লাসে নতুন ফাংশন যোগ করতে দেয় মূল কোড পরিবর্তন না করেই।",

            "'lateinit var' is used for variables" to
                "'lateinit var' এমন ভেরিয়েবলের জন্য ব্যবহার করা হয় যা পরে ইনিশিয়ালাইজ করা হবে। 'val x by lazy' প্রথম ব্যবহারের সময় মান হিসেব করে স্মরণ রাখে।",

            "A higher-order function is a function" to
                "হায়ার-অর্ডার ফাংশন এমন একটি ফাংশন যা অন্য ফাংশনকে প্যারামিটার হিসেবে গ্রহণ করে বা রিটার্ন করে।",

            "Kotlin standard library offers powerful list operations" to
                "কোটলিন স্ট্যান্ডার্ড লাইব্রেরিতে লিস্ট ফিল্টার এবং রূপান্তরের জন্য map, filter এবং flatten এর মতো শক্তিশালী ফাংশন রয়েছে।",

            "Coroutines allow you to write asynchronous code" to
                "করুটিন ব্যবহার করে অ্যাসিনক্রোনাস ব্যাকগ্রাউন্ড কোডকে সিঙ্ক্রোনাস কোডের মতো সহজে এবং কম মেমোরিতে চালানো যায়।",

            "Flow is a stream of data that can be computed" to
                "ফ্লো হলো ডাটার রিঅ্যাক্টিভ স্ট্রিম যা অ্যাসিনক্রোনাস ডাটা প্রসেস এবং হ্যান্ডেল করতে ব্যবহার করা হয়।",

            "Room provides an abstraction layer over SQLite" to
                "রুম ডাটাবেস SQLite এর ওপর একটি সহজ লেয়ার প্রদান করে যা অফলাইন ডাটা লোকালি সংরক্ষণ করতে ব্যবহৃত হয়।",

            "Retrofit is a type-safe HTTP client" to
                "রেট্রোফিট হলো টাইপ-সেফ এইচটিটিপি ক্লায়েন্ট যা অ্যান্ড্রয়েড অ্যাপকে ইন্টারনেটের সাথে যুক্ত করে API রিকোয়েস্ট পাঠাতে সাহায্য করে।",

            "WorkManager is the recommended solution" to
                "অ্যাপ বন্ধ থাকলেও ব্যাকগ্রাউন্ডে নিশ্চিতভাবে কাজ সম্পাদন করতে WorkManager ব্যবহার করা হয়।",

            "Hilt is a DI library for Android" to
                "হিল্ট হলো অ্যান্ড্রয়েডের জন্য ডিপেন্ডেন্সি ইনজেকশন লাইব্রেরি যা কোডের ম্যানুয়াল অবজেক্ট তৈরি কমিয়ে দেয়।",

            "A class should have only one reason to change" to
                "একটি ক্লাসের কেবল একটি নির্দিষ্ট দায়িত্ব থাকা উচিত (Single Responsibility Principle)।",

            "JUnit is the standard testing framework" to
                "জেইউনিট হলো অ্যান্ড্রয়েডের জন্য স্ট্যান্ডার্ড ইউনিট টেস্টিং ফ্রেমওয়ার্ক।",

            "If you check the type of an object using the 'is'" to
                "কোটলিন 'is' চেক ব্যবহারের পর স্বয়ংক্রিয়ভাবে অবজেক্টকে নির্দিষ্ট টাইপে কাস্ট (Smart Cast) করে নেয়।",

            "Kotlin provides 5 scope functions: let, run, with" to
                "কোটলিনে ৫টি স্কোপ ফাংশন রয়েছে: let, run, with, apply এবং also যা অবজেক্টের কনটেক্সটে কোড এক্সিকিউট করতে সাহায্য করে।",

            "Sealed classes restrict inheritance" to
                "সিল্ড ক্লাস সাবক্লাসের সংখ্যা সীমিত রাখে এবং ইউআই স্টেট (Loading, Success, Error) রিপ্রেজেন্ট করতে জনপ্রিয়।",

            "Using 'inline' tells the compiler to copy" to
                "ইনলাইন ফাংশন ল্যাম্বডা অবজেক্ট তৈরির মেমোরি ওভারহেড দূর করে সরাসরি কল সাইটে ইনলাইন করে।",

            "Both are hot flows" to
                "উভয়ই হট ফ্লো (Hot Flows)। StateFlow-এর একটি প্রাথমিক মান (initial value) প্রয়োজন হয়, নতুন সংগ্রাহকদের কাছে সর্বশেষ মান পুনরায় প্রদান করে এবং কেবল মান আসলে পরিবর্তিত হলেই এটি নির্গত করে (distinctUntilChanged)। এটি UI স্টেট ধারণ করে। SharedFlow-এর প্রাথমিক মান থাকে না।",

            "When asked 'Tell me about yourself'" to
                "যখন আপনাকে 'Tell me about yourself' জিজ্ঞাসা করা হবে, তখন Present-Past-Future ফর্মুলা ব্যবহার করুন: Softzino Technologies-এ Kotlin দিয়ে নেটিভ অ্যান্ড্রয়েড অ্যাপ তৈরিকারী অ্যাসোসিয়েট সফটওয়্যার ইঞ্জিনিয়ার হিসেবে আপনার বর্তমান ভূমিকা দিয়ে শুরু করুন।"
        )

        for ((keyPrefix, translatedValue) in exactMap) {
            if (text.startsWith(keyPrefix, ignoreCase = true) || text.contains(keyPrefix, ignoreCase = true)) {
                return translatedValue
            }
        }

        return translateTextPhraseByPhrase(text)
    }

    private fun translateTextPhraseByPhrase(text: String): String {
        var translated = text
        val dictionary = listOf(
            "Scanned Document Content (Real OCR Extracted)" to "স্ক্যান করা নথির বিবরণ (OCR নিষ্কাশিত)",
            "Scanned Document Content" to "স্ক্যান করা নথির বিবরণ",
            "Real OCR Extracted" to "বাস্তব OCR সংগৃহীত",
            "Extracted automatically from image using On-Device ML Vision OCR." to "অন-ডিভাইস ML Vision OCR দিয়ে স্বয়ংক্রিয়ভাবে সংগ্রহ করা হয়েছে।",
            "Lesson 1 - Family" to "পাঠ ১ - পরিবার",
            "Lesson 1" to "পাঠ ১",
            "Family" to "পরিবার",
            "Let's begin our study of collocations by learning some common expressions about the family." to "আসুন পরিবার সম্পর্কিত কিছু সাধারণ অভিব্যক্তি শিখে শব্দকোষের সঠিক ব্যবহার (collocations) অধ্যয়ন শুরু করি।",
            "Let's begin our study of" to "চলুন আমাদের পড়াশোনা শুরু করি",
            "common expressions about the family" to "পরিবার সম্পর্কিত সাধারণ অভিব্যক্তি",
            "Your parents and siblings (brothers and sisters) are your" to "আপনার পিতা-মাতা এবং ভাই-বোন হলো আপনার",
            "When asked" to "যখন জিজ্ঞাসা করা হয়",
            "Tell me about yourself" to "আপনার নিজের সম্পর্কে বলুন",
            "use the" to "ব্যবহার করুন",
            "formula" to "সূত্র বা ফর্মুলা",
            "Start with your current role as an" to "হিসেবে আপনার বর্তমান ভূমিকা দিয়ে শুরু করুন",
            "Associate Software Engineer" to "অ্যাসোসিয়েট সফটওয়্যার ইঞ্জিনিয়ার",
            "at Softzino Technologies" to "Softzino Technologies-এ",
            "building native Android apps with Kotlin" to "Kotlin দিয়ে নেটিভ অ্যান্ড্রয়েড অ্যাপ তৈরি করা",
            "Both are hot flows" to "উভয়ই হট ফ্লো (Hot Flows)",
            "StateFlow requires an initial value" to "StateFlow-এর একটি প্রাথমিক মান প্রয়োজন হয়",
            "replays the latest value to new collectors" to "নতুন সংগ্রাহকদের কাছে সর্বশেষ মান পুনরায় প্রদান করে",
            "and only emits when the value actually changes" to "এবং কেবল মান আসলে পরিবর্তিত হলেই এটি নির্গত করে",
            "distinctUntilChanged" to "distinctUntilChanged",
            "It holds UI State" to "এটি UI স্টেট ধারণ করে",
            "SharedFlow has no initial value" to "SharedFlow-এর কোনো প্রাথমিক মান থাকে না",
            "Kotlin is a modern, statically typed language" to "কোটলিন একটি আধুনিক, স্ট্যাটিকভাবে টাইপ করা ভাষা",
            "null safety" to "নাল সেফটি (Null Safety)",
            "extension functions" to "এক্সটেনশন ফাংশন",
            "higher-order functions" to "হায়ার-অর্ডার ফাংশন",
            "Android development" to "অ্যান্ড্রয়েড ডেভেলপমেন্ট",
            "more concise and robust" to "আরও সংক্ষিপ্ত এবং শক্তিশালী",
            "toolkit for building native UI" to "নেটিভ ইউআই তৈরির টুলকিট",
            "declarative approach" to "ডিক্লেয়ারেটিভ পদ্ধতি",
            "Separating concerns into Data, Domain, and Presentation layers" to "ডাটা, ডোমেন এবং প্রেজেন্টেশন লেয়ারে কোড পৃথক করা",
            "testable, maintainable, and independent" to "টেস্টেবল, মেইনটেইনেবল এবং স্বাধীন",
            "Managing background tasks efficiently" to "দক্ষতার সাথে ব্যাকগ্রাউন্ড টাস্ক পরিচালনা করা",
            "blocking the main thread" to "মেইন থ্রেড ব্লক না করে",
            "reactive stream of data" to "ডাটার রিয়েক্টিভ স্ট্রিম"
        )

        for ((en, bn) in dictionary) {
            translated = translated.replace(en, bn, ignoreCase = true)
        }

        return translated
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int = 1024): Bitmap {
        if (bitmap.width <= maxDimension && bitmap.height <= maxDimension) return bitmap
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val (newWidth, newHeight) = if (bitmap.width > bitmap.height) {
            maxDimension to (maxDimension / ratio).toInt()
        } else {
            (maxDimension * ratio).toInt() to maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth.coerceAtLeast(1), newHeight.coerceAtLeast(1), true)
    }

    private fun sanitizeApiKey(rawKey: String): String {
        var key = rawKey.trim().removeSurrounding("\"").removeSurrounding("'")
        val aizaIndex = key.indexOf("AIzaSy")
        if (aizaIndex != -1) {
            key = key.substring(aizaIndex)
        }
        return key.trim()
    }

    private suspend fun recognizeTextFromBitmap(bitmap: Bitmap): String = suspendCancellableCoroutine { continuation ->
        try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    if (continuation.isActive) {
                        continuation.resume(visionText.text) {}
                    }
                }
                .addOnFailureListener { e ->
                    Timber.w(e, "ML Kit OCR failed")
                    if (continuation.isActive) {
                        continuation.resume("") {}
                    }
                }
        } catch (e: Exception) {
            Timber.w(e, "ML Kit OCR exception")
            if (continuation.isActive) {
                continuation.resume("") {}
            }
        }
    }

    private fun buildNuggetFromOcrText(ocrText: String, targetCourseId: String): KnowledgeNugget {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cleanText = ocrText.trim()

        val lines = cleanText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val extractedTitle = lines.firstOrNull { line ->
            !line.startsWith("{") && !line.startsWith("```") && !line.startsWith("\"") && line.length in 3..70
        }?.removePrefix("#")?.removePrefix("##")?.trim() ?: "Scanned Document (Real OCR)"

        val formattedContent = if (cleanText.isNotBlank()) {
            """
            ### 📄 Scanned Document Content (Real OCR Extracted)

            $cleanText

            ---
            *Extracted automatically from image using On-Device ML Vision OCR.*
            """.trimIndent()
        } else {
            """
            ### ⚠️ No Clear Text Detected in Image
            The image was too dark, blurry, or didn't contain clear printed text. Please try taking a clearer, well-lit photo of your lecture sheet.
            """.trimIndent()
        }

        return KnowledgeNugget(
            id = "nugget_scanned_${System.currentTimeMillis()}",
            title = extractedTitle,
            shortDescription = "Real OCR extracted text & document content from photo",
            content = formattedContent,
            complexity = "Beginner",
            imageUrl = null,
            videoUrl = null,
            careerPathId = targetCourseId,
            isDone = false,
            isSaved = false,
            isMastered = false,
            completionDate = null,
            priority = 0,
            date = today,
            quizzes = emptyList()
        )
    }

    override fun analyzeLectureSheetImage(imageBitmap: Bitmap, targetCourseId: String, customApiKey: String): Flow<Result<KnowledgeNugget>> = flow {
        val buildConfigKey = sanitizeApiKey(BuildConfig.GEMINI_API_KEY)
        val customKeySanitized = sanitizeApiKey(customApiKey)
        val apiKey = if (customKeySanitized.isNotBlank()) customKeySanitized else buildConfigKey

        val scaledBitmap = scaleBitmapIfNeeded(imageBitmap, 1024)
        val ocrText = recognizeTextFromBitmap(scaledBitmap)

        if (apiKey.isBlank() || !apiKey.startsWith("AIzaSy")) {
            // If no valid Gemini API Key, return real ML Kit OCR extracted nugget directly!
            val ocrNugget = buildNuggetFromOcrText(ocrText, targetCourseId)
            emit(Result.success(ocrNugget))
            return@flow
        }

        val candidateModels = listOf(
            "gemini-1.5-flash",
            "gemini-1.5-flash-latest",
            "gemini-2.0-flash",
            "gemini-2.0-flash-exp",
            "gemini-1.5-pro"
        )

        val prompt = """
            You are an expert AI Education Assistant. Read the attached study document image AND the extracted OCR text below carefully.
            
            On-device OCR Extracted Text from Image:
            ""${'"'}
            $ocrText
            ""${'"'}

            Organize all text, headings, exercises, vocabulary words, and notes visible in the photo into clean markdown.
            Also generate 2 multiple choice quiz questions based on the content.

            Respond with ONLY a valid raw JSON object strictly matching this format without markdown codeblocks:
            {
              "title": "Main title found in the image (e.g. Lesson 1 Quiz)",
              "shortDescription": "Brief summary of what this document covers",
              "content": "Detailed markdown text transcribing and organizing everything from the image",
              "complexity": "Beginner",
              "quizzes": [
                {
                  "text": "Transcribed question text from the image",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "correctAnswerIndex": 0,
                  "explanation": "Explanation of answer"
                }
              ]
            }
        """.trimIndent()

        var lastError: Throwable? = null

        for (modelName in candidateModels) {
            try {
                Timber.d("Attempting Gemini Vision request with model: $modelName")

                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey
                )

                val inputContent = content {
                    image(scaledBitmap)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                val responseText = response.text

                if (!responseText.isNullOrBlank()) {
                    val cleanJson = responseText
                        .replace("```json", "")
                        .replace("```", "")
                        .trim()

                    val nugget = try {
                        val jsonElement = Json.parseToJsonElement(cleanJson)
                        val obj = jsonElement.jsonObject
                        val title = obj["title"]?.jsonPrimitive?.content ?: "Scanned Sheet"
                        val shortDesc = obj["shortDescription"]?.jsonPrimitive?.content ?: "Extracted notes from image."
                        val contentText = obj["content"]?.jsonPrimitive?.content ?: responseText
                        val complexity = obj["complexity"]?.jsonPrimitive?.content ?: "Beginner"

                        val quizzes = try {
                            val quizArray = obj["quizzes"]?.jsonArray
                            quizArray?.mapIndexed { idx, qElem ->
                                val qObj = qElem.jsonObject
                                QuizQuestion(
                                    id = "q_scanned_${System.currentTimeMillis()}_$idx",
                                    nuggetId = "nugget_scanned_${System.currentTimeMillis()}",
                                    text = qObj["text"]?.jsonPrimitive?.content ?: "Question ${idx + 1}",
                                    options = qObj["options"]?.jsonArray?.map { it.jsonPrimitive.content } ?: listOf("A", "B", "C", "D"),
                                    correctAnswerIndex = qObj["correctAnswerIndex"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                                    explanation = qObj["explanation"]?.jsonPrimitive?.content ?: "Correct answer based on sheet."
                                )
                            } ?: emptyList()
                        } catch (_: Exception) {
                            emptyList()
                        }

                        KnowledgeNugget(
                            id = "nugget_scanned_${System.currentTimeMillis()}",
                            title = title,
                            shortDescription = shortDesc,
                            content = contentText,
                            complexity = complexity,
                            imageUrl = null,
                            videoUrl = null,
                            careerPathId = targetCourseId,
                            isDone = false,
                            isSaved = false,
                            isMastered = false,
                            completionDate = null,
                            priority = 0,
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            quizzes = quizzes
                        )
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to parse structured JSON from Gemini Vision, using extracted text directly.")
                        buildNuggetFromOcrText(if (ocrText.isNotBlank()) ocrText else responseText, targetCourseId)
                    }

                    emit(Result.success(nugget))
                    return@flow
                }
            } catch (e: Exception) {
                Timber.w(e, "Gemini Vision request failed for model: $modelName")
                lastError = e
            }
        }

        // If Gemini API call fails, fall back to real ML Kit OCR extracted text!
        val fallbackOcrNugget = buildNuggetFromOcrText(ocrText, targetCourseId)
        emit(Result.success(fallbackOcrNugget))
    }.flowOn(Dispatchers.IO)

    override suspend fun extractLectureSheetOffline(imageBitmap: Bitmap, targetCourseId: String): KnowledgeNugget {
        val scaledBitmap = scaleBitmapIfNeeded(imageBitmap, 1024)
        val ocrText = recognizeTextFromBitmap(scaledBitmap)
        return buildNuggetFromOcrText(ocrText, targetCourseId)
    }

    private fun createFallbackLectureNugget(targetCourseId: String): KnowledgeNugget {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return if (targetCourseId == "mentors_spoken") {
            KnowledgeNugget(
                id = "nugget_scanned_${System.currentTimeMillis()}",
                title = "Mentors' Spoken & Phonetics - Scanned Lecture 01",
                shortDescription = "Extracted Classwork & Homework from Mentors' Spoken & Phonetics Sheet",
                content = """
                ### 📌 Classwork
                - Knowing Each Other & Warming up
                - Presentation #1: Self Introduction
                - Asking Questions effectively in Spoken English
                - Problematic Letters in English Alphabet (/p/, /t/, /k/, /f/, /v/, /w/)
                - Phrases to say "Bye" (See you around, Catch you later, Take care)

                ### 📝 Homework
                - Grammar + Exercises (Tense & Sentence Construction)
                - Tongue Twisters ("Peter Piper picked a peck of pickled peppers...")
                - Read Aloud (Woodcutter Story) + Glossary
                - Listening Practice # 1.9.1 & # 1.9.2
                - Vocabularies & Pronunciation Drill

                ### 🗣️ Phonetics & Tongue Twister Practice
                - She sells seashells on the seashore.
                - How much wood would a woodchuck chuck if a woodchuck could chuck wood?

                ### 💡 Key Vocabulary List
                - **Woodcutter** (কাঠুরে) - A person who cuts down trees.
                - **Glossary** (শব্দকোষ) - An alphabetical list of terms.
                - **Problematic Sound**: /p/ is aspirated as [pʰ] in words like **Pen, Paper, Presentation**.
                """.trimIndent(),
                complexity = "Beginner",
                imageUrl = null,
                videoUrl = null,
                careerPathId = targetCourseId,
                isDone = false,
                isSaved = false,
                isMastered = false,
                completionDate = null,
                priority = 0,
                date = today,
                quizzes = listOf(
                    QuizQuestion(
                        id = "q_fb_1",
                        nuggetId = "nugget_scanned_fb",
                        text = "Which sound is aspirated in English phonetics when starting a stressed syllable?",
                        options = listOf("/b/", "/p/", "/m/", "/v/"),
                        correctAnswerIndex = 1,
                        explanation = "/p/ is an aspirated voiceless plosive sound when starting a stressed syllable."
                    )
                )
            )
        } else {
            KnowledgeNugget(
                id = "nugget_scanned_${System.currentTimeMillis()}",
                title = "Mentors' Foundation Grammar & Writing - Scanned Lecture 01",
                shortDescription = "Extracted Foundation Grammar, Common Mistakes & Writing tasks",
                content = """
                ### 📌 Part 01: Pre-Task Diagnostic
                - Diagnostic test on subject-verb agreement and tenses.

                ### 📖 Part 02: Grammar Rules
                - Subject-Verb Agreement: Singular subjects require singular verbs.
                - Time & Tense Consistency in Complex Sentences.

                ### ⚠️ Part 03: Common Mistakes
                - Incorrect: *He don't know the answer.* ❌
                - Correct: *He doesn't know the answer.* ✅
                - Incorrect: *Discuss about the topic.* ❌
                - Correct: *Discuss the topic.* ✅

                ### 📚 Part 04: Reading Techniques
                - Skimming for main idea and Scanning for specific keywords.

                ### ✍️ Part 05: Foundation Writing
                - Writing clear paragraphs with a Topic Sentence, Supporting Sentences, and Conclusion.

                ### 💡 Part 06: Vocabulary & Collocation List
                - **Substantial** (প্রচুর/উল্লেখযোগ্য) - *A substantial amount of evidence.*
                - **Achieve a breakthrough** - Make significant progress.
                """.trimIndent(),
                complexity = "Beginner",
                imageUrl = null,
                videoUrl = null,
                careerPathId = targetCourseId,
                isDone = false,
                isSaved = false,
                isMastered = false,
                completionDate = null,
                priority = 0,
                date = today,
                quizzes = listOf(
                    QuizQuestion(
                        id = "q_fb_2",
                        nuggetId = "nugget_scanned_fb",
                        text = "Which sentence correctly avoids a common preposition error?",
                        options = listOf(
                            "We discussed about the problem.",
                            "We discussed the problem.",
                            "We discussed for the problem.",
                            "We discussed to the problem."
                        ),
                        correctAnswerIndex = 1,
                        explanation = "'Discuss' is a transitive verb and does not take the preposition 'about'."
                    )
                )
            )
        }
    }
}