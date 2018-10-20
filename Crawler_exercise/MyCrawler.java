package Crawler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern.compile(".*(css|feed|rss|svg|js|mp3|zip|gz|vcf|xml|json|ico).*");
    String crawlStorageFolder = "data/srp_hw2/";
    String fetchFile = "fetch_wsj.csv";
    String visitFile = "visit_wsj.csv";
    String urlsFile = "urls_wsj.csv";
    public static int count = 0;
    public static int sizeCount[] = new int[5];
    public static Map<String, Integer> contentTypeMap = new HashMap<>();
    public static int total = 0, unique_in = 0, unique_out = 0;
    public static Set<String> set = new HashSet<>();
    
    
  /**
    * This method receives two parameters. The first parameter is the page
    * in which we have discovered this new url and the second parameter is
    * the new url. You should implement this function to specify whether
    * the given url should be crawled or not (based on your crawling logic).
    * In this example, we are instructing the crawler to ignore urls that
    * have css, js, git, ... extensions and to only accept urls that start
    * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
    * referringPage parameter to make the decision.
    */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        total++;
        try{
            synchronized(this){
                BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder+urlsFile, true));
                
                if(href.startsWith("http://"+Controller.siteURL) || href.startsWith("https://" +Controller.siteURL)) {
                    bw.write(url.getURL().replace(",", "-") + ", OK\n");
                    if(!set.contains(href)) {
                        unique_in++;
                        set.add(href);
                    }
                }
                    
                else {
                    bw.write(url.getURL().replace(",", "-")+ ", N_OK\n");
                    unique_out++;
                    set.add(href);
                }
                bw.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        
        if(!href.contains(Controller.siteURL)) {
            return false;
        }
        return !FILTERS.matcher(href).matches();
    }
    
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);
        
        int size = page.getContentData().length;
        int sizeKB = size/1024;
        int numOfOutlink = page.getParseData().getOutgoingUrls().size();
        String contentType = page.getContentType().split(";")[0];
        
        try{
            synchronized (this){
                if(sizeKB<1)
                    sizeCount[0]++;
                else if(1 <= sizeKB && sizeKB < 10)
                    sizeCount[1]++;
                else if(10 <= sizeKB && sizeKB < 100)
                    sizeCount[2]++;
                else if(100 <= sizeKB && sizeKB <1024)
                    sizeCount[3]++;
                else
                    sizeCount[4]++;
                
                contentTypeMap.put(contentType, contentTypeMap.getOrDefault(contentType, 0)+1);
                
                BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder + visitFile, true));
                bw.write(url.replace(",", "_") + "," + size + "," + numOfOutlink + "," + contentType +"\n");
                bw.close();
                System.out.println(crawlStorageFolder + visitFile);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        
        
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        } 
    }
    
    
    /**
     * This function is called once the header of a page is fetched. It can be
     * overridden by sub-classes to perform custom logic for different status
     * codes. For example, 404 pages can be logged, etc.
     *
     * @param webUrl WebUrl containing the statusCode
     * @param statusCode Html Status Code number
     * @param statusDescription Html Status COde description
     */
    
    @Override
    public void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription){
        count++;
        try{
            synchronized(this){
                BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder+fetchFile,true));
                bw.write(webUrl.getURL().replace(",", "_")+","+statusCode+"\n");
                bw.close();
                }
            } catch(IOException e){
                e.printStackTrace();
            }
    }
}
