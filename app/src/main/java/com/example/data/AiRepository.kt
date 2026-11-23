package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

interface AiRepository {
    suspend fun generateWebsite(input: WebsiteFormInput): GeneratedWebsite
    suspend fun generateHtmlGame(idea: String): GeneratedWebsite
    suspend fun analyzeUrl(rawUrl: String, corporateMode: Boolean = false): UrlAnalysisResult
    suspend fun askToolAssistant(tool: AiTool, question: String): ToolAssistantResponse
    suspend fun generateCommercialCopy(
        channel: CommercialChannel,
        topic: String,
        businessContext: String
    ): CommercialCopyResult
    suspend fun askUniversalQuestion(query: String): String
}

/**
 * Cliente HTTP gratuito sem chave de API utilizando Pollinations AI:
 * https://text.pollinations.ai/{encodeURIComponent(prompt)}
 */
class PollinationsAiRepository : AiRepository {

    override suspend fun generateHtmlGame(idea: String): GeneratedWebsite {
        val cleanIdea = idea.trim().ifEmpty { "Jogo arcade espacial touch para celular" }
        val prompt = """
            Crie um jogo HTML5 completo e 100% jogável por toque em celulares Android em um único arquivo HTML com CSS e JS embutidos.
            Idioma: Português do Brasil. Ideia: "$cleanIdea".
            Visual dark mode (#0b0f19), azul neon (#00d2ff) e verde esmeralda (#10b981).
            Inclua controles touch grandes na tela, pontuação em tempo real e botões Iniciar e Reiniciar.
            Retorne APENAS o código HTML completo começando com <!DOCTYPE html> e terminando com </html>, sem markdown.
        """.trimIndent()

        val rawText = callPollinationsApi(prompt)
        val cleanedHtml = extractHtmlDocument(rawText)
            ?: throw IllegalStateException("A IA não retornou um código HTML5 válido para o jogo. Tente novamente.")

        val title = cleanIdea.take(36).replaceFirstChar { it.uppercase() }
        return GeneratedWebsite(
            companyName = "Jogo: $title",
            htmlCode = cleanedHtml,
            summary = "Jogo HTML5 interativo gerado gratuitamente com IA."
        )
    }

    override suspend fun askUniversalQuestion(query: String): String {
        val prompt = """
            Você é o Assistente Universal por Voz do app NEXUS AI 100 em português do Brasil.
            Responda de forma clara, objetiva e natural para leitura em voz alta (sem asteriscos ou markdown).
            Pergunta: "$query"
        """.trimIndent()

        return callPollinationsApi(prompt)
            .replace("```", "")
            .trim()
    }

    override suspend fun generateWebsite(input: WebsiteFormInput): GeneratedWebsite {
        val company = input.companyName.trim()
        val niche = input.niche.trim()
        val whatsapp = input.whatsapp.trim().ifEmpty { "Não informado" }
        val city = input.cityState.trim().ifEmpty { "Brasil" }
        val services = input.services.trim().ifEmpty { "Atendimento especializado e qualidade garantida" }

        val rawDigits = whatsapp.filter { it.isDigit() }
        val waLinkDigits = when {
            rawDigits.startsWith("55") && rawDigits.length >= 12 -> rawDigits
            rawDigits.length >= 10 -> "55$rawDigits"
            else -> "5511999999999"
        }

        val prompt = """
            Crie um site comercial responsivo (mobile-first) em um único arquivo HTML5 com CSS em <style>.
            Idioma: Português do Brasil.
            Empresa: $company | Ramo: $niche | Cidade: $city | WhatsApp: $whatsapp (link https://wa.me/$waLinkDigits) | Serviços: $services.
            Visual dark mode (#0b0f19) com azul neon (#00d2ff) e verde (#10b981), cabeçalho, hero com CTA WhatsApp, cards de serviços e rodapé.
            Não mencione valores ou preços. Retorne APENAS o código HTML completo de <!DOCTYPE html> até </html>, sem markdown.
        """.trimIndent()

        val rawText = callPollinationsApi(prompt)
        val cleanedHtml = extractHtmlDocument(rawText)
            ?: throw IllegalStateException("A IA não retornou um documento HTML válido. Tente novamente.")

        return GeneratedWebsite(
            companyName = company,
            htmlCode = cleanedHtml,
            summary = "Site gerado gratuitamente com IA para $company."
        )
    }

