package muche.m4a2mp3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class App
{

  private static ArrayList<File> listMP4Files(File baseDir)
  {
    ArrayList<File> allFiles = new ArrayList<File>();

    File[] dirFiles = baseDir.listFiles();
    for (File file : dirFiles)
      if (file.isDirectory())
        allFiles.addAll(listMP4Files(file));
      else if (file.getAbsolutePath().toLowerCase().endsWith(".m4a"))
        allFiles.add(file);
    return allFiles;
  }

  private static boolean checkFFMPEG()
  {
    try
    {
      Process process = Runtime.getRuntime().exec("ffmpeg -version");
      return (process.waitFor() == 0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public static void main(String[] args) throws Exception
  {
    System.out.println("========== M4A-2-MP3 ==========");
    System.out.println("Christoph Muche - www.cmuche.de");
    System.out.println("===============================");

    if (args.length < 1)
      throw new Exception("No parameters!");

    System.out.println("Checking FFMPEG...");
    if (!checkFFMPEG())
      throw new Exception("FFMPEG is not installed or not available in the path!");
    System.out.println("FFMPEG is installled.");

    File baseDir = new File(args[0]);

    if (!baseDir.exists() || !baseDir.isDirectory())
      throw new Exception("Directory does not exist or is not a directory!");

    System.out.println("Directory: " + baseDir);
    System.out.println("Searching for M4A files...");
    ArrayList<File> convertFiles = listMP4Files(baseDir);

    System.out.println("Found " + convertFiles.size() + " files!");
    for (Object file : convertFiles)
      System.out.println(" -> " + file);

    System.out.println("Converting files...");
    int i = 0;
    for (File fileM4A : convertFiles)
    {
      i++;

      File fileMP3 = new File(fileM4A.getAbsolutePath().substring(0, fileM4A.getAbsolutePath().lastIndexOf('.')) + ".mp3");

      if (fileMP3.exists())
      {
        System.out.println("Skipping " + i + "/" + convertFiles.size() + ": \"" + fileM4A + "\" because MP3 file already exists.");
        continue;
      }

      System.out.print("Converting " + i + "/" + convertFiles.size() + ": \"" + fileM4A + "\" to \"" + fileMP3 + "\"...");

      Process process = Runtime.getRuntime().exec(new String[]
      {
        "ffmpeg", "-nostats", "-loglevel", "panic", "-i", fileM4A.getAbsolutePath(), "-acodec", "libmp3lame", fileMP3.getAbsolutePath()
      });
      int exitCode = process.waitFor();
      System.out.println(" Returned " + exitCode);

      if (exitCode != 0)
        throw new Exception("FFMPEG returned " + exitCode);

      System.out.println("Deleting file " + fileM4A + " ...");
      fileM4A.delete();
    }

    System.out.println("[X] Done.");
  }
}
