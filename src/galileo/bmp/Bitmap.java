/*
Copyright (c) 2014, Colorado State University
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

package galileo.bmp;

import java.util.Iterator;

import com.googlecode.javaewah.EWAHCompressedBitmap;

/**
 * A thin wrapper around {@link com.googlecode.javaewah.EWAHCompressedBitmap}
 * to enable us to change bitmap implementations if need be.
 *
 * @author malensek
 */
public class Bitmap implements Iterable<Integer> {

    private EWAHCompressedBitmap bmp;

    public Bitmap() {
        bmp = new EWAHCompressedBitmap();
    }

    private Bitmap(EWAHCompressedBitmap bmp) {
        this.bmp = bmp;
    }

    /**
     * Sets the specified bit(s) in the index.
     *
     * @param bits list of bits to set (as in, set to 1).
     *
     * @throws BitmapException if the bits could not be set.
     */
    public boolean set(int... bits) {
        for (int i : bits) {
            return bmp.set(i);
        }
        return false;
    }

    public Bitmap or(Bitmap otherBitmap) {
        return new Bitmap(this.bmp.or(otherBitmap.bmp));
    }

    public Bitmap xor(Bitmap otherBitmap) {
        return new Bitmap(this.bmp.xor(otherBitmap.bmp));
    }

    public Bitmap and(Bitmap otherBitmap) {
        return new Bitmap(this.bmp.and(otherBitmap.bmp));
    }

    public boolean intersects(Bitmap otherBitmap) {
        return this.bmp.intersects(otherBitmap.bmp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Bitmap b = (Bitmap) obj;
        return this.bmp.equals(b.bmp);
    }

    @Override
    public Iterator<Integer> iterator() {
        return bmp.iterator();
    }
}
