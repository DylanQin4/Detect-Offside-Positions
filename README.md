# Detect Offside Positions in Football Images

Ce projet est une application de traitement d'images qui détecte les positions de hors-jeu dans des images de football à l'aide d'OpenCV. L'application analyse une image, détecte les joueurs et le ballon, puis détermine la ligne de hors-jeu en fonction des positions des joueurs et du ballon.

## Prérequis

- Kit de Développement Java (JDK) 8 ou version ultérieure
- Bibliothèque OpenCV

## Installation

1. Téléchargez et installez OpenCV :
    - Suivez les instructions sur le [site officiel d'OpenCV](https://opencv.org/releases/) pour télécharger et installer OpenCV sur votre système.
    - Ajoutez la bibliothèque OpenCV à votre projet.

## Utilisation

1. Placez l'image d'entrée dans le répertoire `src/itu/ressources/static/images/`.

2. Exécutez la classe `Main` :
    - Faites un clic droit sur la classe `Main` dans l'Explorateur de Projet.
    - Sélectionnez `Exécuter 'Main.main()'`.

3. Utilisez le sélecteur de fichiers pour choisir une image à analyser.

4. L'image traitée avec la ligne de hors-jeu et les joueurs détectés sera enregistrée dans le répertoire `src/itu/ressources/static/images/` sous le nom `result.png`.

## Compilation et Exécution
```bash
javac -d build -cp src/lib/opencv-4100.jar src/itu/opencv/*.java
java -cp build:src/lib/opencv-4100.jar -Djava.library.path=src/lib/ itu/opencv/Main
```

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.