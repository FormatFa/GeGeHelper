package FQQSdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import utils.HttpUtils;
import utils.HttpUtils.HttpResult;
import utils.Utils;

public class QQSDK {



    public static final int ONLOGINED=1,QRLOAD=2,QRRESULT=3,ONERR=4,ONSTOP=5;

    /*

	public void onLogined(QQSDK sdk, String responedUrls);

	public void onQRLoaded(QQSDK sdk, String path);
	public void onQRResult(QQSDK sdk, int requestTime, String result);

	public void onError(QQSDK sdk, String error);
	public void onStop(QQSDK sdk);
}
     */
   private  Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
         switch (msg.what)
         {
             case ONLOGINED:
                 if(loginListener!=null)loginListener.onLogined(QQSDK.this, (String) msg.obj);
                 break;
             case QRLOAD:
                 if(loginListener!=null)loginListener.onQRLoaded(QQSDK.this, (String) msg.obj);
                 break;
             case QRRESULT:
                 if(loginListener!=null)loginListener.onQRResult(QQSDK.this,requestTime, (String) msg.obj);
                 break;
             case ONERR:
                 if(loginListener!=null)loginListener.onError(QQSDK.this, (String) msg.obj);
                 break;
             case ONSTOP:
                 if(loginListener!=null)loginListener.onStop(QQSDK.this);
                 break;

         }
       }
   };

	private LoginListener loginListener;

	private String CSRFToken;

	boolean debug = true;


	int period = 3000;
	boolean isCancel = false;

	private String result = null;

	
	
	
	private List<Friends> qqFriends;
	
	//HashMap<String,String> cookies ;
	
	///String allCookies;
	
	private Context context ;
	
	private HttpUtils httpUtil;
	public HttpUtils getHttpUtil() {
		return httpUtil;
	}
	public LoginListener getLoginListener() {
		return loginListener;
	}

	public void setLoginListener(LoginListener loginListener) {
		this.loginListener = loginListener;
	}
	public void setHttpUtil(HttpUtils httpUtil) {
		this.httpUtil = httpUtil;
	}

	public QQSDK(Context context) {
		super();
		this.context = context;
		httpUtil = new HttpUtils(context);
		
	}

	public List<Friends> loadFriends() throws Exception
	{
	
		qqFriends = new ArrayList<Friends>();

		HashMap postData = new HashMap();
		postData.put("bkn", this.getCSRFToken(this.getCookie("skey")));
		String result =(String) httpUtil.downloadString("http://qun.qq.com/cgi-bin/qun_mgr/get_friend_list", postData).getResult();
		
		return null;
	}


	public String getCookie(String name) {

		for(String key:httpUtil.getCookies().keySet())
		{
			if(key.equals(name))
			{
				System.out.println("get by cookies:"+httpUtil.getCookies().get(key));
				return httpUtil.getCookies().get(key);
			}
			
			
		}
		return name;

	
}

	public static String getptqrtoken(String qrsig) {
		int e = 0;
		for (int i = 0, n = qrsig.length(); n > i; ++i)
			e += (e << 5) + qrsig.charAt(i);
		return String.valueOf(2147483647 & e);

	}

	public String[] ptuiCBParse(String ptui)
	{
		String[] result = new String[6];
		
		if(ptui==null)return result;
		if(ptui.startsWith("ptuiCB")==false)return result;
		
		if(ptui.length()< 8)return result;
		ptui = ptui.substring(7,ptui.length()-2);
		
		
		
		result = ptui.split(",");
		
		
		for(int i = 0;i< result.length;i+=1)
		{
			result[i] = result[i].replace("'","");
		}
			return result;
		
	}
	// 获取好友列表那些要post一个bkn参数
	public String getCSRFToken(String skey) {

		if(skey == null)return null;
		int r = 5381;
		for (int n = 0, o = skey.length(); o > n; ++n)
			r += (r << 5) + skey.charAt(n);
		return this.CSRFToken = String.valueOf(2147483647 & r);

	}

	public static String qrsig = "qrsig";
	public static String urcode = "https://ssl.ptlogin2.qq.com/ptqrshow?appid=715030901";

	@SuppressLint("NewApi")
	public QQSDK() {
		super();

	}

	public void cancel() {
		isCancel = true;
	}

	int requestTime = 0;

	
	public void login(String url) throws Exception
	{
		System.out.print("view the login url");
		HttpResult result = httpUtil.downloadString(url,null);
		//if(debug)
	//	System.out.println("返回的登录成功的url访问结果:" + result.getResult());
		
		Utils.writeString((String)result.getResult(), "/sdcard/LoginResult");
		//addCookies(result.getHeaderFields());
	}
	

	/**
	 * @param urpath 保存2维码的目录
	 * @throws Exception
	 */
	public void startLink(String urpath) throws Exception {

		
		
		
		isCancel = false;
		String url = "https://xui.ptlogin2.qq.com/cgi-bin/xlogin?appid=715030901&daid=73&pt_no_auth=1&s_url=http%3A%2F%2Fqun.qq.com%2F";

		try {
			HttpResult result = httpUtil.down(url);

		
		} catch (Exception e) {

			e.printStackTrace();

			if (loginListener != null)
				loginListener.onError(this, e.toString());
			return;
		}

		HttpResult result = httpUtil.downFile(urcode, urpath, null);

       Message msg = new Message();
       msg.what=QRLOAD;
        msg.obj = urpath;
        handler.sendMessage(msg);
		
		final String qrsign = getCookie(qrsig);

		
		
	

		Timer tim = new Timer();
		requestTime = 0;
		tim.schedule(new TimerTask() {

			@Override
			public void run() {

				// 扫描结果
				/*
				 * ptuiCB('67','0','','0','二维码认证中。(231758162)', '')
				 * ptuiCB('67','0','','0','二维码认证中。(3707783276)', '')
				 * ptuiCB('0','0',
				 * 'http://ptlogin2.qun.qq.com/check_sig?pttype=1&uin=1758759399&service=ptqrlogin&nodirect=0&ptsigx=b1545813dcd3109c25a9f04ac83e013653b72f68b01a7d02c489a92c7d72c108362a0ea649481b2c76c8e5ddf3193f996dfa5f513cef3485a378dfe9ea28cc01&s_url=http%3A%2F%2Fqun.qq.com%2F&f_url=&ptlang=2052&ptredirect=101&aid=715030901&daid=73&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=3&pt_aid=0&pt_aaid=16&pt_light=0&pt_3rd_aid=0','1','登录成功！',
				 * '格式化法')
				 */

				// 判断是否扫了二维码的网址，其中的qrsig 为下载 2维码图片时
				// 返回的cookies，ptqrtoken为根据qrsig计算的

			String	url = "https://ssl.ptlogin2.qq.com/ptqrlogin?u1=http%3A%2F%2Fqun.qq.com%2F&ptqrtoken="
						+

						getptqrtoken(qrsign)
						+ "&ptredirect=1&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=0-0-1514632963316&js_ver=10233&js_type=1&login_sig="
						+ qrsign
						+ "&pt_uistyle=40&aid=715030901&daid=73&has_onekey=1&";

			

				System.out.println("qrsig :"+ qrsign);
				try {
					String sresult = (String) httpUtil
							.downloadString(url, null).getResult();

					System.out.println("Response Result:" + sresult);
					
					
					if (loginListener != null)
                    {
						Message msg = new Message();
                        msg.what=QRRESULT;
                        msg.obj = sresult;
                        handler.sendMessage(msg);
                    }

					
					System.out.println("test:".concat(""+sresult.contains("http://ptlogin2.qun.qq.com/")));
					if(sresult.contains("http://ptlogin2.qun.qq.com/"))
					{
						System.out.println("hava get login sus:"+ sresult);
						String [] pc = ptuiCBParse(sresult);
					
						System.out.println("confirm url:"+ pc[2]);
						login(pc[2]);
						
					if (loginListener != null)
					{
						Message msg = new Message();
						System.out.println("send logined to.....");
                        msg.what=ONLOGINED;
                        msg.obj = sresult;
                        handler.sendMessage(msg);
					//	loginListener.onLogined(QQSDK.this, sresult);
					}
					isCancel = true;
						this.cancel();
						return;
					}
					
				} catch (Exception e) {

					e.printStackTrace();


					Message msg2 = new Message();
                    msg2.what=ONERR;
                    msg2.obj = e.toString();
                    handler.sendMessage(msg2);
					return;
				}
				requestTime += 1;

				if (isCancel)
				{System.out.println("加载二维码连接已取消");	this.cancel();return;}
				
			}

		}, 0, period);

	}


	String mypath = System.getProperty("user.dir","");
	File savePath =new File(mypath,"localcookies");
	@SuppressLint("NewApi")
	public void saveCookies() throws Exception
	{
		
		if(!savePath.exists())
		{
			if(!savePath.mkdirs())System.out.println("create cookies dir fail:" + savePath.getAbsolutePath());
		}
		
		int i = 0;
//		for(URI u:cookiemng.getCookieStore().getURIs())
//		{
//			
//			File out= new File(savePath, i+ ".uri1");
//			Utils.writeObject(out.getAbsolutePath(),u);
//			
//			out = new File(savePath, i+ ".cookie1");
//		//	Utils.writeObject(out.getAbsolutePath(),cookiemng.get(u,null));
//			i+=1;
//			
//			
//		}
		
		
		
		
	}

	
	public void readCookies() throws Exception{
		
		for(int i = 0 ; ;i+=1)
		{
			
			File in= new File(savePath, i+ ".uri1");
	
			File coo = new File(savePath, i+".cookie1");
			if(in.exists() == false)break;
			if(coo.exists() == false)break;
			
			URI u =(URI) Utils.readObject(in.getAbsolutePath());
			
			Map<String,List<String>> data = (Map<String,List<String>>) Utils.readObject(coo.getAbsolutePath()) ;
			
			
		//	cookiemng.put(u, data);
			
		}
	}
}
