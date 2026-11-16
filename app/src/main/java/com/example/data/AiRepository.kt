package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MissingApiKeyException : IllegalStateException(
    "Chave da API do Gemini não configurada. Toque no ícone de chave no topo para inserir sua chave (AIzaSy...)."
)

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
 * Cliente HTTP direto (HttpURLConnection) para a API REST do Gemini 2.5 Flash,
 * utilizando exclusivamente a chave salva localmente pelo usuário em SharedPreferences.
 */
class GeminiRestAiRepository(
    private val prefsStore: LocalPreferencesStore
) : AiRepository {

    override suspend fun generateHtmlGame(idea: String): GeneratedWebsite {
        val cleanIdea = idea.trim().ifEmpty { "Jogo arcade espacial touch para celular" }
        val prompt = """
            Crie um jogo HTML5 completo, divertido e 100% jogável por toque (touchscreen) em celulares Android dentro de um único arquivo HTML5 com CSS e JavaScript embutidos.
            Idioma obrigatório da interface do jogo: Português do Brasil (pt-BR).
            Ideia do jogo: "$cleanIdea"

            Requisitos técnicos:
            1. Visual dark mode (#0b0f19) com destaques em azul neon (#00d2ff) e verde esmeralda (#10b981).
            2. Controles por toque grandes e responsivos na tela (sem depender de teclado físico), pontuação em tempo real, botão Iniciar e Reiniciar.
            3. NÃO mencione valores ou cobranças.
            4. Retorne APENAS o código HTML completo começando com <!DOCTYPE html> e terminando com </html>, sem markdown.
        """.trimIndent()

        val rawText = callGeminiApi(
            prompt = prompt,
            responseMimeType = "text/plain",
            responseSchema = null,
            temperature = 0.75
        )
        val cleanedHtml = stripCodeFences(rawText).trim()
        if (!cleanedHtml.contains("<html", ignoreCase = true) && !cleanedHtml.contains("<!doctype", ignoreCase = true)) {
            throw IllegalStateException("A resposta do Gemini não retornou um jogo HTML válido.")
        }

        val title = cleanIdea.take(36).replaceFirstChar { it.uppercase() }
        return GeneratedWebsite(
            companyName = "Jogo: $title",
            htmlCode = cleanedHtml,
            summary = "Jogo HTML5 interativo gerado pelo Gemini 2.5 Flash."
        )
    }

    override suspend fun askUniversalQuestion(query: String): String {
        val prompt = """
            Você é o Assistente Universal por Voz do aplicativo "NEXUS AI 100" em português do Brasil.
            Responda à pergunta ou pesquisa do usuário de forma clara, objetiva, útil e natural para leitura em voz alta (evite símbolos markdown como asteriscos ou hashtags).
            Pergunta do usuário: "$query"
        """.trimIndent()

        return callGeminiApi(
            prompt = prompt,
            responseMimeType = "text/plain",
            responseSchema = null,
            temperature = 0.6
        ).trim()
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
            Crie um site institucional e comercial COMPLETO, moderno, responsivo (mobile-first) em um único arquivo HTML5 com CSS3 embutido dentro de <style> no <head>.
            Idioma obrigatório: Português do Brasil (pt-BR).
            Dados exatos da empresa:
            - Nome da Empresa: $company
            - Ramo / Especialidade: $niche
            - WhatsApp com DDD: $whatsapp (use botão de ação direta para https://wa.me/$waLinkDigits)
            - Cidade / Estado: $city
            - Serviços / Diferenciais: $services

            Requisitos visuais e técnicos:
            1. Visual dark mode elegante (#0b0f19) ou sofisticado condizente com o ramo, com alto contraste, tipografia limpa e botões grandes para celular.
            2. Inclua cabeçalho com nome da empresa e cidade, seção Hero impactante com botão CTA para WhatsApp, seção de Serviços/Diferenciais em cards responsivos, seção de confiança/sobre e rodapé completo.
            3. NÃO mencione valores, preços ou cobranças.
            4. Retorne APENAS o código HTML completo começando com <!DOCTYPE html> e terminando com </html>, sem markdown e sem explicações fora do HTML.
        """.trimIndent()

        val rawText = callGeminiApi(
            prompt = prompt,
            responseMimeType = "text/plain",
            responseSchema = null,
            temperature = 0.7
        )

        val cleanedHtml = stripCodeFences(rawText).trim()
        if (!cleanedHtml.contains("<html", ignoreCase = true) && !cleanedHtml.contains("<!doctype", ignoreCase = true)) {
            throw IllegalStateException("A resposta do Gemini não retornou um documento HTML válido. Tente novamente.")
        }

        return GeneratedWebsite(
            companyName = company,
            htmlCode = cleanedHtml,
            summary = "Site gerado em tempo real pelo Gemini 2.5 Flash para $company."
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

        val schema = JSONObject().apply {
            put("type", "OBJECT")
            put("properties", JSONObject().apply {
                put("isSafe", JSONObject().apply {
                    put("type", "BOOLEAN")
                    put("description", "true apenas se o item (URL, Pix Copia e Cola ou linha digitável de boleto) aparentar consistência e segurança; false se houver sinais de phishing, fraude financeira, boleto adulterado ou golpe empresarial")
                })
                put("riskScore", JSONObject().apply {
                    put("type", "INTEGER")
                    put("description", "Pontuação de risco de 0 (totalmente seguro) a 100 (risco crítico de fraude/golpe)")
                })
                put("explanation", JSONObject().apply {
                    put("type", "STRING")
                    put("description", "Explicação clara e detalhada em português do Brasil sobre os riscos financeiros/digitais ou conformidade do item analisado")
                })
                put("indicators", JSONObject().apply {
                    put("type", "ARRAY")
                    put("items", JSONObject().apply { put("type", "STRING") })
                    put("description", "Lista objetiva de 3 a 5 sinais técnicos verificados (ex: protocolo, domínio, estrutura EMV Pix, código de banco/dígito verificador do boleto, sinais de fraude)")
                })
            })
            put("required", JSONArray(listOf("isSafe", "riskScore", "explanation", "indicators")))
        }

        val scopeInstruction = if (corporateMode) {
            """
            Analise sob a ótica de Segurança Corporativa e Prevenção a Fraudes Financeiras contra Empresas.
            Tipo detectado: ${payloadType.label}.
            - Se for URL: avalie phishing, clonagem de bancos/fornecedores, typosquatting, HTTPS, TLD suspeito e links de boleto/Pix falsos.
            - Se for código Pix Copia e Cola (EMV): avalie estrutura EMV (000201...), presença de arranjo br.gov.bcb.pix, URLs ou chaves suspeitas, recebedor incomum ou inconsistência de formato, e oriente sobre conferência do CNPJ/recebedor antes de pagar.
            - Se for linha digitável de boleto: identifique os 3 primeiros dígitos do banco emissor (ex: 001 BB, 033 Santander, 104 Caixa, 237 Bradesco, 341 Itaú, 077 Inter, 260 Nubank, 756 Sicoob, 748 Sicredi), verifique quantidade de dígitos (47 para bancário, 48 para concessionária/tributo) e aponte riscos de boleto adulterado/falso contra empresas.
            """.trimIndent()
        } else {
            "Analise riscos de golpe, phishing, engenharia social, clonagem de marca, protocolos inseguros e fraudes financeiras."
        }

        val prompt = """
            Você é um especialista sênior em cibersegurança e prevenção a fraudes financeiras do NEXUS AI 100.
            $scopeInstruction
            Conteúdo submetido para análise: "$normalized"

            Responda em português do Brasil seguindo estritamente o esquema JSON.
        """.trimIndent()

        val jsonText = callGeminiApi(
            prompt = prompt,
            responseMimeType = "application/json",
            responseSchema = schema,
            temperature = 0.2
        )

        val json = JSONObject(stripCodeFences(jsonText))
        val isSafe = json.optBoolean("isSafe", false)
        val riskScore = json.optInt("riskScore", if (isSafe) 12 else 88).coerceIn(0, 100)
        val explanation = json.optString("explanation", "Análise concluída.")
        val indicatorsJson = json.optJSONArray("indicators") ?: JSONArray()
        val indicators = (0 until indicatorsJson.length()).mapNotNull { idx ->
            indicatorsJson.optString(idx).takeIf { it.isNotBlank() }
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

        val schema = JSONObject().apply {
            put("type", "OBJECT")
            put("properties", JSONObject().apply {
                put("guidance", JSONObject().apply {
                    put("type", "STRING")
                    put("description", "Explicação prática passo a passo em português do Brasil de como usar a ferramenta para resolver a dúvida do usuário")
                })
                put("readyPrompt", JSONObject().apply {
                    put("type", "STRING")
                    put("description", "Um prompt pronto, detalhado e copiável em português do Brasil para o usuário colar na ferramenta")
                })
            })
            put("required", JSONArray(listOf("guidance", "readyPrompt")))
        }

        val prompt = """
            Você é o Assistente Oficial do catálogo "100 Ferramentas IA" no app NEXUS AI 100.
            Ferramenta selecionada: ${tool.name}
            Categoria: ${tool.category.label}
            Descrição: ${tool.description}
            Site oficial: ${tool.officialUrl}
            Dúvida do usuário: "$cleanQuestion"

            Forneça um guia prático passo a passo e um prompt pronto para copiar em português do Brasil. Não mencione preços ou valores.
        """.trimIndent()

        val jsonText = callGeminiApi(
            prompt = prompt,
            responseMimeType = "application/json",
            responseSchema = schema,
            temperature = 0.6
        )

        val json = JSONObject(stripCodeFences(jsonText))
        val guidance = json.optString("guidance", "Não foi possível gerar o guia no momento.")
        val readyPrompt = json.optString("readyPrompt", "")

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
        val cleanBiz = businessContext.trim().ifEmpty { "Empresa brasileira buscando conquistar e fidelizar clientes" }

        val schema = JSONObject().apply {
            put("type", "OBJECT")
            put("properties", JSONObject().apply {
                put("title", JSONObject().apply {
                    put("type", "STRING")
                    put("description", "Título ou assunto curto e chamativo para o texto comercial")
                })
                put("body", JSONObject().apply {
                    put("type", "STRING")
                    put("description", "Texto ou proposta comercial completa, pronta para copiar e enviar em português do Brasil, formatada para o canal selecionado")
                })
            })
            put("required", JSONArray(listOf("title", "body")))
        }

        val channelRules = when (channel) {
            CommercialChannel.WHATSAPP ->
                "Formato direto e amigável para WhatsApp (parágrafos curtos, escaneáveis, emojis moderados, chamada para ação clara para responder a mensagem)."
            CommercialChannel.EMAIL ->
                "Formato de proposta profissional por E-mail (saudação corporativa, apresentação objetiva de valor, benefícios em tópicos e fechamento convidando para reunião)."
            CommercialChannel.ANUNCIOS ->
                "Formato de anúncio de alta conversão (gancho forte na primeira linha, dor/benefício claro e CTA irresistível)."
        }

        val prompt = """
            Você é um redator especialista em comunicação comercial B2B e B2C no Brasil.
            Canal escolhido: ${channel.label}.
            Instruções do canal: $channelRules
            Empresa / Contexto: $cleanBiz
            Objetivo da mensagem ou proposta: "$cleanTopic"

            Importante: NÃO invente preços ou cobranças em reais a menos que o usuário tenha pedido. Escreva em português do Brasil natural e persuasivo.
        """.trimIndent()

        val jsonText = callGeminiApi(
            prompt = prompt,
            responseMimeType = "application/json",
            responseSchema = schema,
            temperature = 0.7
        )

        val json = JSONObject(stripCodeFences(jsonText))
        val title = json.optString("title", "Proposta Comercial (${channel.label})")
        val body = json.optString("body", "")
        if (body.isBlank()) {
            throw IllegalStateException("O Gemini não retornou o texto comercial. Tente novamente.")
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

    private suspend fun callGeminiApi(
        prompt: String,
        responseMimeType: String,
        responseSchema: JSONObject?,
        temperature: Double
    ): String {
        val apiKey = prefsStore.getLocalApiKey().trim()
        if (apiKey.isEmpty()) {
            throw MissingApiKeyException()
        }

        return withTimeout(50_000L) {
            withContext(Dispatchers.IO) {
                val encodedKey = URLEncoder.encode(apiKey, "UTF-8")
                val endpoint = "$GEMINI_ENDPOINT?key=$encodedKey"
                val url = URL(endpoint)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 20_000
                    readTimeout = 45_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    setRequestProperty("Accept", "application/json")
                }

                try {
                    val generationConfig = JSONObject().apply {
                        put("temperature", temperature)
                        put("responseMimeType", responseMimeType)
                        if (responseSchema != null) {
                            put("responseSchema", responseSchema)
                        }
                    }

                    val requestBody = JSONObject().apply {
                        put("contents", JSONArray().apply {
                            put(JSONObject().apply {
                                put("role", "user")
                                put("parts", JSONArray().apply {
                                    put(JSONObject().apply {
                                        put("text", prompt)
                                    })
                                })
                            })
                        })
                        put("generationConfig", generationConfig)
                    }

                    OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                        writer.write(requestBody.toString())
                        writer.flush()
                    }

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
                        val apiErrorMsg = parseGeminiErrorMessage(responseBody, statusCode)
                        Log.e(TAG, "Gemini HTTP $statusCode: $responseBody")
                        throw IllegalStateException(apiErrorMsg)
                    }

                    extractCandidateText(responseBody)
                } finally {
                    connection.disconnect()
                }
            }
        }
    }

    private fun extractCandidateText(responseBody: String): String {
        val root = JSONObject(responseBody)
        val candidates = root.optJSONArray("candidates")
        if (candidates == null || candidates.length() == 0) {
            val blockReason = root.optJSONObject("promptFeedback")?.optString("blockReason")
            throw IllegalStateException(
                if (!blockReason.isNullOrBlank()) {
                    "Solicitação bloqueada por segurança ($blockReason)."
                } else {
                    "A API do Gemini retornou uma resposta vazia."
                }
            )
        }

        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val textBuilder = StringBuilder()
        if (parts != null) {
            for (i in 0 until parts.length()) {
                val partText = parts.getJSONObject(i).optString("text")
                textBuilder.append(partText)
            }
        }

        val result = textBuilder.toString()
        if (result.isBlank()) {
            throw IllegalStateException("Nenhum conteúdo de texto retornado pelo Gemini.")
        }
        return result
    }

    private fun parseGeminiErrorMessage(responseBody: String, statusCode: Int): String {
        return runCatching {
            val errObj = JSONObject(responseBody).optJSONObject("error")
            val message = errObj?.optString("message").orEmpty()
            when {
                statusCode == 400 && message.contains("API key", ignoreCase = true) ->
                    "Chave da API do Gemini inválida. Verifique sua chave AIzaSy... no ícone de chave no topo."
                statusCode == 401 || statusCode == 403 ->
                    "Acesso negado pela API do Gemini (HTTP $statusCode). Confira se sua chave AIzaSy... está ativa."
                statusCode == 429 ->
                    "Limite de requisições da sua chave Gemini atingido. Aguarde alguns instantes e tente novamente."
                message.isNotBlank() ->
                    "Erro na API do Gemini (HTTP $statusCode): $message"
                else ->
                    "Falha na comunicação com a API do Gemini (HTTP $statusCode)."
            }
        }.getOrElse {
            "Falha na comunicação com a API do Gemini (HTTP $statusCode)."
        }
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
        private const val TAG = "GeminiRestAiRepo"
        private const val GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
    }
}
