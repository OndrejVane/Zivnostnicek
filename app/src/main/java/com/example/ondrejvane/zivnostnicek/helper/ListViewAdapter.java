package com.example.ondrejvane.zivnostnicek.helper;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;

public class ListViewAdapter extends BaseAdapter {
    Activity context;
    String traderName[];
    String traderContactPerson[];

    public ListViewAdapter(Activity context, String[] traderName, String[] traderContactPerson) {
        this.context = context;
        this.traderName = traderName;
        this.traderContactPerson = traderContactPerson;
    }

    public int getCount() {
        // BaseAdapter vyžaduje několik metod, kterými se nemusíme zabývat
        return traderName.length;
    }

    public Object getItem(int position) {
        // BaseAdapter vyžaduje několik metod, kterými se nemusíme zabývat
        return null;
    }

    public long getItemId(int position) {
        // BaseAdapter vyžaduje několik metod, kterými se nemusíme zabývat
        return 0;
    }

    /**
     * Tato třída obsahuje textView pro nadpis a text, se kterým se pracuje v metodě getView.
     */
    private class ViewHolder {
        TextView txtViewTraderName;
        TextView txtViewTraderContactPerson;
    }

    /**
     * Název metody mluví sám za sebe. Vrací view, které se bude vykreslovat v ListView.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.show_list_item, null);
            holder = new ViewHolder();
            holder.txtViewTraderName = convertView.findViewById(R.id.textViewTraderName);
            holder.txtViewTraderContactPerson = convertView.findViewById(R.id.textViewTradeContactPerson);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTraderName.setText(traderName[position]);
        holder.txtViewTraderContactPerson.setText(traderContactPerson[position]);

        return convertView;
    }
}
