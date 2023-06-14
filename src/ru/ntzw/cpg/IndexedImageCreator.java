package ru.ntzw.cpg;

import java.awt.image.BufferedImage;

public interface IndexedImageCreator {

    IndexedImage createFromBufferedImage(BufferedImage image, int paletteSize);
}
