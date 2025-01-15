package itu.opencv;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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
        int type = (mat.channels() > 1) ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] data = new byte[mat.cols() * mat.rows() * mat.channels()];
        mat.get(0, 0, data);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }
}

