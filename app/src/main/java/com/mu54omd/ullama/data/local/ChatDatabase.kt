package com.mu54omd.ullama.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mu54omd.ullama.domain.model.chat.ChatModel

@Database(entities = [ChatModel::class], version = 1, exportSchema = true)
@TypeConverters(value = [ChatConverter::class])
abstract class ChatDatabase:RoomDatabase() {
    abstract val chatDao: ChatDao
}