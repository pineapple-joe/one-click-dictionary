package com.example.oneclickdictionary
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import java.util.Timer
import java.util.TimerTask

class DictionaryFragment : Fragment(R.layout.dictionary_fragment) {
    private lateinit var databaseHelper: DictionaryDBHelper
    private lateinit var inputBox: EditText
    private lateinit var saveButton: Button
    private lateinit var resultListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var handler: Handler
    private val resultList = ArrayList<String>()

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

    private fun showSaveNotification(word: String) {
        val channelId = "save_word_channel"
        val context = requireContext()
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's small icon
            .setContentTitle("Word Saved")
            .setContentText("The word '$word' has been saved successfully.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                return@with
            }
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.dictionary_fragment, null)
        val context = requireContext()
        databaseHelper = DictionaryDBHelper(context)
        databaseHelper.createDatabase()

        handler = Handler(Looper.getMainLooper())

        inputBox = root.findViewById(R.id.inputBox)
        inputBox.addTextChangedListener(textWatcher)

        resultListView = root.findViewById(R.id.resultListView)
        adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, resultList)
        resultListView.adapter = adapter

        saveButton = root.findViewById(R.id.searchButton)
        saveButton.setOnClickListener {
            val word = inputBox.getText().toString()
            databaseHelper.addWord(word, resultList)
            showSaveNotification(word)
        }

        return root
    }
}
