package ibm.crl.se;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by ADMINIBM on 7/25/2016.
 */
@Entity
@Table(name = "queries", schema = "stack_exchange_querys", catalog = "")
public class QueriesEntity {
    private int id;
    private String title;
    private String description;
    private Integer starsCount;
    private Integer viewsCount;
    private String queryStatements;
    private Timestamp createTime;
    private Integer authorId;
    private String authorName;
    private String arguments;
    private Timestamp crawledTime;
    private String queryStatementsHtml;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "title", nullable = true, length = -1)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "stars_count", nullable = true)
    public Integer getStarsCount() {
        return starsCount;
    }

    public void setStarsCount(Integer starsCount) {
        this.starsCount = starsCount;
    }

    @Basic
    @Column(name = "views_count", nullable = true)
    public Integer getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    @Basic
    @Column(name = "query_statements", nullable = true, length = -1)
    public String getQueryStatements() {
        return queryStatements;
    }

    public void setQueryStatements(String queryStatements) {
        this.queryStatements = queryStatements;
    }

    @Basic
    @Column(name = "create_time", nullable = true)
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "author_id", nullable = true)
    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    @Basic
    @Column(name = "author_name", nullable = true, length = 50)
    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Basic
    @Column(name = "arguments", nullable = true, length = -1)
    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    @Basic
    @Column(name = "crawled_time", nullable = false)
    public Timestamp getCrawledTime() {
        return crawledTime;
    }

    public void setCrawledTime(Timestamp crawledTime) {
        this.crawledTime = crawledTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueriesEntity that = (QueriesEntity) o;

        if (id != that.id) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (starsCount != null ? !starsCount.equals(that.starsCount) : that.starsCount != null) return false;
        if (viewsCount != null ? !viewsCount.equals(that.viewsCount) : that.viewsCount != null) return false;
        if (queryStatements != null ? !queryStatements.equals(that.queryStatements) : that.queryStatements != null)
            return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        if (authorId != null ? !authorId.equals(that.authorId) : that.authorId != null) return false;
        if (authorName != null ? !authorName.equals(that.authorName) : that.authorName != null) return false;
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        if (crawledTime != null ? !crawledTime.equals(that.crawledTime) : that.crawledTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (starsCount != null ? starsCount.hashCode() : 0);
        result = 31 * result + (viewsCount != null ? viewsCount.hashCode() : 0);
        result = 31 * result + (queryStatements != null ? queryStatements.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (authorId != null ? authorId.hashCode() : 0);
        result = 31 * result + (authorName != null ? authorName.hashCode() : 0);
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + (crawledTime != null ? crawledTime.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "query_statements_html", nullable = true, length = -1)
    public String getQueryStatementsHtml() {
        return queryStatementsHtml;
    }

    public void setQueryStatementsHtml(String queryStatementsHtml) {
        this.queryStatementsHtml = queryStatementsHtml;
    }
}
