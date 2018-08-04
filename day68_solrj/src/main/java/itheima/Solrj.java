package itheima;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class Solrj {
    /**
     * 需求:使用solrj实现简单查询
     * 条件:主查询条件
     */
    /**
     * 需求：使用solrj实现简单查询
     * 条件：主查询条件
     *
     * @throws Exception
     */
    @Test
    public void simpleQuery() throws Exception {


        //创建参数封装对象，所有查询参数都必须封装到这个对象
        SolrQuery solrQuery = new SolrQuery();
        //设置主查询条件参数即可
        solrQuery.set("q", "*:*");

        this.executeAndPrintResult(solrQuery);
    }


    /**
     * 需求：使用solrj实现复杂查询
     * 条件：
     * 1，主查询条件
     * 2，过滤条件
     * 3，排序
     * 4，分页
     * 5，映射字段过滤
     * 6，默认查询字段
     * 7，高亮设置
     *
     * @throws Exception
     */
    @Test
    public void comQuery() throws Exception {


        //创建参数封装对象,所有查询参数必须封装到这个对象
        SolrQuery solrQuery = new SolrQuery();

        //设置主查询条件参数即可
        //1,q主查询条件
        solrQuery.setQuery("浴巾");

        //2.fq过滤查询
        //1)需求过滤查询 商品类别 都是 时尚卫浴的商品
        solrQuery.addFilterQuery("product_catalog_name:时尚卫浴");
        //2)需求过滤查询,商品价格20元以上
        solrQuery.addFilterQuery("product_price:[* TO 20]");

        //3.sort排序查询
        solrQuery.setSort("product_price",SolrQuery.ORDER.desc);

        //4,start rows 分页查询
        solrQuery.setStart(0);
        solrQuery.setRows(20);

        //5.fl字段映射查询
        //字段之间是有空格,或者逗号都可
        //solrQuery.setFields("product_name,product_price");

        //6.df 设置默认查询字段
        //缺省字段一般设置为复制域
        solrQuery.set("df","product_keywords");

        //7.高亮查询
        //1)开启高亮
        solrQuery.setHighlight(true);
        //2)指定高亮字段
        solrQuery.addHighlightField("product_name");
        //3)设置高亮前缀
        solrQuery.setHighlightSimplePre("<font color='red'>");
        //4)设置高亮后缀
        solrQuery.setHighlightSimplePost("</font>");
        this.executeAndPrintResult(solrQuery);


    }


    /**
     * 执行查询
     */
    private void executeAndPrintResult(SolrQuery solrQuery) throws Exception {
        //指定远程服务器地址
        //注意:
        //如果索引库名称是默认名称collection1,直接写索引仓库地址即可
        //如果索引库名称被修改,连接地址后面必须格式索引库名称
        String url = "http://localhost:8080/solr/item";

        //创建服务对象,连接远程solr服务
        SolrServer solrServer = new HttpSolrServer(url);

        //使用solr服务查询索引库
        QueryResponse response = solrServer.query(solrQuery);

        //获取查询文档集合
        SolrDocumentList results = response.getResults();

        //获取命中总记录数
        long numFound = results.getNumFound();
        System.out.println("命中总记录数:" + numFound);

        //循环文档集合
        for (SolrDocument doc : results) {
            //获取id
            String id = (String) doc.get("id");
            System.out.println("文档域id:" + id);

            //商品标题
            String product_name = (String) doc.get("product_name");

            //获取高亮
            Map<String,Map<String,List<String>>> highlighting = response.getHighlighting();
            //第一个map的key就是文档id
            Map<String,List<String>> stringListMap = highlighting.get(id);
            //第二个map的key就是高亮字段
            List<String> hList = stringListMap.get("product_name");

            //判断高亮是否存在
            if (hList!=null && hList.size()>0){
                product_name = hList.get(0);
            }
            System.out.println("商品标题:" + product_name);

            //商品价格
            Float product_price = (Float) doc.get("product_price");
            System.out.println("商品价格:" + product_price);

            //描述
            String product_description = (String) doc.get("product_description");
            System.out.println("商品描述:" + product_description);

            //商品图片
            String product_picture = (String) doc.get("product_picture");
            System.out.println("商品图片:" + product_picture);

            //商品分类
            String product_catalog_name = (String) doc.get("product_catalog_name");
            System.out.println("商品分类:" + product_catalog_name);
        }
    }
}