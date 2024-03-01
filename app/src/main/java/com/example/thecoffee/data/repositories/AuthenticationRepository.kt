package com.example.thecoffee.data.repositories

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.data.models.ResponseState
import com.example.thecoffee.data.models.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AuthenticationRepository(_application: Application) {
    private var application: Application
    private var firebaseUserMutableLiveData: MutableLiveData<FirebaseUser>
    private val loadingStateMutableLiveData: MutableLiveData<Boolean>
    private var userLoggedMutableLiveData: MutableLiveData<Boolean>
    private var userImageUpdated: MutableLiveData<Boolean>
    private var userNameUpdated: MutableLiveData<Boolean>
    private var userPhoneUpdated: MutableLiveData<Boolean>
    private var userMutableLiveData: MutableLiveData<User>
    private val auth: FirebaseAuth
    private val storageReference: StorageReference
    private val firestoreDatabase: FirebaseFirestore

    //    private val usersRef: CollectionReference = rootRef.collection("Users");

    val getFirebaseUser: MutableLiveData<FirebaseUser>
        get() = firebaseUserMutableLiveData

    val checkLoadingState : MutableLiveData<Boolean>
        get() = loadingStateMutableLiveData

    val checkLogged : MutableLiveData<Boolean>
        get() = userLoggedMutableLiveData

    val getUserDetail: MutableLiveData<User>
        get() = userMutableLiveData

    val checkUserNameUpdated: MutableLiveData<Boolean>
        get() = userNameUpdated

    val checkUserImageUpdated: MutableLiveData<Boolean>
        get() = userImageUpdated
    val checkUserPhoneUpdated: MutableLiveData<Boolean>
        get() = userPhoneUpdated

    init {
        application = _application
        firebaseUserMutableLiveData = MutableLiveData<FirebaseUser>()
        loadingStateMutableLiveData = MutableLiveData<Boolean>()
        userLoggedMutableLiveData = MutableLiveData<Boolean>()
        // userInfo
        userImageUpdated = MutableLiveData<Boolean>()
        userNameUpdated = MutableLiveData<Boolean>()
        userPhoneUpdated = MutableLiveData<Boolean>()
        userMutableLiveData = MutableLiveData<User>()

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            firebaseUserMutableLiveData.postValue(auth.currentUser)
        }
        storageReference = FirebaseStorage.getInstance().getReference("user_image/"+auth.currentUser?.uid)
        firestoreDatabase = FirebaseFirestore.getInstance()
    }

    //sign in using Google
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential) {
        val authenticatedUserMutableLiveData: MutableLiveData<ResponseState<User>> =
            MutableLiveData()
        auth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                if (auth.currentUser != null) {
                    firebaseUserMutableLiveData.postValue(auth.currentUser)
                    userLoggedMutableLiveData.postValue(true)
                    val userCollection = firestoreDatabase.collection("Users")
                    val uid = auth.currentUser!!.uid
                    val name = auth.currentUser!!.displayName
                    val email = auth.currentUser!!.email
                    val phone = auth.currentUser!!.phoneNumber
                    val avt = auth.currentUser!!.photoUrl.toString()
                    val user = User(uid = uid, name = name, email = email, phone = phone, avt = avt)
                    userCollection.document(uid).set(user).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(application, "Update user's profile successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            Log.e("Firestore", "Error setting user's profile", it.exception)
                        }
                    }
                    authenticatedUserMutableLiveData.value = ResponseState.Success(user)
                }
                Toast.makeText(application, "Sign in with Google successfully", Toast.LENGTH_SHORT).show()
            } else {
                userLoggedMutableLiveData.postValue(false)
                Toast.makeText(application, authTask.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getUserDetail(userId: String){
        loadingStateMutableLiveData.postValue(true) // Hiển thị ProgressBar
        val reference = firestoreDatabase.collection("Users").document(userId)
        reference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                userMutableLiveData.postValue(user!!)
            }
        }.addOnFailureListener {exception ->
            Log.d("getUserDetail", "Error getting user detail: $exception")
        }.addOnCompleteListener {
            loadingStateMutableLiveData.postValue(false) // Ẩn ProgressBar khi truy vấn hoàn thành
        }
    }

    fun updateUserName(userId: String, newName: String) {
        val userRef = firestoreDatabase.collection("Users").document(userId)
        userRef.update("name", newName)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    userNameUpdated.postValue(true)
                    Toast.makeText(application, "Update username successfully", Toast.LENGTH_SHORT).show()
                    getUserDetail(auth.currentUser!!.uid)
                } else {
                    Toast.makeText(application, "Update username failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                userNameUpdated.postValue(false)
                Toast.makeText(application, "Error updating username", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUserPhone(userId: String, newPhone: String) {
        val userRef = firestoreDatabase.collection("Users").document(userId)
        userRef.update("phone", newPhone)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    userPhoneUpdated.postValue(true)
                    Toast.makeText(application, "Update user's phone successfully", Toast.LENGTH_SHORT).show()
                    getUserDetail(auth.currentUser!!.uid)
                } else {
                    Toast.makeText(application, "Update user's phone failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                userPhoneUpdated.postValue(false)
                Toast.makeText(application, "Error updating user's phone", Toast.LENGTH_SHORT).show()
            }
    }

    fun checkUserNameChange(userId: String, newName: String){
        val userRef = firestoreDatabase.collection("Users").whereEqualTo("id", userId)



    }

    fun updateUserImage(imageUri: Uri){
        storageReference.putFile(imageUri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                firestoreDatabase.collection("Users").document(auth.currentUser!!.uid)
                    .update("avt", uri.toString())
                    .addOnSuccessListener {
                        checkFullInfoUser()
                        userImageUpdated.postValue(true)
                        Toast.makeText(application, "Upload image successfully!", Toast.LENGTH_SHORT).show()
                        getUserDetail(auth.currentUser!!.uid)
                    }.addOnFailureListener {
                        userImageUpdated.postValue(false)
                        Toast.makeText(application, "Error updating image", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(application, "Error uploading image", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkFullInfoUser(){
        val userRef = firestoreDatabase.collection("Users").document(auth.currentUser!!.uid).id.toString()
        Log.e("id", userRef)
    }


    // sign out
    fun signOut(){
        auth.signOut()
    }


}

