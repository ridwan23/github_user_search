package com.github.test.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.test.data.model.GitUser
import com.github.test.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    @ExperimentalPagingApi
    fun searchPlayers(keyword: String): Flow<PagingData<GitUser>> {
        return userRepository.getUsers(keyword).cachedIn(viewModelScope)
    }


}