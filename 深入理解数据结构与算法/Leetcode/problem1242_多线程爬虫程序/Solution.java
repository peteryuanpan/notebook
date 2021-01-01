class Solution {

    private final int nThreads = 30;
    private final ThreadPoolExecutor thp = new ThreadPoolExecutor(nThreads, nThreads, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    private final Set<String> urlSet = new HashSet<>(1024);
    private final AtomicInteger nMission = new AtomicInteger(0);
    private String startDomain;
    private HtmlParser htmlParser;

    private void crawl(String startUrl) {
        List<String> urls = htmlParser.getUrls(startUrl);
        for (String url : urls) {
            String domain = url.split("/", 4)[2];
            if (startDomain.equals(domain)) {
                if (!urlSet.contains(url)) {
                    synchronized (this) {
                        if (!urlSet.contains(url)) {
                            urlSet.add(url);
                            nMission.incrementAndGet();
                            thp.submit(() -> crawl(url));
                        }
                    }
                }
            }
        }
        nMission.decrementAndGet();
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        this.htmlParser = htmlParser;
        urlSet.add(startUrl);
        startDomain = startUrl.split("/", 4)[2];
        nMission.incrementAndGet();
        thp.submit(() -> crawl(startUrl));
        while (nMission.get() > 0) {
            Thread.yield();
        }
        thp.shutdown();
        return new ArrayList<>(urlSet);
    }
}
