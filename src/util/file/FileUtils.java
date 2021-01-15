package util.file;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    private final static String NULL_ARGUMENT_ERROR = "Argument cannot be null";


    private FileUtils() {
    }

    //USER INTERFACE
    public static String choosePath() {
        String path = getWorkingDirectory();
        return choosePath(path);
    }

    public static String choosePath(String dir) {
        return choosePath(dir, JFileChooser.FILES_AND_DIRECTORIES, null);
    }

    public static String choosePath(String dir, FileFilter filter) {
        return choosePath(dir, JFileChooser.FILES_AND_DIRECTORIES, filter);
    }

    public static String choosePath(String dir, int mode, FileFilter filter) {
        final JFileChooser fc = new JFileChooser(); //Open the chooser
        //Set settings
        fc.setFileSelectionMode(mode);
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        //Open the dialog box
        if(dir != null) fc.setCurrentDirectory(new File(dir));
        fc.showOpenDialog(null);
        if(fc.getSelectedFile() == null) return null; //error checking
        //return
        return getPath(fc.getSelectedFile());
    }


    // CREATING AND READING
    public static boolean createDir(String path) {
        File file = new File(path);
        if(!file.exists()) return file.mkdirs();
        return true;
    }

    public static boolean createFile(String path, String content) throws IOException {
        String[] splitPath = splitPath(path);
        if(splitPath[0].length() > 1) {
            if(!createDir(splitPath[0])) return false; //create the correct directory
        }
        File file = new File(path); //open file
        return createFile(file, content);
    }

    public static boolean createFile(File file, String content) throws IOException {
        boolean success = true;
        if(!file.exists()) { //if the file does not exist...
            createDir(getParent(getPath(file)));
            success = file.createNewFile(); //create file
        }
        if(content != null) {
            return success && writeFile(file, content); //write the content to the file
        }
        return success;
    }

    public static boolean createFile(File file) throws IOException {
        return createFile(file, null);
    }

    public static boolean createFile(String path) throws IOException {
        return createFile(path, null);
    }

    public static String readFileAsString(File file) throws IOException {
        return readFileAsString(getPath(file));
    }

    public static String readFileAsString(String path) throws IOException {
        StringBuilder result = new StringBuilder(); //the resulting string
        //reader
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            //creates a buffer with the contents of the file
            String line;
            while ((line = br.readLine()) != null) { //reads all the content
                result.append(line).append("\n");
            }
            //Error handling and closing below
        } catch (IOException e) {
            throw e;
        }
        return result.toString(); //return as string
    }

    //readInputStream: used to convert an input stream to a string
    public static String readInputStream(InputStream in)
            throws IOException {
        //We use a stringbuilder for easy management of the resulting string
        StringBuilder result = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in));

            //the buffered reader can read the output of the input stream
            String line; //string used to store the individual lines (separated by the "\n" symbol)
            while((line = br.readLine()) != null) { //when null, we've reached the end of the stream
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            in.close();
            if(br != null) br.close();
        }

        return result.toString(); //convert to string and return
    }

    public static Object readObject(String path) throws ClassNotFoundException, IOException {
        if(path == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);
        Object obj;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            obj = in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw e;
        }

        return obj;
    }

    public static byte[] readBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    //LOADING AND WRITING
    public static File[] listFiles(String path) {
        return listFiles(path, (dir, name) -> true); //accepts all
    }

    public static File[] listFiles(String path, FilenameFilter filter) {
        File file = getFile(path);
        if(file != null) {
            if(filter == null) return file.listFiles();
            else 			   return file.listFiles(filter);
        }

        return new File[0];
    }

    public static File[] listFiles(String path, String extension) {
        return listFiles(path, (dir, name) -> getExtension(name).equals(extension));
    }

    public static File[] listFiles(String path, String[] extensions) {
        return listFiles(path, (dir, name) -> {
            for(String e : extensions) {
                if(name.endsWith(e)) return true;
            }
            return false;
        });
    }

    public static File[] listFilesByTime(String path) {
        File[] files = listFiles(path);
        if(files == null) return null;

        Arrays.sort(files, (f1, f2) -> {
            long d1 = f1.lastModified();
            long d2 = f2.lastModified();
            return Long.compare(d1, d2);
        });

        return files;
    }

    public static List<File> listPathsRecursively(String path, String extension) throws IOException {
        List<File> files = new ArrayList<>();

        //Paths.get("he").endsWith()

        Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().toLowerCase().endsWith(extension))
                .forEach(p -> files.add(new File(p.toString())));

        return files;
    }

    public static File getFile(String path) {
        if(path == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        File file = new File(path); //open file
        if(file.isFile() || file.isDirectory()) return file; //return if file/directory

        return null;
    }

    public static boolean writeFile(File file, String content) throws IOException {
        if(!file.exists()) {
            createFile(file);
        }

        //Setup tools
        BufferedReader br;
        PrintWriter pw;

        br = new BufferedReader(new StringReader(content));
        pw = new PrintWriter(file); //will write to a file
        String line; //string used to store the individual lines (separated by the "\n" symbol)
        while((line = br.readLine()) != null) { //when null, we've reached the end of the stream
            pw.println(line);
        }

        //Close everything properly
        try {
            br.close();
            pw.close();
        } catch (IOException ignored) {}

        return true;
    }

    public static boolean writeFile(String path, String content) throws IOException {
        if(path == null || content == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);
        File file = new File(path);

        return writeFile(file, content);
    }

    public static boolean writeFile(String path, Byte[] bytes) throws IOException {
        byte[] primBytes = new byte[bytes.length];
        for(int i = 0; i < bytes.length; i++) {
            primBytes[i] = bytes[i];
        }
        return writeFile(path, primBytes);
    }

    public static boolean writeFile(String path, byte[] bytes) throws IOException {
        if(path == null || bytes == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes);
        }
        return true;
    }

    public static boolean writeObject(String path, Object obj) throws IOException {
        if(path == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        if(!FileUtils.fileExists(path)) FileUtils.createFile(path);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(obj);
        }

        return true;
    }

    public static boolean appendToFile(String path, String content) throws IOException {
        if(path == null || content == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        String currentContent = readFileAsString(path);

        content = currentContent + content; //append the new content
        return writeFile(path, content); //write the content to the file
    }

    //MOVING
    public static boolean moveFile(String source, String destination) throws IOException {
        createDir(getParent(destination));
        new File(getParent(destination)).mkdirs();
        try {
            Files.move(Paths.get(source), Paths.get(destination));
        } catch(FileAlreadyExistsException e) {
            return false;
        }
        return true;
    }

    //COPYING
    public static boolean copyFile(File source, File dest) throws IOException {
        if(source == null || dest == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        //if the destination does not exist
        if(!dest.exists()) {
            if(!createFile(dest)) return false;
        }

        //prepare streams
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024]; //create a buffer
            int length;
            while ((length = is.read(buffer)) > 0) { //write to the file
                os.write(buffer, 0, length);
            }
        }
        //Close everything

        return true;
    }

    public static boolean copyFile(String source, String dest) throws IOException {
        if(source == null || dest == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);
        //open files
        if(!fileExists(dest)) {
            if(!createFile(dest)) return false;
        }
        File fSource = new File(source);
        File fDest = new File(dest);

        return copyFile(fSource, fDest);
    }

    public static boolean copyDirectory(String source, String dest) throws IOException {
        if(source == null || dest == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        File fSource = new File(source);
        File fDest   = new File(dest);

        return copyDirectory(fSource, fDest);
    }

    public static boolean copyDirectory(File source, File dest) throws IOException {
        if(source.isDirectory()) { //if we're at a directory
            if(!dest.exists()) {
                dest.mkdirs(); //create destination
            }
            String[] files = source.list(); //an array of all the files/directories in the folder
            if(files == null) return false;
            for(String file : files) {
                File fSource = new File(source, file);
                File fDest   = new File(dest, file);

                copyDirectory(fSource, fDest); //recursive call
            }
            return true;
        } else { //if we reach a file
            return copyFile(source, dest); //copy file
        }
    }

    //RENAME
    public static String renameFile(String oldPath, String newPath) throws IOException {
        if(!getParent(oldPath).equals(getParent(newPath))) throw new IOException();
        File newFile = new File(newPath);
        return Files.move(new File(oldPath).toPath(), newFile.toPath()).toString();
    }

    public static String renameFile(File file, String newName) throws IOException {
        File newFile = new File(file.getParent(), newName);
        return Files.move(file.toPath(), newFile.toPath()).toString();
    }

    //DELETE
    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    public static boolean deleteFile(File file) {
        return file.delete();
    }

    public static boolean emptyDirectory(String path) {
        File[] files = listFiles(path);
        if(files == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        boolean success = true;
        for(File f : files) {
            if(!deleteFile(f)) success = false;
        }
        return success;
    }

    public static boolean emptyDirectory(File directory) {
        return emptyDirectory(getPath(directory));
    }

    public static void deleteDirectory(String path) throws IOException {
        Path dir = Paths.get(path);
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    //UTILS
    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    public static boolean fileExists(File file) {
        return file.exists();
    }

    public static boolean isDir(String path) {
        return new File(path).isDirectory();
    }

    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    public static String getWorkingDirectory() {
        return Paths.get("").toAbsolutePath().toString();
    }

    public static String getRelativePathFrom(String fullPath, String from) {
        if(!fullPath.contains(from)) return null;
        int index = fullPath.lastIndexOf(from) + from.length();
        return fullPath.substring(index);
    }

    public static String getPath(File file) {
        return file.getPath();
    }

    public static String getParent(String path) {
        if(path == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);
        String tPath = trimPath(path);

        if(!tPath.contains(File.separator) && !tPath.contains("/")) return "";

        return splitPath(path)[0];
    }

    public static String[] splitPath(String path) {
        if(path == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        String[] pathAndFile = {"", ""};

        String tPath = trimPath(path);

        int i;
        if((i = tPath.lastIndexOf(File.separator)) != -1 || (i = tPath.lastIndexOf("/")) != -1) { //check if the path contains a separator
            pathAndFile[0] = tPath.substring(0, i+1);
            pathAndFile[1] = tPath.substring(i+1);
        } else {
            if(path.contains(File.separator) || path.contains("/")) {
                pathAndFile[0] = tPath;
            } else {
                pathAndFile[1] = tPath;
            }
        }

        return pathAndFile;
    }

    public static String trimPath(String path) {
        if(path == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        path = path.trim();
        if(path.endsWith(File.separator) || path.endsWith("/")) {
            path = path.substring(0, path.length()-1);
        }
        return path;
    }

    public static String getName(String path) {
        if(path == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        String[] split = splitPath(path);
        String name = split[1];
        if(name.contains(".")) {
            int index = name.indexOf(".");
            name = name.substring(0, index);
        }

        return name;
    }

    public static String getName(File file) {
        return getName(getPath(file));
    }

    public static String getExtension(File file) {
        return getExtension(getPath(file));
    }

    public static String getExtension(String path) {
        String name = splitPath(path)[1];
        if(name == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);

        int index;
        if((index = name.indexOf(".")) != -1) {
            return name.substring(index);
        } else {
            return "";
        }
    }

    public static String getNameAndExtension(File file) {
        return getNameAndExtension(getPath(file));
    }

    public static String getNameAndExtension(String path) {
        String name = splitPath(path)[1];
        if(name == null) throw new IllegalArgumentException(NULL_ARGUMENT_ERROR);
        return name;
    }

    public static boolean isChildDirectory(String child, String parent) {
        child = trimPath(child);
        parent = trimPath(parent);
        return parent.startsWith(child);
    }
}
