package com.webrestaurant;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TestCase {

	private WebDriver driver;
	private String url = "https://www.webstaurantstore.com";
	private WebDriverWait wait;
	private JavascriptExecutor js;

	public static void main(String[] args) throws InterruptedException {

		TestCase obj = new TestCase();
		obj.setUp();
		obj.addToCartTest();
		obj.tearDown();

	}

	public void setUp() {
		WebDriverManager.chromiumdriver().setup();
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 10);
		driver.manage().window().maximize();
		driver.get(url);
		js = (JavascriptExecutor) driver;
	}

	public void addToCartTest() throws InterruptedException {
		
		// this is a list that will hold all the titles as Strings
		List<String> itemListTitle = new ArrayList<String>();

		// entering "stainless work table" and using the search button
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchval"))).sendKeys("stainless work table");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Search']"))).click();

		// get the number of pages
		int index = driver.findElements(By.xpath("//div[@id='paging']//ul/li")).size() - 1;
		String numOfPages = driver.findElement(By.xpath("(//div[@id='paging']//li)[" + index + "]/a")).getText();
		int numberOfPages = Integer.parseInt(numOfPages);


		// loop through all pages and add each search result title to the list
		for (int i = 1; i <= numberOfPages; i++) {

			List<WebElement> itemsList = driver.findElements(By.xpath("//a[@data-testid='itemDescription']"));

			for (WebElement item : itemsList) {
				// Store the item title in the list
				String itemTitle = item.getText();
				itemListTitle.add(itemTitle);
				
			}

			// on the last page, add the last item in stock then go the cart and empty the cart
			if (i == numberOfPages) {
				int lastItemNumber = driver.findElements(By.xpath("//input[@name='addToCartButton']")).size(); 
				wait.until(ExpectedConditions
						.elementToBeClickable(By.xpath("(//input[@name='addToCartButton'])[" + lastItemNumber + "]")))
						.click();
				wait.until(ExpectedConditions
						.elementToBeClickable(By.xpath("//a[text()='View Cart']")))
						.click();
				
				// the Thread.sleep is used to stop the execution flow of the code in order to see what is happening in the test
				Thread.sleep(5000);
				
				
				WebElement EmptyCartButton = wait
						.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Empty Cart']"))); 
				
				js.executeScript("arguments[0].click()", EmptyCartButton);
				Thread.sleep(3000);

				WebElement EmptyCartConfirm = wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath("//footer/button[contains(text(),'Empty')]")));
				js.executeScript("arguments[0].click()", EmptyCartConfirm);
				Thread.sleep(2000);

				break;
			}
			// go to next page
			wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//li[@class='inline-block leading-4 align-top rounded-r-md']/a")))
					.click();
		}
		
		// variables that will count the number of items
		int productWithTableCount = 0;
		int productWithoutTableCount = 0;
		int totalNumberOfItems = 0;
		List <String> productWithoutTableList = new ArrayList<String>();

		// looping through the list of titles to check for the presence of "table" ignoring the case
		for (int i = 0; i < itemListTitle.size(); i++) {
			String title = itemListTitle.get(i).toLowerCase();
			if(title.contains(" table")) {
				productWithTableCount++;
			}else {
				System.out.println(title);
				productWithoutTableCount++;
				productWithoutTableList.add(title);
			}
		}
		totalNumberOfItems = productWithoutTableCount + productWithTableCount;
		
		// print out the test results
		System.out.println("Total Product Items: " + totalNumberOfItems);
		System.out.println("Total Product containing 'Table' in its Title: " + productWithTableCount) ;
		System.out.println("Total Product doesn't contain 'Table' in its Title: " + productWithoutTableCount);

	}

	public void tearDown() {

		if (driver != null) {
			driver.quit();
		}

	}

}
