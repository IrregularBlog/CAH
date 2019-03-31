import java.io.*;
import java.net.*;
import java.util.*;

public class DownloadWhitecards {
   private static boolean isRedirected( Map<String, List<String>> header ) {
      for( String hv : header.get( null )) {
         if(   hv.contains( " 301 " )
            || hv.contains( " 302 " )) return true;
      }
      return false;
   }
   
   public static void main( String[] args ) throws Throwable
   {
      String link =
         "https://raw.githubusercontent.com/IrregularBlog/CAH/master/whitecards.txt";
      String            fileName = "whitecards.txt";
      URL               url  = new URL( link );
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      Map< String, List< String >> header = http.getHeaderFields();
      while( isRedirected( header )) {
         link = header.get( "Location" ).get( 0 );
         url    = new URL( link );
         http   = (HttpURLConnection)url.openConnection();
         header = http.getHeaderFields();
      }
      InputStream  input  = http.getInputStream();
      byte[]       buffer = new byte[4096];
      int          n      = -1;
      OutputStream output = new FileOutputStream( new File( "whitecards.txt" ));
      while ((n = input.read(buffer)) != -1) {
         output.write( buffer, 0, n );
      }
      output.close();
   }
}
