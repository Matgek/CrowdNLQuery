package ibm.crl.webcrawler;

import ibm.crl.se.QueriesEntity;
import org.hibernate.Query;
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
import org.htmlparser.tags.LabelTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ADMINIBM on 7/18/2016.
 */
public class ParserHTML {
    private SessionFactory sf;
    private Session session;

    public ParserHTML(){
        Configuration cfg = new Configuration().configure();
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
        sf = cfg.buildSessionFactory(serviceRegistry);
        session = sf.openSession();
    }

    public void close(){
        session.close();
        sf.close();
    }

    public void QueryListCrawler(int startPage, int endPage) {
        Transaction trans = session.beginTransaction();
        for (int pageNum =startPage; pageNum <= endPage; pageNum ++){
            try {
                String url = "http://data.stackexchange.com/stackoverflow/queries?order_by=popular&page=" + pageNum+ "&pagesize=50";
                Parser parser = new Parser(url);
                parser.setEncoding("utf-8");
                Thread.sleep(2000);
                NodeList allNodes = null;
                allNodes = parser.parse(null);
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
                        int star_count = 0;
                        int views_count = 0;

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


                        NodeFilter filterForStars = new CssSelectorNodeFilter("div.favoritecount");
                        NodeList nodesOfStars = subnodesOfQuery.extractAllNodesThatMatch(filterForStars, true);
                        if (nodesOfStars.size() > 0){
                            Node nodeOfStar = nodesOfStars.elementAt(0);
                            star_count = Integer.parseInt(nodeOfStar.toPlainTextString());
                        }

                        NodeFilter filterForViews = new CssSelectorNodeFilter("span.totalViews span");
                        NodeList nodesOfViews = subnodesOfQuery.extractAllNodesThatMatch(filterForViews, true);
                        if (nodesOfViews.size() > 0){
                            Node nodeOfViews = nodesOfViews.elementAt(0);
                            String views_count_str = nodeOfViews.toPlainTextString();
                            if (views_count_str.endsWith("k")){
                                views_count_str = views_count_str.replace("k", "");
                                views_count = (int) (1000 * Float.parseFloat(views_count_str));
                            }else {
                                views_count = Integer.parseInt(views_count_str);
                            }
                        }

                        QueriesEntity qe = new QueriesEntity();
                        qe.setId(queryId);
                        qe.setUrl(queryLink);
                        qe.setTitle(queryTitle);
                        qe.setStarsCount(star_count);
                        qe.setViewsCount(views_count);
                        session.save(qe);
                        System.out.println("Complete page " + pageNum);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (ParserException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        trans.commit();
        close();
    }

    public void CrawlQueryItem() {
        String queryStr = "from QueriesEntity where description = null";
        Query query = session.createQuery(queryStr);
        List<QueriesEntity> queriesList = query.list();
        Transaction trans = session.beginTransaction();
        int i =0;
        for (QueriesEntity queriesEntity : queriesList){
            try {
                Thread.sleep(2000);
                String url = queriesEntity.getUrl();
                Parser parser = new Parser(url);
                parser.setEncoding("utf-8");
                NodeList allNodes = parser.parse(null);

                NodeFilter filterForQueryDescp = new CssSelectorNodeFilter("div.info p");
                Node nodesOfQueryDescp = allNodes.extractAllNodesThatMatch(filterForQueryDescp, true).elementAt(0);
                String description = nodesOfQueryDescp.toPlainTextString();

                NodeFilter filterForSQLText = new CssSelectorNodeFilter("#queryBodyText");
                Node nodesOfSQLText = allNodes.extractAllNodesThatMatch(filterForSQLText, true).elementAt(0);
                String SQLText_Html = nodesOfSQLText.toHtml();

                NodeFilter filterForPostSignature = new CssSelectorNodeFilter("td.post-signature");
                Node nodeOfPostSig = allNodes.extractAllNodesThatMatch(filterForPostSignature, true).elementAt(1);
                NodeFilter filterForCreateTime = new CssSelectorNodeFilter("span.relativetime");
                Span createTimeSpan = (Span) nodeOfPostSig.getChildren().extractAllNodesThatMatch(filterForCreateTime, true).elementAt(0);
                String createTimeStr = createTimeSpan.getAttribute("title");
                Timestamp createTime = null;
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    createTimeStr = createTimeStr.replace("Z", "");
                    Date createDate = dateFormat.parse(createTimeStr);
                    createTime = new Timestamp(createDate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                NodeFilter filterForAuthor = new CssSelectorNodeFilter("div.user-gravatar32 a");
                LinkTag nodeOfAuthor = (LinkTag) allNodes.extractAllNodesThatMatch(filterForAuthor, true).elementAt(1);
                String auhtorLink = nodeOfAuthor.getLink();
                String authorIdStr = getMatcher("/users/([\\d]+)", auhtorLink);
                int author_id = Integer.parseInt(authorIdStr);
                String author_name = nodeOfAuthor.toPlainTextString();

                queriesEntity.setDescription(description);
                queriesEntity.setQueryStatementsHtml(SQLText_Html);
                queriesEntity.setCreateTime(createTime);
                queriesEntity.setAuthorId(author_id);
                queriesEntity.setAuthorName(author_name);

                session.saveOrUpdate(queriesEntity);
                i ++;
                System.out.println("saved " + i + "th query!");
            } catch (ParserException e) {
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        trans.commit();
        close();
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

    public static void main(String[] args) {
        ParserHTML ph = new ParserHTML();
        ph.CrawlQueryItem();
    }
}
