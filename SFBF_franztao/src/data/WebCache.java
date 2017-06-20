package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

public class WebCache {
	public String WebCachefilepath;
	public String dealedWebCachefilepath;
	private FileReader cachefilereader;
	private FileWriter cachefilewriter;
	public int notelength = 1550000;

	public WebCache(String path, String path2) {
		this.WebCachefilepath = path;
		this.dealedWebCachefilepath = path2;
	}

	public void dealwebcachefile() throws IOException {
		String midstr = this.WebCachefilepath;
		int start;
		Long data;
		cachefilereader = new FileReader(midstr);
		midstr = this.dealedWebCachefilepath;
		cachefilewriter = new FileWriter(midstr);
		BufferedReader midreader;
		midreader = new BufferedReader(cachefilereader);
		String tmpString = null;
		for (int i = 0; i < notelength; i++) {
			if(i%10000==0)
				System.out.println(i);
			tmpString = midreader.readLine();
			start = tmpString.indexOf('-', 0);
			if(start>0){
			tmpString = tmpString.substring(0,start-1);
			data = murmurhash(tmpString);
			cachefilewriter.write((new Long(data).intValue()) + "\r\n");
			}
			
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
