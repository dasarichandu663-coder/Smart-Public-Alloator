package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Language
import com.example.data.model.ServiceRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiServiceResponse(
    val explanationText: String,
    val matchedServiceId: String?,
    val isFollowUpNeeded: Boolean,
    val followUpQuestion: String?,
    val isFromGeminiAi: Boolean,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun queryGovernmentService(
        userQuery: String,
        language: Language,
        availableServices: List<ServiceRecord>
    ): AiServiceResponse = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        // If no API key or invalid placeholder, smoothly use classical local knowledge base engine
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiService", "API key missing or placeholder. Using local classical engine.")
            return@withContext runClassicalServiceMatch(userQuery, language, availableServices, isAiUnavailable = true)
        }

        try {
            // Build grounded context from controlled verified knowledge base
            val controlledCatalogText = buildString {
                append("CONTROLLED GOVERNMENT SERVICE KNOWLEDGE BASE (GROUND TRUTH):\n")
                availableServices.forEach { s ->
                    append("- ID: ${s.id}\n")
                    append("  Name: ${s.nameEn} / ${s.nameTe}\n")
                    append("  Category: ${s.category.displayNameEn}\n")
                    append("  Department: ${s.departmentEn} (${s.departmentTe})\n")
                    append("  Authority: ${s.issuingAuthorityEn}\n")
                    append("  Official Portal URL: ${s.officialPortalUrl}\n")
                    append("  Official Info URL: ${s.officialInfoUrl}\n")
                    append("  Verified Status: ${if (s.isVerified) "Verified (Official Source: ${s.officialVerificationSource})" else "Demo - verification required"}\n")
                    append("  Required Documents: ${s.requiredDocuments.joinToString { it.nameEn }}\n")
                    append("  Next Steps: ${s.applicationStepsEn.take(3).joinToString(" -> ")}\n\n")
                }
            }

            val systemInstruction = """
You are the AI Citizen Service Guide for the Smart Public Resource Allocator (SEVA First Innovation Challenge).
Your role is to guide citizens in Andhra Pradesh and India to identify the right public department, verified requirements, and official portals.

CRITICAL RULES:
1. ONLY reference official government URLs and document requirements from the controlled catalog provided below. NEVER invent, hallucinate, or alter any URL.
2. If the user's request is ambiguous or missing state/details, ask a clarifying follow-up question.
3. Answer in ${if (language == Language.TE) "Telugu (తెలుగు)" else "English"}.
4. Provide a clear, respectful summary: (a) Service & Department, (b) Key next step, (c) Verified document checklist overview, (d) Disclaimer note.
5. If the requested service is not in the controlled catalog, explain that it needs official confirmation and direct them to the national portal: https://services.india.gov.in.

Respond in JSON format with this structure:
{
  "matchedServiceId": "string or null",
  "explanation": "concise guidance in ${if (language == Language.TE) "Telugu" else "English"}",
  "isFollowUpNeeded": boolean,
  "followUpQuestion": "string or null"
}
""".trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "$controlledCatalogText\n\nUser Question: $userQuery\nPreferred Language: ${language.displayName}")
                            })
                        }
                        put("parts", partsArray)
                    })
                }
                put("contents", contentsArray)

                put("systemInstruction", JSONObject().apply {
                    val sysParts = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", sysParts)
                })

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errCode = response.code
                Log.w("GeminiService", "Gemini HTTP $errCode: ${response.message}. Falling back to classical engine.")
                return@withContext runClassicalServiceMatch(userQuery, language, availableServices, isAiUnavailable = true)
            }

            val responseBody = response.body?.string() ?: ""
            val jsonRoot = JSONObject(responseBody)
            val candidates = jsonRoot.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (text.isNotBlank()) {
                val parsedAi = JSONObject(text)
                val matchedId = if (parsedAi.isNull("matchedServiceId")) null else parsedAi.optString("matchedServiceId")
                val explanation = parsedAi.optString("explanation", "")
                val isFollowUp = parsedAi.optBoolean("isFollowUpNeeded", false)
                val followUp = if (parsedAi.isNull("followUpQuestion")) null else parsedAi.optString("followUpQuestion")

                return@withContext AiServiceResponse(
                    explanationText = explanation,
                    matchedServiceId = if (matchedId?.isNotEmpty() == true) matchedId else null,
                    isFollowUpNeeded = isFollowUp,
                    followUpQuestion = followUp,
                    isFromGeminiAi = true
                )
            }

            return@withContext runClassicalServiceMatch(userQuery, language, availableServices, isAiUnavailable = false)
        } catch (e: Exception) {
            Log.e("GeminiService", "Gemini API exception", e)
            return@withContext runClassicalServiceMatch(userQuery, language, availableServices, isAiUnavailable = true)
        }
    }

    private fun runClassicalServiceMatch(
        query: String,
        language: Language,
        availableServices: List<ServiceRecord>,
        isAiUnavailable: Boolean
    ): AiServiceResponse {
        val q = query.trim().lowercase()

        // Ambiguity check
        if (q.length < 3 || q == "certificate" || q == "patram" || q == "seva") {
            return if (language == Language.TE) {
                AiServiceResponse(
                    explanationText = "మీరు ఏ ధృవీకరణ పత్రం (ఆదాయం, కులం, లేదా జనన సర్టిఫికెట్) కోసం చూస్తున్నారో దయచేసి స్పష్టంగా పేర్కొనండి.",
                    matchedServiceId = null,
                    isFollowUpNeeded = true,
                    followUpQuestion = "మీరు ఏ ధృవీకరణ పత్రం కోసం చూస్తున్నారు? (ఉదా: ఆదాయ ధృవీకరణ, కుల ధృవీకరణ, జనన ధృవీకరణ)",
                    isFromGeminiAi = !isAiUnavailable
                )
            } else {
                AiServiceResponse(
                    explanationText = "Please specify which certificate or service you are seeking (e.g. Income, Caste, or Birth Certificate) so we can guide you to the exact department.",
                    matchedServiceId = null,
                    isFollowUpNeeded = true,
                    followUpQuestion = "Which specific service or certificate do you need? (e.g., Income Certificate, Caste Certificate, Birth Certificate)",
                    isFromGeminiAi = !isAiUnavailable
                )
            }
        }

        // Direct matching against controlled records
        val matched = availableServices.firstOrNull { s ->
            s.nameEn.lowercase().contains(q) ||
                    s.nameTe.lowercase().contains(q) ||
                    s.altNames.any { it.lowercase().contains(q) } ||
                    q.contains(s.nameEn.lowercase()) ||
                    (q.contains("income") && s.id == "ap_income_cert") ||
                    (q.contains("caste") && s.id == "ap_caste_cert") ||
                    (q.contains("birth") && s.id == "ap_birth_cert") ||
                    (q.contains("ration") && s.id == "ap_rice_card") ||
                    (q.contains("driving") && s.id == "ap_driving_llr") ||
                    (q.contains("solar") && s.id == "demo_solar_subsidy")
        }

        if (matched != null) {
            val explanation = if (language == Language.TE) {
                "మీ అభ్యర్థనకు సరిపోలే అధికారిక సేవ: '${matched.nameTe}'. ఇది ఆంధ్రప్రదేశ్ ${matched.departmentTe} పరిధిలోని ${matched.issuingAuthorityTe} ద్వారా జారీ చేయబడుతుంది. అధికారిక పోర్టల్: ${matched.portalName} (${matched.officialPortalUrl})."
            } else {
                "Found verified service: '${matched.nameEn}'. Responsible department: ${matched.departmentEn}, issued under ${matched.issuingAuthorityEn}. Apply via ${matched.portalName}."
            }
            return AiServiceResponse(
                explanationText = explanation,
                matchedServiceId = matched.id,
                isFollowUpNeeded = false,
                followUpQuestion = null,
                isFromGeminiAi = !isAiUnavailable
            )
        }

        // Not indexed service guidance
        val unindexedExplanation = if (language == Language.TE) {
            "మీరు అడిగిన సేవ మా ధృవీకరించబడిన డేటాబేస్‌లో ప్రస్తుతం గుర్తించబడలేదు. తప్పుడు అధికారిక లింకులు లేదా పత్రాలు చూపించకుండా నిరోధించడానికి, దయచేసి జాతీయ సేవల పోర్టల్ (https://services.india.gov.in) లేదా మీ మండల తహసీల్దార్ కార్యాలయాన్ని సంప్రదించండి."
        } else {
            "The requested service is not yet indexed in our verified database. To prevent hallucination of government URLs, please visit the official National Services Portal (https://services.india.gov.in) or your local MeeSeva Kendra for authoritative guidance."
        }

        return AiServiceResponse(
            explanationText = unindexedExplanation,
            matchedServiceId = null,
            isFollowUpNeeded = true,
            followUpQuestion = if (language == Language.TE) "మీరు ఈ సేవ ఏ శాఖకు సంబంధించినదో తెలుపగలరా?" else "Could you specify which state or department handles this service?",
            isFromGeminiAi = !isAiUnavailable
        )
    }
}
