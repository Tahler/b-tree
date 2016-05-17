package edu.neumont.csc360.btree.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class File {
    private RandomAccessFile randomAccessFile;

    public static void create(String name) {
        java.io.File file = new java.io.File(name);
        try {
            boolean success = file.createNewFile();
            if (!success) {
                throw new RuntimeException("File already exists with name " + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in File::create(String)");
        }
    }

    public static File open(String name) {
        try {
            File file = new File();
            file.randomAccessFile = new RandomAccessFile(name, "rw"); // rws, rwd
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("No file exists with name " + name);
        }
    }

    public static void delete(String name) {
        java.io.File file = new java.io.File(name);
        boolean deleted = file.delete();
        if (!deleted) {
            throw new RuntimeException("Could not delete file with name " + name + ". " +
                    "Are you sure it exists?");
        }
    }

    public void close() {
        try {
            this.randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in File::close()");
        }
    }

    /**
     * Writes the byte to the file at the specified location.
     * @param bite The byte (value must be between 0 and 255) to write.
     * @param location The offset at which to write the byte.
     */
    public void write(int bite, long location) {
        try {
            this.randomAccessFile.seek(location);
            this.randomAccessFile.write(bite);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in File::write(int, long)");
        }
    }

    /**
     * Writes a 4-byte integer to the file at the specified location.
     * @param integer The integer (value must be between 0 and 255) to write.
     * @param location The offset at which to write the byte.
     */
    public void writeInt(int integer, long location) {
        try {
            this.randomAccessFile.seek(location);
            this.randomAccessFile.writeInt(integer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in File::writeInt(int, long)");
        }
    }

    /**
     * Writes the buffer starting at the location.
     * @param buffer
     * @param location The byte offset to begin writing.
     */
    public void write(int[] buffer, long location) {
        try {
            this.randomAccessFile.seek(location);
            for (int bite : buffer) {
                this.randomAccessFile.write(bite);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in File::write(int[], long)");
        }
    }

    /**
     * Reads the byte at location.
     * @param location The offset to read from.
     * @return An int with a value between 0 and 255.
     */
    public int readByte(long location) {
        try {
            this.randomAccessFile.seek(location);
            return this.randomAccessFile.read();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in File::readByte(long)");
        }
    }

    /**
     * Reads the 4-byte integer at location.
     * @param location The offset to read from.
     * @return The integer read from location.
     */
    public int readInt(long location) {
        try {
            this.randomAccessFile.seek(location);
            return this.randomAccessFile.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in File::readByte(long)");
        }
    }

    /**
     * Reads the buffer starting at location for length bytes.
     * @param location The starting point for the file to read.
     * @param length The number of bytes to be read, starting at location.
     * @return The read buffer. An int[] is used because Java has no unsigned bytes.
     */
    public int[] read(long location, int length) {
        try {
            int[] bytesRead = new int[length];
            this.randomAccessFile.seek(location);
            for (int i = 0; i < length; i++) {
                bytesRead[i] = this.randomAccessFile.read();
            }
            return bytesRead;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in File::read(long, int)");
        }
    }
}
