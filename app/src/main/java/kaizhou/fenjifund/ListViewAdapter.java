package kaizhou.fenjifund;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by b-kaizho on 8/28/2015.
 */
public class ListViewAdapter extends BaseAdapter {
    List<FenJiData> list;
    HashMap<Integer,View> map = new HashMap<>();

    Context mContext;
    FenJiService service;

    public ListViewAdapter(Context context, FenJiService service){
        mContext = context;
        this.service = service;
    }

    public void setData(List<FenJiData> list)
    {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (map.get(position) == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.my_listitem, null);
            holder = new ViewHolder();
            holder.selected = (CheckBox)view.findViewById(R.id.list_select);
            holder.name = (TextView)view.findViewById(R.id.ItemTitle);
            holder.address = (TextView)view.findViewById(R.id.ItemText);
            final int p = position;
            map.put(position, view);
            holder.selected.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox)v;
                    list.get(position).notify = cb.isChecked();
                    service.setNotifyProperty(position, cb.isChecked());
                }
            });
            view.setTag(holder);
        }else{
            view = map.get(position);
            holder = (ViewHolder)view.getTag();
        }

        FenJiData item = list.get(position);
        holder.selected.setChecked(item.notify);
        holder.name.setText(item.aName);
        holder.address.setText(String.format("%.3f %f", item.aValue, item.yiJiaLv));

        return view;
    }

    static class ViewHolder{
        CheckBox selected;
        TextView name;
        TextView address;
    }
}

