package kaizhou.fenjifund;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by b-kaizho on 7/23/2015.
 */
public class FenJiData  implements Parcelable  {
    public String aCode;

    public String aName;

    public float aValue;

    public String bCode;

    public String bName;

    public float bValue;

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