package uk.org.lsucs.lanvantracker.retrofit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentDropUp {

    @SerializedName("currentDropUpId")
    @Expose
    private Integer currentDropUpId;

    public Integer getCurrentDropUpId() {
        return currentDropUpId;
    }

    public void setCurrentDropUpId(Integer currentDropUpId) {
        this.currentDropUpId = currentDropUpId;
    }
}