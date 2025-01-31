package ch.sku.karatescore.services;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CategoryService {
    private final StringProperty categoryInfo = new SimpleStringProperty(this, "info", "");
    public StringProperty categoryInfoProperty() {
        return this.categoryInfo;
    }

    public String getCategoryInfo() {
        System.out.println("getCat");
        return this.categoryInfoProperty().get();
    }

    public void resetCategoryInfo(){
        setCategoryInfo("");
    }
    public void setCategoryInfo(String categoryInfo) {
        this.categoryInfoProperty().set(categoryInfo);
    }
}
