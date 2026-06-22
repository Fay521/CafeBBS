package com.bettafish.flarent.ui.pages.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bettafish.flarent.R
import com.bettafish.flarent.models.MessageDialog
import com.bettafish.flarent.ui.widgets.Avatar
import com.bettafish.flarent.utils.relativeTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ConversationPageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>
fun MessagesListPage(
    navigator: DestinationsNavigator,
    viewModel: MessagesViewModel = koinViewModel()
) {
    val dialogs = viewModel.dialogs.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_messages)) }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = dialogs.loadState.refresh is LoadState.Loading,
            onRefresh = { dialogs.refresh() },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    count = dialogs.itemCount,
                    key = dialogs.itemKey { it.id }
                ) { index ->
                    val dialog = dialogs[index]
                    if (dialog != null) {
                        DialogItem(
                            dialog = dialog,
                            onClick = {
                                navigator.navigate(ConversationPageDestination(dialog.id))
                            }
                        )
                    }
                }
                if (dialogs.loadState.append is LoadState.Loading) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogItem(
    dialog: MessageDialog,
    onClick: () -> Unit
) {
    val recipient = dialog.recipients?.firstOrNull()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                Avatar(
                    avatarUrl = recipient?.avatarUrl,
                    name = recipient?.displayName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                if ((dialog.unreadCount ?: 0) > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(dialog.unreadCount.toString())
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = recipient?.displayName ?: dialog.title ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if ((dialog.unreadCount ?: 0) > 0) FontWeight.Bold else FontWeight.Normal
                    )
                    dialog.lastMessageAt?.let {
                        Text(
                            text = it.relativeTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                dialog.lastMessage?.message?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
