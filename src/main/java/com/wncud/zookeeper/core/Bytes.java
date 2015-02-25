package com.wncud.zookeeper.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;

@SuppressWarnings("restriction")
public class Bytes {
	
	private static final Logger LOG= LoggerFactory.getLogger(Bytes.class);

	/**
	 * Converts a byte array to an int value
	 * 
	 * @param bytes
	 *            byte array
	 * @param offset
	 *            offset into array
	 * @return the int value
	 */
	public static int toInt(byte[] bytes, int offset) {
		return toInt(bytes, offset, SIZEOF_INT);
	}

	/**
	 * Converts a byte array to an int value
	 * 
	 * @param bytes
	 *            byte array
	 * @param offset
	 *            offset into array
	 * @param length
	 *            length of int (has to be {@link #SIZEOF_INT})
	 * @return the int value
	 * @throws IllegalArgumentException
	 *             if length is not {@link #SIZEOF_INT} or if there's not enough
	 *             room in the array at the offset indicated.
	 */
	public static int toInt(byte[] bytes, int offset, final int length) {
		if (length != SIZEOF_INT || offset + length > bytes.length) {
			throw explainWrongLengthOrOffset(bytes, offset, length, SIZEOF_INT);
		}
		int n = 0;
		for (int i = offset; i < (offset + length); i++) {
			n <<= 8;
			n ^= bytes[i] & 0xFF;
		}
		return n;
	}
	
	  /**
	   * Write a printable representation of a byte array.
	   *
	   * @param b byte array
	   * @return string
	   * @see #toStringBinary(byte[], int, int)
	   */
	  public static String toStringBinary(final byte [] b) {
	    if (b == null)
	      return "null";
	    return toStringBinary(b, 0, b.length);
	  }
	  
	  /**
	   * Write a printable representation of a byte array. Non-printable
	   * characters are hex escaped in the format \\x%02X, eg:
	   * \x00 \x05 etc
	   *
	   * @param b array to write out
	   * @param off offset to start at
	   * @param len length to write
	   * @return string output
	   */
	  public static String toStringBinary(final byte [] b, int off, int len) {
	    StringBuilder result = new StringBuilder();
	    try {
	      String first = new String(b, off, len, "ISO-8859-1");
	      for (int i = 0; i < first.length() ; ++i ) {
	        int ch = first.charAt(i) & 0xFF;
	        if ( (ch >= '0' && ch <= '9')
	            || (ch >= 'A' && ch <= 'Z')
	            || (ch >= 'a' && ch <= 'z')
	            || " `~!@#$%^&*()-_=+[]{}\\|;:'\",.<>/?".indexOf(ch) >= 0 ) {
	          result.append(first.charAt(i));
	        } else {
	          result.append(String.format("\\x%02X", ch));
	        }
	      }
	    } catch (UnsupportedEncodingException e) {
	      LOG.error("ISO-8859-1 not supported?", e);
	    }
	    return result.toString();
	  }

	private static IllegalArgumentException explainWrongLengthOrOffset(
			final byte[] bytes, final int offset, final int length,
			final int expectedLength) {
		String reason;
		if (length != expectedLength) {
			reason = "Wrong length: " + length + ", expected " + expectedLength;
		} else {
			reason = "offset (" + offset + ") + length (" + length
					+ ") exceed the" + " capacity of the array: "
					+ bytes.length;
		}
		return new IllegalArgumentException(reason);
	}

	
	/**
	   * Write a single byte out to the specified byte array position.
	   * @param bytes the byte array
	   * @param offset position in the array
	   * @param b byte to write out
	   * @return incremented offset
	   */
	  public static int putByte(byte[] bytes, int offset, byte b) {
	    bytes[offset] = b;
	    return offset + 1;
	  }
	  
	  
	  /**
	   * Put an int value out to the specified byte array position.
	   * @param bytes the byte array
	   * @param offset position in the array
	   * @param val int to write out
	   * @return incremented offset
	   * @throws IllegalArgumentException if the byte array given doesn't have
	   * enough room at the offset specified.
	   */
	  public static int putInt(byte[] bytes, int offset, int val) {
	    if (bytes.length - offset < SIZEOF_INT) {
	      throw new IllegalArgumentException("Not enough room to put an int at"
	          + " offset " + offset + " in a " + bytes.length + " byte array");
	    }
	    for(int i= offset + 3; i > offset; i--) {
	      bytes[i] = (byte) val;
	      val >>>= 8;
	    }
	    bytes[offset] = (byte) val;
	    return offset + SIZEOF_INT;
	  }
	  
	  
	  /**
	   * Put bytes at the specified byte array position.
	   * @param tgtBytes the byte array
	   * @param tgtOffset position in the array
	   * @param srcBytes array to write out
	   * @param srcOffset source offset
	   * @param srcLength source length
	   * @return incremented offset
	   */
	  public static int putBytes(byte[] tgtBytes, int tgtOffset, byte[] srcBytes,
	      int srcOffset, int srcLength) {
	    System.arraycopy(srcBytes, srcOffset, tgtBytes, tgtOffset, srcLength);
	    return tgtOffset + srcLength;
	  }
	  
