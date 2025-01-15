

```bash
javac -d build -cp src/lib/opencv-4100.jar src/itu/opencv/*.java
java -cp build:src/lib/opencv-4100.jar -Djava.library.path=src/lib/ itu/opencv/Main
```
