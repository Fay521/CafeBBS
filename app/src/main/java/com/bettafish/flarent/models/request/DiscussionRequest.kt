package com.bettafish.flarent.models.request

data class DiscussionRequest(
    val id: String,
    val near: Int? = 0,
    val limit: Int? = 20,
    val include: String? = null
) {
    fun toQueryMap(): Map<String, String> = buildMap {
        near?.let { put("page[near]", it.toString()) }
        limit?.let { put("page[limit]", it.toString()) }
        include?.let { put("include", it) }
    }
}