package com.bumptech.glide.integration.avif.decoder;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AvifImageDetector {

    // string 'ftyp'
    public static final int AVIF_FTYP = 0x66747970;

    // string 'avif'
    public static final int AVIF_AVIF = 0x61766966;

    // string 'avis'
    public static final int AVIF_AVIS = 0x61766973;

    private static AvifFileType parseAvifFileType(Reader reader) {
        try {
            int smallSize = reader.getInt32BE();
            final int type = reader.getInt32BE();
            if (type != AVIF_FTYP) {
                return null;
            }
            int majorBrand = reader.getInt32BE();
            int minorVersion = reader.getInt32BE();

            final int compatibleBrandsBytes = smallSize - 16;
            if (compatibleBrandsBytes % 4 != 0) {
                return null;
            }
            final byte[] compatibleBrands = new byte[compatibleBrandsBytes];
            int len = reader.read(compatibleBrands, compatibleBrandsBytes);
            if (len != compatibleBrands.length) {
                return null;
            }
            final int compatibleBrandsCount = compatibleBrandsBytes / 4;
            final int[] brands = new int[compatibleBrandsCount];
            for (int i = 0; i < compatibleBrandsCount; i++) {
                brands[i] = getInt32BE(compatibleBrands, i * 4);
            }
            return new AvifFileType(type, majorBrand, minorVersion, brands);
        } catch (IOException ignored) {
        }
        return null;
    }

    public static AvifFileType parseAvifFileType(InputStream is) {
        return parseAvifFileType(new StreamReader(is));
    }

    public static AvifFileType parseAvifFileType(ByteBuffer buffer) {
        return parseAvifFileType(new ByteBufferReader(buffer));
    }

    public static boolean isAvif(InputStream is) {
        final AvifFileType avifFileType = parseAvifFileType(is);
        if (avifFileType == null) {
            return false;
        }
        if (avifFileType.majorBrand == AVIF_AVIF) {
            return true;
        }
        return avifFileType.matchBrand(AVIF_AVIF);
    }

    public static boolean isAvif(ByteBuffer buffer) {
        final AvifFileType avifFileType = parseAvifFileType(buffer);
        if (avifFileType == null) {
            return false;
        }
        if (avifFileType.majorBrand == AVIF_AVIF) {
            return true;
        }
        return avifFileType.matchBrand(AVIF_AVIF);
    }

    public static boolean isAvifs(InputStream is) {
        final AvifFileType avifFileType = parseAvifFileType(is);
        if (avifFileType == null) {
            return false;
        }
        if (avifFileType.majorBrand == AVIF_AVIS) {
            return true;
        }
        return avifFileType.matchBrand(AVIF_AVIS);
    }

    public static boolean isAvifs(ByteBuffer buffer) {
        final AvifFileType avifFileType = parseAvifFileType(buffer);
        if (avifFileType == null) {
            return false;
        }
        if (avifFileType.majorBrand == AVIF_AVIS) {
            return true;
        }
        return avifFileType.matchBrand(AVIF_AVIS);
    }

    private static int getInt32LE(byte[] bytes, int off) {
        return bytes[off] | (bytes[off + 1] << 8) | (bytes[off + 2] << 16) | (bytes[off + 3] << 24);
    }

    private static int getInt32BE(byte[] bytes, int off) {
        return (bytes[off] << 24) | (bytes[off + 1] << 16) | (bytes[off + 2] << 8) | bytes[off + 3];
    }

    private static int getInt16LE(byte[] bytes, int off) {
        return bytes[off] | (bytes[off + 1] << 8);
    }

    private static int getInt16BE(byte[] bytes, int off) {
        return (bytes[off] << 8) | bytes[off + 1];
    }

    private static long getInt64BE(byte[] bytes, int off) {
        return ((long) getInt32LE(bytes, off) << 32) | getInt32LE(bytes, off + 4);
    }

    private static long getInt64LE(byte[] bytes, int off) {
        return getInt32LE(bytes, off) | ((long) getInt32LE(bytes, off + 4) << 32);
    }

    public static class AvifFileType {
        public final int ftyp;
        public final int majorBrand;
        public final int minorVersion;
        public final int[] compatibleBrands;

        AvifFileType(int ftyp, int majorBrand, int minorVersion, int[] compatibleBrands) {
            this.ftyp = ftyp;
            this.majorBrand = majorBrand;
            this.minorVersion = minorVersion;
            this.compatibleBrands = compatibleBrands;
        }

        public boolean matchBrand(int brand) {
            for (int compatibleBrand : compatibleBrands) {
                if (compatibleBrand == brand) {
                    return true;
                }
            }
            return false;
        }
    }

    private interface Reader {

        /**
         * Reads and returns a 8-bit unsigned integer.
         *
         * <p>Throws an {@link EndOfFileException} if an EOF is reached.
         */
        int getInt8() throws IOException;

        /**
         * Reads and returns a 16-bit unsigned integer.
         *
         * <p>Throws an {@link EndOfFileException} if an EOF is reached.
         */
        int getInt16LE() throws IOException;

        int getInt16BE() throws IOException;

        int getInt32LE() throws IOException;

        int getInt32BE() throws IOException;

        /**
         * Reads and returns a byte array.
         *
         * <p>Throws an {@link EndOfFileException} if an EOF is reached before anything was read.
         */
        int read(byte[] buffer, int byteCount) throws IOException;

        long skip(long total) throws IOException;

        // TODO(timurrrr): Stop inheriting from IOException, and make sure all attempts to read from
        //   a Reader correctly handle EOFs.
        final class EndOfFileException extends IOException {
            private static final long serialVersionUID = 1L;

            EndOfFileException() {
                super("Unexpectedly reached end of a file");
            }
        }
    }

    private static final class StreamReader implements Reader {
        private final InputStream is;

        // Motorola / big endian byte order.
        StreamReader(InputStream is) {
            this.is = is;
        }

        @Override
        public int getInt8() throws IOException {
            int readResult = is.read();
            if (readResult == -1) {
                throw new EndOfFileException();
            }
            return readResult;
        }

        @Override
        public int getInt16LE() throws IOException {
            return getInt8() | (getInt8() << 8);
        }

        @Override
        public int getInt16BE() throws IOException {
            return (getInt8() << 8) | getInt8();
        }

        @Override
        public int getInt32LE() throws IOException {
            return getInt8() | (getInt8() << 8) | (getInt8() << 16) | (getInt8() << 24);
        }

        @Override
        public int getInt32BE() throws IOException {
            return (getInt8() << 24) | (getInt8() << 16) | (getInt8() << 8) | getInt8();
        }

        @Override
        public int read(byte[] buffer, int byteCount) throws IOException {
            int numBytesRead = 0;
            int lastReadResult = 0;
            while (numBytesRead < byteCount
                    && ((lastReadResult = is.read(buffer, numBytesRead, byteCount - numBytesRead))
                            != -1)) {
                numBytesRead += lastReadResult;
            }

            if (numBytesRead == 0 && lastReadResult == -1) {
                throw new EndOfFileException();
            }

            return numBytesRead;
        }

        @Override
        public long skip(long total) throws IOException {
            if (total < 0) {
                return 0;
            }

            long toSkip = total;
            while (toSkip > 0) {
                long skipped = is.skip(toSkip);
                if (skipped > 0) {
                    toSkip -= skipped;
                } else {
                    // Skip has no specific contract as to what happens when you reach the end of
                    // the stream. To differentiate between temporarily not having more data and
                    // having finished the stream, we read a single byte when we fail to skip any
                    // amount of data.
                    int testEofByte = is.read();
                    if (testEofByte == -1) {
                        break;
                    } else {
                        toSkip--;
                    }
                }
            }
            return total - toSkip;
        }
    }

    private static final class ByteBufferReader implements Reader {

        private final ByteBuffer byteBuffer;

        ByteBufferReader(ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }

        @Override
        public int getInt8() throws IOException {
            if (byteBuffer.remaining() < 1) {
                throw new EndOfFileException();
            }
            return byteBuffer.get();
        }

        @Override
        public int getInt16LE() throws IOException {
            return getInt8() | (getInt8() << 8);
        }

        @Override
        public int getInt16BE() throws IOException {
            return (getInt8() << 8) | getInt8();
        }

        @Override
        public int getInt32LE() throws IOException {
            return getInt8() | (getInt8() << 8) | (getInt8() << 16) | (getInt8() << 24);
        }

        @Override
        public int getInt32BE() throws IOException {
            return (getInt8() << 24) | (getInt8() << 16) | (getInt8() << 8) | getInt8();
        }

        @Override
        public int read(byte[] buffer, int byteCount) {
            int toRead = Math.min(byteCount, byteBuffer.remaining());
            if (toRead == 0) {
                return -1;
            }
            byteBuffer.get(buffer, 0 /*dstOffset*/, toRead);
            return toRead;
        }

        @Override
        public long skip(long total) {
            int toSkip = (int) Math.min(byteBuffer.remaining(), total);
            byteBuffer.position(byteBuffer.position() + toSkip);
            return toSkip;
        }
    }
}
