package com.example.dodogram.presentation.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dodogram.core.utils.enums.LoginMode
import com.example.dodogram.presentation.MainActivity
import com.example.dodogram.R
import com.example.dodogram.databinding.ActivityLoginBinding
import com.example.dodogram.core.common.UserLogInMode
import com.example.dodogram.domain.model.LoginPageState
import com.example.dodogram.presentation.viewmodel.LoginViewModel
import com.example.dodogram.presentation.viewmodel.LoginViewModelFactory
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private var firebaseAuth: FirebaseAuth? = null
    private var oneTapClient: SignInClient? = null
    private val signInRequest: BeginSignInRequest? = null
    private var signUpRequest: BeginSignInRequest? = null
    private var callbackManager:CallbackManager? = null
    private var showOneTapUI = true
    @Inject lateinit var mViewModel: LoginViewModel

    private val facebookResultContracts = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it.resultCode
    }

    private var logInResultHandler = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result1: ActivityResult ->
        try {
            val credential =
                oneTapClient?.getSignInCredentialFromIntent(result1.data)
            val idToken = credential?.googleIdToken
            if (idToken != null) {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                result1.data?.let { signInWithGoogle(it) }
                Log.d("TAG", "Got ID token. $idToken")
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    run {
                        Log.d("one tap", "One-tap dialog was closed.")
                        // Don't re-prompt the user.
                        showOneTapUI = false
                    }
                    run {
                        Log.d("one tap", "One-tap encountered a network error.")
                        // Try again or just ignore.

                    }
                    run {
                        Log.d(
                            "one tap", "Couldn't get credential from result." +
                                    e.localizedMessage
                        )
                        showOneTapUI = false
                    }
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    run {
                        Log.d("one tap", "One-tap encountered a network error.")
                    }
                    run {
                        Log.d(
                            "one tap", "Couldn't get credential from result." +
                                    e.localizedMessage
                        )
                        showOneTapUI = false
                    }
                }
                else -> {
                    Log.d(
                        "one tap", "Couldn't get credential from result." +
                                e.localizedMessage
                    )
                    showOneTapUI = false
                }
            }
        }
    }

    private fun oneTapSignUp() {
        signUpRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true) // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id)) // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ) // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
        oneTapClient?.beginSignIn(signUpRequest ?: return)
            ?.addOnSuccessListener(
                this
            ) { result ->
                val intentSenderRequest =
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                logInResultHandler.launch(intentSenderRequest)
                //                            startIntentSenderForResult(
                //                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                //                                    null, 0, 0, 0);
            }
            ?.addOnFailureListener(this) { e -> // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.

                Log.d("TAG Failure Listener", e.localizedMessage as String)
            }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.

                        signInWithGoogle(data);
                        Log.d("TAG", "Got ID token.");
                    }
                } catch (ApiException e) {
                    // ...
                }
                break;
        }
    }*/
    @Throws(ApiException::class)
    private fun signInWithGoogle(data: Intent) {
        val googleCredential = oneTapClient?.getSignInCredentialFromIntent(data)
        val idToken = googleCredential?.googleIdToken
        if (idToken != null) {
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth?.signInWithCredential(firebaseCredential)
                ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(task: Task<AuthResult?>) {
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success")
                            val user = firebaseAuth?.currentUser
                            updateUI(user, UserLogInMode.SignIn(LoginMode.GOOGLE))
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.exception)
//                            updateUI(null)
                        }
                    }
                })
        }
    }

    private fun updateUI(user: FirebaseUser?,loginMode: UserLogInMode) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("loginData",user)
        intent.putExtra("loginMode",loginMode)
        startActivity(intent)
        finish()
    }

    //Login user
    private fun logInUser(email: String, password: String) {

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth?.signInWithEmailAndPassword(
                email,
                password
            )
                ?.addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success")
                        //FirebaseUser user = firebaseAuth.getCurrentUser();
                        val user = firebaseAuth?.currentUser
                        updateUI(user, UserLogInMode.SignIn(LoginMode.EMAIL))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@LoginActivity,
                            "Authentication failed due to " + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }


    //registering user
    private fun registerNewUser(email: String, password: String) {
        firebaseAuth?.createUserWithEmailAndPassword(
            email,
            password
        )
            ?.addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = firebaseAuth?.currentUser
                    updateUI(user, UserLogInMode.Register(LoginMode.EMAIL))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@LoginActivity,
                        "" + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth?.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val emailLogIn = binding.email as TextInputEditText
        val passwordLogIn = binding.password as TextInputEditText
        val emailRegister = binding.etNewUserUsername as TextInputEditText
        val passwordRegister = binding.etNewUserPassword as TextInputEditText
        val userName = binding.etNewUserName
        val login = binding.login as MaterialButton
        val btnCreateAcc = binding.btnCreateAcc as MaterialButton
        val loaderLoginPage = binding.pbLogin
        val loaderRegisterPage = binding.pbRegister
        val register = binding.tvRegisterLogin
        val signIn = binding.tvSignIn
        val logInPage = binding.clLoginExistingUser
        val registerPage = binding.clRegisterNewUser
        val btnGoogleSignIn = binding.btnGoogleSignIn
        val btnFacebookSignIn = binding.btnFacebookSignIn
        var isLoginPageVisible = true
        var isRegisterPageVisible = false


        oneTapClient = Identity.getSignInClient(this)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)


        login.isEnabled = true
        btnCreateAcc.isEnabled = true


        btnGoogleSignIn?.setOnClickListener {
            if (isLoginPageVisible){
                loaderLoginPage.visibility = View.VISIBLE
                oneTapSignUp()
            }else{
                loaderRegisterPage?.visibility = View.VISIBLE
            }
        }

        loginViewModel.loginRegisterUiState.observe(this, Observer {

            if (it.isLoginPageVisible){
                isLoginPageVisible = true
                isRegisterPageVisible = false
                registerPage?.visibility = View.GONE
                logInPage?.visibility = View.VISIBLE
            }else{
                isRegisterPageVisible = true
                isLoginPageVisible = false
                logInPage?.visibility = View.GONE
                registerPage?.visibility = View.VISIBLE
            }
        })

        register?.setOnClickListener {
            loginViewModel.loginRegisterUIState(
                LoginPageState(
                isLoginPageVisible = false,
                isRegisterPageVisible = true
            )
            )
        }

        signIn?.setOnClickListener {
            loginViewModel.loginRegisterUIState(
                LoginPageState(
                isLoginPageVisible = true,
                isRegisterPageVisible = false
            )
            )
        }

