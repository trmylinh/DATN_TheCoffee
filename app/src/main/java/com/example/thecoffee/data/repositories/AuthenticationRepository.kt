package com.example.thecoffee.data.repositories

import android.app.Application
import android.provider.Settings.Global.getString
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.R
import com.example.thecoffee.data.models.ResponseState
import com.example.thecoffee.data.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await

class AuthenticationRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersRef: CollectionReference = rootRef.collection("Users");

    //sign in using Google
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential) : MutableLiveData<ResponseState<User>> {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> = MutableLiveData()
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                var isNewUser = authTask.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val name = firebaseUser.displayName
                    val email = firebaseUser.email
                    val phone = firebaseUser.phoneNumber
                    val user = User(uid = uid, name = name, email = email, phone = phone)
                    user.isNew = isNewUser
                    usersRef.document(uid).set(user)
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }
            } else {
                authenticatedUserMutableLiveData.value = authTask.exception?.message?.let {
                    ResponseState.Error(it)
                }
            }
        }
        return authenticatedUserMutableLiveData
    }

}

