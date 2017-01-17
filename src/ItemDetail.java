
public class ItemDetail {
	String category;
	String name;
	String price;
	String website;
	String details;
	

	public ItemDetail(String c, String n, String p, String w, String d) {
		// TODO Auto-generated constructor stub
		this.category = c;
		this.name = n;
		this.website = w;
		this.price = p;
		this.details = d;
	}
	
	public ItemDetail(){}

	public String toString(){
		return (this.category + " - " + this.name + "\n   " + this.price+ "\n   " + this.website + "\n   "+ this.details);
	}

	public ItemDetail fixLine(String stringToFix) {
		// TODO Auto-generated method stub
		return null;
	}
}
