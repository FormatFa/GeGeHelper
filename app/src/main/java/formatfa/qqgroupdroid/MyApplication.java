package formatfa.qqgroupdroid;

import android.app.Application;
import android.content.Context;

import FQQSdk.FGroup;
import FQQSdk.QQSDK;

public class MyApplication extends Application {
    private QQSDK qqsdk;


    private FGroup group;

    public FGroup getGroup() {
        return group;
    }

    public void setGroup(FGroup group) {
        this.group = group;
    }

    public QQSDK getsdk(Context context)
    {

        if(qqsdk ==null)qqsdk =new QQSDK(context);
        return qqsdk;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
