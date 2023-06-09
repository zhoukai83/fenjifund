package kaizhou.fenjifund;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
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

        Date currentDate = new Date();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(currentDate);
        cal.set(Calendar.HOUR_OF_DAY, 15);

        Date clockOn15 = cal.getTime();

        for(FenJiData data : list)
        {
            for(SinaRealTimeData value : valueList)
            {
                if(currentDate.after(clockOn15)) {
                    if (data.aCode.equals(value.id)) {
                        data.aValue = value.current;
                    } else if (data.bCode.equals(value.id)) {
                        data.bValue = value.current;
                        data.bYesterdayValue = value.yesterdayClose;
                        data.bSell1 = value.sell1;
                        data.bSell1Volume = value.sell1Volume;
                        data.bCurrent = value.current;
                        data.bBuy1 = value.buy1;
                    }
                }
                else {
                    if (data.aCode.equals(value.id)) {
                        data.aValue = value.sell1;
                    } else if (data.bCode.equals(value.id)) {
                        data.bValue = value.sell1;
                        data.bYesterdayValue = value.yesterdayClose;
                        data.bSell1 = value.sell1;
                        data.bSell1Volume = value.sell1Volume;
                        data.bCurrent = value.current;
                        data.bBuy1 = value.buy1;
                    }
                }
            }

            data.bIncrease = (data.bValue - data.bYesterdayValue) / data.bYesterdayValue;
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
       ////     Pattern regex = Pattern.compile("tr class=\"line.*?td>(\\d\\d\\d\\d\\d\\d).*?gsz'>(\\d+\\.\\d+).*?(\\d.\\d+)</td></tr");
            Pattern regexFund = Pattern.compile("tr class=\"l.*?tr");
            Matcher regexMatcher = regexFund.matcher(content);

            Pattern regex = Pattern.compile("(\\d{6}).*?gsz'>(\\d+\\.\\d+).*?(\\d+.\\d+)");

            while (regexMatcher.find()) {
                Matcher regexMatcherItem = regex.matcher(regexMatcher.group());
                if (regexMatcherItem.find()) {
                    String motherCode = regexMatcherItem.group(1);
                    for(FenJiData fund : list)
                    {
                        if(fund.motherCode.equals(motherCode))
                        {
                            fund.motherEvaluate = Float.parseFloat(regexMatcherItem.group(2));
                            fund.motherValue = Float.parseFloat(regexMatcherItem.group(3));
                        }
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

           if(fund.notify && fund.yiJiaLv < threshold && fund.yiJiaLv > Constants.invalidYiJiaLvThreshold && fund.bIncrease < 0.099
                   && fund.bSell1 == fund.bCurrent && (fund.bSell1 - fund.bBuy1) <= 0.002)
           {
               find = true;
               fund.exceedYiJiaLv = true;
           }
           else
           {
               fund.exceedYiJiaLv = false;
           }
        }

        Collections.sort(list, new YiJiaLvComparator());

        ArrayList<FenJiData> newArray = new ArrayList<>(list.size());

        for(FenJiData data : list)
        {
            if(data.yiJiaLv > Constants.invalidYiJiaLvThreshold)
            {
                newArray.add(data);
            }
        }

        for(FenJiData data : list)
        {
            if(data.yiJiaLv <= Constants.invalidYiJiaLvThreshold)
            {
                newArray.add(data);
            }
        }

        list.clear();

        for(FenJiData data: newArray)
        {
            list.add(data);
        }

      /*  for(FenJiData fund: list) {
            Log.d("FenJiHelper", String.format("%s,%s,%f,%f,%f,%f,%f", fund.motherCode, fund.motherName, fund.yiJiaLv, fund.combineValue, fund.motherEvaluate, fund.aValue, fund.bValue));
        }*/

        return find;
    }
}
