package com.s1092790.eenglish

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.chat_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

class Chat : AppCompatActivity() {
    companion object {
        const val YOUR_KEY = "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"
        const val URL = "https://api.openai.com/v1/completions"
        //const val URL = "https://api.openai.com/v1/chat/completions"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_main)
        val tvAnswer = findViewById<TextView>(R.id.textView_Answer)

        findViewById<Button>(R.id.button_Send).setOnClickListener {
            val question = findViewById<EditText>(R.id.edittext_Input).text.toString().trim()
            if (question.isEmpty()) return@setOnClickListener

            findViewById<TextView>(R.id.textView_Question).text = question
            tvAnswer.text = "請稍候.."

            // 清空editText
            edittext_Input.setText("")
            edittext_Input.clearFocus()

            // 設置 Header 中的 Content-Type
            val mediaType = MediaType.parse("application/json")
            // 寫入 body
            val content = Gson().toJson(makeRequest(question))
            val requestBody = RequestBody.create(mediaType, content)
            // 發送請求
            HttpRequest().sendPOST(URL, requestBody, object : HttpRequest.OnCallback {
                override fun onOKCall(respond: String?) {
                    respond?.let {
                        Log.d("TAG", "onOKCall: $respond")
                        val chatGPTRespond = Gson().fromJson(it, ChatGPTRespond::class.java)
                        val answer = chatGPTRespond.choices?.get(0)?.text?.trim()
                        runOnUiThread {
                            tvAnswer.text = answer
                        }
                    }
                }

                override fun onFailCall(error: String?) {
                    Log.e("TAG", "onFailCall: $error")
                    tvAnswer.text = error
                }
            })
        }
    }

    // 寫入 body
    private fun makeRequest(input: String): WeakHashMap<String, Any> {
        return WeakHashMap<String, Any>().apply {
            put("model", "text-davinci-003") // 更改模型為 GPT-3.5
            put("prompt", input)
            put("temperature", 0.5)
            put("max_tokens", 500)
            put("top_p", 1)
            put("frequency_penalty", 0)
            put("presence_penalty", 0)
        }
    }

    /*private fun makeRequest(input: String): WeakHashMap<String, Any> {
        return WeakHashMap<String, Any>().apply {
            put("model", "gpt-3.5-turbo")
            put("prompt", input)
            put("temperature", 0.6)
            put("max_tokens", 500)
            put("top_p", 1)
            put("frequency_penalty", 0)
            put("presence_penalty", 0)
            put("stream", false )
            put("stop", "\n" )
            put("user", "my-user-id")
            put("messages", listOf(
                mapOf(
                    "data" to mapOf(
                        "text" to input
                    )
                )
            ))
        }
    }*/

    fun goHome(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        // 調用 gohome() 方法，以便在回到 home.kt 之前更新畫面
        val view = View(this)
        goHome(view)
        // 調用 finish() 方法，回到 home.kt
        finish()
    }
}
