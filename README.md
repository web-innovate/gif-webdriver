# gif-webdriver 
[![Build Status](https://travis-ci.org/web-innovate/gif-webdriver.svg?branch=master)](https://travis-ci.org/web-innovate/gif-webdriver)
[![codecov](https://codecov.io/gh/web-innovate/gif-webdriver/branch/master/graph/badge.svg)](https://codecov.io/gh/web-innovate/gif-webdriver)
[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/github/bogdanlivadariu/gif-webdriver/maven-metadata.xml.svg?style=plastic)](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22gif-webdriver%22)

## add the library to your project's pom.xml file
```xml
<dependency>
    <groupId>com.github.bogdanlivadariu</groupId>
    <artifactId>gif-webdriver</artifactId>
    <version>LATEST</version>
</dependency>
```


## sample usage
```java
    public void sampleGifDriver() {
        // initialize the driver
        WebDriver driver = new GifWebDriver(new ChromeDriver());
        //WebDriver driver = new GifWebDriver(new FirefoxDriver());
        //WebDriver driver = new GifWebDriver(new RemoteWebDriver());

        // you can use either driver webdriver/gifdriver
        GifWebDriver gifDriver = (GifWebDriver) driver;

        // screenshots will be taken implicitly on click events
        driver.findElement(By.id("someIDon a page")).click();

        // on quit the driver will generate the gifs
        driver.quit();

        // if you want to control when gifs are generated you can do it through the API
        File createdGif = gifDriver.getGifScreenshotWorker().createGif();

        //of course you can create screenshots explicitly
        gifDriver.getGifScreenshotWorker().takeScreenshot();

        // if you don't know where the screenshots are taken or where the gifs are created
        String rootFolder = gifDriver.getGifScreenshotWorker().getRootDir();

        // more options about where the gifs are created can be accomplished by using these methods
        GifScreenshotWorker gifWorker = gifDriver.getGifScreenshotWorker();
        gifWorker.setTimeBetweenFramesInMilliseconds(1000);
        gifWorker.setRootDir("some place where files screenshots and gifs will be placed");
        gifWorker.setLoopContinuously(true);

        // these properties can be set during initialization as well
        GifScreenshotWorker myPreciousWorker = new GifScreenshotWorker(
            new ChromeDriver(),
            "rootDir",
            "screenshots folder name",
            "generatedGifs folder name",
            true
        );

        WebDriver myPreciousDriver = new GifWebDriver(new ChromeDriver(), myPreciousWorker);
        // and from here it's pretty much all the same
    }
```
