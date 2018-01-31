package com.mathieu.sauvau.todolist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import com.google.android.gms.common.ConnectionResult
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import android.app.ProgressDialog
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val TAG = "LogInActivity"
    private val RC_SIGN_IN = 123

    private lateinit var googleAccount: GoogleSignInAccount
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val mAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        configureSignIn()
        sign_in_button.setOnClickListener({ logInWithGoogle() })
    }

    // This method configures Google SignIn
    private fun configureSignIn() {
        // Configure sign-in to request the user’s basic profile like name and email
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, options)
    }

    private fun logInWithGoogle() {
        if (user == null) {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (result.isSuccessful) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                googleAccount = result.result
                val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                firebaseAuthWithGoogle(credential)
            } else {
                // Google Sign In failed
                Log.e(TAG, "Login Unsuccessful")
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        val pd = ProgressDialog(this)
        pd.setTitle("Login")
        pd.setMessage("please wait...")
        pd.show()
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        user = mAuth.currentUser
                        print("USER : $user")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        pd.dismiss()
                        finish()
                    } else {
                        Log.w(TAG, "signInWithCredential" + task.exception!!.message)
                        task.exception!!.printStackTrace()
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                    pd.hide()
                }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }
}
