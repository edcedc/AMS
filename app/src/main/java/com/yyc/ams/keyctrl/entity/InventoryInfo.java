package com.yyc.ams.keyctrl.entity;

public class InventoryInfo {
    public String TagId;//标签ID
    public String Rssi;//RSSI
    public String Count = "1";//读取次数
    public String ReadTime = "";//读取时间
    public String MB = "EPC";//数据区域“EPC”或// “TID”

    @Override
    public String toString() {
        return "InventoryInfo{" +
                "TagId='" + TagId + '\'' +
                ", Rssi='" + Rssi + '\'' +
                ", Count='" + Count + '\'' +
                ", ReadTime='" + ReadTime + '\'' +
                ", MB='" + MB + '\'' +
                '}';
    }
}
