package com.example.ondrejvane.zivnostnicek.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.model.model_helpers.BillBox;
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;

import java.util.ArrayList;

public class ListViewBillAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<BillBox> billBox;
    private boolean isExpense = false;

    /**
     * Kosntruktor adaptéru pro faktury.
     *
     * @param context kontext aktivity
     * @param billBox spojový seznam faktur
     */
    public ListViewBillAdapter(Activity context, ArrayList<BillBox> billBox){
        this.billBox = billBox;
        this.context = context;
    }

    public void isExpense(boolean isExpense){
        this.isExpense = isExpense;
    }

    public int getCount() {
        return billBox.size();
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
     * @param position pozice v listu
     * @param convertView view položky
     * @param parent rodič položky v lsitu
     * @return view
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

        holder.txtViewBillName.setText(billBox.get(position).getBill().getName());
        holder.txtViewBillDate.setText(billBox.get(position).getBill().getDate());
        float amount = billBox.get(position).getBill().getAmount();
        if(isExpense){
            holder.txtViewBillAmount.setText(FormatUtility.formatExpenseAmount(amount));
        }else {
            holder.txtViewBillAmount.setText(FormatUtility.formatIncomeAmount(amount));
        }

        //nastavení prvního písmena názvu typu do kruhu
        String typeName = billBox.get(position).getTypeBill().getName();
        holder.txtViewBillCapitalLetter.setText(Character.toString(typeName.toUpperCase().charAt(0)));
        //nastavení barvy podle typu výdaje
        GradientDrawable gradientDrawable = (GradientDrawable) holder.txtViewBillCapitalLetter.getBackground().mutate();
        gradientDrawable.setColor(billBox.get(position).getTypeBill().getColor());

        if(isExpense){
            holder.txtViewBillAmount.setTextColor(Color.RED);
        }

        return convertView;
    }
}
