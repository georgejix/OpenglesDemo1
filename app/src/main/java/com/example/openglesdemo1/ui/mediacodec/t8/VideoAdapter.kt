package com.example.openglesdemo1.ui.mediacodec.t8

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openglesdemo1.R
import kotlinx.android.synthetic.main.item_video.view.tv_name

class VideoAdapter() :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
    private val mData: ArrayList<VideoBean> by lazy { ArrayList() }

    fun setData(list: List<VideoBean>) {
        mData.clear()
        mData.addAll(list)
    }

    fun getData() = mData

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mData[position].apply {
            holder.itemView.tv_name.text = "${if (mIndex > 0) "$mIndex-" else ""}$mName"
            holder.itemView.tv_name.setOnClickListener {
                mChecked = !mChecked
                mCheckTime = System.currentTimeMillis()
                val list = ArrayList(mData)
                list.sortWith { v1, v2 ->
                    if (v1.mCheckTime > v2.mCheckTime) 1 else -1
                }
                var index = 1
                list.forEach {
                    it.mIndex = if (it.mChecked) index++ else 0
                }
                notifyDataSetChanged()
            }
            holder.itemView.tv_name.setTextColor(if (mChecked) Color.RED else Color.BLACK)
        }
    }

    override fun getItemCount(): Int = mData.size
}