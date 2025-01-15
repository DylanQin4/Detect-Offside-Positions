package itu.opencv;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageViewer {

    // Méthode pour afficher l'image dans un nouveau JFrame
    public static void showImage(String title, BufferedImage image) {
        JFrame imageFrame = new JFrame(title);
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        imageFrame.setSize(image.getWidth(), image.getHeight());
        imageFrame.setLayout(new FlowLayout());

        // Créer un JLabel pour afficher l'image
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageFrame.add(imageLabel);

        // Afficher la fenêtre avec l'image
        imageFrame.setVisible(true);
    }

    // Méthode pour convertir Mat en BufferedImage
    public static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        mat.get(0, 0, buffer);

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);

        return image;
    }
}

