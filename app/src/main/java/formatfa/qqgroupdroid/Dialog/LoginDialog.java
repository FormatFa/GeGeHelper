package formatfa.qqgroupdroid.Dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import FQQSdk.LoginListener;
import FQQSdk.QQSDK;
import FQQSdk.ptuiCBLogin;
import formatfa.qqgroupdroid.R;
import formatfa.qqgroupdroid.Tool.LogTool;
import formatfa.qqgroupdroid.Tool.PathTool;
import formatfa.qqgroupdroid.Tool.TipTool;
import utils.HttpUtils;
import utils.Utils;

public class LoginDialog implements LoginListener,View.OnClickListener{

    private Thread qqthread = new Thread()
    {
        @Override
        public void run() {
            sdk.cancel();
            try {
                sdk.getHttpUtil().initnCookies();
                sdk.startLink(qrPath.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Context context;
    private QQSDK sdk;

    private View view;
    private ImageView qricon,icon;
    private TextView msg;;
    private AlertDialog.Builder alertDialog;

    private PathTool pathTool;
    private File qrPath ,cookiepath;
    private final String qr="qrcode.png";

    private LogTool logTool;
    private boolean isFirstLogin = true;

    private LoginedListener loginedListener;

    public File getLoginDataPath()
    {
        return loginDataPath;
    }
    public void setLoginedListener(LoginedListener loginedListener) {
        this.loginedListener = loginedListener;
    }

    private  File loginDataPath;
    public LoginDialog(Context context, QQSDK sdk) {
        this.context = context;
        this.sdk = sdk;

        sdk.setLoginListener(this);
        alertDialog = new AlertDialog.Builder(context);
        pathTool = new PathTool();

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.login_dialog,null);
        qricon = view.findViewById(R.id.qrcode);
        icon = view.findViewById(R.id.icon);
        msg = view.findViewById(R.id.msg);

        msg.setOnClickListener(this);
        qrPath = new File(pathTool.getWorkPath(),qr);
        cookiepath = new File(pathTool.getWorkPath(),"cookie.txt");
        loginDataPath=new File(pathTool.getWorkPath(),"loginresult.txt");


        logTool = new LogTool();

    }




    public void show(boolean check)
    {

        if(check)
        {
            checkLogined();return;
        }
        alertDialog.setView(view);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                sdk.cancel();
            }
        });
        alertDialog.setPositiveButton("取消",null);
        alertDialog.show();







    }

    private void checkLogined() {
        if(loginDataPath.exists())
        {

            try {
                final ptuiCBLogin lg =new ptuiCBLogin(Utils.readString(loginDataPath.getAbsolutePath())) ;

                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setMessage("检测到以下账户已登,录:"+lg.getNick());
                ab.setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HttpUtils utils = sdk.getHttpUtil();
                        try {
                            utils.readLocalCookie(cookiepath);
                            sdk.setHttpUtil(utils);
                            login(lg.getNick());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                ab.setNegativeButton("重新扫描", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        show(false);
                    }
                });
                ab.show();
            } catch (Exception e) {
                e.printStackTrace();
                show(false);
            }
        }

        else
            show(false);

    }

    private void login(String nick)
    {
        if(loginedListener!=null)loginedListener.login(nick);

    }
    private void comfirAgain()
    {

        AlertDialog.Builder ab = new AlertDialog.Builder (context);
        ab.setMessage("重新连接获取二维码？");
        ab.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                qqthread.start();
            }
        });
        ab.setNegativeButton("取消",null).show();




    }

    public void sendmsg(String str)
    {
        logTool.add(str);
        msg.setText(str);
    }
    @Override
    public void onLogined(QQSDK sdk, String responedUrls) {
        sdk.cancel();
        try {
            sdk.getHttpUtil().saveLocalCookie(cookiepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendmsg("on logined:"+responedUrls);

        try {
            login(new ptuiCBLogin(responedUrls).getNick());
        } catch (Exception e) {
            e.printStackTrace();
            login(e.toString());
        }

    }

    @Override
    public void onQRLoaded(QQSDK sdk, String path) {
        sendmsg("qrload:"+path);
        qricon.setImageBitmap(BitmapFactory.decodeFile(path));


        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+path)));

    }

    public String getQQ(String data)
    {
        Pattern pattern = Pattern.compile("&uin=\\d{1,}");
        Matcher matcher =pattern.matcher(data);
        if(matcher.find())
        {
            return matcher.group().substring(5);

   }
        return null;
    }
    @Override
    public void onQRResult(QQSDK sdk, int requestTime, String result) {
        sendmsg("qr result:"+result);
        try {
            Utils.writeString(result,loginDataPath.getAbsolutePath());



        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sdk.getHttpUtil().saveLocalCookie(cookiepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(QQSDK sdk, String error) {

        sendmsg("err:"+error);
    }

    @Override
    public void onStop(QQSDK sdk) {

        sendmsg("stop:");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.msg:
                if(!isFirstLogin)
                {
                    TipTool.SimpleDialog(context,logTool.toString());
                    comfirAgain();

               return;
                }

                isFirstLogin = false;
                qqthread.start();
//                try {
//                    sdk.startLink(qrPath.getAbsolutePath());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    TipTool.SimpleDialog(context,"err".concat(e.toString()));
//                }
                break;
        }
    }
}
