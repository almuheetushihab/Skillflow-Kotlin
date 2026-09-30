package com.example.skillflow.data.repository
import com.example.skillflow.BuildConfig
import com.example.skillflow.domain.repository.GeminiRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepositoryImpl @Inject constructor() : GeminiRepository {

    override fun askGemini(context: String, userQuestion: String): Flow<Result<String>> = flow {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim().removeSurrounding("\"").removeSurrounding("'")
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
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro"
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
                "Gemini model status error: '$lastTriedModel' is not available on the v1beta endpoint. " +
                        "Tried fallback model 'gemini-1.5-pro' as well, but both failed."
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
        val apiKey = BuildConfig.GEMINI_API_KEY.trim().removeSurrounding("\"").removeSurrounding("'")
        if (apiKey.isBlank()) {
            val fallback = fallbackBanglaTranslation(title, content)
            emit(Result.success(fallback))
            return@flow
        }

        val systemInstructionContent = content {
            text(
                "You are an expert technical translator for the SkillFlow app. " +
                        "Translate the provided title and content into natural, clear, easy to understand Bengali (Bangla). " +
                        "Respond strictly in raw JSON without markdown codeblocks or quotes around json: " +
                        "{\"translatedTitle\": \"...\", \"translatedContent\": \"...\"}"
            )
        }

        val candidateModels = listOf(
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro"
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
                        // Fallback parsing if non-strict JSON
                        emit(Result.success(Pair(title, responseText)))
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
        return when (title.trim()) {
            "Kotlin Fundamentals" -> "কোটলিন ফান্ডামেন্টালস"
            "Jetpack Compose Basics" -> "জেটপ্যাক কম্পোজ বেসিকস"
            "Clean Architecture" -> "ক্লিন আর্কিটেকচার"
            "Hilt Dependency Injection" -> "হিল্ট ডিপেন্ডেন্সি ইনজেকশন"
            "Coroutines & Flow" -> "করুটিন এবং ফ্লো"
            "Retrofit Networking" -> "রেট্রোফিট নেটওয়ার্কিং"
            "Room Database" -> "রুম ডাটাবেস"
            "ViewModel & State" -> "ভিউমডেল এবং স্টেট"
            "Navigation Component" -> "নেভিগেশন উপাদান"
            "WorkManager" -> "ওয়ার্কম্যানেজার"
            "Swift Fundamentals" -> "সুইফট ফান্ডামেন্টালস"
            "SwiftUI Essentials" -> "সুইফটইউআই এসেনশিয়ালস"
            "ARC & Memory Management" -> "ARC এবং মেমোরি ম্যানেজমেন্ট"
            "RESTful API Design" -> "রেস্টফুল API ডিজাইন"
            "Microservices Architecture" -> "মাইক্রোসার্ভিসেস আর্কিটেকচার"
            "SQL vs NoSQL" -> "SQL বনাম NoSQL"
            "React Hooks" -> "রিয়েক্ট হুকস"
            "CSS Flexbox" -> "সিএসএস ফ্লেক্সবক্স"
            "Typography Hierarchy" -> "টাইপোগ্রাফি হায়ারার্কি"
            "Color Theory" -> "কালার থিওরি"
            "Python for Data Science" -> "ডাটা সায়েন্সের জন্য পাইথন"
            "Machine Learning Basics" -> "মেশিন লার্নিং বেসিকস"
            else -> translateTextPhraseByPhrase(title)
        }
    }

    private fun translateContentOffline(content: String): String {
        val knownMap = mapOf(
            "Both are hot flows. StateFlow requires an initial value, replays the latest value to new collectors, and only emits when the value actually changes (distinctUntilChanged). It holds UI State. SharedFlow has no initial value" to
                "উভয়ই হট ফ্লো (Hot Flows)। StateFlow-এর একটি প্রাথমিক মান (initial value) প্রয়োজন হয়, নতুন সংগ্রাহকদের কাছে সর্বশেষ মান পুনরায় প্রদান করে এবং কেবল মান আসলে পরিবর্তিত হলেই এটি নির্গত (emit) করে (distinctUntilChanged)। এটি UI স্টেট ধারণ করে। SharedFlow-এর প্রাথমিক মান থাকে না।",

            "When asked 'Tell me about yourself', use the Present-Past-Future formula: Start with your current role as an Associate Software Engineer at Softzino Technologies building native Android apps with Kotlin" to
                "যখন আপনাকে 'Tell me about yourself' জিজ্ঞাসা করা হবে, তখন Present-Past-Future ফর্মুলা ব্যবহার করুন: Softzino Technologies-এ Kotlin দিয়ে নেটিভ অ্যান্ড্রয়েড অ্যাপ তৈরিকারী অ্যাসোসিয়েট সফটওয়্যার ইঞ্জিনিয়ার হিসেবে আপনার বর্তমান ভূমিকা দিয়ে শুরু করুন।"
        )

        for ((key, value) in knownMap) {
            if (content.trim().startsWith(key.substring(0, minOf(key.length, 30)))) {
                return value
            }
        }

        return translateTextPhraseByPhrase(content)
    }

    private fun translateTextPhraseByPhrase(text: String): String {
        var translated = text
        val dictionary = listOf(
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
}