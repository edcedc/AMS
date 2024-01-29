package com.yyc.ams.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;


import com.yyc.ams.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author nike
 * @Date 2023/6/9 15:03
 * @Description
 */
public class AssetTextAdapter2 extends BaseAdapter {

    private Context act;
    private JSONArray jsonArray;

    public AssetTextAdapter2(Context act, JSONArray jsonArray) {
        this.act = act;
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return jsonArray.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = convertView.inflate(act, R.layout.i_text, null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            JSONObject object = jsonArray.getJSONObject(i);

            viewHolder.tv_text.setText(object.optString("title") + "：" + object.optString("text"));

            if (object.optString("title").equals("InventoryStatus")){
                if (object.optInt("text") == 1){
                    viewHolder.tv_text.setText("Inventory Status：" + act.getText(R.string.found));
                }else {
                    viewHolder.tv_text.setText("Inventory Status：" + act.getText(R.string.missing));
                }
            }
            if (object.optString("title").equals("scanStatus")){
                if (object.optInt("text") == 116){
                    viewHolder.tv_text.setText("Scan Status：RFID");
                }else if (object.optInt("text") == 117){
                    viewHolder.tv_text.setText("Scan Status：QRCode");
                }else if (object.optInt("text") == 118){
                    viewHolder.tv_text.setText("Scan Status：Manually");
                }else {
                    viewHolder.tv_text.setText("Scan Status：");
                }
            }
            if (object.optString("title").equals("FoundStatus")){
                if (object.optInt("text") == 116){
                    viewHolder.tv_text.setText("Found Status：RFID");
                }else if (object.optInt("text") == 117){
                    viewHolder.tv_text.setText("Found Status：QRCode");
                }else if (object.optInt("text") == 118){
                    viewHolder.tv_text.setText("Found Status：Manually");
                }else {
                    viewHolder.tv_text.setText("Found Status：");
                }
            }

            if (object.optString("title").equals("ScanDate")){
                viewHolder.tv_text.setText("Scan Date：" + object.optString("text"));
            }

            if (object.optString("title").equals("ScanUser")){
                viewHolder.tv_text.setText("Scan User：" + object.optString("text"));
            }

            if (object.optString("title").equals("LabelTag")){
                viewHolder.tv_text.setText("Label Tag：" + object.optString("text"));
            }

        } catch (JSONException e) {
            throw new RuntimeException("InventoryDesc2Adapter go go go");
        }
        return convertView;
    }

    class ViewHolder{

        AppCompatTextView tv_text;

        public ViewHolder(View convertView) {
            tv_text = convertView.findViewById(R.id.tv_text);
        }

    }

}
