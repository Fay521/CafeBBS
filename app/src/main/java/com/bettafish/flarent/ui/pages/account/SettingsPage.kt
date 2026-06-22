package com.bettafish.flarent.ui.pages.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.App
import com.bettafish.flarent.R
import com.bettafish.flarent.ui.theme.AppThemeMode
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.setting.item.DropdownSetting
import com.bettafish.flarent.ui.widgets.setting.item.TextSetting
import com.bettafish.flarent.utils.appSettings
import com.bettafish.flarent.utils.dataStore
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EditProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

val LocalPrefsDataStore: ProvidableCompositionLocal<DataStore<Preferences>> = compositionLocalOf {
    error("LocalPrefsDataStore not provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingsPage(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val datastore = context.dataStore
    CompositionLocalProvider(LocalPrefsDataStore provides datastore) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings)) },

                    navigationIcon = {
                        BackNavigationIcon { navigator.navigateUp() }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val themeMode = App.INSTANCE.appSettings.themeMode ?: AppThemeMode.SYSTEM.value
                val themeLabels = AppThemeMode.entries.associate { it.value to stringResource(it.labelRes) }
                DropdownSetting(
                    title = stringResource(R.string.theme),
                    leadingIcon = {
                        val icon = when(AppThemeMode.fromPreference(themeMode)){
                            AppThemeMode.SYSTEM -> Icons.Default.WbSunny
                            AppThemeMode.DARK -> Icons.Default.DarkMode
                            AppThemeMode.LIGHT -> Icons.Default.WbSunny
                            AppThemeMode.CAFE_LIGHT -> Icons.Default.WbSunny
                            AppThemeMode.CAFE_DARK -> Icons.Default.DarkMode
                        }
                        Icon(icon, null)
                    },
                    summary = stringResource(AppThemeMode.fromPreference(themeMode).labelRes),
                    key = themeMode,
                    entries = themeLabels,
                    onValueChange = {
                        App.INSTANCE.appSettings.themeMode = it
                    }
                )

                TextSetting(
                    title = stringResource(R.string.edit_profile),
                    minimalHeight = true,
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    onClick = { navigator.navigate(EditProfilePageDestination) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            App.INSTANCE.appSettings.token = null
                            App.INSTANCE.appSettings.userId = null
                            App.INSTANCE.appSettings.user = null
                            navigator.navigateUp()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.logout))
                    }
                }
            }
        }
    }


}
