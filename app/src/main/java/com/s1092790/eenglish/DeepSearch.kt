package com.s1092790.eenglish

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.deep_search.*

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DeepSearch : AppCompatActivity(), OnClickListener, OnLongClickListener, OnTouchListener {
    private lateinit var database: DatabaseReference
    private lateinit var store: Button

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(R.layout.deep_search)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        store = findViewById(R.id.storebtn)

        database = Firebase.database.reference

        if (currentUser == null) {
            // Redirect to sign-in or handle the situation accordingly
            val view = View(this)
            goLogon(view)
        }

        store.setOnClickListener {
            saveLwordToDatabase(sgword.text.toString())
        }

        search(Home.qsn)

        backbtnd.setOnClickListener {
            val intent = Intent(this, Home::class.java )
            startActivity(intent)
        }




    }



    private fun saveLwordToDatabase(text: String) {
        val userId = getUserId()
        val key = database.child("texts").child(userId).child("lovewords").push().key
        database.child("texts").child(userId).child("lovewords").child(key!!).setValue(text)
            .addOnSuccessListener {
                // Your existing success listener...
                NoteAdapter().setItems(listOf(text))
                Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Your existing failure listener...
                Toast.makeText(this, "Error saving text", Toast.LENGTH_SHORT).show()
            }

    }

    fun search(i : Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            if(i == 0){
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://dictionary.cambridge.org/zht/%E8%A9%9E%E5%85%B8/%E8%8B%B1%E8%AA%9E-%E6%BC%A2%E8%AA%9E-%E7%B9%81%E9%AB%94/")//劍橋主頁
                    .build()
                val response = client.newCall(request).execute()
                val body = response.peekBody(Long.MAX_VALUE)
                val html = body.string()
                val doc = Jsoup.parse(html)
                val words = doc.select("p.fs36 a")//每日一詞
                val wordsText = words.map { it.text().lowercase() }.joinToString("\n")

                val request2 = Request.Builder()
                    .url("https://dictionary.cambridge.org/zht/%E8%A9%9E%E5%85%B8/%E8%8B%B1%E8%AA%9E-%E6%BC%A2%E8%AA%9E-%E7%B9%81%E9%AB%94/$wordsText")
                    .build()
                val response2 = client.newCall(request2).execute()
                val body2 = response2.peekBody(Long.MAX_VALUE)
                val html2 = body2.string()
                val doc2 = Jsoup.parse(html2)
                val mean = doc2.getElementsByClass("trans dtrans dtrans-se  break-cj")//抓單字意思
                if(mean.isEmpty()){
                    withContext(Dispatchers.Main) {
                        //sgword.text = "今天沒有喔~"
                        sgword.text = "Home" + " " + "家"
                    }
                }
                else{

                    val meanText = mean.map { it.text().lowercase() }.joinToString("\n")
                    withContext(Dispatchers.Main) {
                        //sgword.text = "今天沒有喔~"
                        sgword.text = wordsText + " " + meanText
                    }

                }
            }
            else{
                val client = OkHttpClient()
                val request3 = Request.Builder()
                    .url("https://tw.voicetube.com/definition")
                    .build()
                val response3 = client.newCall(request3).execute()
                val body3 = response3.peekBody(Long.MAX_VALUE)
                val html3 = body3.string()
                val doc3 = Jsoup.parse(html3)


                val rwords = doc3.getElementsByClass("sc-f549624-0 hEPORM")
                //val rpos = doc3.getElementsByClass("has-text-weight-medium")
                val rmean = doc3.getElementsByClass("sc-a4ea01be-1 cJbrEa")

                if(rwords.isEmpty() || rmean.isEmpty()){
                    withContext(Dispatchers.Main) {
                        //sgword.text = "今天沒有喔~"
                        sgword.text = "錯誤"
                    }
                }
                else{
                    //val rwords = words.subList(0, 1)
                    val r1words = rwords.subList((i-1),i)

                    val r1mean = rmean.subList((i-1),i)
                    val r1wordsText = r1words.map {it.text() }.joinToString("\n")
                    val r1meanText = r1mean.map {it.text() }.joinToString("\n")

                    val request4 = Request.Builder()
                        .url("https://dictionary.cambridge.org/zht/%E8%A9%9E%E5%85%B8/%E8%8B%B1%E8%AA%9E-%E6%BC%A2%E8%AA%9E-%E7%B9%81%E9%AB%94/$r1wordsText")
                        .build()
                    val response4 = client.newCall(request4).execute()
                    val body4 = response4.peekBody(Long.MAX_VALUE)
                    val html4 = body4.string()
                    val doc4 = Jsoup.parse(html4)
                    val rstc = doc4.getElementsByClass("eg deg")
                    val rstctran = doc4.getElementsByClass("trans dtrans dtrans-se hdb break-cj")

                    val r1stc = rstc.subList(0,1)


                    val r1stcText = r1stc.map {it.text() }.joinToString("\n")
                    val r1stctran = rstctran.subList(0,1)
                    val r1stctranText = r1stctran.map {it.text() }.joinToString("\n")

                    /*if(rstc.subList(1,2).isEmpty()){
                        val r2stcText = ""
                        val r2stctranText = ""
                        withContext(Dispatchers.Main) {
                            //sgword.text = "今天沒有喔~"
                            stcword2.text = r2stcText
                            stctran2.text = r2stctranText
                        }
                    }
                    else{
                        val r2stc = rstc.subList(1,2)
                        val r2stcText = r2stc.map {it.text() }.joinToString("\n")
                        val r2stctran = rstctran.subList(1,2)
                        val r2stctranText = r2stctran.map {it.text() }.joinToString("\n")
                        withContext(Dispatchers.Main) {
                            //sgword.text = "今天沒有喔~"
                            stcword2.text = r2stcText
                            stctran2.text = r2stctranText
                        }
                    }*/



                    withContext(Dispatchers.Main) {
                        //sgword.text = "今天沒有喔~"
                        sgword.text = r1wordsText + " " + r1meanText
                        stcword.text = r1stcText
                        stctran.text = r1stctranText
                        stcword2.text = "" //r2stcText
                        stctran2.text = "" //r2stctranText

                    }

                }
            }

        }

    }


    fun goLogon(view: View) {
        val intent = Intent(this, Logon::class.java)
        startActivity(intent)
        finish()
    }

    private fun getUserId(): String {
        return currentUser?.uid ?: ""
    }


    override fun onClick(p0: View?) {
        //0
    }

    override fun onLongClick(p0: View?): Boolean {
        //0
        return false
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        //TODO("Not yet implemented")
        return false
    }


    fun goHome(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        // 調用 gohome() 方法，以便在回到 home.kt 之前更新畫面
        val view = View(this)
        goHome(view)
        // 調用 finish() 方法，回到 home.kt
        finish()
    }


}

