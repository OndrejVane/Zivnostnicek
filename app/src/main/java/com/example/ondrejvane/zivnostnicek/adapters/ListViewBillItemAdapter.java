package com.example.ondrejvane.zivnostnicek.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;

public class ListViewBillItemAdapter extends BaseAdapter {
    private Activity context;
    private String[] billItemName;
    private float[] billItemQuantity;
    private String[] billItemUnit;
    private boolean isTrashHidden = false;

    public ListViewBillItemAdapter(Activity context, String[] billItemName, float[] billItemQuantity, String[] billItemUnit){
        this.context = context;
        this.billItemName = billItemName;
        this.billItemQuantity = billItemQuantity;
        this.billItemUnit = billItemUnit;
    }

    public void isTrashHidden(boolean isTrashHidden){
        this.isTrashHidden = true;
    }

    public int getCount() {
        return billItemUnit.length;
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
        TextView txtViewBillItemName;
        TextView txtViewBillItemQuantity;
        TextView txtViewBillItemUnit;
        ImageView imageViewDelete;
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
        ListViewBillItemAdapter.ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.show_list_item_bill_item, null);
            holder = new ListViewBillItemAdapter.ViewHolder();
            holder.txtViewBillItemName = convertView.findViewById(R.id.storageBillItemName);
            holder.txtViewBillItemQuantity = convertView.findViewById(R.id.storageBillItemQuantity);
            holder.txtViewBillItemUnit = convertView.findViewById(R.id.storageBillItemUnit);
            if (isTrashHidden){
                holder.imageViewDelete = convertView.findViewById(R.id.imgDeleteItem);
                holder.imageViewDelete.setImageBitmap(null);
            }
            convertView.setTag(holder);
        }
        else
        {
            holder = (ListViewBillItemAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtViewBillItemName.setText(billItemName[position]);
        holder.txtViewBillItemQuantity.setText(Float.toString(billItemQuantity[position]));
        holder.txtViewBillItemUnit.setText(billItemUnit[position]);

        return convertView;
    }
}
