package jsoup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class jsoutest {
	public static void main(String args[]) throws IOException{
		for(int pagenumber=14; pagenumber<50; pagenumber++)
		{
			Document doc = Jsoup.connect("http://wanimal.lofter.com/?page="+pagenumber).userAgent("Mozillia").timeout(1000).get();
			//Elements links = doc.select("a[herf]");
			Elements jpgs = doc.select("img[src$=.jpg]");
			int i=0;
			for(Element jpg : jpgs)
			{
				System.out.println(jpg.attr("src"));
				i++;
				URL url = new URL(jpg.attr("src"));
				File outFile = new File("/Users/Linxing/Desktop/testDB/"+pagenumber + "-" + i + ".jpg");
				OutputStream os = new FileOutputStream(outFile);
				InputStream is = url.openStream();
				byte[] buff = new byte[5555];
				while(true)
				{
					int readed = is.read(buff);
					if(readed == -1)
					{
						break;
					}
					byte[] temp = new byte[readed];
					System.arraycopy(buff, 0, temp, 0, readed);
					os.write(temp);
				}
				is.close();
				os.close();
			}
		}
	}
}
