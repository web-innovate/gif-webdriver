package com.github.bogdanlivadariu.gifwebdriver;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Worker responsible of taking,storing screenshots and generating the GIF based on tem
 */
public class GifScreenshotWorker {

    final List<String> screenshotsTaken = new ArrayList<>();

    private final Logger logger = LogManager.getLogger(GifScreenshotWorker.class);

    private final WebDriver driver;

    private final String uniqueName = RandomStringUtils.randomAlphabetic(10);

    private final String separator = File.separator;

    private String rootDir;

    private String screenshotsFolderName;

    private String generatedGIFsFolderName;

    private int timeBetweenFramesInMilliseconds = 500;

    private boolean loopContinuously = false;

    private int counter = 0;

    public GifScreenshotWorker(WebDriver driver) {
        this.driver = driver;

        setRootDir(String.format("gifScreenshotWorker%s%s", separator, getUniqueName()));
        setScreenshotsFolderName("screenshots");
        setGeneratedGIFsFolderName("generatedGifs");
    }

    public GifScreenshotWorker(WebDriver driver, String rootDir, String screenshotsFolder,
        String generatedGIFsFolderName) {
        this.driver = driver;

        setRootDir(rootDir + separator + getUniqueName());
        setScreenshotsFolderName(screenshotsFolder);
        setGeneratedGIFsFolderName(generatedGIFsFolderName);
    }

    /**
     * Takes a screenshot of the current page
     */
    public void takeScreenshot() {
        try {
            byte[] screenShotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            File screenshotFile =
                new File(getScreenshotsFolderName() + counter++ + RandomStringUtils.randomAlphanumeric(10) + ".png");

            FileUtils.writeByteArrayToFile(screenshotFile, screenShotData);

            logger.info(String.format("Screenshot taken at: '%s'", screenshotFile.getAbsolutePath()));

            screenshotsTaken.add(screenshotFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Screenshot could not be taken or saved", e);
        }
    }

    /**
     * Creates the GIF and writes it on the disk
     */
    public void createGif() {
        if (screenshotsTaken.isEmpty()) {
            logger.info("There are no screenshots to process");
            return;
        }

        try {
            BufferedImage firstImage = ImageIO.read(new File(screenshotsTaken.get(0)));

            File outputFile = new File(getGeneratedGIFsFolderName() + uniqueName + ".gif");

            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
            }

            ImageOutputStream output =
                new FileImageOutputStream(new File(getGeneratedGIFsFolderName() + uniqueName + ".gif"));

            Giffer gif = new Giffer(
                output,
                firstImage.getType(),
                getTimeBetweenFramesInMilliseconds(),
                isLoopContinuously());

            for (int i = 1; i < screenshotsTaken.size(); i++) {
                BufferedImage nextImage = ImageIO.read(new File(screenshotsTaken.get(i)));

                gif.writeToSequence(nextImage);
            }

            gif.close();
            output.close();

            logger.info(String.format("Gif created at: '%s'", outputFile.getAbsolutePath()));

            // we don't want to have same images in a new gif :)
            screenshotsTaken.clear();
        } catch (Exception e) {
            logger.error("Gif could not be created or saved", e);
        }

    }

    /**
     * Defaults to 500ms
     *
     * @return delay used to switch from one image to another on the generated GIF
     */
    public int getTimeBetweenFramesInMilliseconds() {
        return timeBetweenFramesInMilliseconds;
    }

    /**
     * Set the delay used to switch from one image to another on thegenerated GIV
     *
     * @param timeBetweenFramesInMilliseconds - value in milliseconds
     */
    public void setTimeBetweenFramesInMilliseconds(int timeBetweenFramesInMilliseconds) {
        this.timeBetweenFramesInMilliseconds = timeBetweenFramesInMilliseconds;
    }

    /**
     * Defaults to false
     *
     * @return true/false weather the generated GIF will loop
     */
    public boolean isLoopContinuously() {
        return loopContinuously;
    }

    /**
     * Set weather the generated GIF will loop
     *
     * @param loopContinuously true / false
     */
    public void setLoopContinuously(boolean loopContinuously) {
        this.loopContinuously = loopContinuously;
    }

    /**
     * @return - Unique name generated used to store every screenshot and GIF as an unique file
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * @return - folder on disk where the screenshots and generated GIF will get stored
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * Folder on disk where screenshots and generated GIFs will be stored
     * Defaults to "project.dir/gifScreenshotsFolder/uniqueId/"
     *
     * @param rootDir - path to folder absolute or relative to project dir
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir + separator;
    }

    /**
     * Defaults to "project.dir/rootDir/uniqueId/screenshots"
     *
     * @return - path to folder where screenshots will be stored relative to rootDir
     */
    public String getScreenshotsFolderName() {
        return screenshotsFolderName;
    }

    /**
     * Set location where screenshots will be stored on disk
     *
     * @param screenshotsFolderName - path to folder where screenshots will be stored relative to rootDir
     */
    public void setScreenshotsFolderName(String screenshotsFolderName) {
        this.screenshotsFolderName = getRootDir() + screenshotsFolderName + separator;
    }

    /**
     * Defaults to "project.dir/rootDir/uniqueId/generatedGifs"
     *
     * @return - folder where the generated GIF will be stored on disk relative to the rootDir
     */
    public String getGeneratedGIFsFolderName() {
        return generatedGIFsFolderName;
    }

    /**
     * Set location where generated GIFs will be stored on disk
     *
     * @param generatedGIFsFolderName - path to folder for generated GIFS relative to rootDir
     */
    public void setGeneratedGIFsFolderName(String generatedGIFsFolderName) {
        this.generatedGIFsFolderName = getRootDir() + generatedGIFsFolderName + separator;
    }
}

