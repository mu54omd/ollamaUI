package com.example.ollamaui.ui.screen.filemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.data.local.objectbox.ChunkDatabase
import com.example.ollamaui.data.local.objectbox.FileDatabase
import com.example.ollamaui.domain.model.LogModel
import com.example.ollamaui.domain.model.embed.EmbedInputModel
import com.example.ollamaui.domain.model.objectbox.Chunk
import com.example.ollamaui.domain.model.objectbox.File
import com.example.ollamaui.domain.objectbox.Splitter
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.ui.screen.chat.AttachedFilesList
import com.example.ollamaui.utils.Constants.OLLAMA_EMBED_ENDPOINT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FileManagerViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
    private val fileDatabase: FileDatabase,
    private val chunkDatabase: ChunkDatabase,
): ViewModel() {
    private val _attachedFiles = fileDatabase.getAllFiles().map { AttachedFilesList(item = it) }
    val attachedFiles = _attachedFiles
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AttachedFilesList()
        )

    fun attachFileToChat(
        attachResult: String?,
        attachError: String?,
        embeddingModel: String,
        fileName: String,
        documentType: String,
        hash: String,
        ollamaBaseAddress: String,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val isImage = documentType in listOf("png", "jpg", "jpeg")
                attachResult?.let { result ->
                    val filteredList = attachedFiles.value.item.filter {
                        (it.attachResult == attachResult)
                                && (it.fileName == fileName)
                                && (it.fileType == documentType)
                                && (it.hash == hash)
                    }
                    if (filteredList.isEmpty()) {
                        val fileId = fileDatabase.addFile(
                            file = File(
                                attachResult = result,
                                fileName = fileName,
                                fileType = documentType,
                                fileAddedTime = System.currentTimeMillis(),
                                hash = hash,
                                isImage = isImage
                            )
                        )
                        if (!isImage) {
                            val chunks = Splitter.createChunks(
                                docText = result,
                                chunkSize = 100,
                                chunkOverlap = 5
                            )
                            ollamaPostEmbed(
                                text = chunks,
                                embeddingModel = embeddingModel,
                                docId = fileId,
                                fileName = fileName,
                                ollamaBaseAddress = ollamaBaseAddress
                            )
                        }
                    }
                }
                when {
                    attachResult != null -> ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "attach-file",
                            content = "Result: Success - $fileName",
                        )
                    )

                    attachError != null -> ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "attach-file",
                            content = "Result: Failed - $fileName - $attachError",
                        )
                    )
                }
            }
        }
    }

    fun removeAttachedFile(fileId: Long, isImage: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!isImage) {
                    fileDatabase.removeFile(fileId = fileId)
                    chunkDatabase.removeChunk(docId = fileId)
                } else {
                    fileDatabase.removeFile(fileId = fileId)
                }
            }
        }
    }

    private fun ollamaPostEmbed(text: List<String>, embeddingModel: String, docId: Long, fileName: String, ollamaBaseAddress: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "ollama-embed",
                        content = "post: ${ollamaBaseAddress}${OLLAMA_EMBED_ENDPOINT}",
                    )
                )
                ollamaRepository.postOllamaEmbed(
                    baseUrl = ollamaBaseAddress,
                    embedEndpoint = OLLAMA_EMBED_ENDPOINT,
                    embedInputModel = EmbedInputModel(
                        model = embeddingModel,
                        input = text
                    )
                )
                    .onRight { response ->
                        response.embeddings.forEachIndexed { index, chunkedEmbedding ->
                            chunkDatabase.addChunk(
                                chunk = Chunk(
                                    docId = docId,
                                    docFileName = fileName,
                                    chunkData = text[index],
                                    chunkEmbedding = chunkedEmbedding,
                                )
                            )
                        }
                        ollamaRepository.insertLogToDb(
                            LogModel(
                                date = LocalDateTime.now().toString(),
                                type = "ollama-embed",
                                content = "Result: Success",
                            )
                        )
                    }
                    .onLeft { error ->
                        ollamaRepository.insertLogToDb(
                            LogModel(
                                date = LocalDateTime.now().toString(),
                                type = "ollama-embed",
                                content = "Result: Failed - ${error.error}",
                            )
                        )
                    }
            }
        }
    }

}