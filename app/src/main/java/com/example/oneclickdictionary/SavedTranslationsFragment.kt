package com.example.oneclickdictionary
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class SavedTranslationsFragment : Fragment(R.layout.saved_translations) {
    private lateinit var databaseHelper: DictionaryDBHelper
    private lateinit var resultListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var resultList: Map<String, List<Word>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.saved_translations, null)
        val context = requireContext()
        databaseHelper = DictionaryDBHelper(context)
        resultList = databaseHelper.getSavedWords()

        resultListView = root.findViewById(R.id.savedTranslationsListView)
        adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, resultList.map { it.key })
        resultListView.adapter = adapter

        return root
    }

}