//        observeLiveData(login,email,password,loading,LogInMode.SignIn)
        /*if (isLoginUIVisible) {
            observeLiveData(login, email, password, loading)
        } else if (isRegisterUIVisible) {
            observeLiveData(btnCreateAcc,emailRegister,passwordRegister,loading)
        }*/


        passwordLogIn.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    loginViewModel.register(
                        emailRegister.text.toString(),
                        passwordRegister.text.toString()
                    )
            }
            false
        }



        emailLogIn.addTextChangedListener{
            if (!isUserNameValid(it.toString())){
                emailLogIn.error = getString(R.string.invalid_username)
                login.isEnabled = false
            }else{
                login.isEnabled = true
            }
        }

        passwordLogIn.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                binding.textInputPassword?.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
        }

        userName?.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                userName.error = "Please enter your name"
            }
        }

        emailRegister.addTextChangedListener{
            if (!isUserNameValid(it.toString())){
                emailRegister.error = getString(R.string.invalid_username)
                btnCreateAcc.isEnabled = false
            }else{
                btnCreateAcc.isEnabled = true
            }
        }

        passwordRegister.addTextChangedListener{
            if (!isPasswordValid(it.toString())){
                passwordRegister.error = getString(R.string.invalid_password)
                btnCreateAcc.isEnabled = false
            }else{
                btnCreateAcc.isEnabled = true
            }
        }


        login.setOnClickListener {
            if (emailLogIn.text.isNullOrEmpty()){
                emailLogIn.error = "Please enter email"
            }
            if (passwordLogIn.text.isNullOrEmpty()){
                binding.textInputPassword?.endIconMode = TextInputLayout.END_ICON_NONE
                passwordLogIn.error = "Please enter password"
            }
            if (!emailLogIn.text.isNullOrEmpty() && !passwordLogIn.text.isNullOrEmpty()){
                logInUser(emailLogIn.text.toString(),passwordLogIn.text.toString())
            }
        }

        btnCreateAcc.setOnClickListener {

            if (userName?.text.isNullOrEmpty()){
                userName?.error = "Please enter your name"
            }
            if (emailRegister.text.isNullOrEmpty()){
                emailRegister.error = "Please enter email"
            }
            if (passwordRegister.text.isNullOrEmpty()){
                binding.textInputPassword?.endIconMode = TextInputLayout.END_ICON_NONE
                passwordRegister.error = "Please enter password"
            }
            if (!emailRegister.text.isNullOrEmpty() && !passwordRegister.text.isNullOrEmpty() && !userName?.text.isNullOrEmpty()){
                registerNewUser(emailRegister.text.toString(),passwordRegister.text.toString())
            }
        }

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,object:FacebookCallback<com.facebook.login.LoginResult>{
            override fun onCancel() {
                Log.d("TAG", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("TAG", "facebook:onError", error)
            }

            override fun onSuccess(result: com.facebook.login.LoginResult ) {
                Log.d("TAG", "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

        })


        btnFacebookSignIn?.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))

        }



       /* loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValidLoginUser

            if (loginState.usernameLoginUserError != null) {
                email.error = getString(loginState.usernameLoginUserError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                logInUser(email.text.toString(), password.text.toString())
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
//            finish()
        })


        email.afterTextChanged {
            loginViewModel.loginDataChanged(
                email.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            email.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(email.text.toString(), password.text.toString())
            }
        }


        loginViewModel.registerFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            btnCreateAcc.isEnabled = loginState.isDataValidLoginUser

            if (loginState.usernameLoginUserError != null) {
                emailRegister.error = getString(loginState.usernameLoginUserError)
            }
            if (loginState.passwordLoginUserError != null ) {
                passwordRegister.error = getString(loginState.passwordLoginUserError)
            }
        })

        loginViewModel.registerResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null){
                registerNewUser(emailRegister.text.toString(),passwordRegister.text.toString())
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
//            finish()
        })


        emailRegister.afterTextChanged {
            loginViewModel.registerDataChanged(
                emailRegister.text.toString(),
                passwordRegister.text.toString()
            )
        }

        passwordRegister.apply {
            afterTextChanged {
                loginViewModel.registerDataChanged(
                    emailRegister.text.toString(),
                    passwordRegister.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.register(
                            emailRegister.text.toString(),
                            passwordRegister.text.toString()
                        )
                }
                false
            }

            btnCreateAcc.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.register(emailRegister.text.toString(), passwordRegister.text.toString())
            }
        }*/

    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        Log.d("TAG", "handleFacebookAccessToken:$accessToken")

        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = firebaseAuth?.currentUser
                    updateUI(user, UserLogInMode.SignIn(LoginMode.FACEBOOK))

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 6
    }

    private fun observeLiveData(
        login: MaterialButton,
        email: TextInputEditText,
        password: TextInputEditText,
        loading: ProgressBar,
        loginMode: LogInMode
    ) {
        if (loginMode == LogInMode.SignIn) {

            loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
                login.isEnabled = loginState.isDataValidLoginUser

                if (loginState.usernameLoginUserError != null) {
                    email.error = getString(loginState.usernameLoginUserError)
                }
            })

            loginViewModel.loginResult.observe(this@LoginActivity, Observer {
                val loginResult = it ?: return@Observer

                loading.visibility = View.GONE
                if (loginResult.error != null) {
                    showLoginFailed(loginResult.error)
                }
                if (loginResult.success != null) {
                    logInUser(email.text.toString(), password.text.toString())
                }
                setResult(Activity.RESULT_OK)

                //Complete and destroy login activity once successful
//            finish()
            })


            email.afterTextChanged {
                loginViewModel.loginDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            password.apply {
                afterTextChanged {
                    loginViewModel.loginDataChanged(
                        email.text.toString(),
                        password.text.toString()
                    )
                }

                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            CoroutineScope(Dispatchers.Main).launch {
                                loginViewModel.login(
                                    email.text.toString(),
                                    password.text.toString()
                                )
                            }

                    }
                    false
                }

                login.setOnClickListener {
                    loading.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        loginViewModel.login(email.text.toString(), password.text.toString())
                    }
                }
            }
        }else if (loginMode == LogInMode.Register){

            loginViewModel.registerFormState.observe(this@LoginActivity, Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
                login.isEnabled = loginState.isDataValidLoginUser

                if (loginState.usernameLoginUserError != null) {
                    email.error = getString(loginState.usernameLoginUserError)
                }
                if (loginState.passwordLoginUserError != null ) {
                    password.error = getString(loginState.passwordLoginUserError)
                }
            })

            loginViewModel.registerResult.observe(this@LoginActivity, Observer {
                val loginResult = it ?: return@Observer

                loading.visibility = View.GONE
                if (loginResult.error != null) {
                    showLoginFailed(loginResult.error)
                }
                if (loginResult.success != null){
                    registerNewUser(email.text.toString(),password.text.toString())
                }
                setResult(Activity.RESULT_OK)

                //Complete and destroy login activity once successful
//            finish()
            })


            email.afterTextChanged {
                loginViewModel.registerDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            password.apply {
                afterTextChanged {
                    loginViewModel.registerDataChanged(
                        email.text.toString(),
                        password.text.toString()
                    )
                }

                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            loginViewModel.register(
                                email.text.toString(),
                                password.text.toString()
                            )
                    }
                    false
                }

                login.setOnClickListener {
                    loading.visibility = View.VISIBLE
                    loginViewModel.register(email.text.toString(), password.text.toString())
                }
            }
        }

    }


    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

enum class LogInMode{
    SignIn,
    Register
}