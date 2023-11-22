package com.s1092790.eenglish

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.logon_main.*
import com.google.firebase.auth.FirebaseAuth



class Logon : AppCompatActivity() {
    private var lastBackPressedTime = 0L
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val TAG = "GoogleSignIn"
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)
        setContentView(R.layout.logon_main)


        /*textView_Result.setTypeface(
            Typeface.createFromAsset(assets,
                "font/SentyCreamPuff.ttf"))*/

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("793569873109-jk70ielh23uqmc71jjamoqoe05jr9dev.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val btSighIn = findViewById<SignInButton>(R.id.button_SignIn)
        btSighIn.setOnClickListener {
                v: View? ->
            startActivityForResult(
                mGoogleSignInClient!!.getSignInIntent(),
                200
            )

        }

        val mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this , null)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()



        button_SignOut.visibility = View.GONE
        button_In.visibility =View.GONE
        // 判斷 Firebase 目前是否有已登入的使用者

        updateButtonVisibility()



        button_In.setOnClickListener{
            val intent = Intent(this, Home::class.java )
            startActivity(intent)
        }
        button_SignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
                if(FirebaseAuth.getInstance().currentUser == null){
                    //textView_Result.text = "登出囉~"
                    Toast.makeText(this, "已登出", Toast.LENGTH_SHORT).show()
                    onAuthStateChanged(this.mAuth)
                }
                else{
                    //textView_Result.text = "登出失敗，意外情形發生"
                }


                /*button_SignIn.visibility = View.VISIBLE
                button_SignOut.visibility = View.GONE*/

            }
        }
    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                Log.d(TAG, "Login success")
                Log.d(TAG, "Email: ${account.email}")
                Log.d(TAG, "Google name: ${account.displayName}")
                Log.d(TAG, "Token: ${account.idToken}")
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            Log.d(TAG, "signInWithCredential:success")
                            Log.d(TAG, "User id: ${user?.uid}")
                            Log.d(TAG, "User email: ${user?.email}")
                            Log.d(TAG, "User name: ${user?.displayName}")

                            /*button_SignIn.visibility = View.GONE
                            button_SignOut.visibility = View.VISIBLE
                            button_In.visibility = View.VISIBLE*/

                            onAuthStateChanged(this.mAuth)

                            /*val tvResult = findViewById<TextView>(R.id.textView_Result)
                            tvResult.text = "登入囉"*/
                            Toast.makeText(this, "登入成功", Toast.LENGTH_SHORT).show()

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            Toast.makeText(this, "登入失敗，Maybe網路出問題", Toast.LENGTH_SHORT).show()
                            updateButtonVisibility()
                        }
                }


            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed with error code: ${e.statusCode}")
                Log.e(TAG, "Google sign in failed with message: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during sign in: ${e.message}")
            }
        }


        /*if(requestCode == 200){
            textView_Result.text = "log in mother fucker"
            button_SignIn.visibility = View.GONE
            button_SignOut.visibility = View.VISIBLE
            val intent = Intent(this, Home::class.java )
            startActivity(intent)
        }
        else{
            textView_Result.text = "幹 沒登進去"
            button_SignIn.visibility = View.VISIBLE
        }*/
    }

    companion object {
        val TAG = Home::class.java.simpleName + "My"
    }

    private fun updateButtonVisibility() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            button_SignIn.visibility = View.GONE
            button_SignOut.visibility = View.VISIBLE
            button_In.visibility = View.VISIBLE
        } else {
            button_SignIn.visibility = View.VISIBLE
            button_SignOut.visibility = View.GONE
            button_In.visibility = View.GONE
        }
    }

    private fun onAuthStateChanged(auth: FirebaseAuth) {
        updateButtonVisibility()
    }


    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()

        // 如果距離上次按下返回鍵的時間小於 2 秒，提示用戶關閉應用程式
        if (currentTime - lastBackPressedTime < 2000) {
            super.onBackPressed()  // 調用父類的 onBackPressed() 方法，關閉應用程式
            //finishAffinity()  //強制退出
        } else {
            lastBackPressedTime = currentTime  // 更新上次按下返回鍵的時間
            Toast.makeText(this, "再按一次返回鍵關閉應用程式", Toast.LENGTH_SHORT).show()  // 提示用戶再次按下返回鍵關閉應用程式
        }
    }
}