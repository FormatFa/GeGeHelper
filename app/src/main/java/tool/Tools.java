package tool;

import com.tencent.qq.widget.QQToast;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class Tools {
	
	
	public static void toast(Context context,String str)
	{
		try{
			QQToast.makeText(context, str).show();
		}
		catch(NoSuchMethodError e)
		{
			
			Toast.makeText(context, str, Toast.LENGTH_LONG).show();
		}
		
	//	
		
	}
	

	public static void copy(Context context,String  str)
	{
		
		ClipboardManager manager = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
		manager.setPrimaryClip(ClipData.newPlainText("copy", str));
		toast(context,"¸´ÖÆ:"+str);
		
	}
public static void SimpleDialog(Context con,String title,String message)
{
	
AlertDialog.Builder ab = new AlertDialog.Builder(con);
ab.setTitle(title);
ab.setMessage(message);
ab.setPositiveButton("ok", null);
ab.show();
	
}
}
