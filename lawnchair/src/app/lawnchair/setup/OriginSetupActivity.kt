package app.lawnchair.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.origin.OriginMode
import app.lawnchair.origin.OriginModeManager
import app.lawnchair.theme.color.ColorOption
import app.lawnchair.preferences2.PreferenceManager2
import app.lawnchair.ui.preferences.components.OriginModeSelector
import app.lawnchair.ui.theme.LawnchairTheme
import kotlinx.coroutines.runBlocking

class OriginSetupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LawnchairTheme {
                SetupWizard()
            }
        }
    }

    companion object {
        private const val EXTRA_REDIRECT = "redirect_to_launcher"
        fun createIntent(context: Context): Intent {
            return Intent(context, OriginSetupActivity::class.java)
        }
    }
}

@Composable
private fun SetupWizard() {
    val context = LocalContext.current
    val prefs2 = PreferenceManager2.getInstance(context)
    val modeManager = OriginModeManager.getInstance(context)
    var currentPage by remember { mutableStateOf(0) }
    var selectedMode by remember { mutableStateOf(OriginMode.Pure) }

    fun finishSetup() {
        runBlocking {
            prefs2.originMode.set(selectedMode)
            prefs2.firstRunCompleted.set(true)
            modeManager.applyConfig(selectedMode.config())
        }
        val intent = context.packageManager.getLaunchIntentForPackage(
            context.packageName,
        )?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        if (intent != null) {
            context.startActivity(intent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Crossfade(
                targetState = currentPage,
                animationSpec = tween(300),
                label = "page",
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> ModePage(
                        selectedMode = selectedMode,
                        onModeSelected = { selectedMode = it },
                    )
                    2 -> AccentPage()
                    3 -> CompletePage(selectedMode = selectedMode)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = {
                runBlocking {
                    prefs2.firstRunCompleted.set(true)
                }
                val intent = context.packageManager.getLaunchIntentForPackage(
                    context.packageName,
                )?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                if (intent != null) {
                    context.startActivity(intent)
                }
            }) {
                Text("Skip")
            }

            PageIndicator(currentPage, 4)

            Button(onClick = {
                when {
                    currentPage < 3 -> currentPage++
                    else -> finishSetup()
                }
            }) {
                if (currentPage < 3) {
                    Text("Next")
                } else {
                    Icon(Icons.Rounded.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Finish")
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Welcome to Origin Launcher",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "A launcher with two distinct experiences — choose the one that fits your style.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Pure: Minimal, clean, Pixel-inspired.\nFlow: Monochrome, glassy, Nothing-inspired.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ModePage(
    selectedMode: OriginMode,
    onModeSelected: (OriginMode) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Choose Your Experience",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "This sets your home screen look, icons, dock, and more.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        OriginModeSelector(
            selectedMode = selectedMode,
            onModeSelected = onModeSelected,
        )
    }
}

@Composable
private fun AccentPage() {
    val context = LocalContext.current
    val prefs2 = PreferenceManager2.getInstance(context)
    val allColors = listOf(
        0xFF6750A4.toInt() to "Purple",
        0xFF0077FF.toInt() to "Blue",
        0xFFE91E63.toInt() to "Pink",
        0xFFFF5722.toInt() to "Orange",
        0xFF4CAF50.toInt() to "Green",
        0xFF607D8B.toInt() to "Gray",
        0xFF9C27B0.toInt() to "Violet",
        0xFF795548.toInt() to "Brown",
    )
    var selectedColorIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Pick an Accent Color",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Choose a color that matches your style. You can change it anytime.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            allColors.chunked(4).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    row.forEach { (colorInt, _) ->
                        val idx = allColors.indexOfFirst { it.first == colorInt }
                        val isSelected = idx == selectedColorIndex
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(colorInt))
                                .clickable {
                                    selectedColorIndex = idx
                                    runBlocking {
                                        prefs2.accentColor.set(ColorOption.CustomColor(colorInt))
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletePage(selectedMode: OriginMode) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "You're All Set!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Your home screen is ready.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Row(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Experience",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(id = selectedMode.displayName),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun PageIndicator(current: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == current) 24.dp else 8.dp, 8.dp)
                    .background(
                        if (index <= current) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp),
                    ),
            )
        }
    }
}
