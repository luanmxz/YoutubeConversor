package com.luanmarcene.youtubeconversor.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;

@Service
public class ConversorService {

    public void convertAndDownloadVideo(String url) {

        Properties properties = new Properties();

        try {
            FileInputStream fs = new FileInputStream(
                    "src\\main\\resources\\config.properties");
            properties.load(fs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String outputFolder = properties.getProperty("outputfolder");
        YoutubeDownloader downloader = new YoutubeDownloader();

        String videoID = extractVideoIDFromUrl(url);

        RequestVideoInfo request = new RequestVideoInfo(videoID)
                .callback(new YoutubeCallback<VideoInfo>() {
                    @Override
                    public void onFinished(VideoInfo videoInfo) {
                        System.out.println("Finished parsing");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getMessage());
                    }
                })
                .async();

        Response<VideoInfo> response = downloader.getVideoInfo(request);
        VideoInfo video = response.data(); // will block thread

        RequestVideoFileDownload requestDownload = new RequestVideoFileDownload(video.bestVideoWithAudioFormat())
                // optional params
                .saveTo(new File(outputFolder))
                .renameTo(video.details().title()) // by default "videos" directory
                .overwriteIfExists(true)
                .maxRetries(2); // if false and file with such name already exits sufix will be added
                                // video(1).mp4
        Response<File> responseDownload = downloader.downloadVideoFile(requestDownload);
        File data = responseDownload.data();

        System.out.println("Finished download of video: " + video.details().title() + " successfully");

    }

    public static String extractVideoIDFromUrl(String url) {

        String videoIdMarker = "v=";
        int startIndex = url.indexOf(videoIdMarker);

        if (startIndex != -1) {
            startIndex += videoIdMarker.length();

            int endIndex = url.indexOf("&", startIndex);
            if (endIndex == -1) {
                return url.substring(startIndex);
            } else {
                return url.substring(startIndex, endIndex);
            }
        }

        return null;

    }

}