	/**
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return True if equal
	 */
	public static boolean equals(final byte[] left, final byte[] right) {
		// Could use Arrays.equals?
		// noinspection SimplifiableConditionalExpression
		if (left == right)
			return true;
		if (left == null || right == null)
			return false;
		if (left.length != right.length)
			return false;
		if (left.length == 0)
			return true;

		// Since we're often comparing adjacent sorted data,
		// it's usual to have equal arrays except for the very last byte
		// so check that first
		if (left[left.length - 1] != right[right.length - 1])
			return false;

		return compareTo(left, right) == 0;
	}

	/**
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return 0 if equal, < 0 if left is less than right, etc.
	 */
	public static int compareTo(final byte[] left, final byte[] right) {
		return LexicographicalComparerHolder.BEST_COMPARER.compareTo(left, 0,
				left.length, right, 0, right.length);
	}

	interface Comparer<T> {
		abstract public int compareTo(T buffer1, int offset1, int length1,
                                      T buffer2, int offset2, int length2);
	}

	static Comparer<byte[]> lexicographicalComparerJavaImpl() {
		return LexicographicalComparerHolder.PureJavaComparer.INSTANCE;
	}

	/**
	 * Provides a lexicographical comparer implementation; either a Java
	 * implementation or a faster implementation based on {@link sun.misc.Unsafe}.
	 * 
	 * <p>
	 * Uses reflection to gracefully fall back to the Java implementation if
	 * {@code Unsafe} isn't available.
	 */
	static class LexicographicalComparerHolder {
		static final String UNSAFE_COMPARER_NAME = LexicographicalComparerHolder.class
				.getName() + "$UnsafeComparer";

		static final Comparer<byte[]> BEST_COMPARER = getBestComparer();

		/**
		 * Returns the Unsafe-using Comparer, or falls back to the pure-Java
		 * implementation if unable to do so.
		 */
		static Comparer<byte[]> getBestComparer() {
			try {
				Class<?> theClass = Class.forName(UNSAFE_COMPARER_NAME);

				// yes, UnsafeComparer does implement Comparer<byte[]>
				@SuppressWarnings("unchecked")
				Comparer<byte[]> comparer = (Comparer<byte[]>) theClass
						.getEnumConstants()[0];
				return comparer;
			} catch (Throwable t) { // ensure we really catch *everything*
				return lexicographicalComparerJavaImpl();
			}
		}

		enum PureJavaComparer implements Comparer<byte[]> {
			INSTANCE;

			@Override
			public int compareTo(byte[] buffer1, int offset1, int length1,
					byte[] buffer2, int offset2, int length2) {
				// Short circuit equal case
				if (buffer1 == buffer2 && offset1 == offset2
						&& length1 == length2) {
					return 0;
				}
				// Bring WritableComparator code local
				int end1 = offset1 + length1;
				int end2 = offset2 + length2;
				for (int i = offset1, j = offset2; i < end1 && j < end2; i++, j++) {
					int a = (buffer1[i] & 0xff);
					int b = (buffer2[j] & 0xff);
					if (a != b) {
						return a - b;
					}
				}
				return length1 - length2;
			}
		}

		enum UnsafeComparer implements Comparer<byte[]> {
			INSTANCE;

			static final Unsafe theUnsafe;

			/** The offset to the first element in a byte array. */
			static final int BYTE_ARRAY_BASE_OFFSET;

