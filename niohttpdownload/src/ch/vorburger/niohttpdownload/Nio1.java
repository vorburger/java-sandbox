package ch.vorburger.niohttpdownload;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * Playing around with different methods of downloading a file via HTTP.
 * 
 * Initially written to try to debug a suspect misuse of transferFrom (one call instead of loop),
 * found while playing around in technicpack.net Technic Platform Minecraft mod pack building with Junior,
 * and to compare NIO with other approach.  It turned out that I just got the DB DL URL wrong :(
 * 
 * The performance bottleneck for HTTP file download is obviously the network, and NIO vs. classic
 * obviously makes absolutely no measurable difference... (this may be different for local file copy).
 * Pushed this GitHub anyways, just in case I ever find myself want to look this up again.
 * 
 * vorburger@linux:~$ time wget https://www.dropbox.com/s/kvirfwugc9d7m7l/SimpleButFun.zip
 * --2014-04-20 22:26:08--  https://www.dropbox.com/s/kvirfwugc9d7m7l/SimpleButFun.zip
 * Resolving www.dropbox.com (www.dropbox.com)... 108.160.165.20
 * Connecting to www.dropbox.com (www.dropbox.com)|108.160.165.20|:443... connected.
 * HTTP request sent, awaiting response... 302 FOUND
 * Location: https://dl.dropboxusercontent.com/s/kvirfwugc9d7m7l/SimpleButFun.zip?token_hash=AAFpWovbL-T81xlr1iMV8qpsSINUovAke5RrzWOsrQpX3Q [following]
 * --2014-04-20 22:26:09--  https://dl.dropboxusercontent.com/s/kvirfwugc9d7m7l/SimpleButFun.zip?token_hash=AAFpWovbL-T81xlr1iMV8qpsSINUovAke5RrzWOsrQpX3Q
 * Resolving dl.dropboxusercontent.com (dl.dropboxusercontent.com)... 107.22.236.52, 107.21.201.0, 54.225.207.37, ...
 * Connecting to dl.dropboxusercontent.com (dl.dropboxusercontent.com)|107.22.236.52|:443... connected.
 * HTTP request sent, awaiting response... 200 OK
 * Length: 22769893 (22M) [application/zip]
 * Saving to: `SimpleButFun.zip'
 * 
 * 100%[==================================================================================>] 22,769,893   850K/s   in 46s     
 *
 * 2014-04-20 22:26:57 (488 KB/s) - `SimpleButFun.zip' saved [22769893/22769893]
 * 
 * 
 * real	0m23.706s
 * user	0m0.316s
 * sys	0m0.332s
 * 
 * vorburger@linux:~$ java -version
 * java version "1.7.0_51"
 * OpenJDK Runtime Environment (IcedTea 2.4.4) (7u51-2.4.4-0ubuntu0.12.04.2)
 * OpenJDK 64-Bit Server VM (build 24.45-b08, mixed mode)
 *
 * vorburger@linux:~$ uname -a
 * Linux linux 3.2.0-60-generic #91-Ubuntu SMP Wed Feb 19 03:54:44 UTC 2014 x86_64 x86_64 x86_64 GNU/Linux
 *
 * @author Michael Vorburger
 */
public class Nio1 {

	public static void main(String[] args) throws Exception {
		// NOT URL url = new URL("https://www.dropbox.com/s/kvirfwugc9d7m7l/SimpleButFun.zip");
		URL url = new URL("https://www.dropbox.com/s/kvirfwugc9d7m7l/SimpleButFun.zip?dl=1"); // NOTE dl=1 
		// OR URL url = new URL("https://dl.dropboxusercontent.com/u/4801603/SimpleButFun.zip");
		File file = new File("./SimpleButFun.zip");
		file.delete();
		long start = System.currentTimeMillis();
		
/*		HttpURLConnection.setFollowRedirects(true);
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);
*/
		// 21s
//		Resources.asByteSource(url).copyTo(Files.asByteSink(file));
//		FileUtils.copyURLToFile(url, file, 30000, 30000);
		
		// 22s
//		Request.Get(url.toURI())
//			.useExpectContinue()
//			.connectTimeout(30000)
//			.socketTimeout(30000)
//			.execute().saveContent(file);
		
		// 21s
//		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
//		FileOutputStream fos = new FileOutputStream(file);
//		try {
//			transferFromTo(rbc, fos.getChannel(), Long.MAX_VALUE /* TODO 22769893 - but how-to-know?? */);
//		} finally {
//			IOUtils.closeQuietly(fos);
//		}
		
		long end = System.currentTimeMillis();
		System.out.println("Took: " + (end-start) + "ms");
		
		System.out.println("Filesize: " + file.length());
	}

	/**
	 * @see http://www.tutorials.de/java/328830-schnell-grosse-dateien-kopieren-mit-java-nio.html
	 */
	private static void transferFromTo(ReadableByteChannel rbc,	FileChannel channel, long lengthInBytes) throws IOException {
		// NOT fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); // USE Long.MAX_VALUE, NOT 1 << 24, NOR Integer.MAX_VALUE

		final long chunckSizeInBytes = Long.MAX_VALUE; // Why wouldn't you just set this as big as possible?!
		long overallBytesTransfered = 0L;
		while (overallBytesTransfered < lengthInBytes) {
			long bytesToTransfer = Math.min(chunckSizeInBytes, lengthInBytes - overallBytesTransfered);
		    long bytesTransfered = channel.transferFrom(rbc, overallBytesTransfered, bytesToTransfer);
		    if (bytesTransfered == 0)
		    	break;
		    overallBytesTransfered += bytesTransfered;
		}
	}

}
