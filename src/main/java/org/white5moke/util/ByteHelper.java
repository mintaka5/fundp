package org.white5moke.util;

import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteHelper {
    private byte[] data;
    private int bufferLength = 64;
    private List<byte[]> stack = new ArrayList<>();

    public ByteHelper(String s, int bufferLength) {
        setData(s.getBytes(StandardCharsets.UTF_8));
        setBufferLength(bufferLength);

        split();
    }

    public ByteHelper(byte[] b, int bufferLength) {
        setData(b);
        setBufferLength(bufferLength);

        split();
    }

    private void split() {
        byte[][] r = new byte[data.length / bufferLength + 1][bufferLength];

        int k = 0;
        for(int i = 0; i < data.length; i++) {
            int j = i % bufferLength;

            r[k][j] = data[i];

            if(j == bufferLength - 1) k++;
        }

        Arrays.stream(r).map(row -> littleEndian(row)).forEach(row -> {
            stack.add(row);
        });
    }

    private void setBufferLength(int l) {
        this.bufferLength = l;
    }

    private void setData(byte[] b) {
        this.data = b;
    }

    public static byte[] littleEndian(byte[] data) {
        ByteBuffer bb = ByteBuffer.allocate(data.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data);

        return bb.array();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        stack.forEach(s -> sb.append(Hex.encodeHexString(s) + "\n"));

        return sb.toString();
    }
}
