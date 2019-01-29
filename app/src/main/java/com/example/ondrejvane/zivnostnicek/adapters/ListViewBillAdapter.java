package com.example.ondrejvane.zivnostnicek.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.helper.ArrayUtility;
import com.example.ondrejvane.zivnostnicek.helper.FormatUtility;

public class ListViewBillAdapter extends BaseAdapter {

    private Activity context;
    private String[] billName;
    private String[] billDate;
    private String[] billAmount;
    private int[] typeBillColor;
    private String[] typeBillName;
    private boolean isExpense = false;

    public ListViewBillAdapter(Activity context, String[] billName, String[] billDate, String[] billAmount, String[] typeBillName, String[] typeBillColor){
        this.context = context;
        this.billName = billName;
        this.billDate = billDate;
        this.billAmount = billAmount;
        this.typeBillName = typeBillName;
        this.typeBillColor = ArrayUtility.arrayStringToInteger(typeBillColor);
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
        if(isExpense){
            holder.txtViewBillAmount.setText(FormatUtility.formatExpenseAmount(billAmount[position]));
        }else {
            holder.txtViewBillAmount.setText(FormatUtility.formatIncomeAmount(billAmount[position]));
        }

        //nastavení prvního písmena názvu typu do kruhu
        holder.txtViewBillCapitalLetter.setText(Character.toString(typeBillName[position].toUpperCase().charAt(0)));

        //nastavení barvy podle typu výdaje
        GradientDrawable gradientDrawable = (GradientDrawable) holder.txtViewBillCapitalLetter.getBackground().mutate();
        gradientDrawable.setColor(typeBillColor[position]);

        if(isExpense){
            holder.txtViewBillAmount.setTextColor(Color.RED);
        }

        return convertView;
    }
}
