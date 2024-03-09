package com.example.thecoffee.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.models.User
import com.example.thecoffee.repositories.AuthenticationRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: AuthenticationRepository
    private var userData: MutableLiveData<FirebaseUser>
    private var loggedStatus: MutableLiveData<Boolean>
    private val loadingState: MutableLiveData<Boolean>
    private var user:MutableLiveData<User>

    init {
        repository = AuthenticationRepository(application)
        userData = repository.getFirebaseUser
        loggedStatus = repository.checkLogged
        loadingState = repository.checkLoadingState
        user = repository.getUserDetail
    }
    val getLoggedStatus: MutableLiveData<Boolean>
        get() = loggedStatus

    val getLoadingState: MutableLiveData<Boolean>
        get() = loadingState

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