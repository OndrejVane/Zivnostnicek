package com.example.ondrejvane.zivnostnicek.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;

public class ListViewIncomeAdapter extends BaseAdapter {

    private Activity context;
    private String[] incomeName;
    private String[] incomeDate;
    private String[] incomeAmount;

    public ListViewIncomeAdapter(Activity context, String[] incomeName, String[] incomeDate, String[] incomeAmount){
        this.context = context;
        this.incomeName = incomeName;
        this.incomeDate = incomeDate;
        this.incomeAmount = incomeAmount;
    }

    public int getCount() {
        return incomeName.length;
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
        TextView txtViewIncomeName;
        TextView txtViewIncomeDate;
        TextView txtViewIncomeAmount;
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
        ListViewIncomeAdapter.ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.show_list_item_income, null);
            holder = new ListViewIncomeAdapter.ViewHolder();
            holder.txtViewIncomeName = convertView.findViewById(R.id.textViewIncomeNameShow);
            holder.txtViewIncomeDate = convertView.findViewById(R.id.textViewIncomeDateShow);
            holder.txtViewIncomeAmount = convertView.findViewById(R.id.textViewIncomeAmountShow);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ListViewIncomeAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtViewIncomeName.setText(incomeName[position]);
        holder.txtViewIncomeDate.setText(incomeDate[position]);
        holder.txtViewIncomeAmount.setText(incomeAmount[position]);

        return convertView;
    }
}
