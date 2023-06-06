package com.ddongwu.roomstudy.database.typeconverter

import androidx.room.TypeConverter
import com.ddongwu.roomstudy.database.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/6/6 11:10 <br/>
 */
class UserBookListTypeConverter {
    private val mGson by lazy {
        Gson()
    }

    @TypeConverter
    fun objectToString(list: List<Book>?): String? {
        if (list == null) return null
        return mGson.toJson(list)
    }

    @TypeConverter
    fun stringToObject(json: String?): List<Book>? {
        if (json.isNullOrEmpty()) {
            return null
        }
        return mGson.fromJson(json, object : TypeToken<List<Book>>() {}.type)
    }
}