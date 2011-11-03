package com.icesoft.faces.context.effects;

public class Focus extends Effect{

     public String getFunctionName() {
        return "Ice.Focus.setFocus";
    }
    
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Focus)) {
            return false;
        }
        return true;
    }
}
