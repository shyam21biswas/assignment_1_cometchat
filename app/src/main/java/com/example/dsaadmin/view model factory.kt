package com.example.dsaadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser

class HomeViewModelFactory(private val user: FirebaseUser?) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}