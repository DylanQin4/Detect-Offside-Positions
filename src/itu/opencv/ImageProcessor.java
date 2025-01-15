package itu.opencv;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static itu.opencv.ImageViewer.matToBufferedImage;
import static itu.opencv.ImageViewer.showImage;

public class ImageProcessor {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void processAndShowImage(File imageFile) {
        Mat image;
        // Charger l'image
        if (imageFile == null) {
            String relativePath = "src/itu/ressources/static/images/8.jpeg";
            image = Imgcodecs.imread(relativePath);
        } else {
            image = Imgcodecs.imread(imageFile.getAbsolutePath());
        }

        // Vérifier si l'image a été chargée
        if (image.empty()) {
            System.out.println("Impossible de charger l'image.");
            return;
        }

        // Convertir en HSV
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Détection des joueurs bleus
        List<DetectedObject> bluePlayers = detectPlayers(hsvImage, new Scalar(100, 150, 50), new Scalar(140, 255, 255));
        System.out.println("Nombre de joueurs bleus : " + bluePlayers.size());

        // Détection des joueurs rouges
        List<DetectedObject> redPlayers = detectPlayers(hsvImage, new Scalar(-10, 150, 150), new Scalar(10, 255, 255)); // Rouge primaire
        System.out.println("Nombre de joueurs rouges : " + redPlayers.size());

        // Détection de la balle noire
        List<DetectedObject> blackBall = detectPlayers(hsvImage, new Scalar(0, 0, 0), new Scalar(180, 255, 50));
        System.out.println("Balle noire : " + blackBall);

        // Génération de l'image avec les positions
        Mat resultImage = image.clone();

        // Annoter les joueurs hors-jeu
        detectOffsideAndDraw(resultImage, redPlayers, bluePlayers, blackBall);

        // Sauvegarder l'image générée
        String outputPath = "src/itu/ressources/static/images/result.png";
        Imgcodecs.imwrite(outputPath, resultImage);
        System.out.println("Image générée et sauvegardée à : " + outputPath);

        // Afficher l'image générée
        showImage("Image Originale", matToBufferedImage(resultImage));
    }

