package galileo.bmp;

import com.googlecode.javaewah.EWAHCompressedBitmap;

/**
 * A thin wrapper around {@link com.googlecode.javaewah.EWAHCompressedBitmap}
 * to enable us to decouple from a particular bitmap implementation in the
 * future if need be.
 *
 * @author malensek
 */
public class Bitmap {

    private EWAHCompressedBitmap bmp;

    public Bitmap() {
        this.bmp = new EWAHCompressedBitmap();
    }

    private Bitmap(EWAHCompressedBitmap bmp) {
        this.bmp = bmp;
    }

    /**
     * Sets the specified bit(s) in the BitArray.
     *
     * @param bits list of bits to set (as in, set to 1).
     *
     * @throws BitArrayException if the bits could not be set.
     */
    public void set(int... bits)
    throws BitmapException {
        for (int i : bits) {
            if (bmp.set(i) == false) {
                throw new BitmapException("Could not set bit at " + i);
            }
        }
    }

    /**
     * Performs a logical AND operation between two Bitmap instances.
     */
    public Bitmap and(Bitmap otherBitmap)
    throws BitmapException {
        return new Bitmap(this.bmp.and(otherBitmap.bmp));
    }

    /**
     * Performs a logical OR operation between two Bitmap instances.
     */
    public Bitmap or(Bitmap otherBitmap)
    throws BitmapException {
        return new Bitmap(this.bmp.or(otherBitmap.bmp));
    }
}
