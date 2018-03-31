package formatfa.qqgroupdroid.Tool;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import FQQSdk.FGroup;
import utils.Utils;

public class PathTool {

    private File root;
    private File workDir;
    public PathTool()
    {

        root = Environment.getExternalStorageDirectory();
        workDir = new File(root,"ApktoolHelper/QQ");
        if(!workDir.exists())workDir.mkdirs();


    }

    public File getDir(String name)
    {
        File file = new File(workDir,name);
        if(file.isFile())file.delete();
        if(!file.exists())file.mkdirs();
        return file;
    }
    public File getWorkPath()
    {
        return workDir;


    }


    public List<FGroup.GroupItem> readGroup(File cachePath) throws Exception {
        List<FGroup.GroupItem> result = new ArrayList<>();
        String data = Utils.readString(cachePath.getAbsolutePath());
        JSONArray ja = new JSONArray(data);

        for(int i = 0;i<ja.length();i+=1)
        {
            FGroup.GroupItem item = new FGroup.GroupItem();

            JSONObject ob = ja.getJSONObject(i);

            item.setAdm_max(ob.getString("adm_max"));
            item.setAdm_num("adm_num");
            item.setCount(ob.getString("count"));
            item.setGc(ob.getString("gc"));
            item.setQn(ob.getString("qn"));
            item.setOwner(ob.getString("owner"));
            item.setType(ob.getString("type"));
            item.setMembers(readMembers(ob.getJSONArray("members")));
           result.add(item);





        }
        return result;



    }

    public List<FGroup.Member> readMembers(JSONArray ja) throws JSONException, IllegalAccessException {

        List<FGroup.Member> result = new ArrayList<>();
        for(int i = 0;i< ja.length();i+=1)
        {
            JSONObject item = ja.getJSONObject(i);
            FGroup.Member member = new FGroup.Member();
            Field fields[] =FGroup.Member.class.getDeclaredFields();

            for(Field field:fields)
            {
                field.setAccessible(true);
                field.set(member,item.get(field.getName()));
            }
            result .add(member);

        }

        return result;
    }




    public void storeGroup(List<FGroup.GroupItem> datas,File out) throws IOException {



            Utils.writeString(parseGroupList(datas).toString(),out.getAbsolutePath());



    }


    public JSONArray parseGroupList(List<FGroup.GroupItem> datas)
    {
        JSONArray allGroups = new JSONArray();



        for(FGroup.GroupItem groupItem :datas)
        {

            allGroups.put(parseGroupItem(groupItem));

        }
        return allGroups;


    }
    public JSONObject parseGroupItem(FGroup.GroupItem item)
    {
        JSONObject ob = new JSONObject();

        try {
            ob.put("adm_num",item.getAdm_num());
            ob.put("adm_max",item.getAdm_max());
            ob.put("count",item.getCount());
            ob.put("gc",item.getGc());
            ob.put("qn",item.getQn());
            ob.put("owner",item.getOwner());
            ob.put("type",item.getType());
            ob.put("members",parseMembers(item.getMembers()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ob;

    }

    public JSONArray parseMembers(List<FGroup.Member> members)
    {

        JSONArray ja = new JSONArray();
        for(FGroup.Member me:members)
        {
            JSONObject amember = new JSONObject();
            Field[] fields = FGroup.Member.class.getDeclaredFields();

            for(Field field:fields)
            {
                field.setAccessible(true);
                try {
                    amember.put(field.getName(),field.get(me));

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            ja.put(amember);

        }


        return  ja;

    }


}
