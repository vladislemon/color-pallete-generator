package ru.ntzw.cpg;

import java.awt.image.BufferedImage;
import java.util.*;

public class FullIndexedImageCreator implements IndexedImageCreator {
    @Override
    public IndexedImage createFromBufferedImage(BufferedImage image, int paletteSize) {
        Map<Color, Integer> colorFrequencies = new HashMap<>();
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                int colorCode = image.getRGB(j, i);
                Color color = new Color(
                        (colorCode >> 16) & 0xFF,
                        (colorCode >> 8) & 0xFF,
                        colorCode & 0xFF
                );
                if(colorFrequencies.containsKey(color)) {
                    colorFrequencies.put(color, colorFrequencies.get(color) + 1);
                } else {
                    colorFrequencies.put(color, 1);
                }
            }
        }
        Color[] palette = colorFrequencies.keySet().toArray(new Color[0]);
        Arrays.sort(palette, (color1, color2) -> {
            int color1Frequency = colorFrequencies.get(color1);
            int color2Frequency = colorFrequencies.get(color2);
            return Integer.compare(color2Frequency, color1Frequency);
        });
        short width = (short) image.getWidth();
        short height = (short) image.getHeight();
        byte[] indices = new byte[width * height];
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                int colorCode = image.getRGB(j, i);
                Color color = new Color(
                        (colorCode >> 16) & 0xFF,
                        (colorCode >> 8) & 0xFF,
                        colorCode & 0xFF
                );
                int index = 0;
                for(int k = 1; k < palette.length; k++) {
                    if(palette[k].equals(color)) {
                        index = k;
                        break;
                    }
                }
                indices[i * width + j] = (byte) index;
            }
        }
        return new IndexedImage(palette, width, height, indices);
    }
}
