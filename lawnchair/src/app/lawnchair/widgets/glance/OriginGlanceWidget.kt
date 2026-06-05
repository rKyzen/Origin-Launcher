package app.lawnchair.widgets.glance

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.lawnchair.smartspace.model.SmartspaceTarget
import app.lawnchair.smartspace.provider.SmartspaceProvider
import app.lawnchair.widgets.OriginWidgetStyle

@Composable
fun OriginGlanceWidget(style: OriginWidgetStyle) {
    val context = LocalContext.current
    val provider = remember { SmartspaceProvider.INSTANCE.get(context) }
    val targets by provider.targets.collectAsState(initial = emptyList())

    when (style) {
        OriginWidgetStyle.PURE -> OriginPureGlance(targets)
        OriginWidgetStyle.FLOW -> OriginFlowGlance(targets)
    }
}

@Composable
fun OriginPureGlance(targets: List<SmartspaceTarget>) {
    Column {
        targets.forEach { target ->
            target.headerAction?.let { action ->
                Text(text = action.title.toString())
                action.subtitle?.let { Text(text = it.toString()) }
            }
        }
    }
}

@Composable
fun OriginFlowGlance(targets: List<SmartspaceTarget>) {
    Column {
        targets.forEach { target ->
            target.headerAction?.let { action ->
                Text(text = action.title.toString())
                action.subtitle?.let { Text(text = it.toString()) }
            }
        }
    }
}
