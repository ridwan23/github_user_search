package com.github.test.data.model


import com.google.gson.annotations.SerializedName

data class GitUserResponse(
    @SerializedName("incomplete_results")
    var incompleteResults: Boolean?,
    @SerializedName("items")
    var items: List<GitUser>,
    @SerializedName("total_count")
    var totalCount: Int
)