package com.yyc.ams.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;

import com.yyc.ams.R;

import java.util.List;

public class OrderTextAdapter extends BaseListViewAdapter<String> {

    public OrderTextAdapter(Context act, List<String> listBean) {
        super(act, listBean);
    }

    @Override
    protected View getCreateVieww(int position, View convertView, ViewGroup parent) {
        String s = listBean.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = convertView.inflate(act, R.layout.i_order_text, null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_text.setText(s);
        return convertView;
    }

    class ViewHolder{

        AppCompatTextView tv_text;
        ViewHolder(View view) {
            tv_text = view.findViewById(R.id.tv_text);
        }
    }

}
