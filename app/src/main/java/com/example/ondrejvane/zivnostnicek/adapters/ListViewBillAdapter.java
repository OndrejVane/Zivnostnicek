package com.example.ondrejvane.zivnostnicek.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;

public class ListViewBillAdapter extends BaseAdapter {

    private Activity context;
    private String[] billName;
    private String[] billDate;
    private String[] billAmount;
    private boolean isExpense = false;

    public ListViewBillAdapter(Activity context, String[] billName, String[] billDate, String[] billAmount){
        this.context = context;
        this.billName = billName;
        this.billDate = billDate;
        this.billAmount = billAmount;
    }

    public void isExpense(boolean isExpense){
        this.isExpense = isExpense;
    }
    public int getCount() {
        return billName.length;
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
        TextView txtViewBillName;
        TextView txtViewBillDate;
        TextView txtViewBillAmount;
        TextView txtViewBillCapitalLetter;

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
        ListViewBillAdapter.ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.show_list_item_bill, null);
            holder = new ListViewBillAdapter.ViewHolder();
            holder.txtViewBillName = convertView.findViewById(R.id.textViewIncomeNameShow);
            holder.txtViewBillDate = convertView.findViewById(R.id.textViewIncomeDateShow);
            holder.txtViewBillAmount = convertView.findViewById(R.id.textViewIncomeAmountShow);
            holder.txtViewBillCapitalLetter = convertView.findViewById(R.id.textViewBillCapitalLetter);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ListViewBillAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtViewBillName.setText(billName[position]);
        holder.txtViewBillDate.setText(billDate[position]);
        holder.txtViewBillAmount.setText(billAmount[position]);
        holder.txtViewBillCapitalLetter.setText(Character.toString(billName[position].toUpperCase().charAt(0)));

        if(isExpense){
            holder.txtViewBillAmount.setTextColor(Color.RED);
            holder.txtViewBillCapitalLetter.setBackgroundColor(0xFF00FF00);
        }

        return convertView;
    }
}
