package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IPCache {
	public String IPcachefilepath;
	public String dealedIPcachefilepath;
	private FileReader cachefilereader;
	private FileWriter cachefilewriter;
	public int notelength = 30000000;

	public IPCache(String path, String path2) {
		this.IPcachefilepath = path;
		this.dealedIPcachefilepath = path2;
	}

	public void dealipcachefile() throws IOException {
		String midstr = this.IPcachefilepath;
		long data;
		cachefilereader = new FileReader(midstr);
		midstr = this.dealedIPcachefilepath;
		cachefilewriter = new FileWriter(midstr);
		BufferedReader midreader;
		midreader = new BufferedReader(cachefilereader);
		String tmpString = null;
		for (int i = 0; i < notelength; i++) {
			tmpString = midreader.readLine();
			tmpString = tmpString.substring(0, tmpString.length() - 2);
			data = murmurhash(tmpString);
			cachefilewriter.write((new Long(data).intValue()) + "\r\n");
			if(i%10000==0)
				System.out.println(i);
		}

	}

	/**
	 * MurMurHash算法，是非加密HASH算法，性能很高，
	 * 比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免）
	 * 等HASH算法要快很多，而且据说这个算法的碰撞率很低. http://murmurhash.googlepages.com/
	 */
	private static Long murmurhash(String key) {

		ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
		int seed = 0x1234ABCD;

		ByteOrder byteOrder = buf.order();
		buf.order(ByteOrder.LITTLE_ENDIAN);

		long m = 0xc6a4a7935bd1e995L;
		int r = 47;

		long h = seed ^ (buf.remaining() * m);

		long k;
		while (buf.remaining() >= 8) {
			k = buf.getLong();

			k *= m;
			k ^= k >>> r;
			k *= m;

			h ^= k;
			h *= m;
		}

		if (buf.remaining() > 0) {
			ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
			// for big-endian version, do this first:
			// finish.position(8-buf.remaining());
			finish.put(buf).rewind();
			h ^= finish.getLong();
			h *= m;
		}

		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		buf.order(byteOrder);
		return h;
	}
}
