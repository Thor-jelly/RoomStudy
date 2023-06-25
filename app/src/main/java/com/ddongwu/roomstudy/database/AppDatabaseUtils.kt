package com.ddongwu.roomstudy.database

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ddongwu.roomstudy.App

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/6/5 11:11 <br/>
 */
class AppDatabaseUtils private constructor() {
    class HOLDER {
        companion object {
            internal val H = AppDatabaseUtils()
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(): AppDatabaseUtils {
            return HOLDER.H
        }
    }

//    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            //User 表 新增Book列
//            database.execSQL("CREATE TABLE Book (bookId TEXT DEFAULT '', bookName TEXT, PRIMARY KEY(bookId))");
//            database.execSQL("ALTER TABLE user ADD COLUMN bookId TEXT DEFAULT ''")
//            database.execSQL("ALTER TABLE user ADD COLUMN bookName TEXT DEFAULT ''")
//        }
//    }

    private val db by lazy {
        Room.databaseBuilder(
            App.app.applicationContext,
            AppDatabase::class.java,
            "room-test"
        )/*.addMigrations(
            MIGRATION_1_2
        )*/
            //.allowMainThreadQueries() //是否允许主线程
            .build()
    }

    val userDao by lazy {
        db.userDao()
    }
}