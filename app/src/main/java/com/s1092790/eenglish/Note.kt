package com.s1092790.eenglish

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class Note : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var saveButton: Button
    private lateinit var database: DatabaseReference
    private lateinit var textView: TextView
    private lateinit var deleteButton: Button

    private lateinit var recyclerView: RecyclerView

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        val adapter = NoteAdapter()
        recyclerView.adapter = adapter

        // 設置 RecyclerView 的點擊監聽器
        adapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // 在這裡處理點擊事件，例如顯示刪除對話框
            }
        })

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        editText = findViewById(R.id.editText)
        saveButton = findViewById(R.id.save1)
        textView = findViewById(R.id.textView)
        deleteButton = findViewById(R.id.deleteButton)

        textView.setTypeface(
            Typeface.createFromAsset(assets,
                "font/HanyiSentyScholar.ttf"))



        database = Firebase.database.reference

        if (currentUser == null) {
            // Redirect to sign-in or handle the situation accordingly
            val view = View(this)
            goLogon(view)
        }

        saveButton.setOnClickListener {
            val text = editText.text.toString()
            saveTextToDatabase(text)
        }




        database.child("texts").child(getUserId()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stringBuilder = StringBuilder()
                for (data in snapshot.children) {
                    val text = data.getValue(String::class.java)
                    if (text != null) {
                        stringBuilder.append(text).append("\n")
                    }
                }
                textView.text = stringBuilder.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Note", "Failed to read value.", error.toException())
            }
        })


        deleteButton.setOnClickListener {
            val userId = getUserId()
            val databaseRef = Firebase.database.reference.child("texts").child(userId)


            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val itemsToDelete = mutableListOf<String>()

                        for (data in snapshot.children) {
                            val itemId = data.key // 獲取項目的 ID
                            if (itemId != null) {
                                itemsToDelete.add(itemId)
                            }
                        }


                        // 依序刪除每個項目
                        for (itemId in itemsToDelete) {
                            val itemRef = databaseRef.child(itemId)
                            itemRef.removeValue()
                                .addOnSuccessListener {
                                    // 在這裡執行刪除成功的處理，例如更新 UI 或顯示消息
                                    Toast.makeText(this@Note, "Item deleted", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    // 在這裡處理刪除失敗的情況
                                    Toast.makeText(this@Note, "Error deleting item", Toast.LENGTH_SHORT).show()
                                }
                        }


                        // 清空 RecyclerView 的數據
                        val adapter = recyclerView.adapter as NoteAdapter
                        adapter.setItems(emptyList())
                    } else {
                        Toast.makeText(this@Note, "No items to delete", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 在這裡處理讀取資料失敗的情況
                    Toast.makeText(this@Note, "Failed to read data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveTextToDatabase(text: String) {
        val userId = getUserId()
        val key = database.child("texts").child(userId).push().key
        database.child("texts").child(userId).child(key!!).setValue(text)
            .addOnSuccessListener {
                // Your existing success listener...
                NoteAdapter().setItems(listOf(text))
                Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show()
                editText.setText("")
            }
            .addOnFailureListener {
                // Your existing failure listener...
                Toast.makeText(this, "Error saving text", Toast.LENGTH_SHORT).show()
            }



    }

    fun goHome(view: View) {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }
    fun goLogon(view: View) {
        val intent = Intent(this, Logon::class.java)
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

    private fun getUserId(): String {
        return currentUser?.uid ?: ""
    }
}