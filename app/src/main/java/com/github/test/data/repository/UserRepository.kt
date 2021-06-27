package com.github.test.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.test.data.api.ApiService
import com.github.test.data.constant.Constants.DEFAULT.NETWORK_PAGE_SIZE
import com.github.test.data.db.AppDatabase
import com.github.test.data.model.GitUser
import com.github.test.data.remotediator.UsersRemoteMediator
import kotlinx.coroutines.flow.Flow


class UserRepository(
    private val apiService: ApiService,
    private val db: AppDatabase
) {
    @ExperimentalPagingApi
    fun getUsers(keyword: String): Flow<PagingData<GitUser>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = UsersRemoteMediator(
                keyword,
                apiService,
                db
            ),
            pagingSourceFactory = {
                val query = "%${keyword}%"
                db.userDao().usersByName(query)
            }
        ).flow
    }

}