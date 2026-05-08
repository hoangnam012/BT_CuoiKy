package com.example.myapplication.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            repository.getUser().collect { user ->
                _userState.value = user
            }
        }
    }

    // Các action giả (sau này gọi API/DB)
    fun onMenuItemClick(menuId: String) {
        // Ví dụ: xử lý khi nhấn vào từng menu
        // Hiện tại chỉ log, sau này thay bằng navigation hoặc gọi DB
        android.util.Log.d("ProfileViewModel", "Clicked: $menuId")
    }
}