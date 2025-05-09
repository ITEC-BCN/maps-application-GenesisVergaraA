package com.example.mapsapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.data.SupabaseManager
import com.example.mapsapp.utils.AuthResponse
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val authManager = SupabaseManager()

    private val _email = MutableLiveData<String>()
    val email = _email
    private val _password = MutableLiveData<String>()
    val password = _password
    private val _authState = MutableLiveData<AuthResponse>()
    val authState = _authState
    private val _showError = MutableLiveData<Boolean>(false)
    val showError = _showError

    fun editEmail(value: String){
        _email.value = value
    }

    fun editPassword(value: String){
        _password.value = value
    }

    fun errorMessageShowed(){
        _showError.value = false
    }

    fun signUp() {
        viewModelScope.launch {
            _authState.value = authManager.signUpWithEmail(_email.value!!, _password.value!!)
            if(_authState.value is AuthResponse.Error){
                _showError.value = true
            }
        }
    }

    fun signIn() {
        viewModelScope.launch {
            _authState.value = authManager.signInWithEmail(_email.value!!, _password.value!!)
            if(_authState.value is AuthResponse.Error){
                _showError.value = true
            }
        }
    }


}