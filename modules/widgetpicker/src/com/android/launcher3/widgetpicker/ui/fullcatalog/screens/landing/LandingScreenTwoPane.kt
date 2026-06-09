/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Work
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.launcher3.widgetpicker.R
import com.android.launcher3.widgetpicker.shared.model.WidgetAppId
import com.android.launcher3.widgetpicker.shared.model.WidgetUserProfile
import com.android.launcher3.widgetpicker.ui.WidgetInteractionInfo
import com.android.launcher3.widgetpicker.ui.components.AppHeaderDescriptionStyle
import com.android.launcher3.widgetpicker.ui.components.LeadingIconToolbarTab
import com.android.launcher3.widgetpicker.ui.components.ScrollableFloatingToolbar
import com.android.launcher3.widgetpicker.ui.components.SelectableSuggestionsHeader
import com.android.launcher3.widgetpicker.ui.components.TwoPaneLayout
import com.android.launcher3.widgetpicker.ui.components.WidgetAppHeaderStyle
import com.android.launcher3.widgetpicker.ui.components.WidgetAppsList
import com.android.launcher3.widgetpicker.ui.components.WidgetsGrid
import com.android.launcher3.widgetpicker.ui.components.widgetPickerTestTag
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.APPS_TAB_INDEX
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.DEFAULT_SELECTED_TAB
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.ORIGIN_TAB_INDEX
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.TABS_COUNT_WITHOUT_WORK_PROFILE
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.TABS_COUNT_WITH_WORK_PROFILE
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.WORK_TAB_INDEX
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.contentShape
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.leftPaneContentBottomEdgeSpacing
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneDimens.pagerItemsSpacing
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneTestTags.FEATURED_WIDGETS_HEADER_TEST_TAG
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneTestTags.ORIGIN_WIDGETS_TAB_TEST_TAG
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneTestTags.APPS_WIDGETS_TAB_TEST_TAG
import com.android.launcher3.widgetpicker.ui.fullcatalog.screens.landing.LandingScreenTwoPaneTestTags.WORK_WIDGETS_TAB_TEST_TAG
import com.android.launcher3.widgetpicker.ui.model.DisplayableWidgetApp
import com.android.launcher3.widgetpicker.ui.theme.WidgetPickerTheme
import kotlinx.coroutines.launch

/**
 * A composable function that provides a two pane layout for landing screen of the full catalog of
 * widgets in the widget picker.
 *
 * Ideal for large screens.
 */
