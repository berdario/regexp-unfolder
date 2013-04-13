import static regexp_unfolder.core.unfold;

public class UnfolderExample{
	public static void main(String[] args){
		@SuppressWarnings("unchecked")
		Iterable<String> strings = unfold("a+b+");
		for (String s : strings){
			System.out.println(s);
		}
	}
}
