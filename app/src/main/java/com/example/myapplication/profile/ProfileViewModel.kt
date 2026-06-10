package com.example.myapplication.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {

    object Loading : ProfileUiState()

    data class Success(
        val user: User,
        val friendStatus: FriendStatus,
        val isOwnProfile: Boolean
    ) : ProfileUiState()

    data class Error(
        val message: String
    ) : ProfileUiState()
}

sealed class ProfileAction {

    object Idle : ProfileAction()

    object Loading : ProfileAction()

    data class Success(
        val message: String
    ) : ProfileAction()

    data class Error(
        val message: String
    ) : ProfileAction()
}

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    private val _actionState =
        MutableStateFlow<ProfileAction>(ProfileAction.Idle)

    val actionState: StateFlow<ProfileAction> =
        _actionState.asStateFlow()

    private var currentUserId = 0

    fun loadProfile(
        targetUserId: Int,
        loggedInUserId: Int
    ) {

        currentUserId = loggedInUserId

        viewModelScope.launch {

            _uiState.value = ProfileUiState.Loading

            val userResult =
                repository.getUserProfile(targetUserId)

            if (userResult.isFailure) {

                _uiState.value = ProfileUiState.Error(
                    userResult.exceptionOrNull()?.message
                        ?: "Không tải được profile"
                )

                return@launch
            }

            val user = userResult.getOrNull()!!

            val isOwnProfile =
                targetUserId == loggedInUserId

            val friendStatus =
                if (isOwnProfile) {

                    FriendStatus.SELF

                } else {

                    val result = repository.checkFriendStatus(
                        loggedInUserId,
                        targetUserId
                    )

                    if (result.isSuccess) {
                        FriendStatus.from(
                            result.getOrNull()!!.status
                        )
                    } else {
                        FriendStatus.NONE
                    }
                }

            _uiState.value =
                ProfileUiState.Success(
                    user,
                    friendStatus,
                    isOwnProfile
                )
        }
    }

    fun sendFriendRequest(receiverId: Int) {

        viewModelScope.launch {

            _actionState.value = ProfileAction.Loading

            val result = repository.sendFriendRequest(
                currentUserId,
                receiverId
            )

            if (result.isSuccess) {

                _actionState.value =
                    ProfileAction.Success(
                        result.getOrNull()!!
                    )

            } else {

                _actionState.value =
                    ProfileAction.Error(
                        result.exceptionOrNull()?.message
                            ?: "Gửi lời mời thất bại"
                    )
            }
        }
    }

    fun updateProfile(
        username: String,
        bio: String,
        avatarUrl: String?
    ) {

        viewModelScope.launch {

            _actionState.value = ProfileAction.Loading

            val result = repository.updateProfile(
                currentUserId,
                username,
                bio,
                avatarUrl
            )

            if (result.isSuccess) {

                _actionState.value =
                    ProfileAction.Success(
                        "Cập nhật thành công"
                    )

            } else {

                _actionState.value =
                    ProfileAction.Error(
                        result.exceptionOrNull()?.message
                            ?: "Cập nhật thất bại"
                    )
            }
        }
    }

    fun resetActionState() {
        _actionState.value = ProfileAction.Idle
    }
}