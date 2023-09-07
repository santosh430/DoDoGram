package com.example.dodogram

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dodogram.databinding.ActivityMainBinding
import com.example.dodogram.enums.LoginMode
import com.example.dodogram.models.UserCredentials
import com.example.dodogram.ui.login.data.UserLogInMode
import com.example.dodogram.utils.AppSharedPreferences
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getParcelableExtra<FirebaseUser>("loginData")
        user?.let {
            Toast.makeText(this, "Welcome ${user.displayName}", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                AppSharedPreferences.setUserLogInSharedPreference(this@MainActivity)
                val userLogInMode = intent.getParcelableExtra<UserLogInMode>("loginMode") ?: UserLogInMode.SignIn(
                    LoginMode.NONE)
                insertUserCredentialsToFirebase(user,userLogInMode)
            }
        }

        //set the username and profile pic
        CoroutineScope(Dispatchers.Main).launch {
            updateUserInfo(user)
        }

        Log.i("firebase user",user?.displayName.toString())
        Log.i("firebase user",user?.email.toString())
        Log.i("firebase user",user?.photoUrl.toString())
        Log.i("firebase user",user?.phoneNumber.toString())

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            ,R.id.navigation_reels,R.id.navigation_profile
            )
        )

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.navigation_dashboard->{
                    navView.visibility = View.GONE
                }
                R.id.navigation_reels ->{
                    //do something
                }
                else -> navView.visibility = View.VISIBLE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun updateUserInfo(user: FirebaseUser? = null) {
        if (user != null){
            // update username and profile pic in the UI
            Log.i("updating user","User info")
        }else{
            //fetch username and profile pic from the local db and update UI
            //or cache the info using firebase caching and update UI
        }
    }

    private fun insertUserCredentialsToFirebase(user: FirebaseUser, userLogInMode: UserLogInMode) {
        val firebaseStorageReference = Firebase.firestore

        val data = UserCredentials(user.uid,user.displayName,user.photoUrl,user.phoneNumber,user.email,Calendar.getInstance().timeInMillis)
        val uploadUserLoginMode = firebaseStorageReference.collection(user.uid)
            .document("UserLoginMode").set(userLogInMode)
            .addOnFailureListener {
                Log.e("FireStore exception",it.toString())
            }
        val uploadUserCredentials = firebaseStorageReference.collection(user.uid)
            .document("UserCredentials").set(data)
            .addOnFailureListener {
                Log.e("FireStore exception",it.toString())
            }
    }
}