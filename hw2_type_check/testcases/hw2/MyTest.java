class Main {                                                                                                                          
        public static void main(String[] a){
	    A a;
	    B b;
	    C c;
	    D d;
                System.out.println(d.run());
      		a = new D();
		
				
        }                                                                                                                             
}                                                                                                                                     
                                                                                                                                      
class A {                                                                                                                             
        public int run() {                                                                                                            
                int x;                                                                                                                
                x = 1;                                                                                                                
                return x;                                                                                                             
        }                                                                                                                             
}

class B extends A{
    boolean y;
}

class C extends B {
    int y;
}

class D extends B {
}
