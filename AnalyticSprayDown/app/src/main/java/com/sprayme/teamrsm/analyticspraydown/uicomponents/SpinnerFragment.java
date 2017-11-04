package com.sprayme.teamrsm.analyticspraydown.uicomponents;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sprayme.teamrsm.analyticspraydown.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpinnerFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.progress_layout, container, false);
  }
}
