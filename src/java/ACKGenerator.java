import java.io.*;

public class ACKGenerator {
		private static ACKGenerator ackGenerator = new ACKGenerator( );

	   /* A private Constructor prevents any other
	    * class from instantiating.
	    */
	   private ACKGenerator() { }

	   /* Static 'instance' method */
	   public static ACKGenerator getInstance( ) {
	      return ackGenerator;
	   }

	   /* Other methods protected by singleton-ness */
	   protected static int generateACK(int lengthOfPacket) {
		   return lengthOfPacket+1; 
	   }

}