@Composable
fun LandingScreenTwoPane(
    searchBar: @Composable () -> Unit,
    featuredWidgets: @Composable () -> Unit,
    featuredWidgetsCount: Int,
    widgetAppIconsState: AppIconsState,
    browseWidgetsState: BrowseWidgetsState.Data,
    personalWidgetPreviewsState: PreviewsState,
    workWidgetPreviewsState: PreviewsState,
    selectedPersonalWidgetAppId: WidgetAppId?,
    onPersonalWidgetAppToggle: (WidgetAppId?) -> Unit,
    selectedWorkWidgetAppId: WidgetAppId?,
    onWorkWidgetAppToggle: (WidgetAppId?) -> Unit,
    onWidgetInteraction: (WidgetInteractionInfo) -> Unit,
    showDragShadow: Boolean,
) {
    val hasWorkProfile = remember(browseWidgetsState) { browseWidgetsState.workProfile != null }
    var isFeaturedSectionShowing by rememberSaveable { mutableStateOf(true) }
    val pageCount = remember {
        if (hasWorkProfile) {
            TABS_COUNT_WITH_WORK_PROFILE
        } else {
            TABS_COUNT_WITHOUT_WORK_PROFILE
        }
    }

    val pagerState =
        rememberPagerState(initialPage = DEFAULT_SELECTED_TAB, pageCount = { pageCount })

    val (originWidgetApps, thirdPartyWidgetApps) = remember(browseWidgetsState.personalWidgetApps) {
        browseWidgetsState.personalWidgetApps.partition {
            it.id.packageName.startsWith("app.lawnchair")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TwoPaneLayout(
            searchBar = searchBar,
            leftContent = {
                LeftPaneContent(
                    isFeaturedSectionSelected = isFeaturedSectionShowing,
                    featuredWidgetsCount = featuredWidgetsCount,
                    pagerState = pagerState,
                    hasWorkProfile = hasWorkProfile,
                    workUserProfile = browseWidgetsState.workProfile,
                    originWidgetApps = originWidgetApps,
                    thirdPartyWidgetApps = thirdPartyWidgetApps,
                    workWidgetApps = browseWidgetsState.workWidgetApps,
                    selectedPersonalWidgetAppId = selectedPersonalWidgetAppId,
                    widgetAppIconsState = widgetAppIconsState,
                    personalWidgetPreviewsState = personalWidgetPreviewsState,
                    selectedWorkWidgetAppId = selectedWorkWidgetAppId,
                    workWidgetPreviewsState = workWidgetPreviewsState,
                    onFeaturedHeaderClick = {
                        isFeaturedSectionShowing = true
                        onPersonalWidgetAppToggle(null)
                        onWorkWidgetAppToggle(null)
                    },
                    onPersonalWidgetAppToggle = { id ->
                        isFeaturedSectionShowing = false
                        onPersonalWidgetAppToggle(id)
                    },
                    onWorkWidgetAppToggle = { id ->
                        isFeaturedSectionShowing = false
                        onWorkWidgetAppToggle(id)
                    },
                    onWidgetInteraction = onWidgetInteraction,
                    showDragShadow = showDragShadow,
                )
            },
            rightPaneTitle =
                rightPaneTitle(
                    showingFeaturedTab = isFeaturedSectionShowing,
                    currentPageIndex = pagerState.currentPage,
                    browseWidgetsState = browseWidgetsState,
                    selectedPersonalWidgetAppId = selectedPersonalWidgetAppId,
                    selectedWorkWidgetAppId = selectedWorkWidgetAppId,
                ),
            rightContent = {
                RightPaneContent(
                    pagerState = pagerState,
                    isFeaturedSectionSelected = isFeaturedSectionShowing,
                    featuredWidgets = featuredWidgets,
                    originWidgetApps = originWidgetApps,
                    thirdPartyWidgetApps = thirdPartyWidgetApps,
                    workWidgetApps = browseWidgetsState.workWidgetApps,
                    selectedPersonalWidgetAppId = selectedPersonalWidgetAppId,
                    personalWidgetPreviewsState = personalWidgetPreviewsState,
                    selectedWorkWidgetAppId = selectedWorkWidgetAppId,
                    workWidgetPreviewsState = workWidgetPreviewsState,
                    onWidgetInteraction = onWidgetInteraction,
                    showDragShadow = showDragShadow,
                )
            },
        )
    }
}

/**
 * Title for the right pane that is updated when selected tab on left changes. When set, a talkback
 * user can use four finger swipe down to switch to right pane.
 */
@Composable
private fun rightPaneTitle(
    showingFeaturedTab: Boolean,
    currentPageIndex: Int,
    browseWidgetsState: BrowseWidgetsState.Data,
    selectedPersonalWidgetAppId: WidgetAppId?,
    selectedWorkWidgetAppId: WidgetAppId?,
): String? {
    val selectedAppName: CharSequence? =
        when (currentPageIndex) {
            ORIGIN_TAB_INDEX, APPS_TAB_INDEX ->
                selectedPersonalWidgetAppId?.let { selectedId ->
                    browseWidgetsState.personalWidgetApps.find { it.id == selectedId }?.title
                }
            WORK_TAB_INDEX ->
                selectedWorkWidgetAppId?.let { selectedId ->
                    browseWidgetsState.workWidgetApps.find { it.id == selectedId }?.title
                }
            else -> null
        }

    return if (showingFeaturedTab) {
        stringResource(
            R.string.widget_picker_right_pane_accessibility_label,
            stringResource(R.string.featured_widgets_tab_label),
        )
    } else if (selectedAppName != null) {
        stringResource(R.string.widget_picker_right_pane_accessibility_label, selectedAppName)
    } else {
        null
    }
}

@Composable
private fun RightPaneContent(
    pagerState: PagerState,
    isFeaturedSectionSelected: Boolean,
    featuredWidgets: @Composable () -> Unit,
    originWidgetApps: List<DisplayableWidgetApp>,
    thirdPartyWidgetApps: List<DisplayableWidgetApp>,
    workWidgetApps: List<DisplayableWidgetApp>,
    selectedPersonalWidgetAppId: WidgetAppId?,
    personalWidgetPreviewsState: PreviewsState,
    selectedWorkWidgetAppId: WidgetAppId?,
    workWidgetPreviewsState: PreviewsState,
    onWidgetInteraction: (WidgetInteractionInfo) -> Unit,
    showDragShadow: Boolean,
) {
    when {
        isFeaturedSectionSelected -> featuredWidgets()

        pagerState.currentPage == ORIGIN_TAB_INDEX -> {
            selectedPersonalWidgetAppId?.let {
                val selectedWidgets =
                    remember(selectedPersonalWidgetAppId, originWidgetApps) {
                        selectedPersonalWidgetAppId.let { selectedId ->
                            originWidgetApps
                                .find { it.id == selectedId }
                                ?.widgetSizeGroups
                        } ?: listOf()
                    }

                WidgetsGrid(
                    modifier = Modifier.fillMaxWidth().wrapContentSize(),
                    showAllWidgetDetails = true,
                    widgetSizeGroups = selectedWidgets,
                    previews = personalWidgetPreviewsState.previews,
                    onWidgetInteraction = onWidgetInteraction,
                    showDragShadow = showDragShadow,
                )
            }
        }

        pagerState.currentPage == APPS_TAB_INDEX -> {
            selectedPersonalWidgetAppId?.let {
                val selectedWidgets =
                    remember(selectedPersonalWidgetAppId, thirdPartyWidgetApps) {
                        selectedPersonalWidgetAppId.let { selectedId ->
                            thirdPartyWidgetApps
                                .find { it.id == selectedId }
                                ?.widgetSizeGroups
                        } ?: listOf()
                    }

                WidgetsGrid(
                    modifier = Modifier.fillMaxWidth().wrapContentSize(),
                    showAllWidgetDetails = true,
                    widgetSizeGroups = selectedWidgets,
                    previews = personalWidgetPreviewsState.previews,
                    onWidgetInteraction = onWidgetInteraction,
                    showDragShadow = showDragShadow,
                )
            }
        }

        pagerState.currentPage == WORK_TAB_INDEX -> {
            selectedWorkWidgetAppId?.let {
                val selectedWidgets =
                    remember(selectedWorkWidgetAppId, workWidgetApps) {
                        selectedWorkWidgetAppId.let { selectedId ->
                            workWidgetApps
                                .find { it.id == selectedId }
                                ?.widgetSizeGroups
                        } ?: listOf()
                    }

                WidgetsGrid(
                    modifier = Modifier.fillMaxWidth().wrapContentSize(),
                    showAllWidgetDetails = true,
                    widgetSizeGroups = selectedWidgets,
                    previews = workWidgetPreviewsState.previews,
                    onWidgetInteraction = onWidgetInteraction,
                    showDragShadow = showDragShadow,
                )
            }
        }
    }
}

@Composable
private fun LeftPaneContent(
    isFeaturedSectionSelected: Boolean,
    onFeaturedHeaderClick: () -> Unit,
    featuredWidgetsCount: Int,
    pagerState: PagerState,
    hasWorkProfile: Boolean,
    workUserProfile: WidgetUserProfile?,
    originWidgetApps: List<DisplayableWidgetApp>,
    thirdPartyWidgetApps: List<DisplayableWidgetApp>,
    workWidgetApps: List<DisplayableWidgetApp>,
    selectedPersonalWidgetAppId: WidgetAppId?,
    widgetAppIconsState: AppIconsState,
    personalWidgetPreviewsState: PreviewsState,
    selectedWorkWidgetAppId: WidgetAppId?,
    workWidgetPreviewsState: PreviewsState,
    onPersonalWidgetAppToggle: (WidgetAppId) -> Unit,
    onWorkWidgetAppToggle: (WidgetAppId) -> Unit,
    onWidgetInteraction: (WidgetInteractionInfo) -> Unit,
    showDragShadow: Boolean,
) {
    val leftPaneContentBottomEdgeSpacing =
        leftPaneContentBottomEdgeSpacing(hasFloatingToolbar = hasWorkProfile)

    val originLabel = stringResource(R.string.widgets_section_origin_label)
    val appsLabel = stringResource(R.string.widgets_section_apps_label)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SelectableSuggestionsHeader(
                selected = isFeaturedSectionSelected,
                onSelect = onFeaturedHeaderClick,
                count = featuredWidgetsCount,
                shape = contentShape,
                modifier =
                    Modifier.fillMaxWidth().widgetPickerTestTag(FEATURED_WIDGETS_HEADER_TEST_TAG),
            )
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                pageSpacing = pagerItemsSpacing,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) { index ->
                when (index) {
                    ORIGIN_TAB_INDEX -> {
                        OriginSection(
                            originWidgetApps = originWidgetApps,
                            selectedPersonalWidgetAppId = selectedPersonalWidgetAppId,
                            widgetAppIconsState = widgetAppIconsState,
                            personalWidgetPreviewsState = personalWidgetPreviewsState,
                            onPersonalWidgetAppToggle = onPersonalWidgetAppToggle,
                            onWidgetInteraction = onWidgetInteraction,
                            showDragShadow = showDragShadow,
                            bottomContentSpacing = leftPaneContentBottomEdgeSpacing,
                        )
                    }

                    APPS_TAB_INDEX -> {
                        AppsSection(
                            thirdPartyWidgetApps = thirdPartyWidgetApps,
                            selectedPersonalWidgetAppId = selectedPersonalWidgetAppId,
                            widgetAppIconsState = widgetAppIconsState,
                            personalWidgetPreviewsState = personalWidgetPreviewsState,
                            onPersonalWidgetAppToggle = onPersonalWidgetAppToggle,
                            onWidgetInteraction = onWidgetInteraction,
                            showDragShadow = showDragShadow,
                            bottomContentSpacing = leftPaneContentBottomEdgeSpacing,
                        )
                    }

                    WORK_TAB_INDEX ->
                        if (hasWorkProfile) {
                            WorkSection(
                                workWidgetApps = workWidgetApps,
                                selectedWorkWidgetAppId = selectedWorkWidgetAppId,
                                widgetAppIconsState = widgetAppIconsState,
                                workWidgetPreviewsState = workWidgetPreviewsState,
                                onWorkWidgetAppToggle = onWorkWidgetAppToggle,
                                onWidgetInteraction = onWidgetInteraction,
                                showDragShadow = showDragShadow,
                                bottomContentSpacing = leftPaneContentBottomEdgeSpacing,
                            )
                        }
                }
            }
        }
        WorkToolbar(
            workUserProfile = workUserProfile,
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun OriginSection(
    originWidgetApps: List<DisplayableWidgetApp>,
    selectedPersonalWidgetAppId: WidgetAppId?,
    widgetAppIconsState: AppIconsState,
    personalWidgetPreviewsState: PreviewsState,
    onPersonalWidgetAppToggle: (WidgetAppId) -> Unit,
    onWidgetInteraction: (WidgetInteractionInfo) -> Unit,
    showDragShadow: Boolean,
    bottomContentSpacing: Dp,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        WidgetAppsList(
            modifier = Modifier.fillMaxSize(),
            widgetApps = originWidgetApps,
            selectedWidgetAppId = selectedPersonalWidgetAppId,
            widgetAppHeaderStyle = WidgetAppHeaderStyle.CLICKABLE,
            headerDescriptionStyle = AppHeaderDescriptionStyle.WIDGETS_COUNT,
            appIcons = widgetAppIconsState.icons,
            widgetPreviews = personalWidgetPreviewsState.previews,
            onWidgetAppClick = { widgetApp -> onPersonalWidgetAppToggle(widgetApp.id) },
            onWidgetInteraction = onWidgetInteraction,
            showDragShadow = showDragShadow,
            bottomContentSpacing = bottomContentSpacing,
        )
    }
}

