import java.io.Serializable;

 public class Pair<T, U> implements Serializable {
  
     /**
	 * 
	 */
	private static final long serialVersionUID = 2800439661344818871L;
	private T firstElement;
     private U secondElement;
  
     public Pair(T t, U u) {
  
         this.firstElement= t;
         this.secondElement= u;
     }

 
     public T getFirstElement(){
    	 return this.firstElement;
     }


	public U getSecondElement() {
		return this.secondElement;
	}


	public void setFirstElement(T firstElement) {
		this.firstElement = firstElement;
	}


	public void setSecondElement(U secondElement) {
		this.secondElement = secondElement;
	}
	
	public boolean isNull(){
		if((this.firstElement.equals(null))||this.secondElement.equals(null)){
			return true;
		}else{
			return false;
		}
		}
	}
	
	
	
	
 
     
 
 
 
 
 
 
 
 
 