package com.vermaxdevs.covid19dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.vermaxdevs.covid19dashboard.R.layout.custom_listview;

public class CustomListViewAdapter extends ArrayAdapter {
    List list = new ArrayList();

    public CustomListViewAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public void add(@Nullable States object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        StateHolder stateHolder = new StateHolder();

        if(row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.custom_listview, parent, false);
            stateHolder.sName = row.findViewById(R.id.state_name);
            stateHolder.confirmed = row.findViewById(R.id.confirmed);
            stateHolder.active = row.findViewById(R.id.active);
            stateHolder.recovered = row.findViewById(R.id.recovered);
            row.setTag(stateHolder);
        }
        else {
            stateHolder = (StateHolder) row.getTag();
        }

        States  states = (States) this.getItem(position);
        stateHolder.sName.setText(states.getsName());
        stateHolder.confirmed.setText(states.getConfirmed());
        stateHolder.active.setText(states.getActive());
        stateHolder.recovered.setText(states.getRecovered());
        return row;
    }

    static class StateHolder {
        TextView sName, confirmed, active, recovered;
    }
}
