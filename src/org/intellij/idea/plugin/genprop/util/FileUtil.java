package org.intellij.idea.plugin.genprop.util;

import java.io.*;

/**
 * Utility methods for file IO.
 *
 * @author Claus Ibsen
 */
public class FileUtil {

    /**
     * Private constructor, as only static methods allowed.
     */
    private FileUtil() {
    }

    /**
     * Reads the files content and return it as a String.
     * <p/>Uses the contect classloader from the current thread.
     *
     * @param resource the resouce name. Will lookup using the classpath.
     * @return the content
     * @throws IOException error reading the file.
     */
    public static String readFile(String resource) throws IOException {
        BufferedInputStream in = new BufferedInputStream(FileUtil.class.getResourceAsStream(resource));
        return readFileContent(in);
    }

    /**
     * Reads the files content and return it as a String.
     *
     * @param file the file to load.
     * @return the content of the file.
     * @throws IOException error reading file.
     */
    public static String readFile(File file) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        return readFileContent(in);
    }

    /**
     * Reads the files content and return it as a String.
     *
     * @param in the file input stream.
     * @return the file content.
     * @throws IOException error reading the file.
     */
    private static String readFileContent(InputStream in) throws IOException {
        StringBuffer buf = new StringBuffer();
        for (int i = in.read(); i != -1; i = in.read()) {
            buf.append((char) i);
        }
        return buf.toString();
    }

    /**
     * Saves the content to the file.
     *
     * @param filename absolute filename of the new file.
     * @param content  the content of the file to be saved.
     * @throws IOException any error saving the content to the file.
     */
    public static void saveFile(String filename, String content) throws IOException {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(filename);
            bos = new BufferedOutputStream(fos);
            bos.write(content.getBytes(), 0, content.length());
        } finally {
            if (bos != null) bos.close();
            if (fos != null) fos.close();
        }
    }

    /**
     * Get's the file extension from the given filename
     *
     * @param filename filename.
     * @return the extension, null if the file does not have an extension (could be a directory).
     */
    public static String getFileExtension(String filename) {
        File file = new File(filename);
        int pos = file.getName().lastIndexOf(".");
        if (pos == -1)
            return null; // no extension

        return filename.substring(pos + 1); // return the extension
    }

    /**
     * Does the given filename already exists?
     *
     * @param filename filename to check if exists.
     * @return true if the file exists, false if not.
     */
    public static boolean existsFile(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    /**
     * Returns the last part of the filename without the directory seperators.
     *
     * @param filename the absolute filename.
     * @return the filename without directory information.
     */
    public static String stripFilename(String filename) {
        int pos = filename.lastIndexOf(File.separatorChar);
        if (pos != -1)
            return filename.substring(pos + 1);
        else
            return filename;
    }

}
