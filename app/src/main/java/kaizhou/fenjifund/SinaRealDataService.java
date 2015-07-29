package kaizhou.fenjifund;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SinaRealDataService {
    public List<SinaRealTimeData> Get(List<String> ids)
    {
        ArrayList<SinaRealTimeData> list = new ArrayList<>();

        for (int i = 0; i < ids.size(); ++i)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("http://hq.sinajs.cn/list=");

            for (int j = 0; j < 200 && i < ids.size(); ++i, ++j)
            {
                sb.append(ToSinaId(ids.get(i)));
                sb.append(",");
            }

            --i;

            String content = WebsiteHelper.InvokeUrlByGet(sb.toString());
            String[] lines = content.split(";");
            for(String line : lines)
            {
                String[] splitString = line.split(",");
                if (splitString.length < 5)
                {
                    continue;
                }

                try
                {
                    String code = null;

                    Pattern regex = Pattern.compile("(?<=(sh|sz)).*?(?==)");
                    Matcher regexMatcher = regex.matcher(splitString[0]);
                    if (regexMatcher.find()) {
                        code = regexMatcher.group();
                    }

                    SinaRealTimeData item = new SinaRealTimeData();
                    item.Id = code;
                    item.Name = splitString[0].substring(splitString[0].lastIndexOf("\"") + 1);
                    item.Current = Float.parseFloat(splitString[3]);
                    item.Sell1 = Float.parseFloat(splitString[7]);
                    list.add(item);

                }catch (PatternSyntaxException ex) {
                    // Syntax error in the regular expression
                }
                catch (Exception ex)
                {
                    // Syntax error in the regular expression
                }
            }
        }

        return list;
    }

    private String ToSinaId(String id)
    {
        if (id.startsWith("6"))
        {
            return "sh" + id;
        }

        if (id == "000001")
        {
            return "sh" + id;
        }

        return "sz" + id;
    }
}
