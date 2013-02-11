
package galileo.dataset;

/**
 * Describes the type of data stored in a FileBlock.  This may be Galileo's
 * native data storage format, a NetCDF file, or some other filetype.
 */
public enum BlockType {
    NATIVE (1),
    NETCDF (2);

    private final int typeId;

    private BlockType(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
}
