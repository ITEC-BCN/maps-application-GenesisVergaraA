package com.example.mapsapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.SupabaseApplication
import com.example.mapsapp.utils.AuthState
import com.example.mapsapp.utils.SharedPreferencesHelper
import kotlinx.coroutines.launch
import kotlin.math.abs

class AuthViewModel(private val sharedPreferences: SharedPreferencesHelper): ViewModel() {
    private val supabaseAuth = SupabaseApplication.supabase
    private val _email = MutableLiveData<String>()
    val email = _email
    private val _password = MutableLiveData<String>()
    val password = _password
    private val _authState = MutableLiveData<AuthState>()
    val authState = _authState
    private val _showError = MutableLiveData<Boolean>(false)
    val showError = _showError
    private val _user = MutableLiveData<String?>()
    val user = _user


    init {
        checkExistingSession()
    }

    //Registrarse
    fun signUp() {
        viewModelScope.launch {
            _authState.value = supabaseAuth.signUpWithEmail(_email.value!!, _password.value!!)
            if (_authState.value is AuthState.Error) {
                _showError.value = true
            } else {
                val session = supabaseAuth.retrieveCurrentSession()
                sharedPreferences.saveAuthData(
                    session!!.accessToken,
                    session.refreshToken
                )
            }
        }
    }

    //Iniciar sesión
    fun signIn() {
        viewModelScope.launch {
            _authState.value = supabaseAuth.signInWithEmail(_email.value!!, _password.value!!)
            if (_authState.value is AuthState.Error) {
                _showError.value = true
            } else {
                val session = supabaseAuth.retrieveCurrentSession()
                sharedPreferences.saveAuthData(
                    session!!.accessToken,
                    session.refreshToken
                )
                _user.value = createCoolUsername(session.user?.email)
            }
        }
    }

    //Comprobar sesión actual
    private fun checkExistingSession() {
        viewModelScope.launch {
            val accessToken = sharedPreferences.getAccessToken()
            val refreshToken = sharedPreferences.getRefreshToken()
            when {
                !accessToken.isNullOrEmpty() -> refreshToken()
                !refreshToken.isNullOrEmpty() -> refreshToken()
                else -> _authState.value = AuthState.Unauthenticated
            }
        }
    }

    //
    private fun refreshToken() {
        viewModelScope.launch {
            try {
                _authState.value = supabaseAuth.refreshSession()
                val session = supabaseAuth.retrieveCurrentSession()
                _user.value = createCoolUsername(session?.user?.email)
            } catch (e: Exception) {
                sharedPreferences.clear()
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    //Cerrar sessión
    fun logout() {
        viewModelScope.launch {
            sharedPreferences.clear()
            _authState.value = AuthState.Unauthenticated
        }
    }

    //Crear un nombre de usuario aleatorio (usando el hashcode del email)
    fun createCoolUsername(email: String?): String {
        val clean = email?.substringBefore('@')
            ?.replace(Regex("[^a-zA-Z]"), "")
            ?.take(6)
            ?.lowercase()
            .orEmpty()
        val stableHash = abs(email?.hashCode() ?: 0) % 1000
        return if (clean.isEmpty()) "user$stableHash"
        else clean + stableHash
    }

    //Mostrar errores
    fun errorMessageShowed(){
        _showError.value = false
    }

    //GETTERS I SETTERS
    fun editEmail(value: String){
        _email.value = value
    }

    fun editPassword(value: String){
        _password.value = value
    }

}