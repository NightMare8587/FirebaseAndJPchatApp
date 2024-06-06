package com.example.firebasechatappjetpackcompose.Utils

import android.util.Patterns

object Utils {
    fun checkIfEmailIsValid(email : String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordLengthValid(password : String) : Boolean {
        return password.length >= 7
    }
}