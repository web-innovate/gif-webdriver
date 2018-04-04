package com.github.bogdanlivadariu.gifwebdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.List;
import java.util.Set;

/**
 * Wrapper over the {@link WebDriver} that allows taking screenshots during test execution
 * and generate a gif based on the set of screenshots taken during test execution
 */
public class GifWebDriver extends AbstractWebDriverEventListener implements WebDriver, JavascriptExecutor {
    private final WebDriver driver;

    private final GifScreenshotWorker gifScreenshotWorker;

    public GifWebDriver(WebDriver driver) {
        EventFiringWebDriver handle = new EventFiringWebDriver(driver);
        handle.register(this);
        this.driver = handle;
        gifScreenshotWorker = new GifScreenshotWorker(this.driver);
    }

    public GifWebDriver(WebDriver driver, GifScreenshotWorker gifScreenshotWorker) {
        EventFiringWebDriver handle = new EventFiringWebDriver(driver);
        handle.register(this);
        this.driver = handle;
        this.gifScreenshotWorker = gifScreenshotWorker;
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        getGifScreenshotWorker().takeScreenshot();
        super.beforeClickOn(element, driver);
    }

    @Override
    public void get(String s) {
        driver.get(s);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        getGifScreenshotWorker().takeScreenshot();
        driver.close();
    }

    @Override
    public void quit() {
        getGifScreenshotWorker().takeScreenshot();
        getGifScreenshotWorker().createGif();
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }

    @Override
    public Object executeScript(String s, Object... objects) {
        return ((JavascriptExecutor) driver).executeScript(s, objects);
    }

    @Override
    public Object executeAsyncScript(String s, Object... objects) {
        return ((JavascriptExecutor) driver).executeAsyncScript(s, objects);
    }

    public GifScreenshotWorker getGifScreenshotWorker() {
        return gifScreenshotWorker;
    }
}

