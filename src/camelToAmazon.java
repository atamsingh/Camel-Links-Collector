import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class camelToAmazon {

	public camelToAmazon() {
		// TODO Auto-generated constructor stub
	}
	
	public static ItemDetail fixLine(String line){
		String[] fancySymbolSplit = line.split("\\|\\|\\|///\\|\\|\\|");
		String ftpData = "NULL";
		if(fancySymbolSplit.length > 1){
			ftpData = fancySymbolSplit[1]; //here is the detail attribute
			if(ftpData.startsWith("{")){
				JSONObject obj = new JSONObject(ftpData);
				JSONArray itemFtpDetail = obj.getJSONArray("itemData");
	
				ftpData = "";
				for (int i = 0; i < itemFtpDetail.length(); i++)
				{
				    String buyingPrice = itemFtpDetail.getJSONObject(i).getDouble("buyingPrice")+"";
				    String asinNum = itemFtpDetail.getJSONObject(i).getString("ASIN");
				    ftpData += asinNum + "," + buyingPrice+",";
				}
			}
		}
		
		int placeOfFirstComma = fancySymbolSplit[0].indexOf(',');
		//System.out.println(fancySymbolSplit[0]);
		String categoryName = fancySymbolSplit[0].substring(0,placeOfFirstComma); //here is the category attribute
		String stuffToBeSorted = fancySymbolSplit[0].substring(placeOfFirstComma);
		
		
		
		String[] stuffToBeSortedArray = stuffToBeSorted.split("http://");
		String webpage = "http://"+ stuffToBeSortedArray[1];  //here is the webpage attribute
		
		
		
		//System.out.println(stuffToBeSortedArray[0]);
		stuffToBeSortedArray = stuffToBeSortedArray[0].split("CDN\\$");
		String price = "NULL";
		if(stuffToBeSortedArray.length > 1){
			price = stuffToBeSortedArray[1].replaceAll("\\r", ""); //here is the price attribute
		}
		
		
		String name = stuffToBeSortedArray[0]; //here if the name attribute
		
		
		name = name.replaceAll("-|,|-","");
		price = price.replaceAll(" |,","");
		webpage = webpage.replaceAll(" |,","");
		
		
		
		
		ItemDetail thisItemDetail = new ItemDetail(categoryName, name, price, webpage, ftpData);
		return thisItemDetail;
	}
	
	public static ItemDetail itemPageDetailCollector(String itemWebpage) throws HttpStatusException{
		Document itemLinkDoc = null;
		ItemDetail currentItem = new ItemDetail();
		try {
			itemLinkDoc = Jsoup.connect(itemWebpage).timeout(100*1000).get();
			String mainCategory = itemLinkDoc.select("#nav-subnav").select("a").first().text();
			String name = itemLinkDoc.select("#productTitle").text();
			String price = itemLinkDoc.select("#priceblock_ourprice").text();
			String details = itemLinkDoc.select("#fbt_item_data").text();
			
			currentItem = new ItemDetail(mainCategory, name, price, itemWebpage, details);

			
			String stringToFix = "";
			stringToFix += (currentItem.category);
			stringToFix += (",");
			stringToFix += (currentItem.name);
			stringToFix += (",");
			stringToFix += (currentItem.price);
			stringToFix += (",");
			stringToFix += (currentItem.website);
			stringToFix += (",");
			stringToFix += ("|||///|||"+currentItem.details);
			stringToFix += ("\n");
			
			currentItem = fixLine(stringToFix);
			
		} catch (HttpStatusException h){
			System.out.println("Error connecting to " + itemWebpage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return currentItem;
	}

	public static void main(String[] args) throws HttpStatusException {
		// TODO Auto-generated method stub
		
		
		try {
			
			FileReader fileReader = new FileReader("camelLinks.csv");
			BufferedReader reader = new BufferedReader(fileReader);
			
			//ArrayList<ItemDetail> allItems = new ArrayList<ItemDetail>();
			
			String thisBeingRead;
			FileWriter fileWriter = null;
			fileWriter = new FileWriter("Camel-Amazon.csv");
			//fileWriter.append("Category,Name,Price,Webpage,ASIN,Price,ASIN,Price,ASIN,Price");
			//fileWriter.append("\n");
			
			try {
				while((thisBeingRead = reader.readLine()) != null){
					System.out.println(thisBeingRead);
					
					if(thisBeingRead.length() > 27){
						String link = thisBeingRead;
						String toAmazonlink = link.substring(27);
						String[] makingString = toAmazonlink.split("/");
						System.out.println(makingString[0]); //productName
						
						String[] productCode = makingString[2].split("\\?");
						System.out.println(productCode[0]); //productCode
						
						String amazonLink = "http://www.amazon.com/"+ makingString[0] + "/dp/" + productCode[0];
						System.out.println(amazonLink);
						
						/*
						ItemDetail current = itemPageDetailCollector(amazonLink);
						
						if(current.name != null){
							allItems.add(current);
							System.out.println(current);
						}else{
							int retries = 0;
							while(current.name == null && retries < 7){
								retries++;
								try{
									TimeUnit.SECONDS.sleep(3);
									 System.out.println("Waiting and trying again");
								}catch(Exception e)
								{
								   System.out.println("Exception caught");
								}
								current = itemPageDetailCollector(thisBeingRead);
								System.out.println("Checking if successful....");
							}
							if(retries < 7){
								System.out.println("Scraped properly");
								allItems.add(current);
								System.out.println(current);
							}else{
								current = new ItemDetail("NULL","NULL","NULL",thisBeingRead,"NULL");
								System.out.println(current);
								allItems.add(current);
							}
						}
						System.out.println("--finished line");
						System.out.println();
						*/
						
						fileWriter.append(amazonLink);
						/*
						fileWriter.append(",");
						fileWriter.append(current.name);
						fileWriter.append(",");
						fileWriter.append(current.price);
						fileWriter.append(",");
						fileWriter.append(current.website);
						fileWriter.append(",");
						fileWriter.append(current.details);
						*/
						fileWriter.append("\n");
						
						System.out.println("--written to file");
						System.out.println();
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try{
					fileWriter.flush();
					fileWriter.close();
				}catch(IOException e){
					System.out.println("Error while flushing/closing fileWriter !!!");
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
