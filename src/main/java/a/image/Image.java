/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package a.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

/**
 *
 * @author ekaterina
 */
public class Image {

    public static void main(String[] args) throws IOException {
        
        BufferedImage image = ImageIO.read(new File("/Users/ekaterina/Downloads/Pintura.jpg"));

        // пул потоков по доступному количеству процессоров
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // высота/ширина в пикселях
        int height = image.getHeight();
        int width = image.getWidth();
        
        // x и y: координаты левого верхнего угла области извлечения  - (0,0)
        //width и height: ширина и высота области извлечения
        //pixels: массив, в который будут сохранены данные пикселей, 
        //в данном случае извлечение в текущий массив
        //offset: смещение в массиве pixels, с которого начнется сохранение данных пикселей
        //scansize: шаг сканирования массива pixels при сохранении данных пикселей - 
        // - последовательное сохранение по ширине без пропусков

        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        // Обработать каждую строку пикселей в отдельном потоке
        for (int i = 0; i < height; i++) {
            final int row = i; // используем файнл для лямбда выражения
            pool.submit(() -> { // отправляем задачу в пул потоков для выполнения
                for (int j = 0; j < width; j++) {
                    // преобразование пикселя в оттенок серого
                    int rgb = pixels[row * width + j];
                    int red = (rgb & 0x00FF0000) >> 16;
                    int green = (rgb & 0x0000FF00) >> 8;
                    int blue = rgb & 0x000000FF;
                    int gray = (int) (0.3 * red + 0.59 * green + 0.11 * blue);

                    // установка оттенка серого
                    pixels[row * width + j] = (gray << 16) | (gray << 8) | gray;
                }
            });
        }

        // завершение работы пула потоков
        pool.shutdown();

        // создание нового изображения по измененным пикселям 
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        newImage.setRGB(0, 0, width, height, pixels, 0, width);

        // сохранение обработанного изображения
        ImageIO.write(newImage, "jpg", new File("/Users/ekaterina/Downloads/Pintura.jpg"));
    }

}
