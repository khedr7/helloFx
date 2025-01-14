
package hellofx;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;

import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.stage.Stage;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnterImage extends Application {
    private Image image;
    private BufferedImage saveImage;
    private ImageView imageView;
    private Button chooseImageButton;
    private Button octreeButton;
    private Button kmeansButton;
    private Button indexedButton;
    private Button mediancutButton;
    private Button searchSimilarImages;
    private Button showFiltedImage;
    private Button selectFolder;
    private Button saveButton;
    private Button showHistogramButton;
    private Button showImageColorPalette;

    private String selectedAlgorithm;
    private Button seachByColor;

    public static void main(String[] args) {
        launch(args);
    }

    private boolean isImageFile(Path path) {
        String fileName = path.getFileName().toString();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension.matches("png|jpe?g|gif|bmp");
    }

    List<SameImageModel> images = new ArrayList<SameImageModel>();
    List<SameImageModel> imagesAnother = new ArrayList<SameImageModel>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Enter Image to search");

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);
        chooseImageButton = new Button("Choose Image");
        selectFolder = new Button("select Folder");
        octreeButton = new Button("Octree");
        kmeansButton = new Button("K-Means");
        indexedButton = new Button("Indexed");
        mediancutButton = new Button("Median Cut");
        showFiltedImage = new Button("ShowFiltered Image");
        saveButton = new Button("Save");
        searchSimilarImages = new Button("Search Similar Images");
        showHistogramButton = new Button("Show Color Histogram");
        showImageColorPalette = new Button("Show Image Color Palette");

        seachByColor = new Button("Search By Color");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png"));

        primaryStage.setTitle("creating color picker");

        TilePane r = new TilePane();

        Label l = new Label("This is a color picker");
        Label l1 = new Label("no selected color ");

        ColorPicker cp = new ColorPicker();

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Color c = cp.getValue();

                l1.setText("Red = " + c.getRed() + ", Green = " + c.getGreen()
                        + ", Blue = " + c.getBlue());
            }
        };

        cp.setOnAction(event);
        r.getChildren().add(l);
        r.getChildren().add(cp);
        r.getChildren().add(l1);
        selectFolder.setOnAction(e -> {
            int count;
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                try {

                    List<File> imageFiles = Files.walk(selectedDirectory.toPath())
                            .filter(p -> isImageFile(p))
                            .map(Path::toFile)
                            .collect(Collectors.toList());
                    count = 0;
                    for (File imageFile : imageFiles) {
                        count += 1;
                        Image image = new Image(imageFile.toURI().toString());
                        GetHistogram.imagePath = image.getUrl().replaceAll("%20", "");
                        GetHistogram getHistogram = new GetHistogram();
                        EnterImage enterImage = new EnterImage();
                        SameImageModel sameImageModel = enterImage.new SameImageModel(
                                getHistogram.getMaximmum(),
                                image.getUrl(),
                                getHistogram.getMaximmum().get("red"),
                                getHistogram.getMaximmum().get("green"),
                                getHistogram.getMaximmum().get("blue"));
                        images.add(count, sameImageModel);
                    }
                } catch (IOException error) {
                    error.printStackTrace();
                }
            } else {

            }
            System.out.println("================finished pick all images===============");
        });

        chooseImageButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    Histogram.imagePath = image.getUrl();
                    GetHistogram.imagePath = image.getUrl().replaceAll("%20", "");
                    GetHistogram getHistogram = new GetHistogram();
                    System.out.println(getHistogram.getMaximmum());
                    EnterImage enterImage = new EnterImage();
                    SameImageModel sameImageModel = enterImage.new SameImageModel(
                            getHistogram.getMaximmum(),
                            image.getUrl(),
                            getHistogram.getMaximmum().get("red"),
                            getHistogram.getMaximmum().get("green"),
                            getHistogram.getMaximmum().get("blue"));
                    images.add(0, sameImageModel);
                    for (int i = 0; i < images.size(); i++) {
                        System.out.println(images.get(i).filePath);
                        System.out.println(images.get(i).colorAndTheirName);
                        System.out.println(images.get(i).blue);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        octreeButton.setOnAction(e -> {
            selectedAlgorithm = "Octree";
            if (image != null) {
                BufferedImage outputImage = OctreeQuantizer.quantize(SwingFXUtils.fromFXImage(image, null));
                saveImage = outputImage;
                imageView.setImage(SwingFXUtils.toFXImage(outputImage, null));
            }
        });

        seachByColor.setOnAction(e -> {

        });

        indexedButton.setOnAction(e -> {
            selectedAlgorithm = "Indexed";
            if (image != null) {
                BufferedImage outputImage = ToIndexedImage
                        .rgbaToIndexedBufferedImage(SwingFXUtils.fromFXImage(image, null));
                saveImage = outputImage;
                imageView.setImage(SwingFXUtils.toFXImage(outputImage, null));
            }
        });
        mediancutButton.setOnAction(e -> {
            Median median = new Median(SwingFXUtils.fromFXImage(image, null));
            Median.main2();
            BufferedImage outputImage = median.readyImage();
            saveImage = outputImage;
            imageView.setImage(SwingFXUtils.toFXImage(outputImage, null));
        });

        kmeansButton.setOnAction(e -> {
            selectedAlgorithm = "K-Means";

            KMeans kMeans = new KMeans();

            BufferedImage dstImage = kMeans.calculate(SwingFXUtils.fromFXImage(image, null),
                    50, 3);
            saveImage = dstImage;
            imageView.setImage(SwingFXUtils.toFXImage(dstImage, null));
        });

        searchSimilarImages.setOnAction(e -> {
            CalculateImage();
        });

        showFiltedImage.setOnAction(e -> {
            HistogramFX histogramFX = new HistogramFX(imagesAnother);
            histogramFX.start(primaryStage);
        });
        showHistogramButton.setOnAction(e -> {
            Histogram.imagePath = image.getUrl();
            Histogram histogram = new Histogram();
            histogram.start(primaryStage);
        });

        saveButton.setOnAction(e -> {
            if (image != null && saveImage != null) {
                FileChooser saveFileChooser = new FileChooser();
                saveFileChooser.setInitialFileName(selectedAlgorithm + "_" + "image.jpg");
                saveFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG Images", "*.jpg"));
                File selectedFile = saveFileChooser.showSaveDialog(primaryStage);
                if (selectedFile != null) {
                    try {
                        ImageIO.write(saveImage, "jpg", selectedFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        VBox buttonsVBox = new VBox(10, chooseImageButton, selectFolder, searchSimilarImages,
                showFiltedImage, octreeButton, kmeansButton,
                indexedButton,
                mediancutButton, saveButton, showHistogramButton,
                showImageColorPalette);
        buttonsVBox.setAlignment(Pos.CENTER);
        buttonsVBox.setPadding(new Insets(10));
        BorderPane root = new BorderPane();
        root.setCenter(imageView);
        root.setRight(buttonsVBox);
        root.setBottom(r);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void CalculateImage() {
        int coun = 0;
        for (int i = 1; i < images.size(); i++) {
            if (isConvergent(images.get(0).red, images.get(i).red) ||
                    isConvergent(images.get(0).green, images.get(i).green) ||
                    isConvergent(images.get(0).blue, images.get(i).blue)) {
                imagesAnother.add(coun, images.get(i));
                coun++;
            }
        }
    }

    public void CalculateImageByColor(Color color) {
        // int coun = 0;
        // for (int i = 0; i < images.size(); i++) {
        //     Optional<String> firstKey = images.get(i).colorAndTheirName.keySet().stream().findFirst();
        //     if (firstKey.isPresent()) {
                
        //     }
        // }
    }
    

    public static boolean isConvergent(Long old, Long newI) {
        if (Math.abs(old - newI) <= 10000) {
            return true;
        }
        return false;
    }

    class SameImageModel {
        private Long blue;
        private Long red;
        private Long green;

        String filePath = "";

        Map<String, Long> colorAndTheirName = new HashMap<String, Long>();

        public double calculateDistance(SameImageModel other) {
            Long blueDiff = this.blue - other.blue;
            Long redDiff = this.red - other.red;
            Long greenDiff = this.green - other.green;
            return Math.sqrt(blueDiff * blueDiff + redDiff * redDiff + greenDiff * greenDiff);
        }

        SameImageModel(Map<String, Long> map, String filePat, Long red, Long green, Long blue) {
            filePath = filePat;
            colorAndTheirName = map;
            this.blue = blue;
            this.red = red;
            this.green = green;

        };
    }
}
