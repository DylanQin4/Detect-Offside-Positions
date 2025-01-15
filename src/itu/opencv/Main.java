package itu.opencv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Main {

    public static void main(String[] args) {
//        // Création de la fenêtre principale
//        JFrame mainFrame = new JFrame("Sélection d'une image");
//        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        mainFrame.setSize(400, 150);
//        mainFrame.setLayout(new FlowLayout());
//
//        // Composants de la fenêtre principale
//        JTextField fileInputField = new JTextField(20);
//        fileInputField.setEditable(false);
//        JButton browseButton = new JButton("Parcourir");
//
//        mainFrame.add(new JLabel("Sélectionnez une image :"));
//        mainFrame.add(fileInputField);
//        mainFrame.add(browseButton);

//        // Action du bouton "Parcourir"
//        browseButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // Ouvrir la boîte de dialogue pour choisir un fichier
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.setDialogTitle("Sélectionner une image");
//                int result = fileChooser.showOpenDialog(mainFrame);
//
//                if (result == JFileChooser.APPROVE_OPTION) {
//                    // Récupérer le fichier sélectionné
//                    File selectedFile = fileChooser.getSelectedFile();
//                    fileInputField.setText(selectedFile.getAbsolutePath());
//                    // Ouvrir un nouveau frame pour afficher l'image avec traitement OpenCV
//                    ImageProcessor.processAndShowImage(selectedFile);
//                }
//            }
//        });

//        mainFrame.setVisible(true);
        ImageProcessor.processAndShowImage();
    }
}