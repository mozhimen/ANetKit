package com.mozhimen.netk.retrofit2.test.mos

import java.io.Serializable

data class ArticleRes(
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int
) : Serializable