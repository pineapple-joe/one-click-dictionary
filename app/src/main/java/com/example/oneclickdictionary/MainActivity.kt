package com.example.oneclickdictionary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DictionaryDBHelper
    private lateinit var inputBox: EditText
    private lateinit var saveButton: Button
    private lateinit var resultListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val resultList = ArrayList<String>()
    private lateinit var handler: Handler

    private var textWatcher: TextWatcher = object : TextWatcher {
        var delay : Long = 1000
        var timer = Timer()

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            timer.cancel()
            timer.purge()
            resultList.clear()
        }

        override fun afterTextChanged(s: Editable) {
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    val word = inputBox.getText().toString()
                    val wordDefinitions = databaseHelper.getWord(word)
                    for (item in wordDefinitions) {
                        resultList.add(item.definition.removeSurrounding("\""))
                    }
                    handler.postDelayed({adapter.notifyDataSetChanged()}, 0)
                }
            }, delay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(Looper.getMainLooper())

        databaseHelper = DictionaryDBHelper(this)
        databaseHelper.createDatabase()

        inputBox = findViewById(R.id.inputBox)
        inputBox.addTextChangedListener(textWatcher)

        resultListView = findViewById(R.id.resultListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resultList)
        resultListView.adapter = adapter

        saveButton = findViewById(R.id.searchButton)
        saveButton.setOnClickListener {
            databaseHelper.addWord(inputBox.getText().toString(), resultList)
        }
    }
}