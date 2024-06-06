package com.example.firebasechatappjetpackcompose.Utils

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.firebasechatappjetpackcompose.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUtils @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFirestore: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences
) {
    private val TAG = "FirebaseUtils"

    /**
     * [createChatAccount] creates a new account in firebase or if account exists then logs in to that account
     * @param email
     * @param password
     *
     */
    fun createChatAccount(
        email: String,
        password: String,
        username: String,
        imageUri: String?,
        isSuccessOrFailure: (Boolean) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    Log.d(TAG, "createChatAccount: user account created successfully")
                    sharedPreferences.edit().putString("username", username).apply()
                    sharedPreferences.edit().putString("email", email).apply()
                    val map = mapOf(
                        "email" to email, "username" to username, "hasProfileImage" to
                                if (imageUri != null) "true" else false
                    )
                    if (imageUri != null) {
                        sharedPreferences.edit().putString("profileImage", imageUri)
                            .apply()
                        firebaseStorage.reference.child("Users").child(firebaseAuth.uid.toString())
                            .child("profileImage").putFile(imageUri.toUri())
                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {
                                    Log.d(TAG, "createChatAccount: Profile image uploaded")

                                    firebaseFirestore.collection("Users")
                                        .document(firebaseAuth.uid.toString())
                                        .set(map).addOnCompleteListener { imageUploadTask ->

                                            if (imageUploadTask.isSuccessful) {
                                                Log.d(
                                                    TAG,
                                                    "createChatAccount: firestore updated with account details"
                                                )
                                                sharedPreferences.edit()
                                                    .putBoolean("accountCreated", true).apply()
                                                isSuccessOrFailure(true)
                                            } else {
                                                Log.e(
                                                    TAG,
                                                    "createChatAccount: error writing in firebase"
                                                )
                                                isSuccessOrFailure(false)
                                            }
                                        }
                                } else
                                    isSuccessOrFailure(false)

                            }
                    } else {
                        // if no image to upload then directly create firebase account
                        firebaseFirestore.collection("Users").document(firebaseAuth.uid.toString())
                            .set(map).addOnCompleteListener { uploadTask ->

                                if (uploadTask.isSuccessful) {
                                    Log.d(
                                        TAG,
                                        "createChatAccount: firestore updated with account details"
                                    )
                                    sharedPreferences.edit().putBoolean("accountCreated", true)
                                        .apply()
                                    isSuccessOrFailure(true)
                                } else {
                                    Log.e(TAG, "createChatAccount: error writing in firebase")
                                    isSuccessOrFailure(false)
                                }
                            }
                    }
                } else {
                    //maybe account is already created ??? try to log in
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { signIn ->

                            if (signIn.isSuccessful) {
                                Log.d(TAG, "createChatAccount: user signed in")

                                isSuccessOrFailure(true)
                            } else {
                                // now something went wrong for real
                                Log.e(
                                    TAG,
                                    "createChatAccount: error logging in or creating account"
                                )
                                isSuccessOrFailure(false)
                            }

                        }
                }
            }
    }

    /**
     * checks weather username already exists/taken by someone in chat
     * @param username
     * compares in [firebaseFirestore] using [firebaseFirestore.whereEqualTo]
     */
    fun checkIfUsernameAlreadyExists(username: String, isUsernameAlreadyTaken: (Boolean) -> Unit) {
        firebaseFirestore.collection("Users").whereEqualTo("username", username).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val result = task.result

                    if (result != null && !result.isEmpty)
                        isUsernameAlreadyTaken(true)
                    else
                        isUsernameAlreadyTaken(false)
                } else isUsernameAlreadyTaken(false)
            }
    }

    fun checkIfCurrenUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun checkIfAccountIsCreated(): Boolean {
        return sharedPreferences.getBoolean("accountCreated", false)
    }

    fun getCurrentUserUID(): String {
        return firebaseAuth.uid.toString()
    }

    fun getCurrentUsername(): String {
        return sharedPreferences.getString("username", "").toString()
    }

    fun getCurrentUserProfileImage(userUID : String, imageUri : (Uri) -> Unit) {
        firebaseStorage.reference.child("Users").child(userUID).child("profileImage").downloadUrl.addOnCompleteListener {
            if(it.isSuccessful) {
                imageUri(it.result)
            }
        }
    }
    fun firestoreChatListener(newDataArrayList: (ArrayList<ChatModel>) -> Unit) {
        firebaseFirestore.collection("GroupChat").addSnapshotListener { value, error ->
            if (value != null) {
                val array = arrayListOf<ChatModel>()
                for (data in value.documentChanges) {
                    val newDarta = data.document.data
                    array.add(
                        ChatModel(
                            newDarta["username"].toString(),
                            newDarta["userUID"].toString(),
                            newDarta["message"].toString()
                        )
                    )
                }
                newDataArrayList(array)
                Log.d(TAG, "firestoreChatListener: $array")
            } else {
                Log.e(TAG, "firestoreChatListener: empty database")
                newDataArrayList(arrayListOf())
            }

            if (error != null) {
                Log.e(TAG, "firestoreChatListener: error ${error.message}")
                newDataArrayList(arrayListOf())
            }
        }
    }

    /**
     * sends a message in groupchat
     * @param map sets to groupchat common db
     */
    fun sendMessageInGroupChat(map: HashMap<String, String>) {
        firebaseFirestore.collection("GroupChat").document(UUID.randomUUID().toString()).set(map)
            .addOnCompleteListener {

            }
    }
}