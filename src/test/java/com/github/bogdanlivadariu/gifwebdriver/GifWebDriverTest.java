package com.github.bogdanlivadariu.gifwebdriver;

import org.mockito.InOrder;
import org.openqa.selenium.*;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@Test
public class GifWebDriverTest {

    public void testConstructor() {
        GifWebDriver gif = new GifWebDriver(mock(WebDriver.class));

        assertNotNull(gif.getGifScreenshotWorker());
    }

    public void testSecondConstructor() {
        WebDriver driverMock = mock(WebDriver.class);
        GifScreenshotWorker workerMock = mock(GifScreenshotWorker.class);

        GifWebDriver gif = new GifWebDriver(driverMock, workerMock);

        assertEquals(gif.getGifScreenshotWorker(), workerMock);
    }

    public void noCastExcetionForWebDriver() {
        try {
            WebDriver driver = new GifWebDriver(mock(WebDriver.class));
        } catch (ClassCastException e) {
            fail("This is bad", e);
        }
    }

    public void onCloseTakeScreenshot() {
        GifScreenshotWorker workerMock = mock(GifScreenshotWorker.class);
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(TakesScreenshot.class));

        GifWebDriver gif = new GifWebDriver(driver, workerMock);
        GifWebDriver gifSpy = spy(gif);

        gifSpy.close();

        verify(workerMock, times(1)).takeScreenshot();
        verify(((WebDriver) gifSpy), times(1)).close();
    }

    public void onQuitTakeScreenshotCreateGif() {
        GifScreenshotWorker workerMock = mock(GifScreenshotWorker.class);
        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(TakesScreenshot.class));

        GifWebDriver gif = new GifWebDriver(driver, workerMock);
        GifWebDriver gifSpy = spy(gif);

        gifSpy.quit();

        InOrder workerOrder = inOrder(workerMock);

        workerOrder.verify(workerMock, times(1)).takeScreenshot();
        workerOrder.verify(workerMock, times(1)).createGif();

        verify(((WebDriver) gifSpy), times(1)).quit();
    }

    public void onWebElementClickTakeScreenshot() {
        GifScreenshotWorker workerMock = mock(GifScreenshotWorker.class);

        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(TakesScreenshot.class));

        WebElement wMock = mock(WebElement.class);
        WebElement cMock = mock(WebElement.class);

        when(driver.findElement(By.name("button"))).thenReturn(wMock);
        when(wMock.findElement(By.name("child"))).thenReturn(cMock);

        GifWebDriver gif = new GifWebDriver(driver, workerMock);
        GifWebDriver gifSpy = spy(gif);

        WebElement element = gifSpy.findElement(By.name("button"));
        element.click();

        verify(workerMock, times(1)).takeScreenshot();

        element.findElement(By.name("child")).click();

        verify(workerMock, times(2)).takeScreenshot();

    }

    public void onOtherWebElementActionsNoTakeScreenshot() {
        GifScreenshotWorker workerMock = mock(GifScreenshotWorker.class);

        WebDriver driver = mock(WebDriver.class, withSettings().extraInterfaces(TakesScreenshot.class));
        WebElement wMock = mock(WebElement.class);
        when(driver.findElement(By.name("button"))).thenReturn(wMock);

        GifWebDriver gif = new GifWebDriver(driver, workerMock);
        GifWebDriver gifSpy = spy(gif);

        WebElement element = gifSpy.findElement(By.name("button"));

        element.sendKeys("foo");
        element.getText();
        element.getAttribute("class");
        element.getCssValue("display");
        element.clear();
        element.submit();

        verify(workerMock, times(0)).takeScreenshot();
    }

    public void noScreenshotsDuringOtherWDApiCalls() {
        GifScreenshotWorker workerMock = mock(GifScreenshotWorker.class);

        WebDriver driver = mock(WebDriver.class,
            withSettings().extraInterfaces(TakesScreenshot.class, JavascriptExecutor.class));

        GifWebDriver gif = new GifWebDriver(driver, workerMock);
        GifWebDriver gifSpy = spy(gif);

        gifSpy.getTitle();
        gifSpy.get("http://foo");
        gifSpy.getCurrentUrl();
        gifSpy.getPageSource();
        gifSpy.getWindowHandle();
        gifSpy.getWindowHandles();
        gifSpy.switchTo();
        gifSpy.navigate();
        gifSpy.manage();

        verify(workerMock, times(0)).takeScreenshot();
    }
}
