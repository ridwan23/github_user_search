package com.github.test.data.remotediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.github.test.data.api.ApiService
import com.github.test.data.constant.Constants.DEFAULT.STARTING_PAGE_INDEX
import com.github.test.data.db.AppDatabase
import com.github.test.data.model.GitUser
import com.github.test.data.model.RemoteKeys
import retrofit2.HttpException
import java.io.IOException


@ExperimentalPagingApi
class UsersRemoteMediator(
    private val keyword: String,
    private val service: ApiService,
    private val db: AppDatabase
) : RemoteMediator<Int, GitUser>() {


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GitUser>
    ): MediatorResult {
        val key = when (val pageKeyData = getKeyPageData(loadType)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as RemoteKeys?
            }
        }

        try {
            val page: Int = key?.nextKey ?: STARTING_PAGE_INDEX
            val keywordQuery = "$keyword in:login"
            val apiResponse = service.searchUsers(keywordQuery, page, state.config.pageSize)
            val usersList = apiResponse.items
            val endOfPaginationReached = apiResponse.items.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().clearRemoteKeys()
                    db.userDao().clearUsers()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                db.remoteKeysDao().insertKey(
                    RemoteKeys(
                        0,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                )
                db.userDao().insert(usersList)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            if (exception.code() == 403) return MediatorResult.Success(endOfPaginationReached = false)
            return MediatorResult.Success(endOfPaginationReached = false)
        }
    }

    private suspend fun getKeyPageData(loadType: LoadType): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                if (db.userDao().count(keyword) > 0) return MediatorResult.Success(false)
                null
            }
            LoadType.APPEND -> {
                getKey()
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
    }

    private suspend fun getKey(): RemoteKeys? {
        return db.remoteKeysDao().getKeys().lastOrNull()
    }


}