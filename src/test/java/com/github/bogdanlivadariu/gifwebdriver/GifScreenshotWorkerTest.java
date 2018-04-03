package com.github.bogdanlivadariu.gifwebdriver;

import org.apache.commons.io.FileUtils;
import org.mockito.Mock;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@Test
public class GifScreenshotWorkerTest {
    @Mock
    private WebDriver driver;

    private GifScreenshotWorker worker;

    private static byte[] bufferedImageToByteArray(BufferedImage image, String type) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, type, out);
            return out.toByteArray();

        }
    }

    @BeforeMethod
    private void setup() {
        worker = new GifScreenshotWorker(null);
    }

    @AfterMethod
    private void tearDown() throws IOException {
        File root = new File(worker.getRootDir());

        if (root.getParentFile() != null && root.getParentFile().exists()) {
            FileUtils.deleteDirectory(root.getParentFile());
        } else {
            FileUtils.deleteDirectory(root);
        }
    }

    public void testTakeScreenshotOnNullDriver() {
        try {
            worker.takeScreenshot();
            assertTrue(worker.screenshotsTaken.isEmpty());
        } catch (Throwable t) {
            fail("This should not happen", t);
        }
    }

    public void testTakeScreenshot() {
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(TakesScreenshot.class));
        when(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)).thenReturn(new byte[1]);

        worker = new GifScreenshotWorker(driver);

        worker.takeScreenshot();
        worker.takeScreenshot();
        worker.takeScreenshot();

        verify(((TakesScreenshot) driver), times(3)).getScreenshotAs(OutputType.BYTES);

        assertEquals(worker.screenshotsTaken.size(), 3);

        worker.screenshotsTaken.forEach(item -> {
            File file = new File(item);

            assertTrue(file.exists());
        });
    }

    public void testCreateGid() throws IOException {
        BufferedImage singlePixelImage = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Color transparent = new Color(0, 0, 0, 0);
        singlePixelImage.setRGB(0, 0, transparent.getRGB());

        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(TakesScreenshot.class));

        when(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES))
            .thenReturn(bufferedImageToByteArray(singlePixelImage, "png"));

        worker = new GifScreenshotWorker(driver);

        worker.takeScreenshot();
        worker.takeScreenshot();
        worker.takeScreenshot();

        worker.createGif();

        assertTrue(new File(worker.getGeneratedGIFsFolderName() + worker.getUniqueName() + ".gif").exists());
    }

    public void testCreateGifOnNullDriver() {
        try {
            worker.createGif();
        } catch (Throwable t) {
            fail("This should not happen", t);
        }
    }

    public void testGetTimeBetweenFramesInMilliseconds() {
        assertEquals(worker.getTimeBetweenFramesInMilliseconds(), 500);
    }

    public void testSetTimeBetweenFramesInMilliseconds() {
        worker.setTimeBetweenFramesInMilliseconds(10);

        assertEquals(worker.getTimeBetweenFramesInMilliseconds(), 10);
    }

    public void testIsLoopContinuously() {
        assertFalse(worker.isLoopContinuously());
    }

    public void testSetLoopContinuously() {
        assertFalse(worker.isLoopContinuously());

        worker.setLoopContinuously(true);

        assertTrue(worker.isLoopContinuously());
    }

    public void testGetUniqueName() {
        assertFalse(worker.getUniqueName().isEmpty());
    }

    public void testGetRootDir() {
        assertEquals(worker.getRootDir(), String.format("gifScreenshotWorker/%s/", worker.getUniqueName()));
    }

    public void testSetRootDir() {
        worker.setRootDir("bamboo");

        assertEquals(worker.getRootDir(), "bamboo/");
    }

    public void testGetScreenshotsFolderName() {
        assertEquals(worker.getScreenshotsFolderName(), worker.getRootDir() + "screenshots/");
    }

    public void testSetScreenshotsFolderName() {
        worker.setScreenshotsFolderName("foo");

        assertEquals(worker.getScreenshotsFolderName(), worker.getRootDir() + "foo/");
    }

    public void testGetGeneratedGIFsFolderName() {
        assertEquals(worker.getGeneratedGIFsFolderName(), worker.getRootDir() + "generatedGifs/");

    }

    public void testSetGeneratedGIFsFolderName() {

        worker.setGeneratedGIFsFolderName("bar");

        assertEquals(worker.getGeneratedGIFsFolderName(), worker.getRootDir() + "bar/");
    }

    public void testConstructor() {
        worker = new GifScreenshotWorker(null, "root", "screens", "gifsLocation");

        String expectedRoot = String.format("root/%s/", worker.getUniqueName());
        assertEquals(worker.getRootDir(), expectedRoot);
        assertEquals(worker.getScreenshotsFolderName(), expectedRoot + "screens/");
        assertEquals(worker.getGeneratedGIFsFolderName(), expectedRoot + "gifsLocation/");
    }
}
