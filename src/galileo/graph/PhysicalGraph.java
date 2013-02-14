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

package galileo.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import galileo.dataset.BlockMetadata;
import galileo.dataset.BlockMetadataImpl;
import galileo.dataset.FileBlock;

import galileo.serialization.SerializationException;
import galileo.serialization.Serializer;
import galileo.util.GeoHash;

/**
 * Handles functionality concerning the physical (on-disk) graph.  The graph is
 * built on disk via a hierarchical tree of directories.
 */
public class PhysicalGraph {
    public static final String METADATA_EXTENSION = ".gmeta";
    public static final String BLOCK_EXTENSION    = ".gblock";

    private File storageDirectory;
    private boolean readOnly = false;

    public PhysicalGraph(File storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    public PhysicalGraph(File storageDirectory, boolean readOnly) {
        this.storageDirectory = storageDirectory;
        this.readOnly = readOnly;
    }

    /**
     * Scans the entire filesystem and returns paths to all blocks in the
     * system.
     *
     * @return ArrayList of block paths.
     */
    public ArrayList<String> getBlockPaths() {
        return scanDirectory(storageDirectory);
    }

    /**
     * Scans a directory (and its subdirectories) for blocks.
     *
     * @param directory
     *     Directory to scan for blocks.
     *
     * @return ArrayList of String paths to blocks on disk.
     */
    public ArrayList<String> scanDirectory(File directory) {
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
            if (fileName.endsWith(METADATA_EXTENSION)) {
                fileList.add(fileName);
            }
        }
    }

    /**
     * Stores a <code>FileBlock</code> on disk.
     *
     * @param block
     *     Block to store on disk
     *
     * @param rawBytes
     *     bytes for the entire block (data + metadata).  This is used to
     *
     * @return
     *     String path to the block's location on disk.
     *
     * @throws StorageNodeException
     *     If there are problems storing the bytes
     */
    public String storeBlock(FileBlock block, byte[] blockBytes)
    throws IOException {
        UUID blockUuid = UUID.nameUUIDFromBytes(blockBytes);

        String blockDirPath = storageDirectory + "/" + getStorageDirectory(block);
        String blockPath = blockDirPath + "/" + blockUuid.toString();

        /* Ensure the storage directory is there. */
        File blockDirectory = new File(blockDirPath);
        if (!blockDirectory.exists()) {
            if (!blockDirectory.mkdirs()) {
                throw new IOException("Failed to create directory (" +
                    blockDirPath + ") for block.");
            }
        }

        /* Write the block content first */
        FileOutputStream blockOutStream
            = new FileOutputStream(blockPath + BLOCK_EXTENSION);
        byte[] blockData = block.getData();
        blockOutStream.write(blockData);
        blockOutStream.close();

        /* Write the metadata separately. */
        FileOutputStream metaOutStream
            = new FileOutputStream(blockPath + METADATA_EXTENSION);
        byte[] metadata = Serializer.serialize(block.getMetadata());
        metaOutStream.write(metadata);
        metaOutStream.close();

        return blockPath;
    }

    /**
     * Construct a <code>FileBlock</code> from a (block, metadata) file pair
     * located on disk.
     *
     * @param blockPath
     *     Path to the block and metadata files; simply use the block UUID
     *     without the block or metadata extension; they are added automatically
     *     to the path.
     */
    public FileBlock loadBlock(String blockPath)
    throws FileNotFoundException, IOException, SerializationException {
        File metaFile = new File(blockPath + METADATA_EXTENSION);
        File dataFile = new File(blockPath + BLOCK_EXTENSION);

        byte[] metaBytes = new byte[(int) metaFile.length()];
        byte[] dataBytes = new byte[(int) dataFile.length()];

        FileInputStream metaInStream = new FileInputStream(metaFile);
        metaInStream.read(metaBytes);
        metaInStream.close();

        FileInputStream dataInStream = new FileInputStream(dataFile);
        dataInStream.read(dataBytes);
        dataInStream.close();

        return new FileBlock(dataBytes, metaBytes);
    }

    public BlockMetadata loadMetadata(String metaPath)
    throws FileNotFoundException, IOException, SerializationException {
        File metaFile = new File(metaPath);
        byte[] metaBytes = new byte[(int) metaFile.length()];

        FileInputStream metaInStream = new FileInputStream(metaFile);
        metaInStream.read(metaBytes);
        metaInStream.close();

        return Serializer.deserialize(BlockMetadataImpl.class, metaBytes);
    }


    /**
     * Determine where a <code>FileBlock</code> should be stored on the host
     * filesystem.
     *
     * Current format:
     *     year/month/day/geohash/feature
     *
     * @param block
     *     Block to find the storage directory for.
     *
     * @return
     *     Storage directory for block
     */
    private String getStorageDirectory(FileBlock block) {
        String directory = "";

        BlockMetadata metadata = block.getMetadata();
        Date blockDate = metadata.getTemporalRange().getLowerBound();

        /* Date */
        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyPattern("yyyy/M/d/");
        directory = formatter.format(blockDate);

        /* GeoHash */
        directory += GeoHash.encode(metadata.getSpatialRange(), 2);

        return directory;
    }

    /**
     * Determines whether the Galileo filesystem is read-only.
     *
     * @return <code>true</code> if the filesystem is read-only.
     */
    public boolean isReadOnly() {
        return readOnly;
    }
}
