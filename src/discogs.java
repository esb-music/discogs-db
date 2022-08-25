/*
 Generate json files from sqlite database of discogs
 © 2022, Burkhardt Renz, THM, no rights reserved
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Vector;

public class discogs {

  /*
  main
  Caveat: no parameter checks!
   */
  public static void main(String[] args) {
    System.out.println("This is discogs Rev 1.0 2022-10-01");
    System.out.println("Usage:");
    System.out.println("  java discogs.jar discogs => generates discogs.json");
    System.out.println("  java discogs.jar albums key => generates key-albums.json");
    System.out.println("  java discogs.jar musicians key => generates key-musicians.json");
    System.out.println("  java discogs.jar tracks key => generates key-tracks.json");


    switch (args[0]) {
      case "discogs":
        genDiscogs();
        System.out.println("\ngenerated file 'discogs.json'");
        break;
      case "albums":
        genAlbums(args[1]);
        System.out.println("\ngenerated file '" + args[1] + "-album.json'");
        break;
      case "musicians":
        genMusicians(args[1]);
        System.out.println("\ngenerated file '" + args[1] + "-musicians.json'");
        break;
      case "tracks":
        genTracks(args[1]);
        System.out.println("\ngenerated file '" + args[1] + "-tracks.json'");
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
          "'key', key, 'name', name, 'img', img, 'alt', alt, 'wiki', wiki)) from discogs");

      // Open output file
      FileWriter fileWriter = new FileWriter("discogs.json");
      PrintWriter printWriter = new PrintWriter(fileWriter);

      // Write json array
      if (rs.next()) printWriter.print(rs.getString(1));

      // Close output file
      printWriter.close();
    } catch (SQLException | IOException e) {
      throw new RuntimeException(e);
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
      throw new RuntimeException(e);
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
          "from musicians where mid in (select mid from tracks where key = '" + key + "')");

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
      throw new RuntimeException(e);
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
      throw new RuntimeException(e);
    }
  }


}



