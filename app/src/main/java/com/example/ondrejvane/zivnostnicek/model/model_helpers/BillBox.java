package com.example.ondrejvane.zivnostnicek.model.model_helpers;

public class BillBox {

    private Bill bill;
    private TypeBill typeBill;

    public BillBox(Bill bill, TypeBill typeBill) {
        this.bill = bill;
        this.typeBill = typeBill;
    }

    public BillBox(){

    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public TypeBill getTypeBill() {
        return typeBill;
    }

    public void setTypeBill(TypeBill typeBill) {
        this.typeBill = typeBill;
    }
}