			static {
				theUnsafe = (Unsafe) AccessController
						.doPrivileged(new PrivilegedAction<Object>() {
							@Override
							public Object run() {
								try {
									Field f = Unsafe.class
											.getDeclaredField("theUnsafe");
									f.setAccessible(true);
									return f.get(null);
								} catch (NoSuchFieldException e) {
									// It doesn't matter what we throw;
									// it's swallowed in getBestComparer().
									throw new Error();
								} catch (IllegalAccessException e) {
									throw new Error();
								}
							}
						});

				BYTE_ARRAY_BASE_OFFSET = theUnsafe
						.arrayBaseOffset(byte[].class);

				// sanity check - this should never fail
				if (theUnsafe.arrayIndexScale(byte[].class) != 1) {
					throw new AssertionError();
				}
			}

			static final boolean littleEndian = ByteOrder.nativeOrder().equals(
					ByteOrder.LITTLE_ENDIAN);

			/**
			 * Returns true if x1 is less than x2, when both values are treated
			 * as unsigned.
			 */
			static boolean lessThanUnsigned(long x1, long x2) {
				return (x1 + Long.MIN_VALUE) < (x2 + Long.MIN_VALUE);
			}

			/**
			 * Lexicographically compare two arrays.
			 * 
			 * @param buffer1
			 *            left operand
			 * @param buffer2
			 *            right operand
			 * @param offset1
			 *            Where to start comparing in the left buffer
			 * @param offset2
			 *            Where to start comparing in the right buffer
			 * @param length1
			 *            How much to compare from the left buffer
			 * @param length2
			 *            How much to compare from the right buffer
			 * @return 0 if equal, < 0 if left is less than right, etc.
			 */
			@Override
			public int compareTo(byte[] buffer1, int offset1, int length1,
					byte[] buffer2, int offset2, int length2) {
				// Short circuit equal case
				if (buffer1 == buffer2 && offset1 == offset2
						&& length1 == length2) {
					return 0;
				}
				int minLength = Math.min(length1, length2);
				int minWords = minLength / SIZEOF_LONG;
				int offset1Adj = offset1 + BYTE_ARRAY_BASE_OFFSET;
				int offset2Adj = offset2 + BYTE_ARRAY_BASE_OFFSET;

				/*
				 * Compare 8 bytes at a time. Benchmarking shows comparing 8
				 * bytes at a time is no slower than comparing 4 bytes at a time
				 * even on 32-bit. On the other hand, it is substantially faster
				 * on 64-bit.
				 */
				for (int i = 0; i < minWords * SIZEOF_LONG; i += SIZEOF_LONG) {
					long lw = theUnsafe.getLong(buffer1, offset1Adj + (long) i);
					long rw = theUnsafe.getLong(buffer2, offset2Adj + (long) i);
					long diff = lw ^ rw;

					if (diff != 0) {
						if (!littleEndian) {
							return lessThanUnsigned(lw, rw) ? -1 : 1;
						}

						// Use binary search
						int n = 0;
						int y;
						int x = (int) diff;
						if (x == 0) {
							x = (int) (diff >>> 32);
							n = 32;
						}

						y = x << 16;
						if (y == 0) {
							n += 16;
						} else {
							x = y;
						}

						y = x << 8;
						if (y == 0) {
							n += 8;
						}
						return (int) (((lw >>> n) & 0xFFL) - ((rw >>> n) & 0xFFL));
					}
				}

				// The epilogue to cover the last (minLength % 8) elements.
				for (int i = minWords * SIZEOF_LONG; i < minLength; i++) {
					int a = (buffer1[offset1 + i] & 0xff);
					int b = (buffer2[offset2 + i] & 0xff);
					if (a != b) {
						return a - b;
					}
				}
				return length1 - length2;
			}
		}
	}

	/**
	 * Size of long in bytes
	 */
	public static final int SIZEOF_LONG = Long.SIZE / Byte.SIZE;
	/**
	 * Size of int in bytes
	 */
	public static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;
	/**
	 * Size of boolean in bytes
	 */
	public static final int SIZEOF_BOOLEAN = Byte.SIZE / Byte.SIZE;

	/**
	 * Size of byte in bytes
	 */
	public static final int SIZEOF_BYTE = SIZEOF_BOOLEAN;

}
