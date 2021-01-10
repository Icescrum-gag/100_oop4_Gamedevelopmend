/*

https://github.com/xerial/sqlite-jdbc/releases

Linux:
javac DEMOsqlite.java
java -classpath ".:sqlite-jdbc-3.32.3.2.jar" DEMOsqlite

Windows:
javac DEMOsqlite.java
java -classpath ".;sqlite-jdbc-3.32.3.2.jar" DEMOsqlite
*/
package sql;
import org.sqlite.SQLiteConnection;
import java.sql.*;

public class DEMOsqlite {

  public static void main( String args[] ) {

   SQLiteConnection c = null;
   Statement stmt = null;
   try {

      c = (SQLiteConnection)DriverManager.getConnection("jdbc:sqlite:db/fragen-spiel-bauer.db");
      c.setAutoCommit(false);
      System.out.println("fragen-spiel-bauer.db: opened  successfully");

      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM FRAGEN;" );
      
      while ( rs.next() ) {
         int id = rs.getInt("id");
         String  fragentext = rs.getString("fragentext");
         String  loesungstext = rs.getString("loesungstext");
         int punkte  = rs.getInt("punkte");
         
         System.out.println( "ID = " + id );
         System.out.println( "FRAGENTEXT = " + fragentext );
         System.out.println( "LÖSUNGSTEXT = " + loesungstext );
         System.out.println( "PUNKTE = " + punkte );
         System.out.println("-------------------------------------------");
         System.out.println();
         
      }
      rs.close();
      stmt.close();
      c.close();
   } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
   }
   System.out.println("Operation done successfully");
  }
}
