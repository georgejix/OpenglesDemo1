package com.example.openglesdemo1.ui.camera.t1

import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openglesdemo1.R
import kotlinx.android.synthetic.main.item_main.view.*

class SizeAdapter(private val list: List<Size>, private val listener: Listener) :
    RecyclerView.Adapter<SizeAdapter.ViewHolder>() {
    private var mCheckedIndex = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_size, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tv_content.text = list[position].toString()
        holder.itemView.tv_content.setOnClickListener {
            mCheckedIndex = position
            notifyDataSetChanged()
            listener?.onClicked(list[position])
        }
        holder.itemView.tv_content.setTextColor(
            holder.itemView.context.resources.getColor(
                if (position == mCheckedIndex) R.color.color_31d77b else R.color.black,
                null
            )
        )
    }

    override fun getItemCount(): Int = list.size

    interface Listener {
        fun onClicked(size: Size)
    }
}