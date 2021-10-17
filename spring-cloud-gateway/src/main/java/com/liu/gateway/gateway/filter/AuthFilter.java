package com.liu.gateway.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.liu.gateway.gateway.utils.EncryptUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /*** 1.获取令牌内容 */
        HttpHeaders headers = exchange.getRequest().getHeaders();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            // 无token访问网关内资源的情况，目 前仅有uua服务直接暴露
            return chain.filter(exchange);
        }
        OAuth2Authentication oauth2Authentication = (OAuth2Authentication) authentication;
        Authentication userAuthentication = oauth2Authentication.getUserAuthentication();
        Object principal = userAuthentication.getPrincipal();

        /*** 2.组装明文token，转发给微服务，放入header，名称为json‐token */
        List<String> authorities = userAuthentication.getAuthorities().stream().map(e->{
            return ((GrantedAuthority)e).getAuthority();
        }).collect(Collectors.toList());

        OAuth2Request oAuth2Request = oauth2Authentication.getOAuth2Request();
        Map<String, String> requestParameters = oAuth2Request.getRequestParameters();
        Map<String, Object> jsonToken = new HashMap<>(requestParameters);
        if (userAuthentication != null) {
            jsonToken.put("principal", userAuthentication.getName());
            jsonToken.put("authorities", authorities);
        }

        headers.add("json‐token", EncryptUtil.encodeUTF8StringBase64(JSON.toJSONString(jsonToken)));

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
