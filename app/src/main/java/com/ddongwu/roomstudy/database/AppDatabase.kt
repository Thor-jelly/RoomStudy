package com.ddongwu.roomstudy.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/6/5 11:09 <br/>
 */
@Database(
    entities = [User::class, Book::class],
    version = 4,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3),
        AutoMigration(3, 4, AppDatabase.AutoMigration_3_4::class)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao

    @RenameColumn("user", "list", "favoriteBookList")
    class AutoMigration_3_4 :AutoMigrationSpec{

    }
}