
package galileo.fs;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import galileo.dataset.BlockMetadata;
import galileo.dataset.FileBlock;
import galileo.dataset.MetaArray;
import galileo.graph.LogicalGraph;
import galileo.graph.LogicalGraphNode;
import galileo.graph.PhysicalGraph;

import galileo.serialization.SerializationException;
import galileo.serialization.Serializer;

public class FileSystem {

    public static final String METADATA_EXTENSION = ".gmeta";
    public static final String BLOCK_EXTENSION    = ".gblock";

    private File storageDirectory;

    private Journal journal;

    /** In-memory representation of the Galileo filesystem. */
    private LogicalGraph logicalGraph;

    /** On-disk representation of the Galileo filesystem.   */
    private PhysicalGraph physicalGraph;

    public FileSystem(String storageRoot) throws FileSystemException {
        System.out.println("Initializing Galileo File System.");
        System.out.println("Storage directory: " + storageRoot);

        /* Ensure the storage directory exists. */
        storageDirectory = new File(storageRoot);
        if (!storageDirectory.exists()) {
            System.out.println(" -> Storage directory does not exist.  " +
                "Attempting to create.");

            if (!storageDirectory.mkdirs()) {
                throw new FileSystemException("Unable to create storage " +
                    "directory.");
            }
        }

        System.out.println("Free space:        " +
            storageDirectory.getFreeSpace());

        //TODO: inode count

        /* Verify permissions. */
        boolean read, write, execute;
        read    = storageDirectory.canRead();
        write   = storageDirectory.canWrite();
        execute = storageDirectory.canExecute();

        System.out.println("File system permissions: " +
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

        boolean readOnly = false;
        if (!write) {
            System.out.println("Warning: storage directory is read-only.");
            readOnly = true;
        }

        try {
            journal = Journal.getInstance();
            journal.setJournalPath(storageRoot + "/journal");
        } catch (IOException e) {
            System.out.println("Could not initialize the journal!");
            e.printStackTrace();
        }

        logicalGraph  = new LogicalGraph();
        physicalGraph = new PhysicalGraph(storageDirectory, readOnly);
    }

    public void recoverMetadata() {
//        logicalGraph.recoverFromJournal();

        //TODO: if we can't recover from the journal, do a manual recovery.
        //recover();
    }

    private void recover() {
        System.out.println("Recovering graph from disk.");
        long recoverStart, recoverEnd, indexStart, indexEnd, metaStart, metaEnd;

        recoverStart = System.nanoTime();

        System.out.print(" -> Recovering index...  ");
        indexStart = System.nanoTime();
        ArrayList<String> blockPaths = physicalGraph.getBlockPaths();
        indexEnd = System.nanoTime();
        System.out.println("Took " + (indexEnd - indexStart) * 1E-6 + " ms.");

        metaStart = System.nanoTime();
        System.out.print(" -> Recovering metadata and building graph...  ");
        int counter = 0;
        for (String path : blockPaths) {
            try {
                BlockMetadata metadata = physicalGraph.loadMetadata(path);
                logicalGraph.addBlock(metadata, path);
                ++counter;
                if (counter % 10000 == 0) {
                    System.out.println(counter);
                }
            } catch (Exception e) {
                System.out.println("Couldn't recover metadata: " + path);
                e.printStackTrace();
            }
        }
        metaEnd = System.nanoTime();
        System.out.println("Took " + (metaEnd - metaStart) * 1E-6 + " ms.");

        recoverEnd = System.nanoTime();
        System.out.println("Recovery complete.  Took "
            + (recoverEnd - recoverStart) * 1E-6 + " ms.");

    }

    public void storeBlock(byte[] blockBytes)
    throws IOException, FileSystemException, SerializationException {
        FileBlock block = Serializer.deserialize(FileBlock.class, blockBytes);
        storeBlock(block, blockBytes);
    }

    public void storeBlock(FileBlock block)
    throws IOException, FileSystemException {
        byte[] blockBytes = Serializer.serialize(block);
        storeBlock(block, blockBytes);
    }

    public void storeBlock(FileBlock block, byte[] blockBytes)
    throws IOException, FileSystemException {
        String blockPath = physicalGraph.storeBlock(block, blockBytes);
        logicalGraph.addBlock(block.getMetadata(), blockPath);
    }

    public byte[] query(String query)
    throws IOException {
        System.out.println(" -> Processing query: " + query);
        LogicalGraphNode[] nodes = logicalGraph.query(query);

        ArrayList<String> nodePaths = new ArrayList<String>();
        for (LogicalGraphNode node : nodes) {
            nodePaths.addAll(node.getBlockPaths());
        }

        MetaArray metas = new MetaArray();
        for (String path : nodePaths) {
            try {
                BlockMetadata meta = physicalGraph.loadMetadata(path);
                meta.getRuntimeMetadata().setPhysicalGraphPath(path);
                metas.add(meta);
            } catch (Exception e) {
                System.out.println("Couldn't recover Metadata: " + path);
                e.printStackTrace();
                continue;
            }
        }
        //metas.deploymentStream = Resource.dstream;
        System.out.println(metas.size());

        return Serializer.serialize(metas);
    }

    /**
     * Reports whether the Galileo filesystem is read-only.
     *
     * @return <code>true</code> if the filesystem is read-only.
     */
    public boolean isReadOnly() {
        return physicalGraph.isReadOnly();
    }
}