    override suspend fun analyzeUrl(rawUrl: String, corporateMode: Boolean): UrlAnalysisResult {
        val trimmed = rawUrl.trim()
        val payloadType = detectPayloadType(trimmed)
        val normalized = when (payloadType) {
            PayloadType.URL -> {
                if (trimmed.startsWith("http://", true) || trimmed.startsWith("https://", true)) {
                    trimmed
                } else {
                    "https://$trimmed"
                }
            }
            PayloadType.PIX_COPIA_E_COLA, PayloadType.BOLETO -> trimmed
        }

        val contextLabel = if (corporateMode) {
            "Segurança Corporativa e Prevenção a Fraudes Financeiras (${payloadType.label})"
        } else {
            "Antivírus de Links, Phishing e Golpes (${payloadType.label})"
        }

        val prompt = """
            Atue como especialista em cibersegurança ($contextLabel) em português do Brasil.
            Analise: "$normalized"
            Verifique phishing, domínio/HTTPS, estrutura EMV Pix (000201 / br.gov.bcb.pix) ou código de banco/tamanho de boleto (47/48 dígitos).
            Responda APENAS um JSON válido neste formato exato, sem texto extra:
            {"isSafe": true/false, "riskScore": 0-100, "explanation": "explicação detalhada", "indicators": ["sinal 1", "sinal 2", "sinal 3"]}
        """.trimIndent()

        val rawText = callPollinationsApi(prompt)
        val parsedJson = extractJsonObject(rawText)

        val isSafe: Boolean
        val riskScore: Int
        val explanation: String
        val indicators: List<String>

        if (parsedJson != null) {
            isSafe = parsedJson.optBoolean("isSafe", false)
            riskScore = parsedJson.optInt("riskScore", if (isSafe) 12 else 85).coerceIn(0, 100)
            explanation = parsedJson.optString("explanation", rawText.trim()).ifBlank { "Análise concluída." }
            val indicatorsJson = parsedJson.optJSONArray("indicators") ?: JSONArray()
            indicators = (0 until indicatorsJson.length()).mapNotNull { idx ->
                indicatorsJson.optString(idx).takeIf { it.isNotBlank() }
            }
        } else {
            val lower = rawText.lowercase()
            val suspiciousWords = listOf("golpe", "phishing", "suspeito", "perigo", "fraude", "falso", "risco alto", "crítico")
            isSafe = !suspiciousWords.any { lower.contains(it) } && (lower.contains("seguro") || lower.contains("confiável"))
            riskScore = if (isSafe) 15 else 82
            explanation = stripCodeFences(rawText).ifBlank { "Análise concluída." }
            indicators = listOf(
                "Tipo analisado: ${payloadType.label}",
                if (isSafe) "Nenhum sinal crítico de golpe identificado na análise." else "Atenção: sinais de risco detectados no conteúdo analisado."
            )
        }

        val badgeTitle = if (isSafe) {
            "Link Seguro"
        } else {
            "Risco Crítico / Golpe Detectado"
        }

        return UrlAnalysisResult(
            normalizedUrl = normalized,
            isSafe = isSafe,
            riskScore = riskScore,
            badgeTitle = badgeTitle,
            detailedExplanation = explanation,
            indicators = indicators
        )
    }

    override suspend fun askToolAssistant(tool: AiTool, question: String): ToolAssistantResponse {
        val cleanQuestion = question.trim().ifEmpty { "Como começar a usar esta ferramenta gratuitamente?" }

        val prompt = """
            Você é o Assistente do catálogo 100 Ferramentas IA em português do Brasil.
            Ferramenta: ${tool.name} (${tool.category.label}) - ${tool.description}
            Dúvida: "$cleanQuestion"
            Não mencione valores ou preços.
            Responda APENAS um JSON válido sem texto fora do JSON:
            {"guidance": "guia passo a passo prático", "readyPrompt": "um prompt pronto para copiar e usar na ferramenta"}
        """.trimIndent()

        val rawText = callPollinationsApi(prompt)
        val parsedJson = extractJsonObject(rawText)

        val guidance = parsedJson?.optString("guidance")?.takeIf { it.isNotBlank() }
            ?: stripCodeFences(rawText).ifBlank { "Não foi possível gerar o guia no momento." }
        val readyPrompt = parsedJson?.optString("readyPrompt").orEmpty()

        return ToolAssistantResponse(
            toolName = tool.name,
            guidance = guidance,
            readyPrompt = readyPrompt
        )
    }

