package com.liu.elasticsearch.utils;

import com.liu.elasticsearch.domain.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 王一宁
 * @date 2020/7/28 9:03
 */
@Component
public class HtmlParseUtil {

    public List<Content> parseJD(String keywords) throws IOException {
        String url = "https://search.jd.com/Search?keyword="+keywords;

        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");

        ArrayList<Content> goodsList = new ArrayList<>();

        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");
        //获取内容
        for (Element el : elements){
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            /*System.out.println("===================");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);*/
            if (""!=img && price!="" && title!=""){
                Content content = new Content();
                content.setImg(img);
                content.setPrice(price);
                content.setTitle(title);
                goodsList.add(content);
            }

        }
        return goodsList;
    }

    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("java").forEach(System.out::println);
    }
}
