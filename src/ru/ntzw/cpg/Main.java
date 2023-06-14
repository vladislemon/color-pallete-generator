package ru.ntzw.cpg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args == null || args.length < 1) {
            System.out.println("No input");
            System.exit(1);
        }
        Path inputPath = Paths.get(args[0]);
        if(Files.notExists(inputPath) || !Files.isRegularFile(inputPath)) {
            System.out.println("Specified file does not exists");
            System.exit(2);
        }
        BufferedImage image;
        try(InputStream inputStream = Files.newInputStream(inputPath)) {
            image = ImageIO.read(inputStream);
        }
        if(image == null) {
            System.out.println("Unable to read input image");
            System.exit(3);
        }
        IndexedImageCreator indexedImageCreator = new MikerIndexedImageCreator();
        IndexedImage indexedImage = indexedImageCreator.createFromBufferedImage(image, 16);
        try(OutputStream outputStream = Files.newOutputStream(Paths.get("D:\\dev\\c\\projects\\ConsoleImage\\cmake-build-debug\\image.wci"))) {
            indexedImage.write(outputStream);
        }
    }
}