    override suspend fun generateCommercialCopy(
        channel: CommercialChannel,
        topic: String,
        businessContext: String
    ): CommercialCopyResult {
        val cleanTopic = topic.trim()
        val cleanBiz = businessContext.trim().ifEmpty { "Empresa brasileira buscando novos clientes" }

        val channelRules = when (channel) {
            CommercialChannel.WHATSAPP ->
                "Mensagem direta para WhatsApp, parágrafos curtos e CTA claro."
            CommercialChannel.EMAIL ->
                "Proposta profissional por E-mail com saudação, benefícios e convite para conversa."
            CommercialChannel.ANUNCIOS ->
                "Anúncio de alta conversão com gancho forte, benefício claro e CTA."
        }

        val prompt = """
            Redator comercial no Brasil. Canal: ${channel.label}. $channelRules
            Empresa: $cleanBiz. Objetivo: "$cleanTopic".
            Escreva em português do Brasil sem inventar preços.
            Responda APENAS um JSON válido sem texto extra:
            {"title": "título ou assunto chamativo", "body": "texto completo pronto para enviar"}
        """.trimIndent()

        val rawText = callPollinationsApi(prompt)
        val parsedJson = extractJsonObject(rawText)

        val title = parsedJson?.optString("title")?.takeIf { it.isNotBlank() }
            ?: "Proposta Comercial (${channel.label})"
        val body = parsedJson?.optString("body")?.takeIf { it.isNotBlank() }
            ?: stripCodeFences(rawText)

        if (body.isBlank()) {
            throw IllegalStateException("A IA não retornou o texto comercial. Tente novamente.")
        }

        return CommercialCopyResult(
            channel = channel,
            title = title,
            body = body
        )
    }

    private enum class PayloadType(val label: String) {
        URL("URL / Link Web"),
        PIX_COPIA_E_COLA("Código Pix Copia e Cola"),
        BOLETO("Linha Digitável de Boleto")
    }

    private fun detectPayloadType(input: String): PayloadType {
        val compact = input.replace(Regex("[\\s.-]"), "")
        return when {
            input.startsWith("000201") || input.contains("br.gov.bcb.pix", ignoreCase = true) ->
                PayloadType.PIX_COPIA_E_COLA
            compact.length in 36..52 && compact.all { it.isDigit() } ->
                PayloadType.BOLETO
            else ->
                PayloadType.URL
        }
    }

    /**
     * Chama gratuitamente sem chave de API:
     * https://text.pollinations.ai/{encodeURIComponent(prompt)}
     */
    private suspend fun callPollinationsApi(prompt: String): String {
        return withTimeout(60_000L) {
            withContext(Dispatchers.IO) {
                val encodedPrompt = encodeURIComponent(prompt)
                val endpoint = "$POLLINATIONS_BASE_URL/$encodedPrompt"
                val url = URL(endpoint)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 20_000
                    readTimeout = 55_000
                    instanceFollowRedirects = true
                    setRequestProperty("Accept", "text/plain, application/json, */*")
                    setRequestProperty("User-Agent", "NexusAI100-Android/1.0")
                }

                try {
                    val statusCode = connection.responseCode
                    val stream = if (statusCode in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                    val responseBody = stream?.let { input ->
                        BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                            reader.readText()
                        }
                    }.orEmpty()

                    if (statusCode !in 200..299) {
                        Log.e(TAG, "Pollinations HTTP $statusCode: $responseBody")
                        throw IllegalStateException(
                            "Serviço de IA temporariamente indisponível (HTTP $statusCode). Tente novamente em instantes."
                        )
                    }

                    if (responseBody.isBlank()) {
                        throw IllegalStateException("A IA retornou uma resposta vazia. Tente novamente.")
                    }

                    responseBody
                } finally {
                    connection.disconnect()
                }
            }
        }
    }

    private fun encodeURIComponent(value: String): String {
        return URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
            .replace("%21", "!")
            .replace("%27", "'")
            .replace("%28", "(")
            .replace("%29", ")")
            .replace("%7E", "~")
    }

    private fun extractHtmlDocument(raw: String): String? {
        val stripped = stripCodeFences(raw)
        val doctypeIdx = stripped.indexOf("<!doctype", ignoreCase = true)
        val htmlStartIdx = stripped.indexOf("<html", ignoreCase = true)
        val startIdx = when {
            doctypeIdx >= 0 -> doctypeIdx
            htmlStartIdx >= 0 -> htmlStartIdx
            else -> return null
        }
        val endTagIdx = stripped.lastIndexOf("</html>", ignoreCase = true)
        return if (endTagIdx >= startIdx) {
            stripped.substring(startIdx, endTagIdx + "</html>".length).trim()
        } else {
            stripped.substring(startIdx).trim()
        }
    }

    private fun extractJsonObject(raw: String): JSONObject? {
        val stripped = stripCodeFences(raw)
        runCatching { return JSONObject(stripped) }
        val firstBrace = stripped.indexOf('{')
        val lastBrace = stripped.lastIndexOf('}')
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            val slice = stripped.substring(firstBrace, lastBrace + 1)
            return runCatching { JSONObject(slice) }.getOrNull()
        }
        return null
    }

    private fun stripCodeFences(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```")) {
            val firstNewline = text.indexOf('\n')
            if (firstNewline != -1) {
                text = text.substring(firstNewline + 1)
            }
            if (text.endsWith("```")) {
                text = text.substring(0, text.length - 3)
            }
        }
        return text.trim()
    }

    companion object {
        private const val TAG = "PollinationsAiRepo"
        private const val POLLINATIONS_BASE_URL = "https://text.pollinations.ai"
    }
}
