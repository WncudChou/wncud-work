package com.wncud.grok;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;
import oi.thekraken.grok.api.exception.GrokException;

/**
 * Created by zhouyajun on 2016/2/29.
 */
public class GrokInvoker {
    public static void main(String[] args) throws Exception {
        Grok grok = Grok.create("D:\\workspace\\wncud-work\\src\\main\\resources\\grok\\patterns");

        grok.compile("%{COMBINEDAPACHELOG}");
        String log = "112.169.19.192 - - [06/Mar/2013:01:36:30 +0900] \"GET / HTTP/1.1\" 200 44346 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22\"";
        Match gm = grok.match(log);
        gm.captures();
        System.out.println(gm.toJson());

        grok.compile("%{NGINXLOG}");
        log = "11.11.1.1 - - [01/Mar/2013:12:23:53 +0800] \"GET /v1/api HTTP/1.1\" api.xx.com 200 4003 \"https://api.xx.com/v1/api\" \"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)\" \"-\" 10.1.1.1:80 200 - \"text/html;charset=UTF-8\" 0.023 > 0.023";
        log = "127.0.0.1 - - [29/Feb/2016:17:19:13 +0800] \"GET /api/account/session HTTP/1.1\" localhost 404 571 \"http://localhost/login.html\" \"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36\" \"-\" - - - \"-\" - > 0.000";
        gm = grok.match(log);
        gm.captures();
        System.out.println(gm.toJson());

        System.out.println("=======================");
        String value = "$http_x_forwarded_for|$http_host|$remote_addr - $remote_user [$time_local]|$request_uri|$request_body|$status|$bytes_sent|$request_time|$upstream_response_time|$http_user_agent|$http_referer|$http_token|$http_storeId|$http_version|$http_sysVersion|$http_platform|$gzip_ratio|$http_device|$http_venderId|$http_apiVersion|$http_channelId";
        String[] xx = value.replace("$", "").split("\\|");
        for(int i = 0; i < xx.length; i++){
            System.out.println("\"" + xx[i] + "\",");
        }
    }
}