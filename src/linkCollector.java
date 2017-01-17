import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class linkCollector {

	public linkCollector() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		ArrayList<String> subSellerLinks = new ArrayList<String>();
		try {
		
			Document doc = Jsoup.connect("http://camelcamelcamel.com/products").timeout(10*1000).get();
			Elements links = doc.select("ul.nobullets").select("a[href]");
			//System.out.println(doc);
			//ArrayList<String> bestsellerLinks = new ArrayList<String>();
			
			
			//int categoryCount = 1;
			for(Element aLink : links){
				
				String s  = aLink.attr("href");
				
				System.out.println(s);
				System.out.println();
				
				s = "http://camelcamelcamel.com/"+s+"&p=";
				for(int i  = 1; i <=100; i++){
					Document sublinkDoc = Jsoup.connect(s+i).timeout(10*1000).get();
					Elements productLinks = sublinkDoc.select("div.product_title").select("a[href]");
					for(Element eachProduct : productLinks){
						subSellerLinks.add(eachProduct.attr("href"));
					}
					System.out.println("scraped page "+i);
				}
			}
			System.out.println(subSellerLinks.size());
			System.out.println(subSellerLinks);
		}
		catch(Exception e){
			System.out.println("Error!!!");
		}
		
		FileWriter fileWriter = null;
		try{
			fileWriter = new FileWriter("camelLinks.csv");
			for(String current : subSellerLinks){
				fileWriter.append(current);
				fileWriter.append("\n");
			}
			System.out.println("CSV file was created successfully !!!");
			
		}catch(Exception e){
			System.out.println("Error in CsvFileWriter !!!");
		}finally{
			try{
				fileWriter.flush();
				fileWriter.close();
			}catch(IOException e){
				System.out.println("Error while flushing/closing fileWriter !!!");
			}
		}
			
			

	}

}
