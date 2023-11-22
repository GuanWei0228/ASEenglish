package com.s1092790.eenglish


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.*
import android.view.GestureDetector.OnGestureListener
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.widget.TextView
import kotlinx.android.synthetic.main.game_main.*
import kotlinx.android.synthetic.main.bp_main.*
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView


class Game  : AppCompatActivity(), OnClickListener,OnLongClickListener,OnTouchListener,OnGestureListener{
    lateinit var txv:TextView
    var counter:Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_main)
        /*txv = findViewById(R.id.testffff)
        txv.text = counter.toString()
        //txv.setOnClickListener(this)*/

        //shiba.setOnClickListener(this)
        val crImageView = findViewById<ImageView>(R.id.cr)
        val cdownDrawable = resources.getDrawable(R.drawable.cdown)



        moveDown.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    crImageView.setImageDrawable(cdownDrawable)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // 在這裡可以寫按鈕釋放後的邏輯
                    true
                }
                else -> false
            }
        }

        val cupDrawable = resources.getDrawable(R.drawable.cup)

        moveUp.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    crImageView.setImageDrawable(cupDrawable)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // 在這裡可以寫按鈕釋放後的邏輯
                    true
                }
                else -> false
            }
        }


        val cleftDrawable = resources.getDrawable(R.drawable.cleft)

        moveLeft.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    crImageView.setImageDrawable(cleftDrawable)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // 在這裡可以寫按鈕釋放後的邏輯
                    true
                }
                else -> false
            }
        }


        val crightDrawable = resources.getDrawable(R.drawable.cright)

        moveRight.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    crImageView.setImageDrawable(crightDrawable)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // 在這裡可以寫按鈕釋放後的邏輯
                    true
                }
                else -> false
            }
        }
        //moveUp.setOnLongClickListener(this)

        // 點擊按鈕時，彈出背包對話框
        bp.setOnClickListener {
            openBackpackDialog(this)
        }

        quest.setOnClickListener {
            openQuestDialog(this)
        }

    }

    private fun openBackpackDialog(context: Context) {
        // 創建一個 Dialog 對象
        val dialog = Dialog(context)
        // 設置對話框標題
        dialog.setTitle("背包")
        // 設置對話框內容
        dialog.setContentView(R.layout.bp_main)
        // 設置對話框的大小和位置
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        // 設置對話框可以取消
        dialog.setCancelable(true)
        // 設置對話框內的按鈕事件
        dialog.xclose.setOnClickListener {
            // 點擊確定按鈕時，關閉對話框
            dialog.dismiss()
        }
        // 顯示對話框
        dialog.show()
    }

    private fun openQuestDialog(context: Context) {
        // 創建一個 Dialog 對象
        val dialog = Dialog(context)
        // 設置對話框標題
        dialog.setTitle("成就任務")
        // 設置對話框內容
        dialog.setContentView(R.layout.quest_main)
        // 設置對話框的大小和位置
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        // 設置對話框可以取消
        dialog.setCancelable(true)
        // 設置對話框內的按鈕事件
        dialog.xclose.setOnClickListener {
            // 點擊確定按鈕時，關閉對話框
            dialog.dismiss()
        }
        // 顯示對話框
        dialog.show()
    }

    fun happy(v: View){
        //var txv: TextView = findViewById(R.id.txv)
        counter = (1..100).random()
        txv.text = counter.toString()
    }



    fun goHome(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        counter++
        txv.text = counter.toString()
    }

    override fun onLongClick(p0: View?): Boolean {
        //TODO("Not yet implemented")
        counter+=2
        txv.text = counter.toString()
        return true
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        /*if (event?.action == MotionEvent.ACTION_MOVE) {
            testffff.text = "手指按下"
        }
        else if (event?.action == MotionEvent.ACTION_UP){
            testffff.text = "手指彈開"
        }*/
        /*testffff.setTypeface(
            Typeface.createFromAsset(assets,
                "font/SentyCreamPuff.ttf"))*/

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // API 31以上
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {  //API < 31
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (event?.action == MotionEvent.ACTION_DOWN) {
            //testffff.text = "手指按下"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  //API >= 26
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(1000)
            }
        }
        else if (event?.action == MotionEvent.ACTION_UP){
            //testffff.text = "手指彈開"
            vibrator.cancel()
        }


        return true

    }


    override fun onBackPressed() {
        // 調用 gohome() 方法，以便在回到 home.kt 之前更新畫面
        val view = View(this)
        goHome(view)
        // 調用 finish() 方法，回到 home.kt
        finish()
    }

    override fun onDown(e: MotionEvent): Boolean {
        TODO("Not yet implemented")
    }

    override fun onShowPress(e: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        TODO("Not yet implemented")
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onLongPress(e: MotionEvent) {
        TODO("Not yet implemented")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        TODO("Not yet implemented")
    }

}


