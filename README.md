[TOC]

# 学习数据库Room

> **Room不能在主线程即UI线程上操作**
>
> [官方文档](https://developer.android.google.cn/jetpack/androidx/releases/room?hl=zh-cn#kts)  [官方用户说明](https://developer.android.com/training/data-storage/room?hl=zh-cn)
>
> [ksp](https://developer.android.com/studio/build/migrate-to-ksp?hl=zh-cn)  [kapt](https://www.kotlincn.net/docs/reference/kapt.html)
>
> [协程](https://github.com/Kotlin/kotlinx.coroutines)  [将 Kotlin 协程与生命周期感知型组件一起使用](https://developer.android.com/topic/libraries/architecture/coroutines?hl=zh-cn)
>
> [sqlite altert](http://www.sqlite.org/lang_altertable.html)

## 依赖

```
dependencies {
    def room_version = "2.5.1"

    implementation "androidx.room:room-runtime:$room_version"
    annotationProcessor "androidx.room:room-compiler:$room_version"

    // To use Kotlin annotation processing tool (kapt)
    kapt "androidx.room:room-compiler:$room_version"
    // To use Kotlin Symbol Processing (KSP)
    ksp "androidx.room:room-compiler:$room_version"

    // optional - RxJava2 support for Room
    implementation "androidx.room:room-rxjava2:$room_version"

    // optional - RxJava3 support for Room
    implementation "androidx.room:room-rxjava3:$room_version"

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation "androidx.room:room-guava:$room_version"

    // optional - Test helpers
    testImplementation "androidx.room:room-testing:$room_version"

    // optional - Paging 3 Integration
    implementation "androidx.room:room-paging:$room_version"
}



android {
    ...
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += [
                    "room.schemaLocation":"$projectDir/schemas".toString(),
                    "room.incremental":"true"
                    ]
            }
        }
    }
}
```



## 基本用法

### 创建数据库实体[官方说明](https://developer.android.com/training/data-storage/room/defining-data?hl=zh-cn)

以下代码定义了一个 `User` 数据实体。`User` 的每个实例都代表应用数据库中 `user` 表中的一行。

```
@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id: String,
    //如果数据字段名称 同 后台返回名称 可以直接 不行@ColumnInfo 默认是字段名称
    @ColumnInfo(name = "first_name")
    val firstName: String?,
    @ColumnInfo(name = "last_name")
    val lastName: String?,
    //如果需要忽略该字段使用@Ignore
    @Ignore
    val bitmap: Bitmap?
)
```

### 定义DAO[官方说明](https://developer.android.com/training/data-storage/room/accessing-data?hl=zh-cn)

以下代码定义了一个名为 `UserDao` 的 DAO。`UserDao` 提供了应用的其余部分用于与 `user` 表中的数据交互的方法。



## 数据库

```
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```



## 用法

```
class AppDatabaseUtils private constructor(){
    class HOLDER{
        companion object{
            internal val H = AppDatabaseUtils()
        }
    }
    
    companion object{
        @JvmStatic
        fun getInstance(): AppDatabaseUtils {
            return HOLDER.H
        }
    }
    
    private val db by lazy {
        Room.databaseBuilder(
            App.app.applicationContext,
            AppDatabase::class.java,
            "room-test"
        ).build()
    }
    
    val userDao by lazy { 
        db.userDao()
    }
}
```



## 数据库升级

直接自动升级[官方](https://developer.android.google.cn/training/data-storage/room/migrating-db-versions?hl=zh-cn)

