package formatfa.qqgroupdroid.Tool;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.tencent.qq.widget.QQDialog;

public class TipTool {

    public static void SimpleDialog(Context context,String msg)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setMessage(msg).setTitle(">_<");
        dialog.setPositiveButton("确定",null).show();


    }


    public static void toast(Context context,String message)
    {
        System.out.println("toast:"+message);
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }
}
