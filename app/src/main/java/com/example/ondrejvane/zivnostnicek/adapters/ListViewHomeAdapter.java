package com.example.ondrejvane.zivnostnicek.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ondrejvane.zivnostnicek.R;
import com.example.ondrejvane.zivnostnicek.utilities.FormatUtility;

public class ListViewHomeAdapter extends BaseAdapter{

    private Activity context;
    private int[] typeColor;
    private String[] typeBillName;
    private float[] typeTotalAmount;
    private boolean isExpense;


    public ListViewHomeAdapter(Activity activity, int[] typeColor, String[] typeBillName, float[] typeTotalAmount){
        this.context = activity;
        this.typeColor = typeColor;
        this.typeBillName = typeBillName;
        this.typeTotalAmount = typeTotalAmount;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public void setExpense(boolean expense) {
        this.isExpense = expense;
    }

    public int getCount(){
        return typeBillName.length;
    }

    public Object getItem(int position){
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    /**
     * Tato třída obsahuje textView pro nadpis a text, se kterým se pracuje v metodě getView.
     */
    private class ViewHolder {
        TextView txtViewHomeCapitalLetter;
        TextView txtViewHomeTitle;
        TextView txtViewHomeAmount;

    }

    /**
     * Metoda vrací view, které se bude vykreslovat v ListView.
     * @param position  pozice v list view
     * @param convertView   view
     * @param parent    parent view
     * @return  view
     */
    @SuppressLint({"InflateParams", "SetTextI18n"})
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ListViewHomeAdapter.ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.show_list_item_home, null);
            holder = new ListViewHomeAdapter.ViewHolder();
            holder.txtViewHomeCapitalLetter = convertView.findViewById(R.id.textViewHomeCapitalLetter);
            holder.txtViewHomeTitle = convertView.findViewById(R.id.textViewHomeNameShow);
            holder.txtViewHomeAmount = convertView.findViewById(R.id.textViewHomeAmountShow);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ListViewHomeAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtViewHomeTitle.setText(typeBillName[position]);
        if(isExpense){
            holder.txtViewHomeAmount.setText(FormatUtility.formatExpenseAmount(typeTotalAmount[position]));
        }else {
            holder.txtViewHomeAmount.setText(FormatUtility.formatIncomeAmount(typeTotalAmount[position]));
        }

        //nastavení prvního písmena názvu typu do kruhu
        holder.txtViewHomeCapitalLetter.setText(Character.toString(typeBillName[position].toUpperCase().charAt(0)));

        //nastavení barvy částky podle toho, jestli se jedná o příjem nebo výdaj
        if(isExpense){
            holder.txtViewHomeAmount.setTextColor(Color.RED);
        }

        //nastavení barvy podle typu výdaje
        GradientDrawable gradientDrawable = (GradientDrawable) holder.txtViewHomeCapitalLetter.getBackground().mutate();
        gradientDrawable.setColor(typeColor[position]);

        return convertView;
    }

}
