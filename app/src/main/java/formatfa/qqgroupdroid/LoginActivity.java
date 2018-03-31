package formatfa.qqgroupdroid;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import FQQSdk.FGroup;
import FQQSdk.ptuiCBLogin;
import formatfa.qqgroupdroid.Dialog.LoginDialog;
import formatfa.qqgroupdroid.Dialog.LoginedListener;
import formatfa.qqgroupdroid.Tool.PathTool;
import formatfa.qqgroupdroid.Tool.TipTool;
import formatfa.qqgroupdroid.adapter.GroupAdapter;
import formatfa.qqgroupdroid.adapter.ListDecoration;
import utils.Utils;

public class LoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GroupAdapter.OnGroupClickListener ,LoginedListener{


    public static final String key_type="type";
    private ImageView icon;

    private ProgressBar progressBar;
    private RecyclerView list;
    private MyApplication application;

    private String[] permission={Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private LoginDialog loginDialog;
    private GroupAdapter adapter;
    private FGroup group;
    private PathTool pathTool;
    private File localCache;
    private File iconPath;

    private TextView nick;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        application = (MyApplication)getApplication();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginDialog.show(true);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);drawer.addDrawerListener(toggle);
        toggle.syncState();
      navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loginDialog = new LoginDialog(LoginActivity.this,application.getsdk(LoginActivity.this));

        loginDialog.setLoginedListener(this);
        requestPermission();
        initView();
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission,1);
        }


    }

    private void initView() {

        icon =navigationView.getHeaderView(0). findViewById(R.id.icon);
        list = findViewById(R.id.list);

        progressBar = findViewById(R.id.progressBar);
        nick = navigationView.getHeaderView(0).findViewById(R.id.nick);
        progressBar.setVisibility(View.INVISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        list.setLayoutManager(layoutManager);
        list.addItemDecoration(new ListDecoration(this));
        pathTool = new PathTool();
        localCache = new File(pathTool.getWorkPath(),"cache.json");

        iconPath = new File(pathTool.getWorkPath(),"icon.png");
        group = new FGroup(application.getsdk(LoginActivity.this));
       new loadLocalTask().execute(0);
    }

    private void refresh()
    {



    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.module) {
            Intent intent = new Intent(this,SelectModuleActivity.class);
            intent.putExtra(key_type,"all");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

     if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_send) {

        }

        else if(id==R.id.nav_site)
     {
         Intent intent = new Intent(Intent.ACTION_VIEW);
         intent.setData(Uri.parse("http://api.formatfa.top/GeGeGroupHelper"));
         try {
             startActivity(intent);
         }
         catch (ActivityNotFoundException e){
      TipTool.toast(this,"没有找到相应的浏览器打开");
     }
     }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void clickGroup(int p, FGroup.GroupItem item) {

        Toast.makeText(this,"点击了:"+item.getQn(),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,SelectModuleActivity.class);
        intent.putExtra(key_type,"group");
        intent.putExtra("number",item.getGc());
        startActivity(intent);
    }

    @Override
    public void login(String nick) {
        Toast.makeText(this,"start load list...",Toast.LENGTH_SHORT).show();
        group.init();
        new loadGroupTask(false).execute(0);
    }


    class loadGroupTask extends AsyncTask
    {


        private boolean isFromLocal;

        public loadGroupTask(boolean isFromLocal) {
            this.isFromLocal = isFromLocal;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            progressBar.setVisibility(View.INVISIBLE);
            if(o!=null)
            {
                TipTool.SimpleDialog(LoginActivity.this,"加载群失败:"+o);
            }
            else
            {

                if(!isFromLocal)
                try {
                    pathTool.storeGroup(group.getGroupList(),localCache);
                } catch (Exception e) {
                    e.printStackTrace();
                    TipTool.toast(LoginActivity.this,e.toString());

                }
                list.setAdapter(adapter);
            }
            Toast.makeText(LoginActivity.this,"load group list done...:"+adapter.getItemCount(),Toast.LENGTH_SHORT).show();
            application.setGroup(group);
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                List<FGroup.GroupItem > gs =LoginActivity.this.group.getGroupList();

                for(FGroup.GroupItem f:gs)
                {
                    if(f.getMembers()==null)
                    f.getMembers();
                }
                adapter = new GroupAdapter(LoginActivity.this,group.getGroupList());
                adapter.setListener(LoginActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
            //---------icon==---------------
            String qq = null;
            try {
                qq = loginDialog. getQQ(Utils.readString(loginDialog.getLoginDataPath().getAbsolutePath()));
                if(qq!=null)
                {
                    String str = "http://q2.qlogo.cn/headimg_dl?bs=QQ号&dst_uin=QQ号&dst_uin=QQ号&;dst_uin=QQ号&spec=100&url_enc=0&referer=bu_interface&term_type=PC".replace("QQ号",qq);

                    try {
                        application.getsdk(LoginActivity.this).getHttpUtil().downFile(str,new File(pathTool.getWorkPath(),"icon.png").getAbsolutePath(),null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



            return null;
        }
    }
    class loadInfoTask extends AsyncTask
    {


        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }
    class loadLocalTask extends AsyncTask
    {


        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TipTool.toast(LoginActivity.this,"开始加载本地缓存.....");
            progressDialog = ProgressDialog.show(LoginActivity.this,"haha","请稍后，加载缓存中....");
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if(!localCache.exists())return "缓存文件不存在";

            try {
                List<FGroup.GroupItem> result = pathTool.readGroup(localCache);


                group.setGroups(result);
            } catch (Exception e) {
                e.printStackTrace();
            }









            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            progressDialog.dismiss();
            if(iconPath.exists())
            {
                icon.setImageBitmap(BitmapFactory.decodeFile(iconPath.getAbsolutePath()));
            }


            try {
                final ptuiCBLogin lg =new ptuiCBLogin(Utils.readString(loginDialog.getLoginDataPath().getAbsolutePath())) ;
                nick.setText(lg.getNick());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(o!=null)TipTool.toast(LoginActivity.this,"加载本地群数据缓存失败:"+o);
            else
            {
                TipTool.toast(LoginActivity.this,"加载本地缓存完成!");
                new loadGroupTask(true).execute(0);
            }
        }
    }
}
