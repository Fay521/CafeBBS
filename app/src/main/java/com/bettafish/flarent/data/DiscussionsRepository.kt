package com.bettafish.flarent.data

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.request.DiscussionListRequest
import com.bettafish.flarent.models.request.DiscussionRequest

interface DiscussionsRepository {
    suspend fun fetchDiscussionList(request: DiscussionListRequest): List<Discussion>
    suspend fun fetchDiscussion(request: DiscussionRequest): Discussion
    suspend fun createDiscussion(title: String, content: String, tagId: String): Discussion
    suspend fun updateLastReadPostNumber(id: String, lastReadPostNumber: Int)
}
