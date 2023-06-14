package ru.ntzw.cpg;

import java.io.IOException;
import java.io.OutputStream;

public class IndexedImage {

    private static final byte[] MAGIC = {'\0', 'W', 'C', 'I'};

    private Color[] palette;
    private short width, height;
    private byte[] indices;

    public IndexedImage(Color[] palette, short width, short height, byte[] indices) {
        this.palette = palette;
        this.width = width;
        this.height = height;
        this.indices = indices;
    }

    public void write(OutputStream outputStream) throws IOException {
        writeMagic(outputStream);
        writeHeader(outputStream);
        writePalette(outputStream);
        writeIndices(outputStream);
    }

    private void writeMagic(OutputStream outputStream) throws IOException {
        outputStream.write(MAGIC);
    }

    private void writeHeader(OutputStream outputStream) throws IOException {
        outputStream.write((width >> 8) & 0xFF);
        outputStream.write((width) & 0xFF);
        outputStream.write((height >> 8) & 0xFF);
        outputStream.write((height) & 0xFF);
        outputStream.write((palette.length >> 8) & 0xFF);
        outputStream.write((palette.length) & 0xFF);
    }

    private void writePalette(OutputStream outputStream) throws IOException {
        for (Color color : palette) {
            outputStream.write(color.r);
            outputStream.write(color.g);
            outputStream.write(color.b);
        }
    }

    private void writeIndices(OutputStream outputStream) throws IOException {
        for(byte index : indices) {
            outputStream.write(index);
        }
    }
}
