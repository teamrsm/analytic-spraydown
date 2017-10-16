package com.sprayme.teamrsm.analyticspraydown.uicomponents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sprayme.teamrsm.analyticspraydown.R;
import com.sprayme.teamrsm.analyticspraydown.models.Pyramid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created by climbak on 10/15/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ActivityViewHolder> {
    private final LayoutInflater inflater;
    List<Pyramid> activityData = Collections.EMPTY_LIST;

    public RecyclerAdapter(Context context, List<Pyramid> subActivityData) {
        inflater = LayoutInflater.from(context);
        this.activityData = subActivityData;
    }


    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.pyramid_card, parent, false);
        view.setMinimumWidth(parent.getMeasuredWidth());
        ActivityViewHolder subActivityViewHolder = new ActivityViewHolder(view);
        return subActivityViewHolder;
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        Pyramid currentCard = activityData.get(position);
        holder.title.setText(currentCard != null ? currentCard.getRouteType().toString() : "null");
        holder.pyramidView.setPyramid(currentCard);
        holder.pyramidView.invalidate();
    }

    @Override
    public int getItemCount() {
        return activityData.size();
    }

    public void update(List<Pyramid> pyramids){
        activityData.clear();
        for (Pyramid pyramid: pyramids) {
            activityData.add(pyramid);
        }
        notifyDataSetChanged();

    }

    class ActivityViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        SprayamidView pyramidView;

        public ActivityViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.card_text);
            pyramidView = (SprayamidView) itemView.findViewById(R.id.pyramidViewCard);
        }
    }
}
