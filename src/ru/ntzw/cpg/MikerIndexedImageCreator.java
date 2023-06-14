package ru.ntzw.cpg;

import java.awt.image.BufferedImage;
import java.util.*;

public class MikerIndexedImageCreator implements IndexedImageCreator {

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
        Map<Color, Integer> colorMap = new HashMap<>();
        int delta = 0;
        while(true) {
            List<Color> availableColors = new ArrayList<>(colors);
            for(int i = 0; i < paletteSize; i++) {
                Iterator<Color> iterator = availableColors.iterator();
                if(!iterator.hasNext()) break;
                Color mainColor = iterator.next();
                iterator.remove();
                //palette[i] = mainColor;
                colorMap.put(mainColor, i);
                List<Color> temp = new ArrayList<>();
                temp.add(mainColor);
                while (iterator.hasNext()) {
                    Color color = iterator.next();
                    if(color.getDifferenceSq(mainColor) <= delta*delta) {
                        iterator.remove();
                        colorMap.put(color, i);
                        temp.add(color);
                    }
                }
                int divider = 0;
                int r = 0, g = 0, b = 0;
                for (Color color : temp) {
                    int frequency = colorFrequencies.get(color);
                    divider += frequency;
                    r += color.r * frequency;
                    g += color.g * frequency;
                    b += color.b * frequency;
                }
                Color average = new Color(r / divider, g / divider, b / divider);
                palette[i] = average;
            }
            if(!availableColors.isEmpty()) {
                delta++;
                colorMap.clear();
            } else {
                break;
            }
        }
        /*delta = 0;
        while (true) {
            List<Color> availableColors = new ArrayList<>(colors);
            for(int i = 0; i < paletteSize; i++) {
                Iterator<Color> iterator = availableColors.iterator();
                Color mainColor = palette[i];
                while (iterator.hasNext()) {
                    Color color = iterator.next();
                    if(color.getDifferenceSq(mainColor) <= delta*delta) {
                        iterator.remove();
                        colorMap.put(color, i);
                    }
                }
            }
            if(!availableColors.isEmpty()) {
                delta++;
                colorMap.clear();
            } else {
                break;
            }
        }*/

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

                indices[i * width + j] = colorMap.get(color).byteValue();
            }
        }
        return new IndexedImage(palette, width, height, indices);
    }
}
