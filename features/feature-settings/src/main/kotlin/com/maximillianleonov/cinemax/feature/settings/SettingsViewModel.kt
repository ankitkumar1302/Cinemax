/*
 * Copyright 2022 Maximillian Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maximillianleonov.cinemax.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximillianleonov.cinemax.core.ui.R
import com.maximillianleonov.cinemax.core.ui.common.EventHandler
import com.maximillianleonov.cinemax.core.ui.model.UserMessage
import com.maximillianleonov.cinemax.domain.usecase.GetSettingsVersionUseCase
import com.maximillianleonov.cinemax.domain.usecase.SettingsClearCacheUseCase
import com.maximillianleonov.cinemax.feature.settings.common.SettingsGroup
import com.maximillianleonov.cinemax.feature.settings.common.SettingsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsClearCacheUseCase: SettingsClearCacheUseCase,
    private val getSettingsVersionUseCase: GetSettingsVersionUseCase
) : ViewModel(), EventHandler<SettingsEvent> {
    private val _uiState = MutableStateFlow(getInitialUiState())
    val uiState = _uiState.asStateFlow()

    override fun onEvent(event: SettingsEvent) = when (event) {
        SettingsEvent.ClearCache -> onClearCache()
        SettingsEvent.ClearUserMessage -> onClearUserMessage()
    }

    private fun getInitialUiState(): SettingsUiState {
        val version = getSettingsVersionUseCase()

        val generalSettingsItems = listOf(
            SettingsItem.Action(
                iconResourceId = R.drawable.ic_trash,
                titleResourceId = R.string.clear_cache,
                onClick = { onEvent(SettingsEvent.ClearCache) }
            )
        )

        val moreSettingsItems = listOf(
            SettingsItem.Info(
                iconResourceId = R.drawable.ic_info,
                titleResourceId = R.string.version,
                value = version
            )
        )

        val generalSettingsGroup = SettingsGroup(
            titleResourceId = R.string.general,
            settingsItems = generalSettingsItems
        )

        val moreSettingsGroup = SettingsGroup(
            titleResourceId = R.string.more,
            settingsItems = moreSettingsItems
        )

        val settingsGroups = listOf(generalSettingsGroup, moreSettingsGroup)
        return SettingsUiState(settingsGroups = settingsGroups)
    }

    private fun onClearCache() {
        viewModelScope.launch { settingsClearCacheUseCase() }
        setUserMessage(UserMessage(messageResourceId = R.string.cache_cleared))
    }

    private fun setUserMessage(userMessage: UserMessage) =
        _uiState.update { it.copy(userMessage = userMessage) }

    private fun onClearUserMessage() = _uiState.update { it.copy(userMessage = null) }
}