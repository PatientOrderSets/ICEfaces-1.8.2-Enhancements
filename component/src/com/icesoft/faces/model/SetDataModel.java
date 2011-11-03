package com.icesoft.faces.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.model.ListDataModel;

public class SetDataModel extends ListDataModel {
    public SetDataModel() {
        super();
    }
   
    public SetDataModel(Set set) {
        super();
        List list = new ArrayList(set);
        setWrappedData(list);
    } 
}
