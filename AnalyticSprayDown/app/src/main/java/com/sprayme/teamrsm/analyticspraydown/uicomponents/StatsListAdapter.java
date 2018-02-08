package com.sprayme.teamrsm.analyticspraydown.uicomponents;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sprayme.teamrsm.analyticspraydown.R;
import com.sprayme.teamrsm.analyticspraydown.models.Statistic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by climbak on 11/4/17.
 */

public class StatsListAdapter extends ArrayAdapter {
  private final Activity context;
  private final List<Statistic> stats;

  public StatsListAdapter(Activity context, List<Statistic> stats){
    super(context, R.layout.stats_list_item , stats != null ? stats : new ArrayList<>());
    this.context = context;
    this.stats = stats != null ? stats : new ArrayList<>();
  }

  public View getView(int position, View view, ViewGroup parent) {
    if (stats.isEmpty())
      return null;

    View rowView = view;
    // reuse views
    if (rowView == null) {
      LayoutInflater inflater = context.getLayoutInflater();
      rowView = inflater.inflate(R.layout.stats_list_item, null);
//    rowView = inflater.inflate(R.layout.stats_list_item, null,true);
      // configure view holder
      ViewHolder viewHolder = new ViewHolder();
      viewHolder.name = (TextView) rowView.findViewById(R.id.statName);
      viewHolder.value = (TextView) rowView.findViewById(R.id.statValue);
      rowView.setTag(viewHolder);
    }

    // fill data
    ViewHolder holder = (ViewHolder) rowView.getTag();
    Statistic thisStat = stats.get(position);
    holder.name.setText(thisStat.getName());
    holder.value.setText(thisStat.getValueString());

    return rowView;

  }

  public @Override void clear(){
    super.clear();
    stats.clear();
  }

  public @Override void add(Object object){
    super.add(object);
    stats.add((Statistic)object);
  }

  public @Override void addAll(Collection collection){
    super.addAll(collection);
    for(Object item : collection.toArray())
      stats.add((Statistic) item);
  }

  class ViewHolder {
    public TextView name;
    public TextView value;
  }
}