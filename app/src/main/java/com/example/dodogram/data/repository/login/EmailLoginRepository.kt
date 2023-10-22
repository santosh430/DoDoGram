package com.example.dodogram.data.repository.login

import com.example.dodogram.core.common.Result
import com.example.dodogram.domain.model.LoggedInUser
import com.example.dodogram.domain.repository.LoginRepository
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class EmailLoginRepository @Inject constructor(private val firebaseAuth: FirebaseAuth):LoginRepository {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

//    fun logout() {
//        user = null
//        dataSource.logout()
//    }

    override fun login(username: String?, password: String?): Flow<Result<LoggedInUser>> = flow{
        // handle login
        if (username != null && password != null) {
            firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener {
                if (it.isSuccessful){
                    val user = firebaseAuth.currentUser
                    CoroutineScope(Dispatchers.IO).launch {
                        emit(Result.Success(LoggedInUser(user?.uid,user?.displayName)))
                    }

                }else{
                    CoroutineScope(Dispatchers.IO).launch {
                        emit(Result.Error(IOException("Logged In Error: ",it.exception)))
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)
        .catch {
            emit(Result.Error(IOException("Error logging: ",it)))
        }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}