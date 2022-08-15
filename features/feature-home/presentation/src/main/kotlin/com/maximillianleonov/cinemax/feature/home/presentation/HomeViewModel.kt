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

package com.maximillianleonov.cinemax.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maximillianleonov.cinemax.core.presentation.common.EventHandler
import com.maximillianleonov.cinemax.core.presentation.mapper.toMovie
import com.maximillianleonov.cinemax.core.presentation.util.handle
import com.maximillianleonov.cinemax.domain.model.MovieModel
import com.maximillianleonov.cinemax.domain.usecase.GetUpcomingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase
) : ViewModel(), EventHandler<HomeEvent> {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    @Suppress("UnusedPrivateMember")
    private var upcomingMoviesJob = loadUpcomingMovies()

    override fun onEvent(event: HomeEvent) = Unit

    private fun loadUpcomingMovies() = viewModelScope.launch {
        getUpcomingMoviesUseCase().handle(
            onLoading = { upcomingMovies ->
                _uiState.update {
                    it.copy(
                        upcomingMovies = upcomingMovies?.map(MovieModel::toMovie).orEmpty(),
                        isUpcomingMoviesLoading = true
                    )
                }
            },
            onSuccess = { upcomingMovies ->
                _uiState.update {
                    it.copy(
                        upcomingMovies = upcomingMovies.map(MovieModel::toMovie),
                        isUpcomingMoviesLoading = false
                    )
                }
            },
            onFailure = { throwable ->
                _uiState.update {
                    it.copy(
                        error = throwable,
                        isUpcomingMoviesLoading = false
                    )
                }
            }
        )
    }
}