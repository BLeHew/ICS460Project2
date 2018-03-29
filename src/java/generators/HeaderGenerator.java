package generators;

public class HeaderGenerator {
		private static HeaderGenerator ackGenerator = new HeaderGenerator( );

	   /* A private Constructor prevents any other
	    * class from instantiating.
	    */
	   private HeaderGenerator() { }

	   /* Static 'instance' method */
	   public static HeaderGenerator getInstance( ) {
	      return ackGenerator;
	   }

	   /* Other methods protected by singleton-ness */
	   protected static int generateACK(int lengthOfPacket) {
		   return lengthOfPacket+1;
	   }
	   public static long checksum(byte[] buf, int length) {
	       int i = 0;
	       long sum = 0;
	       while (length > 0) {
	           sum += (buf[i++]&0xff) << 8;
	           if ((--length)==0) break;
	           sum += (buf[i++]&0xff);
	           --length;
	       }

	       return (~((sum & 0xFFFF)+(sum >> 16)))&0xFFFF;
	   }
}
