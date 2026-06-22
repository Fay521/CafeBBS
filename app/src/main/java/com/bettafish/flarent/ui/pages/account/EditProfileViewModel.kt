package com.bettafish.flarent.ui.pages.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.App
import com.bettafish.flarent.data.UsersRepository
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.SuspendCommand3
import com.bettafish.flarent.utils.appSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val repository: UsersRepository
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveResult = MutableStateFlow<Boolean?>(null)
    val saveResult: StateFlow<Boolean?> = _saveResult

    val saveCommand = SuspendCommand3<String, String, String>({ displayName, bio, birthday ->
        saveProfile(displayName, bio, birthday)
    }, viewModelScope)

    init {
        viewModelScope.launch {
            val currentUser = App.INSTANCE.appSettings.user
            if (currentUser != null) {
                _user.value = currentUser
            }
        }
    }

    private suspend fun saveProfile(displayName: String, bio: String, birthday: String) {
        try {
            _isSaving.value = true
            val userId = App.INSTANCE.appSettings.userId ?: return
            val update = User().apply {
                id = userId
                this.displayName = displayName
                this.bio = bio.takeIf { it.isNotBlank() }
                this.birthday = birthday.takeIf { it.isNotBlank() }
            }
            val updatedUser = repository.updateUser(userId, update)
            // Refresh cached user
            App.INSTANCE.appSettings.user = updatedUser
            _user.value = updatedUser
            _saveResult.value = true
        } catch (_: Exception) {
            _saveResult.value = false
        } finally {
            _isSaving.value = false
        }
    }
}
