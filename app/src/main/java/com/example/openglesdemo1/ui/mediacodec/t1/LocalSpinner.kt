package com.example.openglesdemo1.ui.mediacodec.t1

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.example.openglesdemo1.R

class LocalSpinner : LinearLayout {
    private val mContext: Context
    private var mName: String? = null
    private var mTv: TextView? = null
    private var mSpinner: Spinner? = null
    private var mEntries: Array<CharSequence>? = null
    var mListener: Listener? = null

    interface Listener {
        fun onChanged(str: String)
    }

    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        mContext = context
        val type = mContext.obtainStyledAttributes(attr, R.styleable.LocalSpinner)
        mName = type.getString(R.styleable.LocalSpinner_android_name)
        mEntries = type.getTextArray(R.styleable.LocalSpinner_android_entries)
        type.recycle()
        initView()
    }

    private fun initView() {
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_local_spinner, this, false)
        mTv = view.findViewById(R.id.tv)
        mSpinner = view.findViewById(R.id.spinner)
        mSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?,
                position: Int, id: Long
            ) {
                mListener?.onChanged(mEntries?.get(position)?.toString() ?: "")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        mTv?.text = mName
        refreshAdapter()
        addView(view)
    }

    fun setEntries(entries: Array<CharSequence>?) {
        mEntries = entries
        refreshAdapter()
    }

    private fun refreshAdapter() {
        mEntries?.let {
            val adapter: ArrayAdapter<CharSequence> = ArrayAdapter<CharSequence>(
                context, android.R.layout.simple_spinner_item, it
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSpinner?.setAdapter(adapter)
        }
    }

    fun getSelected() = mEntries?.get(mSpinner?.selectedItemPosition ?: 0)?.toString() ?: ""
}