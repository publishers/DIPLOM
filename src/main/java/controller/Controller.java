package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import methods.*;
import model.SpecialPoints;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Created by Admin on 15.11.15.
 */
public class Controller implements Initializable{


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BernEPS.setText(String.valueOf(Bernsen.eps));
        BernRadius.setText(String.valueOf(Bernsen.r));
        someThreholds = new ArrayList();
        isOpenFileColorImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!isOpenFileColorImage.isSelected()) {
                    openColorFile = null;
                    loadedImageView.setImage(null);
                }
            }
        });

    }

    @FXML
    public void close(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void openFile(ActionEvent actionEvent) throws IOException {
        FileChooser fChooser = new FileChooser();
        fChooser.setTitle("Open file");
        fChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.gif", "*.png")
        );
        if(isOpenFileColorImage.isSelected()){
            File file = fChooser.showOpenDialog(null);
            if(file != null){
                this.openColorFile = file;
                showImage(loadedImageView, file.getAbsolutePath());
            }
        }else{
            File file = fChooser.showOpenDialog(null);
            if(file != null){
                this.file = file;
                readIMGFile();
            }
        }

    }

    @FXML
    public void var1() throws IOException{
        if(file != null) {
            createHistogram(otsu.threshold1());
            showImage(imgAfterMethod, "OTSUImage.jpg");
        }
    }

    @FXML
    public void var2() throws IOException{
        if(file != null) {
            createHistogram(otsu.threshold2());
            showImage(imgAfterMethod, "OTSUImage.jpg");
        }
    }

    @FXML
    public void var3() throws IOException{
        if(file != null) {
            createHistogram(otsu.threshold3());
            showImage(imgAfterMethod, "OTSUImage.jpg");
        }
    }

    @FXML
    public void var4() throws IOException{
        if(file != null) {
            if (thresholdsUse.isSelected()) {
                otsu.createImage(Integer.parseInt(thresholdsLow.getText()), Integer.parseInt(thresholdsHight.getText()));
            } else {
                otsu.createImage(otsu.threshold4(), otsu.threshold4Hight());
                dataMethodH.setText("" + otsu.threshold4Hight());
                dataMethodL.setText("" + otsu.threshold4());
            }
            createHistogram();
            showImage(imgAfterMethod, "OTSUImage.jpg");
        }
    }

    @FXML
    public void var5() throws IOException{
        if(file != null) {
            createHistogram(otsu.threshold5());
            showImage(imgAfterMethod, "OTSUImage.jpg");
        }
    }

    @FXML
    public void bernsenMethod() throws IOException{
        if(file != null) {
            Bernsen bern = new Bernsen();
            bern.setEps(Integer.parseInt(BernEPS.getText()));
            bern.setR(Integer.parseInt(BernRadius.getText()));
            bern.start();
            bern.createImage();
            showImage(imgAfterMethod, "BERNSENImage.jpg");
        }
    }

    @FXML
    public void setImageOfBerns() throws IOException{
        showHeadSkeletPoints("BERNSENImage.jpg");
    }

    @FXML
    public void doKMeansMethod(ActionEvent actionEvent) throws IOException {
        if(isOpenColorFile()){
            final KMeans kmeans = new KMeans();

            int k = Integer.parseInt(clustersKmeans.getText());
            new Thread(){
                @Override
                public void run() {
                    BufferedImage dstImage = kmeans.calculate(KMeans.loadImage(openColorFile.getAbsolutePath()), k, 2);
                    KMeans.saveImage("KMeans.png", dstImage);
                    JOptionPane.showMessageDialog(null, "Method K-means is done!");

                        showImage(imageViewKMeans, "KMeans.png");

                }
            }.start();
            System.gc();
        }
    }

    @FXML
    public void doWatershedMethod(ActionEvent actionEvent) {
        if(isOpenColorFile()){

            int floodPoints = Integer.parseInt(this.floodPoints.getText()); //уровень заполнения водой
            int windowWidth = Integer.parseInt(this.windowWidth.getText());//это интенсивность построения
            final int connectedPixels = 8;

            Watershed watershed = new Watershed();
            String imageName = openColorFile.getAbsolutePath();
            showWithoutImage = false;
            new Thread(){
                @Override
                public void run() {
                    BufferedImage dstImage = watershed.calculate(Watershed.loadImage(imageName),
                            floodPoints,
                            windowWidth, connectedPixels);
                    watershed.saveImage("WaterRegions.png", dstImage);
                    dstImage = watershed.makeImage(imageName, dstImage);
                    watershed.saveImage("Water.png", dstImage);
                    JOptionPane.showMessageDialog(null, "Method Watershed is done!");

                        showImage(imageViewKMeans, "Water.png");

                }
            }.start();
            System.gc();

        }
    }

    @FXML
    public void doWatershedMethodWithoutImage(ActionEvent actionEvent) throws IOException {
        if (!showWithoutImage) {
            showImage(imageViewKMeans, "WaterRegions.png");
            showWithoutImage = true;
        }else {
            showImage(imageViewKMeans, "Water.png");
            showWithoutImage = false;
        }
    }

    @FXML
    public void setImageOfOtsu() throws IOException {
        showHeadSkeletPoints("OTSUImage.jpg");
    }

    @FXML
    public void recognitionTheImage(ActionEvent actionEvent) throws IOException{
        ZongSun Zs = new ZongSun(new File("Zong.jpg"));
        Zs.init();
        while (Zs.start()) {}
        byte[][] colors = Zs.getColors();
        SpecialPoints sp = new SpecialPoints(colors);
        recognitionFingerOfPrint(sp);

    }

    private boolean isOpenColorFile(){
        return isOpenFileColorImage.isSelected() && openColorFile != null;
    }

    public void recognitionFingerOfPrint(SpecialPoints sp) throws IOException {
//        BufferedReader br = Files.newBufferedReader(Paths.get("db.txt"), StandardCharsets.UTF_8);
        ArrayList<SpecialPoints> db = new ArrayList<SpecialPoints>();
        Stream<String> stream = Files.lines(Paths.get("db.txt"));
        stream.forEach(s -> {
            String[] pars = s.split("\\|");
            db.add(ProcentsOfCorcordance.createSpecPoints(pars));
        });
        String FName = null;
        double maxPersent = 0;
        for (SpecialPoints SP : db){
            double tmp;
            if( (tmp = ProcentsOfCorcordance.getProcent(SP, sp)) > ProcentsOfCorcordance.MINPROCENTS && tmp > maxPersent){
                maxPersent = tmp;
                FName = SP.getFName();
            }
        }
        if(FName != null){
            JOptionPane.showMessageDialog(null, "It's " + FName + "!");
        } else JOptionPane.showMessageDialog(null, "Data base don't have the same fingerprints!");
    }

    private void showHeadSkeletPoints(String fileName) throws IOException {

        ZongSun Zs = new ZongSun(new File(fileName));
        Zs.init();
        while (Zs.start()) {}

        byte[][] colors = Zs.getColors();
        SpecialPoints sp = new SpecialPoints(colors);

        if(saveSkeletonToDB.isSelected()){
            FileWriter fw = new FileWriter("db.txt", true);
            String name = JOptionPane.showInputDialog(null);
            if(name != null) {
                sp.setFName(name);
                fw.append(sp + "\n");
            }
            fw.flush();
            fw.close();
        }
        ArrayList<Point> points = sp.getRamifications();
        ArrayList<Point> pointsAdges = sp.getAdges();

        BufferedImage img = new BufferedImage(colors.length, colors[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < colors[i].length; j++) {
                int y = 255 - colors[i][j] * 255;
                img.setRGB(i, j, new Color(y, y, y).getRGB());
            }
        }
        points.forEach(point -> {
                    img.setRGB((int) point.getX()-1, (int) point.getY()-1, new Color(255, 0, 0).getRGB());
                    img.setRGB((int) point.getX(), (int) point.getY()-1, new Color(255, 0, 0).getRGB());
                    img.setRGB((int) point.getX()+1, (int) point.getY()-1, new Color(255, 0, 0).getRGB());

                    img.setRGB((int) point.getX()-1, (int) point.getY(), new Color(255, 0, 0).getRGB());
                    img.setRGB((int) point.getX(), (int) point.getY(), new Color(255, 0, 0).getRGB());
                    img.setRGB((int) point.getX()+1, (int) point.getY(), new Color(255, 0, 0).getRGB());

                    img.setRGB((int) point.getX()-1, (int) point.getY()+1, new Color(255, 0, 0).getRGB());
                    img.setRGB((int) point.getX(), (int) point.getY()+1, new Color(255, 0, 0).getRGB());
                    img.setRGB((int) point.getX()+1, (int) point.getY()+1, new Color(255, 0, 0).getRGB());

            }
        );
        pointsAdges.forEach(point ->{
                    img.setRGB((int) point.getX()-1, (int) point.getY()-1, new Color(0, 0, 255).getRGB());
                    img.setRGB((int) point.getX(), (int) point.getY()-1, new Color(0, 0, 255).getRGB());
                    img.setRGB((int) point.getX()+1, (int) point.getY()-1, new Color(0, 0, 255).getRGB());

                    img.setRGB((int) point.getX()-1, (int) point.getY(), new Color(0, 0, 255).getRGB());
                    img.setRGB((int) point.getX(), (int) point.getY(), new Color(0, 0, 255).getRGB());
                    img.setRGB((int) point.getX()+1, (int) point.getY(), new Color(0, 0, 255).getRGB());

                    img.setRGB((int) point.getX()-1, (int) point.getY()+1, new Color(0, 0, 255).getRGB());
                    img.setRGB((int) point.getX(), (int) point.getY()+1, new Color(0, 0, 255).getRGB());
                    img.setRGB((int) point.getX()+1, (int) point.getY()+1, new Color(0, 0, 255).getRGB());
                }
        );
        File fileIMAGE = new File("Zong.jpg");
        try {
            ImageIO.write(img, "jpg", fileIMAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showImage(imgBeforeSkelet, fileName);
        showImage(imgSkeleton, "Zong.jpg");

    }

    private void readIMGFile() throws IOException {
        BufferedImage img = ImageIO.read(new File(file.getAbsolutePath()));
        someThreholds.clear();
        showImage(imageOrigin, file.getAbsolutePath());
        Color[][] colors = new Color[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {

                int R = new Color(img.getRGB(i,j)).getRed();
                int G = new Color(img.getRGB(i,j)).getGreen();
                int B = new Color(img.getRGB(i,j)).getBlue();

                int y = (int) Math.abs((0.2125 * R) + (0.7154 * G) + (0.0721 * B));
                colors[i][j] = new Color(y, y, y);
                img.setRGB(i, j, new Color(y, y, y).getRGB());
            }
        }
        File fileIMAGE = new File("image.jpg");
        ImageIO.write(img, "jpg", fileIMAGE);
        showImage(whiteBlack, "image.jpg");
        otsu = new OTSU();
        otsu.start();
        createHistogram();
        System.gc();
    }

    private void createHistogram(int T){
        barChart.getData().clear();
        XYChart.Series<Number, String> series = new XYChart.Series();
        int []hist = otsu.getHistogram();
        otsu.createImage(T);
        for (int i = 1; i <= 255; i++) {
            series.getData().add(new XYChart.Data(""+i, hist[i-1]));
        }
        barChart.getData().add(series);
    }

    public void createHistogram()throws IOException{
        barChart.getData().clear();
        XYChart.Series<Number, String> series = new XYChart.Series();
        int []hist = otsu.getHistogram();
        for (int i = 1; i <= 255; i++) {
            series.getData().add(new XYChart.Data(""+i, hist[i-1]));
        }
        barChart.getData().add(series);
    }

    private void showImage(ImageView imgView, String nameIMG){
        try {
            imgView.setImage(null);
            BufferedImage img = ImageIO.read(new File(nameIMG));
            Image image = SwingFXUtils.toFXImage(img, null);
            imgView.setImage(image);
            img.flush();
        }catch (IOException ioe){
            JOptionPane.showMessageDialog(null, "image not found ");
        }
    }

    private File file;
    private File openColorFile;
    private boolean showWithoutImage = false;


    private ArrayList someThreholds;
    private OTSU otsu;
    @FXML
    public ImageView whiteBlack;

    @FXML
    public ImageView imageOrigin;
    @FXML
    public BarChart barChart;
    @FXML
    public ImageView imgAfterMethod;
    @FXML
    public TextField BernEPS;
    @FXML
    public TextField BernRadius;
    @FXML
    public TextField thresholdsHight;
    @FXML
    public TextField thresholdsLow;
    @FXML
    public CheckBox thresholdsUse;
    @FXML
    public Label dataMethodH;
    @FXML
    public Label dataMethodL;
    @FXML
    public ImageView imgSkeleton;
    @FXML
    public ImageView imgBeforeSkelet;
    @FXML
    public CheckMenuItem saveSkeletonToDB;
    @FXML
    public MenuBar tabColorImage;
    @FXML
    public RadioMenuItem isOpenFileColorImage;
    @FXML
    public ImageView imageViewKMeans;
    @FXML
    public ImageView loadedImageView;
    @FXML
    public TextField clustersKmeans;
    @FXML
    public TextField windowWidth;
    @FXML
    public TextField floodPoints;
}