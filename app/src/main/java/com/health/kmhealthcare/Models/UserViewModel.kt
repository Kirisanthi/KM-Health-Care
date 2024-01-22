package com.health.kmhealthcare.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.health.kmhealthcare.Repository.UserRepository

class UserViewModel {
    private val repository : UserRepository
    private val _allUsers = MutableLiveData<List<User>>()
    val allUsers : LiveData<List<User>> = _allUsers


    init {

        repository = UserRepository().getInstance()
        repository.loadUsers(_allUsers)

    }

}