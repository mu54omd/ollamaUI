package com.example.ollamaui.data.repository

import arrow.core.Either
import com.example.ollamaui.data.local.ChatDao
import com.example.ollamaui.data.local.LogDao
import com.example.ollamaui.data.remote.OllamaApi
import com.example.ollamaui.domain.model.LogModel
import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.chat.ChatInputModel
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.model.chat.ChatResponse
import com.example.ollamaui.domain.model.embed.EmbedInputModel
import com.example.ollamaui.domain.model.embed.EmbedResponse
import com.example.ollamaui.domain.model.pull.PullInputModel
import com.example.ollamaui.domain.model.pull.PullResponse
import com.example.ollamaui.domain.model.tag.TagResponse
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.mapper.toNetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import okio.Buffer
import javax.inject.Inject

class OllamaRepositoryImpl @Inject constructor(
    private val ollamaApi: OllamaApi,
    private val chatDao: ChatDao,
    private val logDao: LogDao
): OllamaRepository {
    override suspend fun getOllamaStatus(baseUrl: String, baseEndpoint: String): Either<NetworkError, String> {
        return Either.catch { ollamaApi.ollamaState(fullUrl = baseUrl + baseEndpoint) }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getOllamaModelsList(baseUrl: String, tagEndpoint: String): Either<NetworkError, TagResponse> {
        return Either.catch { ollamaApi.ollamaModelsList(fullUrl = baseUrl + tagEndpoint) }.mapLeft { it.toNetworkError() }
    }

    override suspend fun postOllamaChat(
        baseUrl: String,
        chatEndpoint: String,
        chatInputModel: ChatInputModel?
    ): Flow<Either<NetworkError, ChatResponse>> = flow {
        try {
            val responseBody = ollamaApi.ollamaChat(fullUrl = baseUrl + chatEndpoint, chatInputModel = chatInputModel)
            responseBody.use { body ->
                val source = body.source()
                val buffer = Buffer()
                val accumulatedJson = StringBuilder()
                while (!source.exhausted()) {
                    val bytesRead = source.read(buffer, 8192)
                    if (bytesRead != -1L) {
                        accumulatedJson.append(buffer.readUtf8())
                        val jsonText = accumulatedJson.toString()
                        if (jsonText.trim().endsWith("}")) {
                            val customJson = Json { ignoreUnknownKeys = true }
                            val chatResponse = customJson.decodeFromString<ChatResponse>(jsonText)
                            emit(Either.Right(chatResponse))
                            accumulatedJson.clear()
                        }
                    }
                }
            }
        }catch (e: Exception){
            emit(Either.Left(e.toNetworkError()))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun postOllamaEmbed(
        baseUrl: String,
        embedEndpoint: String,
        embedInputModel: EmbedInputModel
    ): Either<NetworkError, EmbedResponse> {
        return Either.catch { ollamaApi.ollamaEmbed(fullUrl = baseUrl + embedEndpoint, embedInputModel = embedInputModel) }.mapLeft { it.toNetworkError() }
    }

    override suspend fun postOllamaPull(
        baseUrl: String,
        pullEndpoint: String,
        pullInputModel: PullInputModel
    ): Either<NetworkError, PullResponse> {
        return Either.catch { ollamaApi.ollamaPull(fullUrl = baseUrl + pullEndpoint, pullInputModel = pullInputModel) }.mapLeft { it.toNetworkError() }
    }

    override suspend fun insertToDb(chatModel: ChatModel) {
        chatDao.insert(chatModel)
    }

    override suspend fun deleteFromDb(chatModel: ChatModel) {
        chatDao.delete(chatModel)
    }

    override suspend fun deleteFromDbById(chatId: Int) {
        chatDao.deleteById(chatId)
    }

    override suspend fun updateDbItem(chatModel: ChatModel) {
        chatDao.update(chatModel)
    }

    override fun getChats(): Flow<List<ChatModel>> {
        return chatDao.getChats().flowOn(Dispatchers.IO)
    }

    override suspend fun getChat(chatId: Int): ChatModel? {
        return chatDao.getChat(chatId)
    }

    override suspend fun getLastId(): Int {
        return chatDao.getLastId()
    }

    override suspend fun insertLogToDb(logModel: LogModel) {
        logDao.insert(logModel = logModel)
    }

    override suspend fun deleteLogFromDb() {
        logDao.delete()
    }

    override fun getLogsFromDb():Flow<List<LogModel>>{
        return logDao.getLogs().flowOn(Dispatchers.IO)
    }
}