    /**
     * Détecter les joueurs hors-jeu et dessiner la ligne de hors-jeu en prenant en compte le sens de l'attaque.
     *
     * @param image         Mat image sur laquelle dessiner
     * @param bluePlayers   Liste des positions des joueurs bleus
     * @param redPlayers    Liste des positions des joueurs rouges
     * @param ball          Position du ballon
     */
    private static void detectOffsideAndDraw(Mat image, List<DetectedObject> bluePlayers, List<DetectedObject> redPlayers, List<DetectedObject> ball) {
        if (ball.isEmpty()) {
            System.out.println("Ballon introuvable.");
            return;
        }

        // Déterminer le sens d'attaque pour chaque équipe
        boolean isBlueAttackingUp = isTeamAttackingUp(bluePlayers, image.height());
        boolean isRedAttackingUp = isTeamAttackingUp(redPlayers, image.height());

        if (isBlueAttackingUp == isRedAttackingUp) {
            System.out.println("Impossible de déterminer le sens d'attaque : incohérence détectée.");
            return;
        }
        if (isRedAttackingUp) {
            System.out.println("L'équipe rouge attaque vers le haut.");
        } else {
            System.out.println("L'équipe rouge attaque vers le bas.");
        }

        // Identifier l'équipe attaquante en fonction de la position du ballon
        DetectedObject ballPosition = ball.get(0);
        DetectedObject closestToBall = findClosestPlayer(ballPosition, bluePlayers, redPlayers);
        boolean isBlueAttacking = bluePlayers.contains(closestToBall);
        List<DetectedObject> attackingTeam = isBlueAttacking ? bluePlayers : redPlayers;
        List<DetectedObject> defendingTeam = isBlueAttacking ? redPlayers : bluePlayers;

        // Déterminer la direction d'attaque de l'équipe attaquante
        boolean isAttackingUp = isBlueAttacking ? isBlueAttackingUp : isRedAttackingUp;

        // Trouver les deux derniers défenseurs dans le camp défensif
        defendingTeam.sort((p1, p2) -> Double.compare(isAttackingUp ? p2.getPosition().y : p1.getPosition().y,
                isAttackingUp ? p1.getPosition().y : p2.getPosition().y));

        if (defendingTeam.size() < 2) {
            System.out.println("Pas assez de défenseurs détectés pour tracer la ligne de hors-jeu.");
            return;
        }

        // DetectedObject lastDefender = defendingTeam.get(0);
        DetectedObject secondLastDefender = defendingTeam.get(1);

        // Trouver la ligne de hors-jeu
        double offsideLineY = isAttackingUp ? secondLastDefender.getPosition().y + secondLastDefender.getRadius() : secondLastDefender.getPosition().y - secondLastDefender.getRadius();

        // Identifier les joueurs attaquants hors-jeu
        List<DetectedObject> offsidePlayers = getOffsidePlayers(attackingTeam, offsideLineY, isAttackingUp);
        // Verifier si le joueur le plus proche du ballon est dans le zone de hors-jeu, si oui, on doit deplacer la ligne de hors-jeu
        for (DetectedObject attacker : offsidePlayers) {
            if (attacker.equals(closestToBall)) {
                if (isAttackingUp) {
                    offsideLineY = attacker.getPosition().y + attacker.getRadius();
                } else {
                    offsideLineY = attacker.getPosition().y - attacker.getRadius();
                }
                Imgproc.line(image, new Point(0, offsideLineY), new Point(image.width(), offsideLineY), new Scalar(0, 255, 0), 2);
                System.out.println("Ligne de hors-jeu déplacée à Y = " + offsideLineY);
                offsidePlayers = getOffsidePlayers(attackingTeam, offsideLineY, isAttackingUp);
                break;
            }
        }

        // Tracer la ligne de hors-jeu
        Imgproc.line(image, new Point(0, offsideLineY), new Point(image.width(), offsideLineY), new Scalar(0, 255, 0), 2);
        System.out.println("Ligne de hors-jeu tracée à Y = " + offsideLineY);

        for (DetectedObject attacker : offsidePlayers) {
            if (!attacker.equals(closestToBall)) {
                Imgproc.putText(image, "HJ", new Point(attacker.getPosition().x + 10, attacker.getPosition().y - 10),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 255), 1);
                System.out.println("Joueur hors-jeu détecté à : " + attacker.getPosition());
            }
        }
    }

    /**
     * Vérifie si les joueurs d'une équipe sont hors-jeu en fonction de la ligne de hors-jeu et du sens de l'attaque.
     *
     * @param attackers Liste des joueurs attaquants
     * @param offsideLinePosition Position de la ligne de hors-jeu (en Y)
     * @param attackingUp Indique si l'équipe attaque vers le haut
     * @return Liste des joueurs identifiés comme hors-jeu
     */
    private static List<DetectedObject> getOffsidePlayers(List<DetectedObject> attackers, double offsideLinePosition, boolean attackingUp) {
        return attackers.stream()
                .filter(player -> {
                    double playerY = attackingUp? player.getPosition().y + player.getRadius() : player.getPosition().y - player.getRadius();

                    // Si l'équipe attaque vers le haut, les joueurs hors-jeu sont au-dessus (plus petit Y que la ligne)
                    if (attackingUp) {
                        return playerY > offsideLinePosition;
                    }
                    // Si l'équipe attaque vers le bas, les joueurs hors-jeu sont en dessous (plus grand Y que la ligne)
                    else {
                        return playerY < offsideLinePosition;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Détermine si une équipe attaque vers le haut en fonction de la position moyenne des joueurs
     * et des positions extrêmes (gardien de but présumé).
     *
     * @param players Liste des joueurs d'une équipe
     * @param imageHeight Hauteur de l'image
     * @return true si l'équipe attaque vers le haut, false sinon
     */
    private static boolean isTeamAttackingUp(List<DetectedObject> players, double imageHeight) {
        if (players == null || players.isEmpty()) {
            System.out.println("Liste des joueurs vide ou nulle, impossible de déterminer le sens d'attaque.");
            return false;
        }

        // Trouver le joueur le plus haut (plus petite valeur Y) et le joueur le plus bas (plus grande valeur Y)
        DetectedObject highestPlayer = players.stream()
                .min(Comparator.comparingDouble(player -> player.getPosition().y))
                .get();

        DetectedObject lowestPlayer = players.stream()
                .max(Comparator.comparingDouble(player -> player.getPosition().y))
                .get();

        // Calcul des distances
        double distanceToTop = highestPlayer.getPosition().y; // Distance du joueur le plus haut au haut de l'image
        double distanceToBottom = imageHeight - lowestPlayer.getPosition().y; // Distance du joueur le plus bas au bas de l'image

        // Le gardien de but est généralement plus proche de sa propre ligne de but
        return distanceToTop < distanceToBottom;
    }




    /**
     * Trouver le joueur le plus proche du ballon.
     *
     * @param ball          Position du ballon
     * @param bluePlayers   Liste des joueurs bleus
     * @param redPlayers    Liste des joueurs rouges
     * @return Joueur le plus proche
     */
    private static DetectedObject findClosestPlayer(DetectedObject ball, List<DetectedObject> bluePlayers, List<DetectedObject> redPlayers) {
        List<DetectedObject> allPlayers = new ArrayList<>();
        allPlayers.addAll(bluePlayers);
        allPlayers.addAll(redPlayers);

        DetectedObject closestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        for (DetectedObject player : allPlayers) {
            double distance = Math.sqrt(Math.pow(player.getPosition().x - ball.getPosition().x, 2)
                    + Math.pow(player.getPosition().y - ball.getPosition().y, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closestPlayer = player;
            }
        }

        return closestPlayer;
    }


    /**
     * Méthode pour détecter les joueurs ou objets dans une plage de couleurs donnée.
     *
     * @param hsvImage Mat image en espace de couleur HSV
     * @param lower    Limite inférieure HSV
     * @param upper    Limite supérieure HSV
     * @return Liste des positions (DetectedObject) des objets détectés
     */
    private static List<DetectedObject> detectPlayers(Mat hsvImage, Scalar lower, Scalar upper) {
        Mat mask = new Mat();
        Core.inRange(hsvImage, lower, upper, mask);

        // Trouver les contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Calculer les centres des contours et les rayons
        List<DetectedObject> detectedObjects = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Moments moments = Imgproc.moments(contour);
            if (moments.get_m00() != 0) {
                int x = (int) (moments.get_m10() / moments.get_m00());
                int y = (int) (moments.get_m01() / moments.get_m00());
                double radius = Math.sqrt(moments.get_m00() / Math.PI); // Approximation du rayon
                detectedObjects.add(new DetectedObject(new Point(x, y), radius));
            }
        }

        return detectedObjects;
    }
}