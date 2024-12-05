package com.example.oneclickdictionary
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment

class SavedDefinitionsFragment : Fragment(R.layout.saved_definitions) {
    private lateinit var databaseHelper: DictionaryDBHelper
    private lateinit var resultList: MutableMap<String, MutableList<String>>
    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: SavedTranslationsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.saved_definitions, container, false)
        expandableListView = view.findViewById(R.id.savedDefinitionsListView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        databaseHelper = DictionaryDBHelper(context)
        resultList = databaseHelper.getSavedWords()

        adapter = SavedTranslationsAdapter(requireContext(), resultList.map { it.key }, resultList)
        expandableListView.setAdapter(adapter)
    }

}
