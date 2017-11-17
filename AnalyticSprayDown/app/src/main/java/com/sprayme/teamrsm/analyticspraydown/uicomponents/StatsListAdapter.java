package com.sprayme.teamrsm.analyticspraydown.uicomponents;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sprayme.teamrsm.analyticspraydown.R;
import com.sprayme.teamrsm.analyticspraydown.models.Statistic;

/**
 * Created by climbak on 11/4/17.
 */

public class StatsListAdapter extends ArrayAdapter {
  private final Activity context;
  private final Statistic[] statArray;

  public StatsListAdapter(Activity context, Statistic[] stats){
    super(context, R.layout.stats_list_item , stats);
    this.context = context;
    this.statArray = stats;
  }

  public View getView(int position, View view, ViewGroup parent) {
    LayoutInflater inflater=context.getLayoutInflater();
    View rowView=inflater.inflate(R.layout.stats_list_item, null,true);

    //this code gets references to objects in the listview_row.xml file
    TextView nameTextField = (TextView) rowView.findViewById(R.id.statName);
    TextView valueTextField = (TextView) rowView.findViewById(R.id.statValue);

    //this code sets the values of the objects to values from the arrays
    nameTextField.setText(statArray[position].getName());
    valueTextField.setText(statArray[position].getValueString());

    return rowView;

  };
}