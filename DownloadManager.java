package Download;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DownloadManager implements Callable<String> {
    URLConnection connection;
    FileChannel outputChann;
    public static volatile int count = 0;

    public static void main(String[] args) throws Exception {
    	ExecutorService poll = Executors.newFixedThreadPool(100);
    	
    	for(int pagenumber=1; pagenumber<50; pagenumber++)
		{
    		String fileName = null;
			Document doc = Jsoup.connect("http://wanimal.lofter.com/?page="+pagenumber).userAgent("Mozillia").timeout(1000).get();
			Elements jpgs = doc.select("img[src$=.jpg]");
			int i=0;
			for(Element jpg : jpgs)
			{
				fileName = "/Users/Linxing/Desktop/testDB/NewPic/"+pagenumber + "-" + i + ".jpg";
				System.out.println(jpg.attr("src"));
				i++;
				poll.submit(new DownloadManager(jpg.attr("src"),
	                    (new FileOutputStream(fileName)).getChannel()));
			}
		}
        poll.shutdown();

        long start = System.currentTimeMillis();
        while (!poll.isTerminated()) {
            Thread.sleep(1000);
            System.out.println("已运行"
                    + ((System.currentTimeMillis() - start) / 1000) + "秒，"
                    + DownloadManager.count + "个任务还在运行");
        }
    }

    public DownloadManager(String url, FileChannel fileChannel) throws Exception {
        synchronized (DownloadManager.class) {
            count++;
        }
        connection = (new URL(url)).openConnection();;
        this.outputChann = fileChannel;
    }

    @Override
    public String call() throws Exception {
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        ReadableByteChannel rChannel = Channels.newChannel(inputStream);
        outputChann.transferFrom(rChannel, 0, Integer.MAX_VALUE);
        // System.out.println(Thread.currentThread().getName() + " completed!");
        inputStream.close();
        outputChann.close();
        synchronized (DownloadManager.class) {
            count--;
        }
        return null;
    }
}