package FQQSdk;

import java.lang.reflect.Field;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;








import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;





//import net.sf.json.JSONArray;
//import net.sf.json.JSONException;
//import net.sf.json.JSONObject;
import utils.HttpUtils;

public class FGroup {

	
	boolean debug = true;
	public static class Member
	{
	    public Member()
        {

        }
	public Member(String card, String flag, String g, String join_time,
				String last_speak_time, String nick, String qage, String role,
				String tags, String uin) {
			super();
			this.card = card;
			this.flag = flag;
			this.g = g;
			this.join_time = join_time;
			this.last_speak_time = last_speak_time;
			this.nick = nick;
			this.qage = qage;
			this.role = role;
			this.tags = tags;
			this.uin = uin;
		}
	
	public String getByFiledName(String name) throws Exception
	{
		Field field = this.getClass().getField(name);
		String value =(String) field.get(this);
		return value;
	}
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getG() {
		return g;
	}
	public void setG(String g) {
		this.g = g;
	}
	public String getJoin_time() {
		return join_time;
	}
	public void setJoin_time(String join_time) {
		this.join_time = join_time;
	}
	public String getLast_speak_time() {
		return last_speak_time;
	}
	public void setLast_speak_time(String last_speak_time) {
		this.last_speak_time = last_speak_time;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getQage() {
		return qage;
	}
	public void setQage(String qage) {
		this.qage = qage;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getUin() {
		return uin;
	}
	public void setUin(String uin) {
		this.uin = uin;
	}
	String card;
	String flag;
	String g;
	String join_time;
	String last_speak_time;
	String nick;
	String qage;
	String role;
	String tags;
	String uin;
	}
	public static  class GroupItem
	{
		
		public GroupItem(FGroup group,String gc, String qn, String owner) {
		super();
		this.group=group;
		this.gc = gc;
		this.qn = qn;
		this.owner = owner; }
		public GroupItem ()
        {

        }
	public String getGc() {
		return gc;
	}
	public void setGc(String gc) {
		this.gc = gc;
	}
	public String getQn() {
		return qn;
	}
	public void setQn(String qn) {
		this.qn = qn;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

		
		public void setMembers(List<Member> members)
        {
            this.members=members;
        }
		public List<Member> getMembers() throws Exception
		{
			System.out.println("get members:"+this.gc);
			if(members== null)
			{
				members =group.getMembers(this,gc);			}
			return members;
		}
		public GroupItem(FGroup group,String gc, String qn, String owner, String type) {
			super();
			this.group=group;
			this.gc = gc;
			this.qn = qn;
			this.owner = owner;
			this.type = type;
		}
		
		List<Member> members = null;
		FGroup group;
		String qn;
		String owner;
		//create or manage or join
		String type;
        String gc ;
		String count;
        String adm_max;
        String adm_num;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCount() {
			return count;
		}
		public void setCount(String count) {
			this.count = count;
		}
		public String getAdm_max() {
			return adm_max;
		}
		public void setAdm_max(String adm_max) {
			this.adm_max = adm_max;
		}
		public String getAdm_num() {
			return adm_num;
		}
		public void setAdm_num(String adm_num) {
			this.adm_num = adm_num;
		}

	}
	private QQSDK sdk;
	public FGroup(QQSDK sdk) {
		super();
		this.sdk = sdk;
	}

	public void init()
	{
		groups = null;
	}
	public GroupItem getGroupByNumber(String name) throws Exception
	{
		if(groups == null)getGroupList();
		for(GroupItem item:groups)
		{
			if(item.getGc().equals(name))return item;
			
			
		}
		return null;
	}
	
	public String getGroupListData() throws Exception
	{
		HashMap postData = new HashMap();
		postData.put("bkn", sdk.getCSRFToken(sdk.getCookie("skey")));
		//System.out.println("FGroup set cookie "+sdk.getCookie());
//		postData.put("cookie", sdk.getCookie());
		String result =(String)sdk.getHttpUtil().downloadString("http://qun.qq.com/cgi-bin/qun_mgr/get_group_list", postData).getResult();
		return result;
		
	}
	
	public List<Member> getMembers(GroupItem item,String gc) throws Exception
	{
		
		List<Member> result = new ArrayList<Member>();
		String data = getMembersData(gc);
		
		JSONObject obj = new JSONObject(data);
		
		try
		{
		item.setCount(obj.getString("count"));
		item.setAdm_max(obj.getString("adm_max"));
		item.setAdm_num(obj.getString("adm_num"));
		
		}
		catch(Exception e){
			
		}
		JSONArray array = null;
		
		try{
		array = obj.getJSONArray("mems");
		}
		catch(Exception e)
		{
			
			return result;
		}
		for(int i = 0;i< array.length();i+=1)
		{
			
			JSONObject ms = array.getJSONObject(i);
			
			try
			{
			//	System.out.println("get item:" + ms);
			Member mem = new Member(ms.getString("card"),
					ms.getString("flag"),
					ms.getString("g"),
					ms.getString("join_time"),
					ms.getString("last_speak_time"),
					ms.getString("nick"),
					ms.getString("qage"),
					ms.getString("role"),
					ms.getString("tags"),
					ms.getString("uin")
					);
			result.add(mem);
			}
			catch(JSONException e){
				
			}
			
		}
		
		return result;
		
		
	}
	public String setCard(GroupItem group,Member member,String newcard)
	{
		
		String url ="http://qun.qq.com/cgi-bin/qun_mgr/set_group_card";
		HashMap<String,String> post = new HashMap<String,String>();
		post.put("bkn", sdk.getCSRFToken(sdk.getCookie("skey")));
		post.put("gc", group.getGc());
		post.put("u", member.getUin());
	    post.put("name",newcard);
	    try {
			String result = (String) sdk.getHttpUtil().downloadString(url,post).getResult();
		System.out.println("set Card result:" +result);
		return result;
	    
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String deleteMember(GroupItem group,List<Member> member)
	{
		String url = "http://qun.qq.com/cgi-bin/qun_mgr/delete_group_member";
		return url;
		
		
		
	}
	public String getMembersData(String gc) throws Exception
	{
		HashMap postData = new HashMap();
		postData.put("bkn", sdk.getCSRFToken(sdk.getCookie("skey")));
		postData.put("gc", gc);
		postData.put("st", "0");
		postData.put("end","2000");
		postData.put("data", "0");
		
	
//		postData.put("cookie", sdk.getCookie());
		String result =(String) sdk.getHttpUtil().downloadString("http://qun.qq.com/cgi-bin/qun_mgr/search_group_members", postData).getResult();
//	System.out.println("first get members:" + result);
	
	
	    
		
		
		return result;
		
		
		
		
		
		
	}
	
	public void cleanGroups()
	{
		groups = null;
	}
	private List<GroupItem> groups;

	public void setGroups(List<GroupItem> groups)
    {
        this.groups = groups;
    }
	public List<GroupItem> getGroupList() throws Exception
	{
		if(groups!=null)return groups;
	groups = new ArrayList<GroupItem>(); 	
		String json = getGroupListData();
		
	
		if(debug)
		System.out.println("ÈºÊý¾Ý£º"+ json);
		JSONObject object = new JSONObject(json);
		String[] types = {
				"create","manage","join"
		};
		for(String typename:types)
		{
			
		
		JSONArray gs = null;
			try
			{
		gs = object.getJSONArray(typename);
			}
			catch(Exception e)
			{
				continue;
			}
		//{"create":[{"gc":139317763,"gn":"¹þ¹þ¹þ¹þ","owner":1758759399},{"gc":200735125,"gn":"À²À²À²","owner":1758759399},{"gc":317281120,"gn":"Fdex·´À¡Èº","
		for(int i = 0 ;i < gs.length();i+=1)
		{
			JSONObject item = gs.getJSONObject(i);
			
			System.out.println("item:" + item.toString());
			String gc = item.getString("gc");
			String qn = item.getString("gn");
			String owner = item.getString("owner");
			
			GroupItem a = new  GroupItem(this,gc,qn,owner,typename);
			groups.add(a);
			
			
		}
		}
		return groups;
	}
	
	

}
