package ru.ntzw.cpg;

import java.awt.image.BufferedImage;
import java.util.*;
//MFC = Most Frequent Colors
public class MFCIndexedImageCreator implements IndexedImageCreator {

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
        List<Color> colors = new ArrayList<>(colorFrequencies.keySet());
        colors.sort((color1, color2) -> {
            int color1Frequency = colorFrequencies.get(color1);
            int color2Frequency = colorFrequencies.get(color2);
            return Integer.compare(color2Frequency, color1Frequency);
        });
        Color[] palette = new Color[paletteSize];
        Arrays.fill(palette, new Color(0, 0, 0));
        int paletteColorCount = Math.min(paletteSize, colors.size());
        for(int i = 0; i < paletteColorCount; i++) {
            palette[i] = colors.get(i);
        }
        System.out.println("Palette: " + Arrays.toString(palette));
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
                int closestColorIndex = 0;
                int smallestDifference = color.getDifferenceSq(palette[0]);
                for(int k = 1; k < paletteSize; k++) {
                    int difference = color.getDifferenceSq(palette[k]);
                    if(difference < smallestDifference) {
                        closestColorIndex = k;
                        smallestDifference = difference;
                    }
                }
                indices[i * width + j] = (byte) closestColorIndex;
                System.out.println("Index " + (i * width + j) + " : " + closestColorIndex);
            }
        }
        return new IndexedImage(palette, width, height, indices);
    }
}