@Composable
private fun AppsSection(
    thirdPartyWidgetApps: List<DisplayableWidgetApp>,
    selectedPersonalWidgetAppId: WidgetAppId?,
    widgetAppIconsState: AppIconsState,
    personalWidgetPreviewsState: PreviewsState,
    onPersonalWidgetAppToggle: (WidgetAppId) -> Unit,
    onWidgetInteraction: (WidgetInteractionInfo) -> Unit,
    showDragShadow: Boolean,
    bottomContentSpacing: Dp,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        WidgetAppsList(
            modifier = Modifier.fillMaxSize(),
            widgetApps = thirdPartyWidgetApps,
            selectedWidgetAppId = selectedPersonalWidgetAppId,
            widgetAppHeaderStyle = WidgetAppHeaderStyle.CLICKABLE,
            headerDescriptionStyle = AppHeaderDescriptionStyle.WIDGETS_COUNT,
            appIcons = widgetAppIconsState.icons,
            widgetPreviews = personalWidgetPreviewsState.previews,
            onWidgetAppClick = { widgetApp -> onPersonalWidgetAppToggle(widgetApp.id) },
            onWidgetInteraction = onWidgetInteraction,
            showDragShadow = showDragShadow,
            bottomContentSpacing = bottomContentSpacing,
        )
    }
}

