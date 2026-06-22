package com.bettafish.flarent.data

import com.bettafish.flarent.models.Poll

interface PollsRepository {
    suspend fun fetchPoll(id: String, include: String? = "options,myVotes"): Poll
    suspend fun votePoll(pollId: String, optionIds: List<String>): Poll
    suspend fun createPoll(
        discussionId: String,
        question: String,
        options: List<String>,
        publicPoll: Boolean = false,
        allowMultipleVotes: Boolean = false,
        maxVotes: Int? = null,
        endDate: String? = null,
        allowOptionImage: Boolean = false
    ): Poll
}
