package com.yyc.ams.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

/**
 * @auther ${Nike}
 * @date 2023/5/24
 * @time 11:24.
 */
public abstract class BaseListViewAdapter<T> extends BaseAdapter {

    protected List<T> listBean;
    protected Context act;

    public BaseListViewAdapter(Context act, List<T> listBean) {
        this.listBean = listBean;
        this.act = act;
    }

    @Override
    public int getCount() {
        return listBean.size();
    }

    @Override
    public Object getItem(int position) {
        return listBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCreateVieww(position, convertView, parent);
    }

    protected abstract View getCreateVieww(int position, View convertView, ViewGroup parent);

    protected void showToast(String title) {
        ToastUtils.showShort(title);
    }

}
