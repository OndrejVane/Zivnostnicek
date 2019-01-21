package com.example.ondrejvane.zivnostnicek.helper;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;

public class ListViewStorageAdapter extends BaseAdapter {
    Activity context;
    String storageItemName[];
    float storageItemQuantity[];
    String storageItemUnit[];

    public ListViewStorageAdapter(Activity context, String[] storageItemName,float storageItemQuantity[], String[] storageItemUnit) {
        this.context = context;
        this.storageItemName = storageItemName;
        this.storageItemQuantity = storageItemQuantity;
        this.storageItemUnit = storageItemUnit;
    }

    public int getCount() {
        // BaseAdapter vyžaduje několik metod, kterými se nemusíme zabývat
        return storageItemName.length;
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
        TextView txtViewStorageItemName;
        TextView txtViewStorageItemQuantity;
        TextView txtViewStorageItemUnit;
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
            convertView = inflater.inflate(R.layout.show_list_item_storage, null);
            holder = new ViewHolder();
            holder.txtViewStorageItemName = convertView.findViewById(R.id.storageItemName);
            holder.txtViewStorageItemQuantity = convertView.findViewById(R.id.storageItemQuantity);
            holder.txtViewStorageItemUnit = convertView.findViewById(R.id.storageItemUnit);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewStorageItemName.setText(storageItemName[position]);
        holder.txtViewStorageItemQuantity.setText(Float.toString(storageItemQuantity[position]));
        holder.txtViewStorageItemUnit.setText(storageItemUnit[position]);

        return convertView;
    }
}
