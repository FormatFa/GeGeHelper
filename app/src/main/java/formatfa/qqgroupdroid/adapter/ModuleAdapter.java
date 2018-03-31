package formatfa.qqgroupdroid.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import formatfa.qqgroupdroid.R;


public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.MuduleAdapterHolder> {


    private CommonListClickListener listClickListener;

    public CommonListClickListener getListClickListener() {
        return listClickListener;
    }

    public void setListClickListener(CommonListClickListener listClickListener) {
        this.listClickListener = listClickListener;
    }

    class MuduleAdapterHolder extends RecyclerView.ViewHolder
    {

        private TextView name;

        public MuduleAdapterHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }
    private Context context;
    private File[] files;

    public ModuleAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public MuduleAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.module_item,null);
        MuduleAdapterHolder holder = new MuduleAdapterHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MuduleAdapterHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(listClickListener!=null)
                {
                    listClickListener.onItemClick(position,files[position]);
                }
            }
        });
        holder.name.setText(files[position].getName());

    }

    @Override
    public int getItemCount() {
        return files.length;
    }
}
