class LinearSearch{
    public static void main(String[] a){
	System.out.println(new LS().Start(10));
    }
}


// This class contains an array of integers and
// methods to initialize, print and search the array
// using Linear Search
class LS {
    int[] number ;
    int size ;
    
    // Invoke methods to initialize, print and search
    // for elements on the array
    public int Start(int sz){
	int aux01 ;
	int aux02 ;

	aux01 = this.Init(sz);
	return 55 ;
    }

    // initialize array of integers with some
    // some sequence
    public int Init(int sz){
	int j ;
	int k ;
	int aux01 ;
	int aux02 ;

	size = sz ;
	number = new int[sz] ;
	
	j = 1 ;
	k = size + 1 ;
	while (j < (size)) {
	    j = j + 1 ;
	}
	return 0 ;	
    }

}
