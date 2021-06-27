package com.github.test.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.test.data.model.GitUser

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<GitUser>)

    @Query("SELECT * FROM users WHERE login LIKE :queryString")
     fun usersByName(queryString: String): PagingSource<Int, GitUser>

    @Query("SELECT COUNT(id) from users  WHERE login LIKE :queryString")
    suspend fun count(queryString: String): Int

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}