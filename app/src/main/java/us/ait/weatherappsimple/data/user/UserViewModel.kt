package us.ait.weatherappsimple.data.user

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun saveUserData(user: FirebaseUser?) {
        // Implement the code to save user data to Firebase
        // Use the 'user' parameter to access the user's information
        // Example: auth.currentUser?.let { firebaseUser -> /* save user data here */ }
    }
}
