package com.s1092790.eenglish

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.cloud.translate.Translate
import kotlinx.android.synthetic.main.activity_main.*

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.logon_main.*
import java.net.URLDecoder
import java.net.URLEncoder

class Home : AppCompatActivity(), OnClickListener, OnLongClickListener, OnTouchListener {
    //lateinit var binding: ActivityMainBinding
    private var translate: Translate? = null
    var size:Float = 30f

    var touchDownTime = System.currentTimeMillis()
    var touchUpTime = System.currentTimeMillis()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main)


        eve.setTypeface(
            Typeface.createFromAsset(assets,
                "font/SentyCreamPuff.ttf"))
        rec.setTypeface(
            Typeface.createFromAsset(assets,
                "font/SentyCreamPuff.ttf"))


        //binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)

        backbtn.setOnClickListener {
            val intent = Intent(this, Logon::class.java )
            startActivity(intent)
        }


        gamebtn.visibility = View.GONE
        chatbtn.visibility = View.GONE
        musicbtn.visibility = View.GONE

        gamebtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchDownTime = System.currentTimeMillis()
                    gamebtn.setImageResource(R.drawable.gbtnclick)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    touchUpTime = System.currentTimeMillis()
                    val touchDuration = touchUpTime - touchDownTime
                    gamebtn.setImageResource(R.drawable.gbtn)
                    if(touchDuration <= 1000){
                        val intent = Intent(this, Game::class.java)
                        startActivity(intent)
                    }
                    true

                }
                else -> false
            }
        }

        musicbtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchDownTime = System.currentTimeMillis()
                    musicbtn.setImageResource(R.drawable.mbtnclick)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    touchUpTime = System.currentTimeMillis()
                    val touchDuration = touchUpTime - touchDownTime
                    musicbtn.setImageResource(R.drawable.mbtn)
                    if(touchDuration <= 1000){
                        val intent = Intent(this, sing::class.java)
                        startActivity(intent)
                    }
                    true

                }
                else -> false
            }
        }

        chatbtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchDownTime = System.currentTimeMillis()
                    chatbtn.setImageResource(R.drawable.cbtnclick)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    touchUpTime = System.currentTimeMillis()
                    val touchDuration = touchUpTime - touchDownTime
                    chatbtn.setImageResource(R.drawable.cbtn)
                    if(touchDuration <= 1000){
                        val intent = Intent(this, Chat::class.java)
                        startActivity(intent)
                    }
                    true

                }
                else -> false
            }
        }

        notebtn.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 在按下時更改圖片
                    notebtn.setImageResource(R.drawable.notebtngray)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 在釋放或取消時還原圖片
                    notebtn.setImageResource(R.drawable.notebtn)
                }
            }
            // 返回false表示該事件未被消耗，以便繼續觸發點擊事件
            false
        }

        notebtn.setOnClickListener {
            // 在這裡處理點擊事件
            val intent = Intent(this, Note::class.java)
            startActivity(intent)
        }

        translatebtn.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 在按下時更改圖片
                    translatebtn.setImageResource(R.drawable.translatebtngray)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 在釋放或取消時還原圖片
                    translatebtn.setImageResource(R.drawable.translatebtn)
                }
            }
            // 返回false表示該事件未被消耗，以便繼續觸發點擊事件
            false
        }

        translatebtn.setOnClickListener {
            // 在這裡處理點擊事件
            val view = View(this)
            translate(view)
        }



        //tv_hello.text = "主頁"

        http()
        //testtxv2.text = ""
        //btn_click.setOnClickListener { btn_click.text="您點了一下下"}






    }


    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 設置要用哪個menu檔做為選單
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }*/


    fun http() {
        lifecycleScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://dictionary.cambridge.org/zht/%E8%A9%9E%E5%85%B8/%E8%8B%B1%E8%AA%9E-%E6%BC%A2%E8%AA%9E-%E7%B9%81%E9%AB%94/")//劍橋主頁
                .build()
            val response = client.newCall(request).execute()
            val body = response.peekBody(Long.MAX_VALUE)
            val html = body.string()
            val doc = Jsoup.parse(html)
            val words = doc.select("p.fs36 a")//每日一詞

            val wordsText = words.map {it.text().lowercase() }.joinToString("\n")

            val request2 = Request.Builder()
                .url("https://dictionary.cambridge.org/zht/%E8%A9%9E%E5%85%B8/%E8%8B%B1%E8%AA%9E-%E6%BC%A2%E8%AA%9E-%E7%B9%81%E9%AB%94/$wordsText")
                .build()
            val response2 = client.newCall(request2).execute()
            val body2 = response2.peekBody(Long.MAX_VALUE)
            val html2 = body2.string()
            val doc2 = Jsoup.parse(html2)


            val mean = doc2.getElementsByClass("trans dtrans dtrans-se  break-cj")//抓單字意思
            val tmean = mean.subList(0,1)
            //val meanText = tmean.map {it.text().lowercase() }.joinToString("\n")
            val meanText = tmean.joinToString("\n") { it.text().lowercase() }

            val pos = doc2.getElementsByClass("pos dpos")//抓詞性
            //val posText = pos.map { it.text().lowercase() }.joinToString("\n")
            val posText = pos.joinToString("\n") { it.text().lowercase() }

            val request3 = Request.Builder()
                .url("https://tw.voicetube.com/definition")
                .build()
            val response3 = client.newCall(request3).execute()
            val body3 = response3.peekBody(Long.MAX_VALUE)
            val html3 = body3.string()
            val doc3 = Jsoup.parse(html3)


            val rwords = doc3.getElementsByClass("sc-f549624-0 hEPORM")
            val rpos = doc3.getElementsByClass("has-text-weight-medium")
            val rmean = doc3.getElementsByClass("sc-a4ea01be-1 cJbrEa")
            val r1words = rwords.subList(0,1)
            val r2words = rwords.subList(1,2)
            val r3words = rwords.subList(2,3)
            val r4words = rwords.subList(3,4)
            val r5words = rwords.subList(4,5)
            val r6words = rwords.subList(5,6)
            val r7words = rwords.subList(6,7)
            val r8words = rwords.subList(7,8)
            val r9words = rwords.subList(8,9)
            val r10words = rwords.subList(9,10)
            val r1pos = rpos.subList(0,1)
            val r2pos = rpos.subList(1,2)
            val r3pos = rpos.subList(2,3)
            val r4pos = rpos.subList(3,4)
            val r5pos = rpos.subList(4,5)
            val r6pos = rpos.subList(5,6)
            val r7pos = rpos.subList(6,7)
            val r8pos = rpos.subList(7,8)
            val r9pos = rpos.subList(8,9)
            val r10pos = rpos.subList(9,10)
            val r1mean = rmean.subList(0,1)
            val r2mean = rmean.subList(1,2)
            val r3mean = rmean.subList(2,3)
            val r4mean = rmean.subList(3,4)
            val r5mean = rmean.subList(4,5)
            val r6mean = rmean.subList(5,6)
            val r7mean = rmean.subList(6,7)
            val r8mean = rmean.subList(7,8)
            val r9mean = rmean.subList(8,9)
            val r10mean = rmean.subList(9,10)
            val r1wordsText = r1words.map {it.text() }.joinToString("\n")
            val r2wordsText = r2words.map {it.text() }.joinToString("\n")
            val r3wordsText = r3words.map {it.text() }.joinToString("\n")
            val r4wordsText = r4words.map {it.text() }.joinToString("\n")
            val r5wordsText = r5words.map {it.text() }.joinToString("\n")
            val r6wordsText = r6words.map {it.text() }.joinToString("\n")
            val r7wordsText = r7words.map {it.text() }.joinToString("\n")
            val r8wordsText = r8words.map {it.text() }.joinToString("\n")
            val r9wordsText = r9words.map {it.text() }.joinToString("\n")
            val r10wordsText = r10words.map {it.text() }.joinToString("\n")
            val r1posText = r1pos.map {it.text() }.joinToString("\n")
            val r2posText = r2pos.map {it.text() }.joinToString("\n")
            val r3posText = r3pos.map {it.text() }.joinToString("\n")
            val r4posText = r4pos.map {it.text() }.joinToString("\n")
            val r5posText = r5pos.map {it.text() }.joinToString("\n")
            val r6posText = r6pos.map {it.text() }.joinToString("\n")
            val r7posText = r7pos.map {it.text() }.joinToString("\n")
            val r8posText = r8pos.map {it.text() }.joinToString("\n")
            val r9posText = r9pos.map {it.text() }.joinToString("\n")
            val r10posText = r10pos.map {it.text() }.joinToString("\n")
            val r1meanText = r1mean.map {it.text() }.joinToString("\n")
            val r2meanText = r2mean.map {it.text() }.joinToString("\n")
            val r3meanText = r3mean.map {it.text() }.joinToString("\n")
            val r4meanText = r4mean.map {it.text() }.joinToString("\n")
            val r5meanText = r5mean.map {it.text() }.joinToString("\n")
            val r6meanText = r6mean.map {it.text() }.joinToString("\n")
            val r7meanText = r7mean.map {it.text() }.joinToString("\n")
            val r8meanText = r8mean.map {it.text() }.joinToString("\n")
            val r9meanText = r9mean.map {it.text() }.joinToString("\n")
            val r10meanText = r10mean.map {it.text() }.joinToString("\n")



            withContext(Dispatchers.Main) {
                if(wordsText.isEmpty()){
                    eveword.text = "今天維修中~"
                }
                else{
                    eveword.text = wordsText + "\n" + posText + "\n" +meanText
                }

                word1.text = r1wordsText + "\n" + r1meanText
                word2.text = r2wordsText + "\n" + r2meanText
                word3.text = r3wordsText + "\n" + r3meanText
                word4.text = r4wordsText + "\n" + r4meanText
                word5.text = r5wordsText + "\n" + r5meanText
                word6.text = r6wordsText + "\n" + r6meanText
                word7.text = r7wordsText + "\n" + r7meanText
                word8.text = r8wordsText + "\n" + r8meanText
                word9.text = r9wordsText + "\n" + r9meanText
                word10.text = r10wordsText + "\n" + r10meanText

                //testtxv.text = wordsText + "  " + meanText //要做推薦單字
                //testtxv2.text = r5wordsText  //記得改
                //texttxvchi.text = r6wordsText  //記得改

                /*<TextView
                        android:id="@+id/testtxv2"
                        android:layout_width="180dp"
                        android:layout_height="wrap_content"
                        android:layout_marginLeft="50dp"
                        android:layout_marginTop="10dp"
                        android:layout_weight="1"
                        android:text=""
                        android:textSize="20dp" />

                    <TextView
                        android:id="@+id/texttxvchi"
                        android:layout_width="180dp"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="10dp"
                        android:layout_weight="1"
                        android:textSize="20dp" />*/
            }
        }
    }


    fun logg(view: View){
        val intent = Intent(this, Logon::class.java )
        startActivity(intent)
    }


    fun translate(view: View) {
        GlobalScope.launch(Dispatchers.IO) {
            val originalText: String = inputToTranslate.text.toString()
            val client = OkHttpClient()
            val request2 =
                Request.Builder()
                    .url("https://dictionary.cambridge.org/zht/%E8%A9%9E%E5%85%B8/%E8%8B%B1%E8%AA%9E-%E6%BC%A2%E8%AA%9E-%E7%B9%81%E9%AB%94/%22"+originalText)
                        .build()
                        val response2 = client.newCall(request2).execute()
            val body2 = response2.peekBody(Long.MAX_VALUE)
            val html2 = body2.string()
            val doc2 = Jsoup.parse(html2)
            val mean = doc2.getElementsByClass("trans dtrans dtrans-se  break-cj")
            if (mean.isEmpty()) {
                withContext(Dispatchers.Main) {
                    translatedTv.text = "查無相關資料"
                }
            } else {
                val tmean = mean.subList(0,1)
                val meanText = tmean.map {it.text() }.joinToString("\n")
                withContext(Dispatchers.Main){
                    translatedTv.text = meanText
                }
            }
        }
    }



    fun goGame(view: View) {
        val intent = Intent(this, Game::class.java)
        startActivity(intent)
    }

    fun goSing(view: View) {
        val intent = Intent(this, sing::class.java)
        startActivity(intent)
    }

    fun goChat(view: View) {
        val intent = Intent(this, Chat::class.java)
        startActivity(intent)
    }
    fun goNote(view: View) {
        val intent = Intent(this, Note::class.java)
        startActivity(intent)
    }

    fun goLogon(view: View) {
        val intent = Intent(this, Logon::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        // 調用 gohome() 方法，以便在回到 home.kt 之前更新畫面
        val view = View(this)
        goLogon(view)
        // 調用 finish() 方法，回到 home.kt
        finish()
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

}