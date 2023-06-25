package com.ddongwu.roomstudy.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/6/2 17:51 <br/>
 */
@Dao
interface UserDao {
    //查询所有
    @Query("SELECT * FROM user")
    fun findAll(): List<User>?

    //分页数据
    //@Query("SELECT * FROM user ORDER BY uid LIMIT :limit OFFSET :offset")
    @Query("SELECT * FROM user LIMIT :limit OFFSET :offset")
    fun findByPage(limit: Int, offset: Int): List<User>?

    //根据用户id查询
    @Query("SELECT * FROM user WHERE uid IN (:userId)")
    fun loadAllByIds(vararg userId: Int): List<User>?

    //根据用户名查询
    @Query("SELECT * FROM user WHERE first_name LIKE :firstName AND lastName LIKE :lastName LIMIT 1")
    fun findByName(firstName: String, lastName: String): User?

    //模糊查询 ||相当于+号 百分号（%）代表零个、一个或多个数字或字符。下划线（_）代表一个单一的数字或字符。这些符号可以被组合使用。
    @Query("SELECT * FROM user WHERE first_name LIKE '%' || :firstName || '%'")
    fun findByNameFuzzy(firstName: String): User?

    //更新用户数据
    @Update
    fun updateUser(user: User): Int

    //根据用户id更新数据
    @Query("UPDATE user SET first_name='f' || :userId ||'  fend', lastName = 'l' || :userId || '  lend' WHERE uid=:userId")
    fun updateUser(userId: String): Int

    // 插入用户的注解，vararg表示可以一次插入多个用户
    // onConflict标识插入遇到冲突时的解决策略，可选REPLACE,ROLLBACK,ABORT,FAIL,IGNORE，简单明了，看单词就能明白作用
    // 并且replace会把原用数据移动到最后一条上
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User): List<Long>?

    //删除用户
    @Delete
    fun delete(user: User): Int

    //根据id删除数据
    @Query("DELETE FROM user WHERE uid=:userId")
    fun delete(userId: String): Int

    //删除所有
    @Query("DELETE FROM user")
    fun deleteAll()

    //查询总条数
    @Query("SELECT COUNT(*) FROM user")
    fun count(): Long
}