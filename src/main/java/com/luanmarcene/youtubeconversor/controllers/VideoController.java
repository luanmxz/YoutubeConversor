package com.luanmarcene.youtubeconversor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.luanmarcene.youtubeconversor.services.ConversorService;

import java.util.List;

@Controller
@RequestMapping("/api")
public class VideoController {

    @Autowired
    ConversorService conversorService = new ConversorService();

    @PostMapping("/converter")
    public void converterVideo(@RequestBody UrlRequest urlRequest) {

        List<String> url = urlRequest.getUrl();

        url.forEach(u -> {
            try {
                conversorService.convertAndDownloadVideo(u);
            } catch (Exception e) {
                throw e;
            }

        });
    }

    static class UrlRequest {
        private List<String> url;

        public List<String> getUrl() {
            return this.url;
        }
    }

}
