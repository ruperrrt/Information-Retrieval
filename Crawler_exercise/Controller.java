package Crawler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
    public final static String siteURL = "www.wsj.com/";
    public static void main(String[] args) throws Exception {
        
        String crawlStorageFolder = "data/srp_hw2/";
        String fetchFile = "fetch_wsj.csv";
        String visitFile = "visit_wsj.csv";
        String urlsFile = "urls_wsj.csv";
        String statsFile = "stats.txt";
        
        int numberOfCrawlers = 7;
        int MaxDepthOfCrawling = 16;
        int maxPagesToFetch = 20000;
        CrawlConfig config = new CrawlConfig();
        
        
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(MaxDepthOfCrawling);
        config.setMaxPagesToFetch(maxPagesToFetch);
        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(true);
        config.setIncludeHttpsPages(true);
        config.setFollowRedirects(true);
        
        
        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        
        
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder+fetchFile));
            bw.write("URL, status code\n");
            bw.close();

            bw = new BufferedWriter(new FileWriter(crawlStorageFolder+visitFile));
            bw.write("URL, Size, Outgoing links, Content-type\n");
            bw.close();
            
            bw = new BufferedWriter(new FileWriter(crawlStorageFolder+urlsFile));
            bw.write("encountered URL, indicator\n");
            bw.close();
           
        }catch (IOException e){
            e.printStackTrace();
        }
        
        /*
        * For each crawl, you need to add some seed urls. These are the first
        * URLs that are fetched and then the crawler starts following links
        * which are found in these pages
        */
        controller.addSeed("https://"+siteURL);
        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(MyCrawler.class, numberOfCrawlers);
        
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder+statsFile));

            bw.write("News site crawled: www.wsj.com/\n\n");
            
            
            
            bw.write("Fetch Statistics\n");
            bw.write("============================\n");
            

            
            bw.write("Outgoing URLs\n");
            bw.write("============================\n");
            
            bw.write("Total URLs extracted:"+MyCrawler.total+"\n");
            bw.write("# unique URLs extracted:"+String.valueOf(MyCrawler.unique_in+MyCrawler.unique_out)+"\n");
            bw.write("# unique URLs within News Site:"+MyCrawler.unique_in+"\n");
            bw.write("# unique URLs outside News Site:"+MyCrawler.unique_out+"\n");
            
            bw.write("Status Codes:\n");
            bw.write("============================\n");
            
            
            bw.write("File Sizes:\n");
            bw.write("============================\n");
            
            bw.write("< 1KB: "+MyCrawler.sizeCount[0]+"\n");
            bw.write("1KB ~ <10KB: "+MyCrawler.sizeCount[1]+"\n");
            bw.write("10KB ~ <100KB: "+MyCrawler.sizeCount[2]+"\n");
            bw.write("100KB ~ <1MB: "+MyCrawler.sizeCount[3]+"\n");
            bw.write(">= 1MB: "+MyCrawler.sizeCount[4]+"\n");
            
            bw.write("Content Types:\n");
            bw.write("============================\n");
            
            Map<String, Integer> map = MyCrawler.contentTypeMap;
            for(String key : map.keySet()) {
                bw.write(key + " : " + map.get(key)+"\n");
            }
            
            bw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Fethced attemps: "+MyCrawler.count);
    }
}