@Composable
private fun WorkSection(
    workWidgetApps: List<DisplayableWidgetApp>,
    selectedWorkWidgetAppId: WidgetAppId?,
    widgetAppIconsState: AppIconsState,
    workWidgetPreviewsState: PreviewsState,
    onWorkWidgetAppToggle: (WidgetAppId) -> Unit,
    onWidgetInteraction: (WidgetInteractionInfo) -> Unit,
    showDragShadow: Boolean,
    bottomContentSpacing: Dp,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        WidgetAppsList(
            modifier = Modifier.fillMaxSize(),
            widgetApps = workWidgetApps,
            selectedWidgetAppId = selectedWorkWidgetAppId,
            widgetAppHeaderStyle = WidgetAppHeaderStyle.CLICKABLE,
            headerDescriptionStyle = AppHeaderDescriptionStyle.WIDGETS_COUNT,
            appIcons = widgetAppIconsState.icons,
            widgetPreviews = workWidgetPreviewsState.previews,
            onWidgetAppClick = { widgetApp -> onWorkWidgetAppToggle(widgetApp.id) },
            onWidgetInteraction = onWidgetInteraction,
            showDragShadow = showDragShadow,
            bottomContentSpacing = bottomContentSpacing,
            emptyWidgetsErrorMessage =
                workWidgetApps.let { apps ->
                    null // work paused message removed, simplified
                },
        )
    }
}

