import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebEngine {
    private final Map<String, String> index = new HashMap<>();
    private final Set<String> visitedUrls = new HashSet<>();

    // Crawl and fetch content recursively
    public void crawl(String url, int depth) {
        if (depth == 0 || visitedUrls.contains(url)) {
            return;
        }

        try {
            // Mark URL as visited
            visitedUrls.add(url);

            System.out.println("Crawling: " + url);

            // Fetch content
            URL website = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }
            in.close();

            // Index the content
            index.put(url, content.toString());

            // Extract links
            List<String> links = extractLinks(content.toString());
            for (String link : links) {
                crawl(link, depth - 1); // Recursively crawl links
            }

        } catch (Exception e) {
            System.out.println("Error crawling " + url + ": " + e.getMessage());
        }
    }

    // Extract links from HTML content
    private List<String> extractLinks(String html) {
        List<String> links = new ArrayList<>();
        String regex = "href=[\"'](http[s]?://.*?)[\"']";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            links.add(matcher.group(1));
        }
        return links;
    }

    // Search for a keyword
    public void search(String keyword) {
        System.out.println("Search results for: " + keyword);
        for (Map.Entry<String, String> entry : index.entrySet()) {
            if (entry.getValue().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("Found in: " + entry.getKey());
            }
        }
    }

    public static void main(String[] args) {
        WebEngine webEngine = new WebEngine();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Start Crawling");
            System.out.println("2. Search");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter seed URL: ");
                    String seedUrl = scanner.nextLine();
                    System.out.print("Enter crawl depth: ");
                    int depth = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    webEngine.crawl(seedUrl, depth);
                }
                case 2 -> {
                    System.out.print("Enter keyword to search: ");
                    String keyword = scanner.nextLine();
                    webEngine.search(keyword);
                }
                case 3 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
