package com.example.oneclickdictionary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class SavedTranslationsAdapter(
    private val context: Context,
    private val words: List<String>,
    private val definitions: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return definitions[words[groupPosition]]!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val definition = getChild(groupPosition, childPosition)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(android.R.layout.simple_list_item_1, null)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = definition
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return definitions[words[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): String {
        return words[groupPosition]
    }

    override fun getGroupCount(): Int {
        return words.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_group_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.groupTextView)
        textView.text = getGroup(groupPosition).replaceFirstChar { it.uppercase() }
        return view
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}