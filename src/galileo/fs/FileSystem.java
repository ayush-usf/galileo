/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package galileo.fs;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.dataset.Block;
import galileo.dataset.BlockMetadata;
import galileo.dataset.FileBlock;
import galileo.dataset.MetaArray;
import galileo.dataset.Metadata;
import galileo.graph.LogicalGraph;
import galileo.graph.LogicalGraphNode;
import galileo.graph.MetadataGraph;
import galileo.graph.PhysicalGraph;
import galileo.query.Query;
import galileo.serialization.SerializationException;
import galileo.serialization.Serializer;

public abstract class FileSystem {

    private static final Logger logger = Logger.getLogger("galileo");

    public static final String METADATA_EXTENSION = ".gmeta";
    public static final String BLOCK_EXTENSION = ".gblock";

    protected File storageDirectory;
    private boolean readOnly;

    public FileSystem(String storageRoot)
    throws FileSystemException, IOException {
        initialize(storageRoot);
    }

    protected void initialize(String storageRoot)
    throws FileSystemException, IOException {
        logger.info("Initializing Galileo File System.");
        logger.info("Storage directory: " + storageRoot);

        /* Ensure the storage directory exists. */
        storageDirectory = new File(storageRoot);
        if (!storageDirectory.exists()) {
            logger.warning("Root storage directory does not exist.  " +
                    "Attempting to create.");

            if (!storageDirectory.mkdirs()) {
                throw new FileSystemException("Unable to create storage " +
                    "directory.");
            }
        }

        logger.info("Free space: " + getFreeSpace());

        /* Verify permissions. */
        boolean read, write, execute;
        read    = storageDirectory.canRead();
        write   = storageDirectory.canWrite();
        execute = storageDirectory.canExecute();

        logger.info("File system permissions: " +
                (read ? 'r' : "") +
                (write ? 'w' : "") +
                (execute ? 'x' : ""));

        if (!read) {
            throw new FileSystemException("Cannot read storage directory.");
        }

        if (!execute) {
            throw new FileSystemException("Storage Directory " +
                    "is not Executable.");
        }

        readOnly = false;
        if (!write) {
            logger.warning("Storage directory is read-only.  Starting " +
                    "file system in read-only mode.");
            readOnly = true;
        }
    }

    /**
     * Scans a directory (and its subdirectories) for blocks.
     *
     * @param directory
     *     Directory to scan for blocks.
     *
     * @return ArrayList of String paths to blocks on disk.
     */
    protected ArrayList<String> scanDirectory(File directory) {
        ArrayList<String> blockPaths = new ArrayList<String>();
        scanSubDirectory(directory, blockPaths);
        return blockPaths;
    }

    /**
     * Scans a directory (and its subdirectories) for blocks.
     *
     * @param directory
     *     Directory file descriptor to scan
     *
     * @param fileList
     *     ArrayList of Strings to populate with FileBlock paths.
     */
    private void scanSubDirectory(File directory, ArrayList<String> fileList) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanSubDirectory(file, fileList);
                continue;
            }

            String fileName = file.getAbsolutePath();
            if (fileName.endsWith(FileSystem.BLOCK_EXTENSION)) {
                fileList.add(fileName);
            }
        }
    }

    /**
     * Does a full recovery from disk; this scans every block in the system,
     * reads its metadata, and performs a checksum to verify block integrity.
     * If not already obvious, this could be very slow.
     */
    protected void fullRecovery() {
        logger.info("Performing full recovery from disk");
        recover(storageDirectory);
    }

    /**
     * Does a full recovery from disk on a particular Galileo partition; this
     * scans every block in the partition, reads its metadata, and performs a
     * checksum to verify block integrity.
     */
    protected void recover(File storageDir) {
        logger.info("Recovering path index");
        ArrayList<String> blockPaths = scanDirectory(storageDir);

        logger.info("Recovering metadata and building graph");
        long counter = 0;
        for (String path : blockPaths) {
            try {
                Metadata metadata = loadMetadata(path);
                storeMetadata(path, metadata);
                ++counter;
                if (counter % 10000 == 0) {
                    logger.info(String.format("%d blocks scanned, " +
                                "recovery %.2f%% complete.", counter,
                                ((float) counter / blockPaths.size()) * 100));
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to recover metadata " +
                        "for block: " + path, e);
            }
        }
    }

    /**
     * Loads a {@link Block} instance from disk.
     *
     * @param blockPath the on-disk path of the Block to load.
     *
     * @return Block stored at blockPath.
     */
    public Block loadBlock(String blockPath)
    throws IOException, SerializationException {
        File blockFile = new File(blockPath);
        byte[] blockBytes = new byte[(int) blockFile.length()];
        Block block = Serializer.deserialize(Block.class, blockBytes);
        return block;
    }

    /**
     * Recovers {@link Metadata} from disk.
     *
     * @param blockPath the on-disk path of the Block to recover Metadata from.
     *
     * @return Metadata stored at blockPath.
     */
    public Metadata loadMetadata(String blockPath)
    throws IOException, SerializationException {
        /* We can just load the block as usual, but only perform the
         * deserialization on the Metadata.  Metadata is stored as the first
         * item in a serialized Block instance. */
        File blockFile = new File(blockPath);
        byte[] blockBytes = new byte[(int) blockFile.length()];
        Metadata meta = Serializer.deserialize(Metadata.class, blockBytes);
        return meta;
    }

    /**
     * Persists a {@link Block} to disk.  The location of the Block will be
     * determined by the particular FileSystem implementation being used.
     *
     * @param block Block to store on disk.
     *
     * @return String representing the path of the Block on disk.
     */
    public String storeBlock(Block block)
    throws FileSystemException, IOException {
        String name = block.getMetadata().getName();
        if (name.equals("")) {
            UUID blockUUID = UUID.nameUUIDFromBytes(block.getData());
            name = blockUUID.toString();
        }

        String blockPath = storageDirectory + "/" + name
            + FileSystem.BLOCK_EXTENSION;

        FileOutputStream blockOutStream = new FileOutputStream(blockPath);
        byte[] blockData = Serializer.serialize(block);
        blockOutStream.write(blockData);
        blockOutStream.close();

        return blockPath;
    }

    /**
     * Inserts Metadata into the file system.  In many cases, Metadata is not
     * stored individually on disk but placed in an index instead.  This method
     * is useful during a full recovery operation for re-linking indexed
     * Metadata with its associated files on disk, or could be used in
     * situations where information should only be indexed and not stored.
     */
    protected void storeMetadata(String blockPath, Metadata metadata)
    throws FileSystemException, IOException {
        /* The default implementation does not actually perform any indexing. */
        return;
    }

    /**
     * Reports whether the Galileo filesystem is read-only.
     *
     * @return true if the filesystem is read-only.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Reports the amount of free space (in bytes) in the root storage
     * directory.
     *
     * @return long integer with the amount of free space, in bytes.
     */
    public long getFreeSpace() {
        return storageDirectory.getFreeSpace();
    }
}
