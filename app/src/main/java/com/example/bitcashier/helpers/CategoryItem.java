package com.example.bitcashier.helpers;

public class CategoryItem {

    private int spinnerItemImage;
    private String spinnerItemName;

    public CategoryItem(int spinnerItemImage, String spinnerItemName) {
        this.spinnerItemImage = spinnerItemImage;
        this.spinnerItemName = spinnerItemName;
    }

    public int getSpinnerItemImage() {
        return spinnerItemImage;
    }

    public void setSpinnerItemImage(int spinnerItemImage) {
        this.spinnerItemImage = spinnerItemImage;
    }

    public String getSpinnerItemName() {
        return spinnerItemName;
    }

    public void setSpinnerItemName(String spinnerItemName) {
        this.spinnerItemName = spinnerItemName;
    }
}
