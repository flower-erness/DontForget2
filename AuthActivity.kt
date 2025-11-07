package com.functions.reminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient // New: Google Sign-In Client
    private var isLoginMode = true

    // Tag for logging
    private val TAG = "AuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()

        // 1. Configure Google Sign-In
        // This MUST include the R.string.default_web_client_id
        // obtained from your google-services.json file.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize UI elements
        val titleText = findViewById<TextView>(R.id.titleText)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val btnAuthAction = findViewById<Button>(R.id.btnAuthAction)
        val switchModeText = findViewById<TextView>(R.id.switchModeText)
        val btnGoogleSignIn = findViewById<SignInButton>(R.id.btnGoogleSignIn) // New: Google Sign-In Button

        // Switch between Login and Sign Up
        switchModeText.setOnClickListener {
            isLoginMode = !isLoginMode
            if (isLoginMode) {
                titleText.text = "Welcome Back"
                btnAuthAction.text = "Login"
                switchModeText.text = "Don't have an account? Sign Up"
            } else {
                titleText.text = "Create Account"
                btnAuthAction.text = "Sign Up"
                switchModeText.text = "Already have an account? Login"
            }
        }

        // Handle Email/Password Login or Sign Up
        btnAuthAction.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isLoginMode) {
                loginUser(email, password)
            } else {
                signUpUser(email, password)
            }
        }

        // Handle Google Sign-In click
        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    // --- Google Sign-In Functions ---

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // Launcher to handle the result of the Google Sign-In Intent
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, now authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Google Sign In successful!", Toast.LENGTH_SHORT).show()
                    // NEW: Navigate to the Main Activity
                    navigateToMain()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "Firebase auth with Google failed", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // --- Existing Email/Password Functions ---

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sign up failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    // --- Navigation Helper ---

    private fun navigateToMain() {
        // Use FLAG_ACTIVITY_CLEAR_TASK and FLAG_ACTIVITY_NEW_TASK
        // to prevent the user from returning to the Auth screen via the back button
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // Close the AuthActivity so the user can't go back
    }
}