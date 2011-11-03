package com.icesoft.faces.component.outputchart;

import com.icesoft.faces.context.ByteArrayResource;

import java.io.Serializable;

public class ChartResource extends ByteArrayResource implements Serializable {
    private static long prevDigest = 0;
    
    public ChartResource(byte[] content) {
        super(content);
    }

    public String calculateDigest() {
        long digest = System.currentTimeMillis();
        synchronized (getClass()) { // ICE-3052
            if (digest <= prevDigest) {
                digest = prevDigest + 1;
            }
            prevDigest = digest;
        }
        return String.valueOf("CHART"+digest);
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof ChartResource)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return true;
    }
}
