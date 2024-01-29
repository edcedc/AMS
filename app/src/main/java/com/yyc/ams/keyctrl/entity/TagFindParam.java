package com.yyc.ams.keyctrl.entity;

public class TagFindParam {
    public String ScanMB;
    public byte[] Data;
    public byte[] Mask;

    public boolean isMatching(byte[] data) {
        boolean isM = true;
        if(data != null && data.length > 0) {
            if(Data != null && Data.length > 0 && Mask != null && Mask.length > 0) {
                int dLen = data.length;
                int DLen = Data.length;
                for (int i = 0; i < 8; i++) {
                    if (i >= dLen || i >= DLen)
                        break;
                    for (int j = 0; j < 8; j++) {
                        if ((Mask[i] & (0x01 << j)) > 0) {
                            if ((data[i] & (0x01 << j)) != (Data[i] & (0x01 << j))) {
                                isM = false;
                                break;
                            }
                        }
                    }
                    if (!isM)
                        break;
                }
            }
        }
        else
            isM = false;
        return isM;
    }
}
