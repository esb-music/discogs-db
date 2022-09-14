package thm.spg;
/*
 Generate json files from sqlite database of discogs
 and validate them
 © 2022, Burkhardt Renz, THM, no rights reserved
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.SchemaException;
import net.jimblackler.jsonschemafriend.SchemaStore;
import net.jimblackler.jsonschemafriend.Validator;

import static java.lang.System.exit;

public class discogs {

  /*
  main
   */
  public static void main(String[] args) {
    System.out.println("This is discogs Rev 1.0 2022-09-16");

    // Parameter check
    boolean error = false;
    switch (args.length) {
      case 1:
        if (!args[0].equals("discogs")) {
          error = true;
        }
        break;
      case 2:
        ArrayList<String> kind = new ArrayList<>(Arrays.asList(
            "albums", "musicians", "tracks", "propagate"));
        if (!(kind.contains(args[0]) || keys().contains(args[1]))) {
          error = true;
        }
        break;
      case 3:  
        if (!(args[0].equals("validate"))){
          error = true;
        }
        break;
      default:
        error = true;
    }
    if (error) {
      usage();
      exit(-1);
    }  
    
    switch (args[0]) {
      case "discogs":
        genDiscogs();
        System.out.println("=> generated file 'discogs.json'");
        break;
      case "albums":
        genAlbums(args[1]);
        System.out.println("=> generated file '" + args[1] + "-album.json'");
        break;
      case "musicians":
        genMusicians(args[1]);
        System.out.println("=> generated file '" + args[1] + "-musicians.json'");
        break;
      case "tracks":
        genTracks(args[1]);
        System.out.println("=> generated file '" + args[1] + "-tracks.json'");
        break;
      case "propagate":
        propagateMusicianToPerforming(args[1]);
        break;
      case "validate":
        validate(args[1], args[2]);
        break;
      default:
        System.out.println(args[0]);
    }

  }

  /*
  Generates discogs.json
   */
  private static void genDiscogs() {
    Connection con;
    Statement stmt;
    ResultSet rs;

    try {
      // retrieve data from table discogs
      con = DriverManager.getConnection("jdbc:sqlite:discogs.sqlite");
      stmt = con.createStatement();
      rs = stmt.executeQuery("select json_group_array(json_object(" +
          "'key', key, 'name', name, 'img', img, 'alt', alt, 'wiki', wiki, 'active', active)) " +
          "from discogs " +
          "where active >= 0");

      // Open output file
      FileWriter fileWriter = new FileWriter("discogs.json");
      PrintWriter printWriter = new PrintWriter(fileWriter);

      // Write json array
      if (rs.next()) printWriter.print(rs.getString(1));

      // Close output file
      printWriter.close();
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  /*
  Generates key-albums.json for the given key
   */
  private static void genAlbums(String key) {
    Connection con;
    Statement stmt;
    ResultSet rs;

    try {
      // retrieve data from table albums and view musician_on_album
      con = DriverManager.getConnection("jdbc:sqlite:discogs.sqlite");
      stmt = con.createStatement();
      rs = stmt.executeQuery("select json_object(" +
          "'key', key, 'aid', aid, 'title', title, 'subtitle', subtitle, 'img', img," +
          "'alt', alt, 'wiki', wiki, 'label', label, 'released', released, " +
          "'recorded', recorded, 'producer', producer, 'venue', venue, 'length', length," +
          "'musicians', json_group_array(json(json_object('mid', mid, 'name', name)))) " +
          "from albums left join musician_on_album using (key, aid) where key = '" + key + "'" +
          "group by aid");

      // Write result in vector
      Vector<String> jsonStrings = new Vector<>();
      while (rs.next()) {
        jsonStrings.add(rs.getString(1));
      }

      // Open output file
      FileWriter fileWriter = new FileWriter(key + "-albums.json");
      PrintWriter printWriter = new PrintWriter(fileWriter);

      // Write jsonStrings as a json array
      printWriter.print("[\n");
      printWriter.print(String.join(",\n", jsonStrings.toArray(new String[0])));
      printWriter.print("\n]");

      // Close output file
      printWriter.close();
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  /*
  Generates key-musicians.json for the given key
   */
  private static void genMusicians(String key) {
    Connection con;
    Statement stmt;
    ResultSet rs;

    try {
      // retrieve data from table albums
      con = DriverManager.getConnection("jdbc:sqlite:discogs.sqlite");
      stmt = con.createStatement();
      rs = stmt.executeQuery("select json_object('mid', mid, 'name', name, 'info', info, " +
          "'img', img, 'alt', alt, 'wiki', wiki, 'bdate', bdate, 'ddate', ddate), mid " +
          "from musicians join " +
          "(select mid, count(distinct aid) as albumcnt from performing where key = '" + key + "' group by mid) m " +
          "using (mid) order by albumcnt desc, mid" +
          "");

      // Write result in vector
      Vector<String> jsonStrings = new Vector<>();
      while (rs.next()) {
        jsonStrings.add(rs.getString(1));
      }

      // Open output file
      FileWriter fileWriter = new FileWriter(key + "-musicians.json");
      PrintWriter printWriter = new PrintWriter(fileWriter);

      // Write jsonStrings as a json array
      printWriter.print("[\n");
      printWriter.print(String.join(",\n", jsonStrings.toArray(new String[0])));
      printWriter.print("\n]");

      // Close output file
      printWriter.close();
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  /*
 Generates key-tracks.json for the given key
  */
  private static void genTracks(String key) {
    Connection con;
    Statement stmt;
    ResultSet rs;

    try {
      // retrieve data from table albums
      con = DriverManager.getConnection("jdbc:sqlite:discogs.sqlite");
      stmt = con.createStatement();
      rs = stmt.executeQuery("select key, aid, " +
          "json_object('key', key, 'aid', aid, 'title', title, 'tracks', json_group_array(json(track))) " +
          "from albums natural join" +
          "(select key, aid, " +
          "json_object('tno', tno, 'title', title, 'length', length, " +
          "'performing', json(performing)) as track from tracks natural join " +
          "(select key, aid, tno, " +
          "json_group_array(json(json_object('name', name, 'instruments', instruments))) " +
          "as performing " +
          "from performing join musicians using(mid) " +
          "where key = '" + key + "' group by aid, tno) as P) as T group by aid");

      // Write result in vector
      Vector<String> jsonStrings = new Vector<>();
      while (rs.next()) {
        jsonStrings.add(rs.getString(3));
      }

      // Open output file
      FileWriter fileWriter = new FileWriter(key + "-tracks.json");
      PrintWriter printWriter = new PrintWriter(fileWriter);

      // Write jsonStrings as a json array
      printWriter.print("[\n");
      printWriter.print(String.join(",\n", jsonStrings.toArray(new String[0])));
      printWriter.print("\n]");

      // Close output file
      printWriter.close();
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }

  private static void propagateMusicianToPerforming(String key) {
    Connection con;
    Statement stmt;
    ResultSet rs;
    PreparedStatement insertStmt;

    try {
      con = DriverManager.getConnection("jdbc:sqlite:discogs.sqlite");
      stmt = con.createStatement();
      insertStmt = con.prepareStatement("insert into performing(key, aid, tno, mid, instruments) " +
          "values (?, ?, ?, ?, ?)");

      // Collect input
      Scanner input1 = new Scanner(System.in);
      System.out.print("Enter album id (aid): ");
      int aid = input1.nextInt();
      System.out.print("Enter musician id (mid): ");
      int mid = input1.nextInt();
      System.out.print("Enter instruments: ");
      Scanner input2 = new Scanner(System.in);
      String instruments = input2.nextLine();
      System.out.println(instruments);

      int tcnt = 0;

      // Get number of tracks
      rs = stmt.executeQuery("select count(*) from tracks " +
          "where key = '" + key + "' and aid = " + aid);
      while (rs.next()) {
        tcnt = rs.getInt(1);
        System.out.println("Number of tracks: " + tcnt);
      }
      for (int tno = 1; tno <= tcnt; tno++) {
        insertStmt.setString(1, key);
        insertStmt.setInt(2, aid);
        insertStmt.setInt(3, tno);
        insertStmt.setInt(4, mid);
        insertStmt.setString(5, instruments);
        insertStmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void validate(String jsonFilename, String schemaFilename) {
    System.out.println("Validates whether " + jsonFilename +
        " conforms to " + schemaFilename);

    try {
      SchemaStore schemaStore = new SchemaStore();
      Schema schema = schemaStore.loadSchema(new File(schemaFilename));
      new Validator().validate(schema, new File(jsonFilename));
      System.out.println(jsonFilename + " conforms to " + schemaFilename);
    } catch (SchemaException | IOException e) {
      e.printStackTrace();
    }
  }
  
  private static ArrayList<String> keys() {
    Connection con;
    Statement stmt;
    ResultSet rs;
    
    ArrayList<String> result = new ArrayList<>();
    
    try{
      con = DriverManager.getConnection("jdbc:sqlite:discogs.sqlite");
      stmt = con.createStatement();
      rs = stmt.executeQuery("select key from discogs");
      while (rs.next()) {
        result.add(rs.getString(1));
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static void usage() {
    System.out.println("Usage:");
    System.out.println("  java discogs.jar discogs => generates discogs.json");
    System.out.println("  java discogs.jar albums key => generates key-albums.json");
    System.out.println("  java discogs.jar musicians key => generates key-musicians.json");
    System.out.println("  java discogs.jar tracks key => generates key-tracks.json");
    System.out.println("  java discogs.jar propagate key => propagates musician and instrument to all tracks of an album");
    System.out.println("  java discogs.jar validate json-file json-schema => validates json-file with respect to json-schema");
  }

}



