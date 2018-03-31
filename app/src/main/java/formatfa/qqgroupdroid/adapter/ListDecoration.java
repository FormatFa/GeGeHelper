package formatfa.qqgroupdroid.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import formatfa.qqgroupdroid.R;


public class ListDecoration extends RecyclerView.ItemDecoration {
    private Context context;

    private Drawable mDivider;
    public ListDecoration(Context context) {
        this.context = context;
        mDivider = context.getResources().getDrawable(R.drawable.divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int left = parent.getPaddingLeft();
        int right = parent.getWidth()-parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for(int i = 0;i< childCount;i+=1)
        {
            View view = parent.getChildAt(i);
             RecyclerView.LayoutParams params =(RecyclerView.LayoutParams)view.getLayoutParams();
             int top = view.getBottom()+params.bottomMargin;
             int bottom = top  + mDivider.getIntrinsicHeight();
             mDivider.setBounds(left,top,right,bottom);
             mDivider.draw(c);

        }
        super.onDrawOver(c, parent, state);
    }
}
