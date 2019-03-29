package com.example.ondrejvane.zivnostnicek.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;

public class ListViewStorageAdapter extends BaseAdapter {
    private Activity context;
    private String[] storageItemName;
    private float[] storageItemQuantity;
    private String[] storageItemUnit;

    /**
     * Konstruktor adapteru pro skladové položky
     *
     * @param context             kontext aktivity
     * @param storageItemName     název skladových položek
     * @param storageItemQuantity kvantita skladových položek
     * @param storageItemUnit     jednotky skladových položek
     */
    public ListViewStorageAdapter(Activity context, String[] storageItemName, float storageItemQuantity[], String[] storageItemUnit) {
        this.context = context;
        this.storageItemName = storageItemName;
        this.storageItemQuantity = storageItemQuantity;
        this.storageItemUnit = storageItemUnit;
    }

    public int getCount() {
        return storageItemName.length;
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
        TextView txtViewStorageItemName;
        TextView txtViewStorageItemQuantity;
        TextView txtViewStorageItemUnit;
    }

    /**
     * Metoda, která nastaví text do list view
     *
     * @param position    pozice v list view
     * @param convertView view
     * @param parent      rodič listu view
     * @return view
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.show_list_item_storage, null);
            holder = new ViewHolder();
            holder.txtViewStorageItemName = convertView.findViewById(R.id.storageItemName);
            holder.txtViewStorageItemQuantity = convertView.findViewById(R.id.storageItemQuantity);
            holder.txtViewStorageItemUnit = convertView.findViewById(R.id.storageItemUnit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewStorageItemName.setText(storageItemName[position]);
        holder.txtViewStorageItemQuantity.setText(Float.toString(storageItemQuantity[position]));
        holder.txtViewStorageItemUnit.setText(storageItemUnit[position]);

        return convertView;
    }
}
