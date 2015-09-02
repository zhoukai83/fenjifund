package kaizhou.fenjifund;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Comparator;

/**
 * Created by b-kaizho on 7/23/2015.
 */
public class FenJiData  implements Parcelable  {
    public String aCode;

    public String aName;

    public float aValue;

    public float aSell1Volume;

    public String bCode;

    public String bName;

    public float bValue;

    public float bSell1;

    public int bSell1Volume;

    public float bBuy1;

    public float bCurrent;

    public float bYesterdayValue;

    public float bIncrease;

    public String motherCode;

    public String motherName;

    public float motherValue;

    public float motherEvaluate;

    public String zhiShuCode;

    public String zhiShuName;

    public int aRatio;

    public int bRatio;

    public float combineValue;

    public float yiJiaLv;

    public boolean notify;

    public boolean exceedYiJiaLv;

    public FenJiData()
    {
        notify = true;
        exceedYiJiaLv = false;
    }

    @Override
    public String toString()
    {
        return String.format("%.3f %f", aValue, yiJiaLv);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(aCode);
        dest.writeString(aName);
        dest.writeFloat(aValue);

        dest.writeString(bCode);
        dest.writeString(bName);
        dest.writeFloat(bValue);
        dest.writeFloat(bSell1);
        dest.writeInt(bSell1Volume);
        dest.writeFloat(bBuy1);
        dest.writeFloat(bCurrent);
        dest.writeFloat(bYesterdayValue);
        dest.writeFloat(bIncrease);

        dest.writeString(motherCode);
        dest.writeString(motherName);
        dest.writeFloat(motherValue);
        dest.writeFloat(motherEvaluate);

        dest.writeString(zhiShuCode);
        dest.writeString(zhiShuName);

        dest.writeInt(aRatio);
        dest.writeInt(bRatio);

        dest.writeFloat(combineValue);
        dest.writeFloat(yiJiaLv);
        dest.writeByte((byte) (notify ? 1 : 0));
        dest.writeByte((byte) (exceedYiJiaLv ? 1 : 0));
    }

    public static final Parcelable.Creator<FenJiData> CREATOR = new Creator<FenJiData>() {
        public FenJiData createFromParcel(Parcel source) {
            FenJiData data = new FenJiData();
            data.aCode = source.readString();
            data.aName = source.readString();
            data.aValue = source.readFloat();

            data.bCode = source.readString();
            data.bName = source.readString();
            data.bValue = source.readFloat();
            data.bSell1 = source.readFloat();
            data.bSell1Volume = source.readInt();
            data.bBuy1 = source.readFloat();
            data.bCurrent = source.readFloat();
            data.bYesterdayValue = source.readFloat();
            data.bIncrease = source.readFloat();

            data.motherCode = source.readString();
            data.motherName = source.readString();
            data.motherValue = source.readFloat();
            data.motherEvaluate = source.readFloat();

            data.zhiShuCode = source.readString();
            data.zhiShuName = source.readString();

            data.aRatio = source.readInt();
            data.bRatio = source.readInt();

            data.combineValue = source.readFloat();
            data.yiJiaLv = source.readFloat();
            data.notify = source.readByte() != 0;
            data.exceedYiJiaLv = source.readByte() != 0;

            return data;
        }
        public FenJiData[] newArray(int size) {
            return new FenJiData[size];
        }
    };
}


class YiJiaLvComparator implements Comparator<FenJiData> {
    @Override
    public int compare(FenJiData o1, FenJiData o2) {
        return Float.compare(o1.yiJiaLv, o2.yiJiaLv);
    }
}