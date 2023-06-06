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

```
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
```

### 定义DAO[官方说明](https://developer.android.com/training/data-storage/room/accessing-data?hl=zh-cn)    [多重映射方法](https://developer.android.com/training/data-storage/room/accessing-data?hl=zh-cn#multiple-tables)

以下代码定义了一个名为 `UserDao` 的 DAO。`UserDao` 提供了应用的其余部分用于与 `user` 表中的数据交互的方法。

```
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>?

    @Query("SELECT * FROM user WHERE uid IN (:userId)")
    fun loadAllByIds(vararg userId: Int): List<User>?

    @Query("SELECT * FROM user WHERE first_name LIKE :firstName AND lastName LIKE :lastName LIMIT 1")
    fun findByName(firstName: String, lastName: String): User?

    //模糊查询 ||相当于+号 百分号（%）代表零个、一个或多个数字或字符。下划线（_）代表一个单一的数字或字符。这些符号可以被组合使用。
    @Query("SELECT * FROM user WHERE first_name LIKE '%' || :firstName || '%'")
    fun findByNameFuzzy(firstName: String): User?

    @Update
    fun updateUser(user: User): Int

    @Query("UPDATE user SET first_name='f' || :userId ||'  fend', lastName = 'l' || :userId || '  lend' WHERE uid=:userId")
    fun updateUser(userId: String): Int

    // 插入用户的注解，vararg表示可以一次插入多个用户
    // onConflict标识插入遇到冲突时的解决策略，可选REPLACE,ROLLBACK,ABORT,FAIL,IGNORE，简单明了，看单词就能明白作用
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User): List<Long>?

    @Delete
    fun delete(user: User): Int

    //根据id删除数据
    @Query("DELETE FROM user WHERE uid=:userId")
    fun delete(userId: String): Int
}
```

## 数据库

```
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```

```
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
```

## 用法

```
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
        )*/.build()
    }

    val userDao by lazy {
        db.userDao()
    }
}
```



## 数据库升级

直接自动升级[官方](https://developer.android.google.cn/training/data-storage/room/migrating-db-versions?hl=zh-cn)

```
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
```



## 基于 AutoValue 的对象

> **注意**：此功能仅适用于基于 Java 的实体。要在基于 Kotlin 的实体中实现相同的功能，最好使用数据类。

在 Room 2.1.0 或更高的版本中，你可以使用基于 Java 的[不可变值类](https://github.com/google/auto/blob/master/value/userguide/index.md)（使用 `@AutoValue` 进行注释）作为应用程序数据库中的实体。如果实体的两个实例的列包含相同的值，则此支持特别有用。

使用带 `@AutoValue` 注释的类作为实体时，可以使用 `@PrimaryKey`，`@ColumnInfo`，`@Embedded` 和 `@Relation` 注释类的抽象方法。但是，在使用这些注释时，必须每次都包含 `@CopyAnnotations` 注释，以便 Room 可以正确解释方法的自动生成实现。

以下代码段显示了一个使用 `@AutoValue` 注释的类，Room 将其识别为实体：

```
@AutoValue
@Entity
public abstract class User {
    // Supported annotations must include `@CopyAnnotations`.
    @CopyAnnotations
    @PrimaryKey
    public abstract long getId();

    public abstract String getFirstName();
    public abstract String getLastName();

    // Room uses this factory method to create User objects.
    public static User create(long id, String firstName, String lastName) {
        return new AutoValue_User(id, firstName, lastName);
    }
}
```

## 嵌套对象

> **注意**：嵌入字段还可以包含其他嵌入字段。

有时，你希望将实体或简单的 Java 对象（POJO）表达为数据库逻辑中的一个整体，即使该对象包含多个字段。在这些情况下，你可以使用 [@Embedded](https://developer.android.com/reference/androidx/room/Embedded.html) 注释来表示要分解到表中子字段的对象。然后，你可以像查询其他单个列一样查询嵌入字段。

如果实体具有多个相同类型的嵌入字段，则可以通过设置 [prefix](https://developer.android.com/reference/androidx/room/Embedded.html#prefix()) 属性使每个列保持唯一。然后，Room 会将提供的值添加到嵌入对象中每个列名称的开头。

## 复杂数据 [官方](https://developer.android.com/training/data-storage/room/referencing-data?hl=zh-cn)

自定义数据类型的值存储在数据库的单个列中。为了支持自定义类型，需要提供一个 [TypeConverter](https://developer.android.com/reference/android/arch/persistence/room/TypeConverter.html)，它可以将自定义类型转换为 Room 能够持久化的已知类型。

接下来，将 [@TypeConverters](https://developer.android.com/reference/android/arch/persistence/room/TypeConverters.html) 注释添加到 `AppDatabase` 类，这样 Room 就可以使用你为该 `AppDatabase` 中的每个 [Entity](https://developer.android.com/training/data-storage/room/defining-data.html) 和 [DAO](https://developer.android.com/training/data-storage/room/accessing-data.html) 定义的转换器

你还可以将 [@TypeConverters](https://developer.android.com/reference/android/arch/persistence/room/TypeConverters.html) 的作用范围限制在单个实体、DAO 和 DAO 方法内。有关详细信息，请参阅 [@TypeConverters](https://developer.android.com/reference/android/arch/persistence/room/TypeConverters.html) 的参考文档。

```
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
```
