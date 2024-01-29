package com.yyc.ams.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.yyc.ams.R
import org.json.JSONArray

/**
 * @Author
 * @Date 2023/6/9 10:59
 * @Description
 */
class AssetDetailsAdapter (
    act: Context,
    jsonArray: JSONArray
) : RecyclerView.Adapter<ViewHolder>() {

    val act = act
    val jsonArray = jsonArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.i_asset_text, parent, false)
        )
    }

    override fun getItemCount(): Int = jsonArray.length()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val opt = jsonArray.getJSONObject(position)
        viewHolder.setText(R.id.tv_text, opt.optString("title"))
        val list = opt.optJSONArray("list")
        var adapter = AssetTextAdapter2(act, list)
        viewHolder.getView<ListView>(R.id.recyclerView).adapter = adapter
        adapter?.notifyDataSetChanged()
    }

}