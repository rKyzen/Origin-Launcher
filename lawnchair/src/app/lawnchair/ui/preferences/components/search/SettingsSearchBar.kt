package app.lawnchair.ui.preferences.components.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.lawnchair.ui.preferences.navigation.PreferenceRootRoute

data class SettingsSearchEntry(
    val label: String,
    val route: PreferenceRootRoute,
)

private val settingsEntries = listOf(
    SettingsSearchEntry("Google Feed", app.lawnchair.ui.preferences.navigation.GoogleFeed),
    SettingsSearchEntry("Origin Mode", app.lawnchair.ui.preferences.navigation.OriginModes),
    SettingsSearchEntry("App Drawer", app.lawnchair.ui.preferences.navigation.AppDrawer),
    SettingsSearchEntry("About", app.lawnchair.ui.preferences.navigation.About),
    SettingsSearchEntry("Home Screen", app.lawnchair.ui.preferences.navigation.HomeScreen),
    SettingsSearchEntry("Dock", app.lawnchair.ui.preferences.navigation.Dock),
    SettingsSearchEntry("Search Bar", app.lawnchair.ui.preferences.navigation.Search()),
    SettingsSearchEntry("Folders", app.lawnchair.ui.preferences.navigation.Folders),
    SettingsSearchEntry("Gestures", app.lawnchair.ui.preferences.navigation.Gestures),
    SettingsSearchEntry("Recents", app.lawnchair.ui.preferences.navigation.Quickstep),
    SettingsSearchEntry("Backup & Restore", app.lawnchair.ui.preferences.navigation.BackupAndRestore),
    SettingsSearchEntry("Experimental Features", app.lawnchair.ui.preferences.navigation.ExperimentalFeatures),
)

private val PillShape = RoundedCornerShape(28.dp)

@Composable
fun SettingsSearchBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val results = remember(query) {
        if (query.isBlank()) {
            emptyList()
        } else {
            val q = query.lowercase()
            settingsEntries.filter { it.label.lowercase().contains(q) }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = PillShape,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = true }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
                if (isExpanded) {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                        placeholder = {
                            Text(
                                "Search settings",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { focusManager.clearFocus() },
                        ),
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        },
                    )
                } else {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search settings",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded && results.isNotEmpty(),
                enter = fadeIn() + slideInVertically { -it / 2 },
                exit = fadeOut() + slideOutVertically { -it / 2 },
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .padding(horizontal = 8.dp)
                            .imePadding(),
                    ) {
                        items(results) { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        query = ""
                                        isExpanded = false
                                        focusManager.clearFocus()
                                        navController.navigate(entry.route) {
                                            launchSingleTop = true
                                            popUpTo(navController.graph.id)
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = entry.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
