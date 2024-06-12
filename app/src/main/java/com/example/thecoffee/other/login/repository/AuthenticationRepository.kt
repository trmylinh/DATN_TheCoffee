package com.example.thecoffee.other.login.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.thecoffee.other.user.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AuthenticationRepository(_application: Application) {
    private var application: Application
    private var firebaseUserMutableLiveData: MutableLiveData<User>
    private val loadingStateMutableLiveData: MutableLiveData<Boolean>
    private var userLoggedMutableLiveData: MutableLiveData<Boolean>
    private var userImageUpdated: MutableLiveData<Boolean>
    private var userNameUpdated: MutableLiveData<Boolean>
    private var userPhoneUpdated: MutableLiveData<Boolean>
    private var userMutableLiveData: MutableLiveData<User>
    private var isNewUser: MutableLiveData<Boolean>
    private var uidUser: MutableLiveData<String>
    private val auth: FirebaseAuth
    private val storageReference: StorageReference
    private val db: FirebaseFirestore

    //    private val usersRef: CollectionReference = rootRef.collection("Users");

    val getFirebaseUser: MutableLiveData<User>
        get() = firebaseUserMutableLiveData

    val checkLoadingState: MutableLiveData<Boolean>
        get() = loadingStateMutableLiveData

    val checkLogged: MutableLiveData<Boolean>
        get() = userLoggedMutableLiveData

    val getUserDetail: MutableLiveData<User>
        get() = userMutableLiveData

    val checkUserNameUpdated: MutableLiveData<Boolean>
        get() = userNameUpdated

    val checkUserImageUpdated: MutableLiveData<Boolean>
        get() = userImageUpdated
    val checkUserPhoneUpdated: MutableLiveData<Boolean>
        get() = userPhoneUpdated

    val getIsNewUser: MutableLiveData<Boolean>
        get() = isNewUser

    val getUidUser: MutableLiveData<String>
        get() = uidUser

    init {
        application = _application
        firebaseUserMutableLiveData = MutableLiveData<User>()
        loadingStateMutableLiveData = MutableLiveData<Boolean>()
        userLoggedMutableLiveData = MutableLiveData<Boolean>()
        isNewUser = MutableLiveData<Boolean>()
        uidUser = MutableLiveData<String>()
        // userInfo
        userImageUpdated = MutableLiveData<Boolean>()
        userNameUpdated = MutableLiveData<Boolean>()
        userPhoneUpdated = MutableLiveData<Boolean>()
        userMutableLiveData = MutableLiveData<User>()

        auth = FirebaseAuth.getInstance()
//        if (auth.currentUser != null) {
//            firebaseUserMutableLiveData.postValue(auth.currentUser)
//        }
        storageReference =
            FirebaseStorage.getInstance().getReference("user_image/" + auth.currentUser?.uid)
        db = FirebaseFirestore.getInstance()
    }

    //sign in using Google
    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential) {
        auth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                userLoggedMutableLiveData.postValue(true)
                val useRef = db.collection("Users").document(auth.currentUser!!.uid)
                db.collection("Users").whereEqualTo("email", auth.currentUser!!.email)
                    .get()
                    .addOnCompleteListener { task->
                        if(task.isSuccessful){
                            val documents = task.result
                            if(documents.isEmpty){
                                // chua co tai khoan voi email nay -> tao tkhoan moi
                                isNewUser.postValue(true)
                                val uid = auth.currentUser!!.uid
                                val name = auth.currentUser!!.displayName
                                val email = auth.currentUser!!.email
                                val phone = auth.currentUser!!.phoneNumber
                                val avt = auth.currentUser!!.photoUrl.toString()
                                val user = User(uid = uid, name = name, email = email, phone = phone, avt = avt)
                                uidUser.postValue(uid)
                                firebaseUserMutableLiveData.postValue(user)
                                useRef.set(user).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Log.d("Firestore","create user's profile successfully")
                                    } else {
                                        Toast.makeText(application, it.exception.toString(), Toast.LENGTH_SHORT)
                                            .show()
                                        Log.e("Firestore", "Error create user's profile", it.exception)
                                    }
                                }
                                Log.d("Firestore","Sign in with Google successfully")
                            }else {
                                for(document in documents){
                                    val user = document.toObject(User::class.java)
                                    val userRefUpdated = db.collection("Users").document(user.uid!!)
                                    userRefUpdated.update("uid", auth.currentUser!!.uid)
                                    uidUser.postValue(user.uid!!)
                                    firebaseUserMutableLiveData.postValue(user)
                                }
                                isNewUser.postValue(false)
                            }
                        }
                    }
            }else {
                userLoggedMutableLiveData.postValue(false)
                Toast.makeText(application, authTask.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun firebaseSignInWithPhone(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential).addOnCompleteListener{authTask ->
            if (authTask.isSuccessful) {
                userLoggedMutableLiveData.postValue(true)
                val useRef = db.collection("Users").document(auth.currentUser!!.uid)
                val internationalNumber = auth.currentUser!!.phoneNumber
                var originalNumber = ""
                if (internationalNumber!!.startsWith("+84")) {
                    originalNumber = "0" + internationalNumber.drop(3)
                }
                db.collection("Users").whereEqualTo("phone", originalNumber)
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val documents = task.result
                            if (documents.isEmpty) {
                                // chua co tai khoan voi sdt nay -> tao tkhoan moi
                                isNewUser.postValue(true)
                                val uid = auth.currentUser!!.uid
                                val name = auth.currentUser!!.displayName
                                val email = auth.currentUser!!.email
                                val phone = originalNumber
                                val avt = auth.currentUser!!.photoUrl.toString()
                                val user = User(
                                    uid = uid,
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    avt = avt
                                )
                                uidUser.postValue(uid)
                                firebaseUserMutableLiveData.postValue(user)
                                useRef.set(user)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Log.d("phone","Update user's profile successfully" )
                                        } else {
                                            Log.e(
                                                "Firestore",
                                                "Error setting user's profile",
                                                it.exception
                                            )
                                        }
                                    }

                            } else {
                                for(document in documents){
                                    val user = document.toObject(User::class.java)
                                    val userRefUpdated = db.collection("Users").document(user.uid!!)
                                    userRefUpdated.update("uid", auth.currentUser!!.uid)
                                    uidUser.postValue(user.uid!!)
                                    firebaseUserMutableLiveData.postValue(user)
                                }
                                isNewUser.postValue(false)
                            }
                        }
                    }
            }
            else {
                userLoggedMutableLiveData.postValue(false)
                Toast.makeText(application, authTask.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getUserDetail(userId: String) {
        loadingStateMutableLiveData.postValue(true) // Hiển thị ProgressBar
        val reference = db.collection("Users").document(userId)
        reference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                userMutableLiveData.postValue(user!!)
            }
        }.addOnFailureListener { exception ->
            Log.d("getUserDetail", "Error getting user detail: $exception")
        }.addOnCompleteListener {
            loadingStateMutableLiveData.postValue(false) // Ẩn ProgressBar khi truy vấn hoàn thành
        }
    }

    fun updateUserName(userId: String, newName: String) {
        val userRef = db.collection("Users").document(userId)
        userRef.update("name", newName)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userNameUpdated.postValue(true)
                    Log.d("Firestore", "Update username successfully")
                    getUserDetail(auth.currentUser!!.uid)
                } else {
                    Log.d("Firestore", "Update username failed")
                }
            }.addOnFailureListener {
                userNameUpdated.postValue(false)
                Log.d("Firestore", "Error updating username")
            }
    }

    fun updateUserPhone(userId: String, newPhone: String) {
        val userRef = db.collection("Users").document(userId)
        userRef.update("phone", newPhone)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userPhoneUpdated.postValue(true)
                    Log.d("Firestore", "Update user's phone successfully")
                    getUserDetail(auth.currentUser!!.uid)
                } else {
                    Log.d("Firestore", "Update user's phone failed")
                }
            }.addOnFailureListener {
                userPhoneUpdated.postValue(false)
                Log.d("Firestore", "Error updating user's phone")
            }
    }

    fun updateUserEmail(userId: String, newEmail: String) {
        val userRef = db.collection("Users").document(userId)
        userRef.update("email", newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    userPhoneUpdated.postValue(true)
                    Log.d("Firestore", "Update user's newEmail successfully")
                    getUserDetail(auth.currentUser!!.uid)
                } else {
                    Log.d("Firestore", "Update user's newEmail failed")
                }
            }.addOnFailureListener {
//                userPhoneUpdated.postValue(false)
                Log.d("Firestore", "Error updating user's newEmail")
            }
    }

    fun updateUserImage(imageUri: Uri) {
        storageReference.putFile(imageUri).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                db.collection("Users").document(auth.currentUser!!.uid)
                    .update("avt", uri.toString())
                    .addOnSuccessListener {
                        userImageUpdated.postValue(true)
                        Log.d("Firestore", "Upload image successfully!")
                        getUserDetail(auth.currentUser!!.uid)
                    }.addOnFailureListener {
                        Log.d("Firestore", "Error updating image")
                    }
            }
        }.addOnFailureListener {
            Log.d("Firestore", "Error updating image")
        }
    }

    fun checkFullInfoUser() {
        val userRef = db.collection("Users").document(auth.currentUser!!.uid).id.toString()
    }


    // sign out
    fun signOut() {
        auth.signOut()
    }


}

