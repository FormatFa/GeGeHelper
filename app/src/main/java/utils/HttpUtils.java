package utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
/**
 * @author formatfa
 *  Http工具类，下载字符，图片那些
 */
public class HttpUtils {
	
	
	/**
	 * @author formatfa
	 * Http返回结果，headerFiels为返回的头，object 自行转换为相应的类型
	 */
	public static class HttpResult
	{
		public HttpResult(Object result, Map<String, List<String>> headerFields) {
			super();
			this.result = result;
			this.headerFields = headerFields;
		}
		Object result;
		
		public Object getResult() {
			return result;
		}

		public void setResult(Object result) {
			this.result = result;
		}

		public Map<String, List<String>> getHeaderFields() {
			return headerFields;
		}

		public void setHeaderFields(Map<String, List<String>> headerFields) {
			this.headerFields = headerFields;
		}
		Map<String, List<String>> headerFields;
		
		
	}
	
	
	private Context context;
	private HashMap<String,String> cookies ;
	
	public HashMap<String, String> getCookies() {
		return cookies;
	}



	public HttpUtils(Context context) {
		super();
		this.context = context;
		
		cookies = new HashMap<String,String>();
	}



	public void readLocalCookie(File path) throws Exception
	{
		
		cookies = (HashMap<String, String>) Utils.readObject(path.getAbsolutePath());
		if(cookies == null)cookies = new HashMap<String,String>();
		
	}
	public void initnCookies()
	{
		cookies = new HashMap<String,String>();
	}
	public void saveLocalCookie(File out) throws Exception
	{
		
		Utils.writeObject(out.getAbsolutePath(), cookies);
	}
	public void addCookiestr(String str)
	{
		if(str == null)return;
		String[] allPair = str.split(";");
		
		for(String item:allPair)
		{
			if(item.startsWith(" "))continue;
			
			

			String[] s = item.split("=");
			if(s.length<2)continue;
			
			
			cookies.put(s[0], s[1]);
			
			
		}
		
		
		
	}

	/**
	 * @param url 下载的路径
	 * @param path 保存的目录
	 * @param maps post参数键
	 * @return 下载结果
	 * @throws Exception
	 */
	public HttpResult downFile(String url,String path,HashMap<String,String> maps) throws Exception {
		HttpResult re = down(url,maps);
		InputStream is =(InputStream) re.getResult();
		
		OutputStream os = new FileOutputStream (path);
		copyStream(is,os);
		
		return re;
	}
	
	
	
	
	public  HttpResult downloadString(String url,HashMap<String,String > cookies) throws Exception
	{
	return downloadString(url,cookies,"UTF-8");
	}
	
	/**
	 * @param url 网址
	 * @param post post键值对
	 * @param charset 下载返回的编码
	 * @return httpresult string
	 * @throws Exception
	 */
	public HttpResult downloadString(String url,HashMap<String,String > post,String charset) throws Exception
	{
		
		StringBuilder result = new StringBuilder();
		
		HttpResult re = down(url,post);
		
		InputStream is =(InputStream) re.getResult();
		
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(is,charset));
				
		String line = null;
				
				while(  (line = buffReader.readLine())!=null )
				{
					result.append(line)
;				}
		return new HttpResult(result.toString(), re.getHeaderFields());
		
		
	}
	
	/**
	 * @param url
	 * @return inputstream
	 * @throws Exception
	 */
	public  HttpResult down(String url) throws Exception
	{
		return down(url,null);
	}
	
	public  HttpResult down(String url,HashMap<String,String> cook) throws Exception
	{
		
		URL u = new  URL(url);
		
		HttpURLConnection connection = (HttpURLConnection)u.openConnection();
		
		//禁止网络重定向，获取cookie，终于好了
		HttpURLConnection.setFollowRedirects(false);
		
		if(cook!=null)
		{
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			
		}
		else
			connection.setRequestMethod("GET");
		
		
		
        Map<String, List<String>> requestheader = connection.getRequestProperties();
		

        
        StringBuilder cooksb = new StringBuilder();
        
        for(String key:cookies.keySet())
        {
        	cooksb.append(key + "=" + cookies.get(key));
        	cooksb.append(";");
        	
        }
        
        System.out.println("http request with cookie:"+ cooksb.toString());
     
        //
        connection.addRequestProperty("Cookie", cooksb.toString());
		connection.connect();
		
		
		
		
		
		
		if(cook!=null)
		{
			DataOutputStream os = new DataOutputStream(connection.getOutputStream());
			StringBuilder sb = new StringBuilder();
			for(String str:cook.keySet())
				
			{
				String s = str+"="+cook.get(str);
				sb.append(s);
				
				sb.append('&');
			}
			
			String po = sb.toString();
			
			if(po.endsWith("&"))
				po = po.substring(0,po.length()-1);
	
			os.write(po.getBytes());
			os.flush();
			os.close();
		}
		
		


		Map<String, List<String>> header = connection.getHeaderFields();
		
		String cookie = connection.getHeaderField("Set-Cookie");
		addCookiestr(cookie);
		System.out.println("----------print Header Fields ");
		for(String keys:header.keySet())
		{
			if(keys==null)continue;
			System.out.println("Fields +"+header.get(keys));
			
			if(keys.equals("Set-Cookie"))
			{
				
				List<String> fs = header.get(keys);
				for(String item:fs)
					addCookiestr(item);
				
			}
			
			
			
			
			
			
		}
	 return new HttpResult(connection.getInputStream(),header);
	}
	

	public static void copyStream (InputStream is,OutputStream os) throws IOException
	{
	byte[] buff = new byte[1024*4];
	int readcout;
	
	while (   (readcout=is.read(buff))!=-1   )
	{
		os.write(buff,0,readcout);
	}
	
	os.close();
	
	
		
	}

}
