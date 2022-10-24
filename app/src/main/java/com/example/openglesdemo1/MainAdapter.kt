package com.example.openglesdemo1

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openglesdemo1.ui.base.BaseActivity
import kotlinx.android.synthetic.main.item_main.view.*

class MainAdapter(private val list: List<MainBean<out Activity>>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tv_content.text = list[position].name
        holder.itemView.tv_content.setOnClickListener {
            list[position].clazz?.let {
                holder.itemView.context.startActivity(
                    Intent(
                        holder.itemView.context,
                        it
                    )
                )
            }
        }
        holder.itemView.tv_content.setTextColor(
            holder.itemView.context.resources.getColor(
                R.color.color_31d77b,
                null
            )
        )
    }

    override fun getItemCount(): Int = list.size
}