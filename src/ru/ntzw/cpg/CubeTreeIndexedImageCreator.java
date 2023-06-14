package ru.ntzw.cpg;

import java.awt.image.BufferedImage;
import java.util.*;

public class CubeTreeIndexedImageCreator implements IndexedImageCreator {

    @Override
    public IndexedImage createFromBufferedImage(BufferedImage image, int paletteSize) {
        List<Color> colors = new ArrayList<>();
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                int colorCode = image.getRGB(j, i);
                Color color = new Color(
                        (colorCode >> 16) & 0xFF,
                        (colorCode >> 8) & 0xFF,
                        colorCode & 0xFF
                );
                colors.add(color);
            }
        }
        List<Cube> cubes = new ArrayList<>(paletteSize);
        cubes.add(new Cube(255/2f, 255/2f, 255/2f, 255, 255, 255));
        while(cubes.size() < 16) {
            Cube mostValuableCube = cubes.stream().max((cube1, cube2) -> {
                int cube1ColorCount = cube1.getColorInsideCount(colors);
                int cube2ColorCount = cube2.getColorInsideCount(colors);
                return Integer.compare(cube2ColorCount, cube1ColorCount);
            }).get();
            cubes.remove(mostValuableCube);
            cubes.addAll(mostValuableCube.split());
        }
        Color[] palette = new Color[paletteSize];
        Map<Color, Integer> colorMap = new HashMap<>();
        for(int i = 0; i < paletteSize; i++) {
            double r = 0, g = 0, b = 0;
            Cube cube = cubes.get(i);
            List<Color> cubeColors = cube.getColorsInside(colors);
            for(Color color : cubeColors) {
                colorMap.put(color, i);
                r += color.r;
                g += color.g;
                b += color.b;
            }
            r /= cubeColors.size();
            g /= cubeColors.size();
            b /= cubeColors.size();
            palette[i] = new Color(
                    (int) Math.round(r),
                    (int) Math.round(g),
                    (int) Math.round(b)
            );
        }
        short width = (short) image.getWidth();
        short height = (short) image.getHeight();
        byte[] indices = new byte[width * height];
        int inCube = 0, nonInCube = 0;
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                int colorCode = image.getRGB(j, i);
                Color color = new Color(
                        (colorCode >> 16) & 0xFF,
                        (colorCode >> 8) & 0xFF,
                        colorCode & 0xFF
                );
                if(colorMap.containsKey(color)) {
                    inCube++;
                    indices[i * width + j] = colorMap.get(color).byteValue();
                } else {
                    nonInCube++;
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
                }
            }
        }
        System.out.println("In Cube: " + inCube);
        System.out.println("Non In Cube: " + nonInCube);
        return new IndexedImage(palette, width, height, indices);
    }

    public static class Cube {
        float x, y, z, length, width, height;

        public Cube(float x, float y, float z, float length, float width, float height) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.length = length;
            this.width = width;
            this.height = height;
        }

        public List<Cube> split() {
            List<Cube> result = new ArrayList<>(2);
            float maxDim = Math.max(length, Math.max(width, height));
            if(maxDim == length) {
                result.add(new Cube(x + length/4, y, z, length/2, width, height));
                result.add(new Cube(x - length/4, y, z, length/2, width, height));
            } else if(maxDim == width) {
                result.add(new Cube(x, y + width/4, z, length, width/2, height));
                result.add(new Cube(x, y - width/4, z, length, width/2, height));
            } else {
                result.add(new Cube(x, y, z + height/4, length, width, height/2));
                result.add(new Cube(x, y, z - height/4, length, width, height/2));
            }
            return result;
        }

        public int getColorInsideCount(List<Color> colors) {
            int count = 0;
            for(Color color : colors) {
                if(Math.abs(color.r - x) <= length/2 &&
                        Math.abs(color.g - y) <= width/2 &&
                        Math.abs(color.b - z) <= height/2) {
                    count++;
                }
            }
            return count;
        }

        public List<Color> getColorsInside(List<Color> colors) {
            List<Color> result = new ArrayList<>();
            for(Color color : colors) {
                if(Math.abs(color.r - x) <= length/2 &&
                        Math.abs(color.g - y) <= width/2 &&
                        Math.abs(color.b - z) <= height/2) {
                    result.add(color);
                }
            }
            return result;
        }
    }
}
