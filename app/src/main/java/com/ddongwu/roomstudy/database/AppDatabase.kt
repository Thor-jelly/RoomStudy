package com.ddongwu.roomstudy.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/6/5 11:09 <br/>
 */
@Database(
    entities = [User::class/*, Book::class*/],
    version = 1,
//    autoMigrations = [
//        AutoMigration(1, 2)
//    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    //abstract fun bookDao(): BookDao
}