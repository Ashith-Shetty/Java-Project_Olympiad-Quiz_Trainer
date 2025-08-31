package project;

public class TestClass {

	public static void main(String[] args) {
		String s = "a) 8 square units b) 12 square units c) 16 square units d) 24 square units";
		
		 String[] options = s.split("[a-z]\\)");
		 for(String k : options)
		 {
			 //System.out.println(k);
		 }
		 String c = "b) 3";
		 c = c.substring(3);
		 System.out.println(c);
	}

}
