package com.ddongwu.roomstudy.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ddongwu.roomstudy.database.typeconverter.UserBookListTypeConverter

/**
 * 类描述：//TODO:(这里用一句话描述这个方法的作用)    <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/6/2 17:44 <br/>
 */
@Entity(tableName = "user")
@TypeConverters(UserBookListTypeConverter::class)
data class User(
    @PrimaryKey
    var uid: String = "",
    //如果数据字段名称 同 后台返回名称 可以直接 不行@ColumnInfo 默认是字段名称
    @ColumnInfo(name = "first_name")
    var firstName: String? = null,
    var lastName: String? = null,
    //我最爱看的一本书
    @Embedded
    var favoriteBook: Book? = null,
    var favoriteBookList: List<Book>? = null,
    //如果需要忽略该字段使用@Ignore
    @Ignore
    var bitmap: Bitmap? = null
)
