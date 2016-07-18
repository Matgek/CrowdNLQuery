package ibm.crl.webcrawler;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ADMINIBM on 7/18/2016.
 */
public class ParserHTML {

    public String getMatcher(String regex, String souceStr) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(souceStr);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    public void Crawler() {
        try {
            String url = "http://data.stackexchange.com/stackoverflow/queries?order_by=popular&page=3&pagesize=50";
            Parser parser = new Parser(url);
            parser.setEncoding("utf-8");
            NodeList allNodes = parser.parse(null);
            NodeFilter filterForQueryList = new CssSelectorNodeFilter("ul.querylist li");
            NodeList nodesOfQueryList = allNodes.extractAllNodesThatMatch(filterForQueryList, true);
            int extractNum = nodesOfQueryList.size();
            if (extractNum == 0) return;
            NodeIterator queryListItr = nodesOfQueryList.elements();
            while (queryListItr.hasMoreNodes()) {
                try {
                    String queryTitle = null;
                    int queryId = 0;
                    String queryLink = null;

                    Node nodeOfQuery = queryListItr.nextNode();
                    NodeList subnodesOfQuery = nodeOfQuery.getChildren();
                    NodeFilter filterForQueryTitle = new CssSelectorNodeFilter("div.title a");
                    NodeList nodesOfLink = subnodesOfQuery.extractAllNodesThatMatch(filterForQueryTitle, true);
                    if (nodesOfLink.size() > 0) {
                        LinkTag nodeOfLink = (LinkTag) nodesOfLink.elementAt(0);
                        String link = nodeOfLink.getLink();
                        String queryIDStr = getMatcher(".*query/([\\d]+)/.*", link);
                        queryId = Integer.parseInt(queryIDStr);
                        queryTitle = nodeOfLink.getStringText().trim();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (ParserException e) {
            e.printStackTrace();
            System.err.println("error: parser error");
        }

    }
}
