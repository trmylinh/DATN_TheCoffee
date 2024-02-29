package com.example.thecoffee.viewmodel

import android.app.Application
import android.media.Image
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thecoffee.data.models.ResponseState
import com.example.thecoffee.data.models.User
import com.example.thecoffee.data.repositories.AuthenticationRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: AuthenticationRepository
    private var userData: MutableLiveData<FirebaseUser>
    private var loggedStatus: MutableLiveData<Boolean>
    private var user:MutableLiveData<User>

    init {
        repository = AuthenticationRepository(application)
        userData = repository.getFirebaseUser
        loggedStatus = repository.checkLogged
        user = repository.getUserDetail
    }
    val getLoggedStatus: MutableLiveData<Boolean>
        get() = loggedStatus
    val getUserData: MutableLiveData<FirebaseUser>
        get() = userData

    val getUserDetail: MutableLiveData<User>
        get() = user

    val checkUserImageUpdated: MutableLiveData<Boolean>
        get() = repository.checkUserImageUpdated

    val checkUserNameUpdated: MutableLiveData<Boolean>
        get() = repository.checkUserNameUpdated

    val checkUserPhoneUpdated: MutableLiveData<Boolean>
        get() = repository.checkUserPhoneUpdated


    fun signInWithGoogle(googleAuthCredential: AuthCredential) {
        repository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun getUserDetail(userId: String){
        repository.getUserDetail(userId)
    }

    fun updateUserName(userId: String, newName: String){
        repository.updateUserName(userId, newName)
    }

    fun updateUserPhone(userId: String, newPhone: String){
        repository.updateUserPhone(userId, newPhone)
    }

    fun updateUserImage(imageUri: Uri){
        repository.updateUserImage(imageUri)
    }

    fun signOut(){
        repository.signOut()
    }
}