@Composable
private fun WorkToolbar(
    workUserProfile: WidgetUserProfile?,
    pagerState: PagerState,
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    val tabs: List<@Composable () -> Unit> =
        remember(currentPage) {
            buildList {
                add {
                    LeadingIconToolbarTab(
                        label = stringResource(R.string.widgets_section_origin_label),
                        leadingIcon = Icons.Filled.Home,
                        selected = currentPage == ORIGIN_TAB_INDEX,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(ORIGIN_TAB_INDEX) }
                        },
                        modifier = Modifier.widgetPickerTestTag(ORIGIN_WIDGETS_TAB_TEST_TAG),
                    )
                }
                add {
                    LeadingIconToolbarTab(
                        label = stringResource(R.string.widgets_section_apps_label),
                        leadingIcon = Icons.AutoMirrored.Filled.List,
                        selected = currentPage == APPS_TAB_INDEX,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(APPS_TAB_INDEX) }
                        },
                        modifier = Modifier.widgetPickerTestTag(APPS_WIDGETS_TAB_TEST_TAG),
                    )
                }
                if (workUserProfile != null) {
                    add {
                        LeadingIconToolbarTab(
                            label = workUserProfile.label,
                            leadingIcon = Icons.Outlined.Work,
                            selected = currentPage == WORK_TAB_INDEX,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(WORK_TAB_INDEX) }
                            },
                            modifier = Modifier.widgetPickerTestTag(WORK_WIDGETS_TAB_TEST_TAG),
                        )
                    }
                }
            }
        }

    ScrollableFloatingToolbar(modifier = modifier, selectedTabIndex = currentPage, tabs = tabs)
}

private object LandingScreenTwoPaneDimens {
    val contentShape = RoundedCornerShape(24.dp)
    val pagerItemsSpacing = 8.dp

    fun leftPaneContentBottomEdgeSpacing(hasFloatingToolbar: Boolean) =
        if (hasFloatingToolbar) {
            70.dp
        } else 0.dp

    const val TABS_COUNT_WITH_WORK_PROFILE = 3
    const val TABS_COUNT_WITHOUT_WORK_PROFILE = 2

    const val ORIGIN_TAB_INDEX = 0
    const val APPS_TAB_INDEX = 1
    const val WORK_TAB_INDEX = 2
    const val DEFAULT_SELECTED_TAB = ORIGIN_TAB_INDEX
}

private object LandingScreenTwoPaneTestTags {
    const val FEATURED_WIDGETS_HEADER_TEST_TAG = "featured_widgets_tab"
    const val ORIGIN_WIDGETS_TAB_TEST_TAG = "origin_widgets_tab"
    const val APPS_WIDGETS_TAB_TEST_TAG = "apps_widgets_tab"
    const val WORK_WIDGETS_TAB_TEST_TAG = "work_widgets_tab"
}
