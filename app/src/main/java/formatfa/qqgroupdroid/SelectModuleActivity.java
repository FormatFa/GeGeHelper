package formatfa.qqgroupdroid;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;

import formatfa.qqgroupdroid.Tool.PathTool;
import formatfa.qqgroupdroid.Tool.TipTool;
import formatfa.qqgroupdroid.adapter.CommonListClickListener;
import formatfa.qqgroupdroid.adapter.ListDecoration;
import formatfa.qqgroupdroid.adapter.ModuleAdapter;

public class SelectModuleActivity extends AppCompatActivity implements CommonListClickListener{

    private PathTool pathTool;
    private File moduledir;
    private File[] modules;

    private RecyclerView list;
    Intent fromIntent;
    public static final String index = "index.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_module);

        getSupportActionBar().setSubtitle("选择操作");

        fromIntent = getIntent();
        initView();
        loadData();


    }

    private void initView()
    {
        list = findViewById(R.id.list);
        pathTool = new PathTool();
        moduledir = pathTool.getDir("module");



    }
    private void loadData() {

        modules = moduledir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.isFile())
                return false;

                if(new File(file,index).exists())return true;
                return false;
            }
        });

        if(modules.length==0)
        {
            TipTool.toast(this,"没有找到合适的模块");return;
        }
        ModuleAdapter ma = new ModuleAdapter(this,modules);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new ListDecoration(this));

        ma.setListClickListener(this);
        list.setAdapter(ma);

    }


    @Override
    public void onItemClick(int i, Object object) {
        Intent intent = new Intent(this,ScriptActivity.class);
        intent.putExtra("path",((File)object).getAbsolutePath());
        intent.putExtra("type",fromIntent.getStringExtra("type"));
        intent.putExtra("number",fromIntent.getStringExtra("number"));
        startActivity(intent);
    }
}
