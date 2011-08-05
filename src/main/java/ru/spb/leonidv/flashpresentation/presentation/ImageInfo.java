/*
 * ImageInfo.java
 *
 * Created on 8 Ноябрь 2006 г., 15:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ru.spb.leonidv.flashpresentation.presentation;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @author Leonid
 */
public class ImageInfo {
    /**
     * Ширина миниатюры изображения
     */
    public static int THUMBNAIL_WIDTH = 102;

    /**
     * Высота миниатюры изображения
     */
    public static int THUMBNAIL_HEIGHT = 77;

    /**
     * Отношение ширины миниатюры к высоте
     * 
     * @return THUMBAIL_WIDTH / THUMBAIL_HEIGHT
     */
    public static double getThumbnailRatio() {
        return (double) THUMBNAIL_WIDTH / THUMBNAIL_HEIGHT;
    }

    // Имя файла с изображением
    private String name;

    // Ширина изображения
    private int width;

    // Высота изображения
    private int height;

    // Миниатюра изображения
    ImageIcon thumbnail;

    /**
     * Осуществляет пропорциональное сжатие изображения.
     * 
     */
    private void initThumbnail(BufferedImage image) {
        /*
         * Получаем коэффециент преобразования изображения. Нам нужно выбрать, по какой стороне
         * будет делать масштабирование.
         */
        double imageRatio = (double) image.getWidth() / image.getHeight();
        double scaleCoeff;
        if (Double.compare(imageRatio, getThumbnailRatio()) >= 0) {
            scaleCoeff = (double) THUMBNAIL_WIDTH / image.getWidth();
        } else {
            scaleCoeff = (double) THUMBNAIL_HEIGHT / image.getHeight();
        }

        /*
         * Создаем и применяем преобразование.
         */
        AffineTransform transform = AffineTransform.getScaleInstance(scaleCoeff, scaleCoeff);
        AffineTransformOp operation = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        thumbnail = new ImageIcon(operation.filter(image, null));
    }

    /**
     * Создает изображение на основе переданного пути к файлу.
     * 
     * @param fileName
     *            - имя файла на диске, для которого будет создан объект с информацией. Не может
     *            быть пустой строкой или null.
     * @throws IOException
     *             - выбрасывается при ошибки чтения картинки из файла.
     */
    public ImageInfo(String fileName) throws IOException {

        /*
         * Загружаем изображение из переданного файла.
         */
        File imageFile = new File(fileName);

        if (!imageFile.canRead()) {
            System.out.println("u-u-ups!");
            return;
        }
        BufferedImage image = ImageIO.read(imageFile);

        height = image.getHeight();
        width = image.getWidth();
        int fileNameStart = fileName.lastIndexOf(File.separatorChar);
        name = fileName.substring(fileNameStart + 1);
        initThumbnail(image);
    }

    /**
     * Возвращает относительное имя файла с изображением. Путь к изображению не возвращается.
     * 
     * @return - имя файла с изображением.
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает ширину изображения
     * 
     * @return ширина изображения
     * @uml.property name="width"
     */
    public int getWidth() {
        return width;
    }

    /**
     * Возвращает высоту изображение.
     * 
     * @return - высота изображения
     * @uml.property name="height"
     */
    public int getHeight() {
        return height;
    }

    /**
     * Дает строкое описание изображения вида: <code>
     * imagename [1024x768]
     * </code>
     */
    public String toString() {
        return getName() + " [" + getWidth() + 'x' + getHeight() + ']';
    }

    /**
     * Возвращает минитатюру изображения. Граниченые размеры миниатюры задаются в константах
     * THUMBNAIL_HEIGHT и THUMBNAIL_WIDTH.
     * 
     * @return - миниатюра изображения
     * @uml.property name="thumbnail"
     */
    public ImageIcon getThumbnail() {
        return thumbnail;
    }

}
