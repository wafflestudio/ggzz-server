package com.wafflestudio.ggzz.global.common.dto

data class ListResponse<T: Any> (
    val count: Int,
    val data: List<T>
) {
    constructor(list: List<T>): this(
        count = list.size,
        data = list
    )
}