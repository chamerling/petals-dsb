/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.messaging.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ReaderInputStream extends InputStream {
	/**
	 * The initial {@link Reader}.
	 */
	private final Reader reader;

	/**
	 * As a buffer reads from a Reader is bigger than a buffer reads from an
	 * InputStream, we need to keep characters read from the Reader and not
	 * returned to the client. They are stored in this buffer.
	 * 
	 */
	private byte[] internalBuffer = null;

	private int internalBufferPos = 0;

	/**
	 * The constructor.
	 * 
	 * @param reader
	 *            The <code>Reader</code> to wrap. Not null.
	 */
	public ReaderInputStream(final Reader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("Reader cannot be null.");
		}
		this.reader = reader;
	}

	/**
	 * Returns available bytes in the internal buffer incremented by one if the
	 * reader is ready (see {@link Reader#ready()}).
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {

		if (this.reader.ready()) {
			// A next read on the reader can be done withoutto be blocked.
			// We do the read to check if a character is available or EOF is
			// expected
			final int availableChar = this.reader.read();
			if (availableChar != -1) {
				// We push the read character in the internal buffer
				if (this.internalBuffer != null) {
					final byte[] newBuffer = new byte[this.internalBuffer.length + 1];
					System.arraycopy(this.internalBuffer, 0, newBuffer, 0,
							this.internalBuffer.length);
					newBuffer[newBuffer.length - 1] = (byte) availableChar;
					this.internalBuffer = newBuffer;
				} else {
					this.internalBuffer = new String(
							new char[] { (char) availableChar }).getBytes();
					this.internalBufferPos = 0;
				}
			}
		}
		return this.internalBuffer == null ? 0
				: (this.internalBuffer.length - this.internalBufferPos);
	}

	@Override
	public void close() throws IOException {
		this.reader.close();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		byte[] buffer = new byte[1];
		int nbByteRead = this.read(buffer);
		if (nbByteRead != 1) {
			throw new IOException("No byte read.");
		}
		return buffer[0];
	}

	@Override
	public int read(final byte[] cbuf) throws IOException {
		return this.read(cbuf, 0, cbuf.length);
	}

	@Override
	public int read(final byte[] cbuf, final int off, final int len)
			throws IOException {
		return this.localRead(cbuf, off, len, false);
	}

	@Override
	public long skip(long n) throws IOException, IllegalArgumentException {
		if (n > Long.MAX_VALUE) {
			throw new IllegalArgumentException("Only value lesser "
					+ Integer.MAX_VALUE + "are accepted.");
		}
		return this.localRead(null, 0, (int) n, true);
	}

	private int localRead(final byte[] buf, final int off, final int len,
			final boolean skip) throws IOException {

		int remainingLen = len;
		int offset = off;

		// First we read from the internal buffer
		int bytesToReadInInternalBuffer = this.internalBuffer == null ? 0
				: Math.min(len, this.internalBuffer.length
						- this.internalBufferPos);
		if (bytesToReadInInternalBuffer > 0) {
			if (!skip) {
				System.arraycopy(this.internalBuffer, this.internalBufferPos,
						buf, offset, bytesToReadInInternalBuffer);
			}
			remainingLen -= bytesToReadInInternalBuffer;
			this.internalBufferPos += bytesToReadInInternalBuffer;
		}

		if (remainingLen > 0) {
			offset += bytesToReadInInternalBuffer;
			// We must complete the provided buffer with bytes read from the
			// reader
			final char[] cbuf = new char[remainingLen];
			int charRead = this.reader.read(cbuf, 0, remainingLen);
			if (charRead == -1) {
				// EOF: No more character in the reader
				if (len == remainingLen) {
					// No bytes available --> EOF
					return -1;
				} else {
					// Few characters bytes have read from the internal buffer
					return len - remainingLen;
				}
			} else {
				this.internalBuffer = new String(cbuf, 0, charRead).getBytes();
				this.internalBufferPos = 0;
				bytesToReadInInternalBuffer = Math.min(remainingLen, charRead);
				if (!skip) {
					System.arraycopy(this.internalBuffer, 0, buf, offset,
							bytesToReadInInternalBuffer);
				}
				this.internalBufferPos += bytesToReadInInternalBuffer;

				// Now the provided buffer should be full
				return bytesToReadInInternalBuffer;
			}

		} else {
			// No more bytes to read, the provided buffer is full
			return len;
		}
	}
}
