package com.example.thecoffee.other.login.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.other.user.model.User
import com.example.thecoffee.other.login.repository.AuthenticationRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential

class AuthenticationViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: AuthenticationRepository
    private var userData: MutableLiveData<User>
    private var loggedStatus: MutableLiveData<Boolean>
    private val loadingState: MutableLiveData<Boolean>
    private var isNewUser: MutableLiveData<Boolean>
    private var uidUser: MutableLiveData<String>
    private var user:MutableLiveData<User>

    init {
        repository = AuthenticationRepository(application)
        userData = repository.getFirebaseUser
        loggedStatus = repository.checkLogged
        loadingState = repository.checkLoadingState
        user = repository.getUserDetail
        isNewUser = repository.getIsNewUser
        uidUser = repository.getUidUser
    }
    val getLoggedStatus: MutableLiveData<Boolean>
        get() = loggedStatus

    val getIsNewUser: MutableLiveData<Boolean>
        get() = isNewUser

    val getUidUser: MutableLiveData<String>
        get() = uidUser

    val getLoadingState: MutableLiveData<Boolean>
        get() = loadingState

    val getUserData: MutableLiveData<User>
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

    fun signInWithPhone(credential: PhoneAuthCredential) {
        repository.firebaseSignInWithPhone(credential)
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

    fun updateUserEmail(userId: String, newEmail: String){
        repository.updateUserEmail(userId, newEmail)
    }

    fun updateUserImage(imageUri: Uri){
        repository.updateUserImage(imageUri)
    }

    fun signOut(){
        repository.signOut()
    }
}