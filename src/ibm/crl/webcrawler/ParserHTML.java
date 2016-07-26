package ibm.crl.webcrawler;

import ibm.crl.se.QueriesEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ADMINIBM on 7/18/2016.
 */
public class ParserHTML {
    private SessionFactory sf;
    private Session session;
    private Transaction transaction;

    public void QueryListCrawler() {
        try {
            String url = "http://data.stackexchange.com/stackoverflow/queries?order_by=popular&page=49&pagesize=50";
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
                        queryLink = nodeOfLink.getLink();
                        String queryIDStr = getMatcher(".*query/([\\d]+)/.*", queryLink);
                        queryId = Integer.parseInt(queryIDStr);
                        queryTitle = nodeOfLink.getStringText().trim();
                    }
                    System.out.println("QueryLink: " + queryLink);
                    System.out.println("QueryId: " + queryId);
                    System.out.println("queryTitle: " + queryTitle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (ParserException e) {
            e.printStackTrace();
            System.err.println("error: parser error");
        }
    }

    public void CrawlQueryItem(int queryId) {
        try {
            String url = "http://data.stackexchange.com/stackoverflow/query/10392/your-posting-quality-reputation-per-posts";
            Parser parser = new Parser(url);
            parser.setEncoding("utf-8");
            NodeList allNodes = parser.parse(null);

            NodeFilter filterForQueryDescp = new CssSelectorNodeFilter("div.info p");
            Node nodesOfQueryDescp = allNodes.extractAllNodesThatMatch(filterForQueryDescp, true).elementAt(0);

            NodeFilter filterForSQLText = new CssSelectorNodeFilter("#queryBodyText");
            Node nodesOfSQLText = allNodes.extractAllNodesThatMatch(filterForSQLText, true).elementAt(0);
            String SQLText = nodesOfSQLText.getText();
            String SQLText1 = nodesOfSQLText.toPlainTextString();
            String SQLText2 = nodesOfSQLText.toHtml();
            String SQLText3 = nodesOfSQLText.toString();
            NodeIterator itr = nodesOfSQLText.getChildren().elements();
            while (itr.hasMoreNodes()) {
                System.out.println(itr.nextNode().toPlainTextString());
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    public String getMatcher(String regex, String souceStr) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(souceStr);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    public void querysDBTest() {
        Configuration cfg = new Configuration().configure();
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
        sf = cfg.buildSessionFactory(serviceRegistry);
        session = sf.openSession();
        transaction = session.beginTransaction();
        QueriesEntity qe = new QueriesEntity();
        qe.setId(11);
        qe.setTitle("title");
        qe.setDescription("description");
        qe.setQueryStatements("queryStatements");
        qe.setArguments("sss");
        session.save(qe);
        transaction.commit();
        session.close();
        sf.close();
    }

    public static void main(String[] args) {
        ParserHTML ph = new ParserHTML();
//        ph.CrawlQueryItem(12);
        ph.querysDBTest();
    }
}
