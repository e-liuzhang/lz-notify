package com.liuzhang.weathernotify.time;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


/**
 * @author shangang_luo
 * @date 2021/12/20
 */
@Slf4j
@Configuration
@EnableScheduling
public class WeatherSchedule {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${auth.dingding}")
    private String dingUrl;

    /**
     * 每天1点执行
     */
    @PostConstruct
    @Scheduled(cron = "0 0 1 * * ?")
    private void updateSiteStatisticsAttrs() {
        sendDingDing("23");
    }

    public void sendDingDing(String msg) {
        String uri = dingUrl;
        Map<String, String> paramMap = new HashMap<>(16);
        paramMap.put("msgtype", "text");
        paramMap.put("text", "{\"content\": " + msg + "}");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=utf-8");
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(paramMap, httpHeaders);
        String result = restTemplate.postForObject(uri, httpEntity, String.class);
        JSONObject jsonObect = JSON.parseObject(result);
        if (!"0".equals(jsonObect.get("errcode").toString())) {
            log.error("Reason : {} , errcode is {} , errmsg is {}", "fail to sendDingDing", jsonObect.get("errcode"), jsonObect.get("errmsg"));
        }


    }
}
