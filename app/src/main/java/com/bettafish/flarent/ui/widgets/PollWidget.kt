package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
import com.bettafish.flarent.R
import com.bettafish.flarent.models.Poll
import com.bettafish.flarent.models.PollOption
import com.bettafish.flarent.utils.relativeTime

@Composable
fun PollWidget(
    viewModel: PollViewModel,
    modifier: Modifier = Modifier
) {
    val poll by viewModel.poll.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val canVote by viewModel.voteCommand.canExecute.collectAsState()

    poll?.let {
        PollContent(
            poll = it,
            isLoading = isLoading,
            canVoteExec = canVote,
            onVote = { optionId -> viewModel.voteCommand.execute(optionId) },
            modifier = modifier
        )
    }
}

@Composable
private fun PollContent(
    poll: Poll,
    isLoading: Boolean,
    canVoteExec: Boolean,
    onVote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVotes = poll.voteCount ?: poll.options?.sumOf { it.voteCount ?: 0 } ?: 0
    val userHasVoted = poll.hasVoted == true || poll.myVotes?.isNotEmpty() == true
    val canVoteNow = poll.canVote == true && !userHasVoted

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Poll question
            poll.question?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Options
            poll.options?.forEach { option ->
                PollOptionItem(
                    option = option,
                    totalVotes = totalVotes,
                    canVote = canVoteNow,
                    isSelected = option.isVoted == true,
                    isLoading = isLoading || !canVoteExec,
                    onClick = {
                        if (canVoteNow && !isLoading && canVoteExec) {
                            onVote(option.id)
                        }
                    }
                )
            }

            // Bottom info row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.poll_total_votes, totalVotes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                // Show voting status
                if (userHasVoted) {
                    Text(
                        text = stringResource(R.string.poll_voted),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // End date
            poll.endDate?.let { endDate ->
                Text(
                    text = stringResource(R.string.poll_ends_at, endDate.relativeTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Public poll indicator
            if (poll.publicPoll == true) {
                Text(
                    text = stringResource(R.string.poll_public),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Allow multiple votes hint
            if (poll.allowMultipleVotes == true) {
                val max = poll.maxVotes
                val hint = if (max != null) {
                    stringResource(R.string.poll_multi_vote_max, max)
                } else {
                    stringResource(R.string.poll_multi_vote)
                }
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun PollOptionItem(
    option: PollOption,
    totalVotes: Int,
    canVote: Boolean,
    isSelected: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val percentage = if (totalVotes > 0) {
        ((option.voteCount ?: 0).toFloat() / totalVotes * 100f).toInt()
    } else 0

    val shape = RoundedCornerShape(8.dp)

    Surface(
        shape = shape,
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (canVote) Modifier.clickable { onClick() }
                else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Option image if available
                option.imageUrl?.let { imageUrl ->
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(imageUrl)
                            .size(Size.ORIGINAL)
                            .build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.answer ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Vote count / percentage
                if (!canVote || isLoading) {
                    Text(
                        text = "${option.voteCount ?: 0}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Progress bar (visible after voting or always for public polls)
            if (!canVote) {
                LinearProgressIndicator(
                    progress = { if (totalVotes > 0) percentage / 100f else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondaryContainer,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PollPreview() {
    val samplePoll = Poll().apply {
        id = "1"
        question = "What's your favorite coffee?"
        canVote = true
        hasVoted = false
        publicPoll = true
        allowMultipleVotes = false
        voteCount = 15
        options = listOf(
            PollOption().apply {
                id = "1"
                answer = "Latte"
                voteCount = 8
            },
            PollOption().apply {
                id = "2"
                answer = "Cappuccino"
                voteCount = 5
            },
            PollOption().apply {
                id = "3"
                answer = "Espresso"
                voteCount = 2
            }
        )
    }

    MaterialTheme {
        Surface {
            PollContent(
                poll = samplePoll,
                isLoading = false,
                canVoteExec = true,
                onVote = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
