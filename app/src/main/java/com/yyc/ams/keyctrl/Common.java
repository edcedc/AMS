package com.yyc.ams.keyctrl;


import com.yyc.ams.keyctrl.mytable.DataGridColumParam;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import rfid.uhfapi_y2007.ApiApplication;
import rfid.uhfapi_y2007.IPort;
import rfid.uhfapi_y2007.core.ConfigFile;
import rfid.uhfapi_y2007.core.ConfigFileItem;
import rfid.uhfapi_y2007.entities.InventoryConfig;
import rfid.uhfapi_y2007.entities.TagParameter;
import rfid.uhfapi_y2007.protocol.vrp.Reader;


/**
 * Created by tcy001 on 2018/3/27.
 */

public class Common {

    //public static ConfigFile configFile = new ConfigFile();
    public static Reader reader;
    public static ScanType scanType = ScanType.Scan;
    public static TagParameter selectParam;
    public static TagParameter selectEpcParam;
    public static String curUser = "";
    public static boolean isEnableBeep = true;
    public static boolean isIsEnableLed = true;
    public static String tagEncoding = "HEX";

    public static InventoryConfig getInventoryConfig(){
        ConfigFile cf = new ConfigFile();
        ConfigFileItem cfi = cf.FindReaderItem(reader.ReaderName);
        InventoryConfig config = new InventoryConfig(cfi.getInventoryConfig());
        return config;
    }

    public static void setInventoryConfig(InventoryConfig config){
        ConfigFile cf = new ConfigFile();
        ConfigFileItem cfi = cf.FindReaderItem(reader.ReaderName);
        cfi.setInventoryConfig(config.getConfigString());
        cf.ModifyReaderItem(reader.ReaderName,cfi);
    }

    public static void setReaderConnPort(String readerName, IPort iPort){
        ConfigFile cf = new ConfigFile();
        ConfigFileItem cfi = cf.FindReaderItem(readerName);
        String str = iPort.getConnStr();
        if(str.indexOf(",") != -1){
            cfi.setPortType("RS232");
            cfi.setConnStr(str);
        }
        else if(str.indexOf(":") != -1){
            cfi.setPortType("TcpClient");
            cfi.setConnStr(str);
        }

        cf.ModifyReaderItem(readerName,cfi);
    }

    /* c 要填充的字符
     *  length 填充后字符串的总长度
     *  content 要格式化的字符串
     *  格式化字符串，左补齐
     * */
    public static String padLeft(char c, long length, String content){
        String str = "";
        long cl = 0;
        String cs = "";
        if (content.length() > length){
            str = content;
        }else{
            for (int i = 0; i < length - content.length(); i++){
                cs = cs + c;
            }
        }
        str = cs + content;
        return str;
    }

    /* c 要填充的字符
     *  length 填充后字符串的总长度
     *  content 要格式化的字符串
     *  格式化字符串，右补齐
     * */
    public static String padRight(char c, long length, String content){
        String str = "";
        long cl = 0;
        String cs = "";
        if (content.length() > length){
            str = content;
        }else{
            for (int i = 0; i < length - content.length(); i++){
                cs = cs + c;
            }
        }
        str = content + cs;
        return str;
    }

    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    public static List<DataGridColumParam> getDataGridViewColumnInfo(int infoName){
        List<DataGridColumParam> columParams = new ArrayList<>();
        String[] ps = ApiApplication.getContext().getResources().getStringArray(infoName);
        for (String s : ps) {
            String[] ary = s.split(",");
            DataGridColumParam ocp = new DataGridColumParam();
            ocp.index = Integer.parseInt(ary[0]);
            ocp.name = ary[1];
            ocp.text = ary[2];
            ocp.width = Integer.parseInt(ary[3]);
            ocp.visible = (ocp.width != 0);
            columParams.add(ocp);
        }
        return columParams;
    }

    public static String getRSSI(int ri)
    {
        byte rssi = 0;
        rssi = (byte)ri;
        int mantissa = (rssi & 0x07);
        int exponent = (rssi >> 3);
        double r = (20 * Math.log10(Math.pow(2, exponent) * (1 + mantissa / Math.pow(2, 3)))) - 110;
        return String.format("%.1f", r);
    }

    public static String bytesToAscii(byte[] bytes)
    {
        String str = "";
        if(bytes != null && bytes.length > 0)
        {
            try {
                str = new String(bytes,"ascii");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return  str;
    }

    public static byte[] asciiToBytes(String str)
    {
        byte[] bs = null;
        try {
            bs=str.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  bs;
    }
}
