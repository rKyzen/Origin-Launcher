package app.lawnchair.ui.preferences.destinations

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.dp
import app.lawnchair.ui.preferences.components.layout.PreferenceGroup
import app.lawnchair.ui.preferences.components.layout.TwoTabPreferenceLayout
import com.android.launcher3.R

@Composable
fun WidgetPreferences(
    modifier: Modifier = Modifier,
) {
    TwoTabPreferenceLayout(
        label = stringResource(id = R.string.widgets_label),
        modifier = modifier,
        backArrowVisible = true,
        firstPageLabel = stringResource(R.string.widgets_standard),
        firstPageContent = {
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceGroup {
                Item {
                    Text(
                        text = stringResource(R.string.widgets_standard_description),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        secondPageLabel = stringResource(R.string.widgets_origin),
        secondPageContent = {
            Spacer(modifier = Modifier.height(8.dp))
            PreferenceGroup {
                Item {
                    Text(
                        text = stringResource(R.string.widgets_origin_description),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
    )
}
