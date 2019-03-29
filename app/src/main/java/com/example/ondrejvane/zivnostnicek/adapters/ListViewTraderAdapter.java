package com.example.ondrejvane.zivnostnicek.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;

public class ListViewTraderAdapter extends BaseAdapter {

    private Activity context;
    private String traderName[];
    private String traderContactPerson[];


    /**
     * Konstruktor adaptéru pro list obchodníků.
     *
     * @param context             kontext aktivity
     * @param traderName          názvy obchodníků
     * @param traderContactPerson kontaktní osoby
     */
    public ListViewTraderAdapter(Activity context, String[] traderName, String[] traderContactPerson) {
        this.context = context;
        this.traderName = traderName;
        this.traderContactPerson = traderContactPerson;
    }

    public int getCount() {
        return traderName.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
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
     * Metoda, která nastaví příslušný text do list view.
     *
     * @param position    pozice v list view
     * @param convertView view
     * @param parent      rodič položky listu
     * @return view
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.show_list_item_trader, null);
            holder = new ViewHolder();
            holder.txtViewTraderName = convertView.findViewById(R.id.textViewTraderName);
            holder.txtViewTraderContactPerson = convertView.findViewById(R.id.textViewTradeContactPerson);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTraderName.setText(traderName[position]);
        holder.txtViewTraderContactPerson.setText(traderContactPerson[position]);

        return convertView;
    }
}
