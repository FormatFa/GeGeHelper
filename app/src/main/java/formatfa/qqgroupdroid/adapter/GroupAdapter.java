package formatfa.qqgroupdroid.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import FQQSdk.FGroup;
import formatfa.qqgroupdroid.R;

public class GroupAdapter  extends RecyclerView.Adapter<GroupAdapter.MyHolder>{
    private Context context;
    private List<FGroup.GroupItem> groupList;

    public interface  OnGroupClickListener
    {
        public void clickGroup(int p,FGroup.GroupItem item);

    }
    OnGroupClickListener listener;

    public void setListener(OnGroupClickListener listener) {
        this.listener = listener;
    }

    class MyHolder extends RecyclerView.ViewHolder
    {


        public MyHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
        }
        private  TextView name;
        private TextView number;
    }
    public GroupAdapter(Context context, List<FGroup.GroupItem> groupList) {
        this.context = context;
        this.groupList = groupList;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.group_item,null);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        holder.name.setText(groupList.get(position).getQn());
        holder.number.setText(groupList.get(position).getGc());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null)listener.clickGroup(position,groupList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}
