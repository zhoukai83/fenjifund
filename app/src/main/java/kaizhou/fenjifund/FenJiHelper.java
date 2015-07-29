package kaizhou.fenjifund;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by b-kaizho on 7/24/2015.
 */
public class FenJiHelper {
    public ArrayList<FenJiData> FetchFenJiData()
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/sdcard/fenjiFund.db", null);
        Cursor c = db.rawQuery("SELECT * FROM fundinfo", null);

        ArrayList<FenJiData> list = new ArrayList<>();
        while (c.moveToNext()) {
            FenJiData data = new FenJiData();

            data.aCode = c.getString(c.getColumnIndex("FundACode"));
            data.aName = c.getString(c.getColumnIndex("FundAName"));
            data.bCode = c.getString(c.getColumnIndex("FundBCode"));
            data.bName = c.getString(c.getColumnIndex("FundBName"));

            data.motherCode = c.getString(c.getColumnIndex("FundMotherCode"));
            data.motherName = c.getString(c.getColumnIndex("FundMotherName"));

            data.aRatio = c.getInt(c.getColumnIndex("FundAWeight"));
            data.bRatio = c.getInt(c.getColumnIndex("FundBWeight"));

            data.zhiShuCode = c.getString(c.getColumnIndex("FundZhiShuCode"));
            data.zhiShuName = c.getString(c.getColumnIndex("FundZhiShuName"));

            list.add(data);
        }
        c.close();
        db.close();

        return list;
    }

    public ArrayList<FenJiData> UpdateFenJiValue(ArrayList<FenJiData> list)
    {
        SinaRealDataService service = new SinaRealDataService();

        ArrayList<String> ids = new ArrayList<>();
        for(FenJiData data : list)
        {
            ids.add(data.aCode);
            ids.add(data.bCode);
        }

        List<SinaRealTimeData> valueList = service.Get(ids);

        for(FenJiData data : list)
        {
            for(SinaRealTimeData value : valueList)
            {
                if(data.aCode.equals(value.Id))
                {
                    data.aValue = value.Sell1;
                }
                else if(data.bCode.equals(value.Id))
                {
                    data.bValue = value.Sell1;
                }
            }
        }

        for(FenJiData data : list)
        {
            data.combineValue = data.aValue * data.aRatio / 10 + data.bValue * data.bRatio / 10;
        }

        return list;
    }

    public ArrayList<FenJiData> GetMotherFundValue(ArrayList<FenJiData> list)
    {
        String content = WebsiteHelper.InvokeUrlByGet("http://fund.eastmoney.com/Ruanjian_Pzgz_gszzl.html");

        try {
            Pattern regex = Pattern.compile("tr class=\"line.*?td>(\\d\\d\\d\\d\\d\\d).*?gsz'>(\\d+\\.\\d+).*?(\\d.\\d+)</td></tr");
            Matcher regexMatcher = regex.matcher(content);
            while (regexMatcher.find()) {

                for(FenJiData fund : list)
                {
                    if(fund.motherCode.equals(regexMatcher.group(1)))
                    {
                        fund.motherEvaluate = Float.parseFloat(regexMatcher.group(2));
                        fund.motherValue = Float.parseFloat(regexMatcher.group(3));
                    }
                }
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }

        return list;
    }

    public boolean Notify(ArrayList<FenJiData> list, float threshold)
    {
        boolean find = false;

        for(FenJiData fund : list)
        {
            if(fund.combineValue == 0)
            {
                continue;
            }

            fund.yiJiaLv = (fund.combineValue - fund.motherEvaluate) / fund.motherEvaluate;

           if(fund.yiJiaLv < threshold)
               find = true;
        }


        Collections.sort(list, new YiJiaLvComparator());

        for(FenJiData fund: list) {
            Log.d("FenJiHelper", String.format("%s,%s,%f,%f,%f,%f,%f", fund.motherCode, fund.motherName, fund.yiJiaLv, fund.combineValue, fund.motherEvaluate, fund.aValue, fund.bValue));
        }

        return find;
    }
}
