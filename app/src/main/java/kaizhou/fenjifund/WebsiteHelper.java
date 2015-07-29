package kaizhou.fenjifund;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by b-kaizho on 7/23/2015.
 */
public class WebsiteHelper {
    public static String InvokeUrlByGet(String url)
    {
        String result = "";

        HttpClient client = new DefaultHttpClient();
        try {
            HttpGet get = new HttpGet(url);

            HttpResponse response = client.execute(get);

            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                result = EntityUtils.toString(resEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }

        return result;
    }

    public String GetDataByPost()
    {
        // replace with your url
        HttpPost httpPost = new HttpPost("http://www.jisilu.cn/data/sfnew/arbitrage_vip_list/?___t=1437632304641");
        List nameValuePair = new ArrayList();
        nameValuePair.add(new BasicNameValuePair("is_search", "1"));
        nameValuePair.add(new BasicNameValuePair("avolume", ""));
        nameValuePair.add(new BasicNameValuePair("bvolume", ""));
        nameValuePair.add(new BasicNameValuePair("market[]", "sh"));
        nameValuePair.add(new BasicNameValuePair("market[]", "sz"));
        nameValuePair.add(new BasicNameValuePair("ptype", "sell"));
        nameValuePair.add(new BasicNameValuePair("rp", "50"));

        String url = "http://192.168.1.103/index.php";
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost(url);
            List params = new ArrayList();
            params.add(new BasicNameValuePair("get1", "hello"));
            params.add(new BasicNameValuePair("get2", "usrl"));
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() ==200){
                HttpEntity entity = response.getEntity();

                Header contentEncoding = response
                        .getFirstHeader("Content-Encoding");
                if (contentEncoding != null
                        && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(entity.getContent()));
                    InputStreamReader isr = new InputStreamReader(is);
                    java.io.BufferedReader br = new java.io.BufferedReader(isr);
                    StringBuffer sb = new StringBuffer();
                    String tempbf;
                    while ((tempbf = br.readLine()) != null) {
                        sb.append(tempbf);
                        sb.append("\r\n");
                    }

                    Log.d("FenJiData", sb.toString());
                }
                else
                {
                    Log.d("FenJiData", entity.toString());
                    String content = EntityUtils.toString(entity,"utf-8");
                    Log.d("FenJiData", content);
                    return content;
                }





            }else{
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            Log.d("FenJiData", e.getMessage());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Log.d("FenJiData", e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("FenJiData", e.getMessage());
        }

        return "";
    }
}
