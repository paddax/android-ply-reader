package com.powdermonkey.mapping.v3n3;

public class V3N3 {
	
	public static final int BYTES_PER_FLOAT = 4;

    private int positionElementCount = 3;
    private int positionBytesCount = positionElementCount * BYTES_PER_FLOAT;
    
    private int normalElementCount = 3;
    private int normalByteCount = normalElementCount * BYTES_PER_FLOAT; 
    
    private int stride = positionBytesCount + normalByteCount;
    
    public int getStride() {
        return stride;
    }

    public int getPositionCount() {
        return positionElementCount;
    }
    
    public int getPositionByteOffset() {
        return 0;
    }

    public int getNormalCount() {
        return normalElementCount;
    }
    
    public int getNormalByteOffset() {
        return positionBytesCount;
    }

}
