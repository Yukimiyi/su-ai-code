package com.yukina.suaicode.service;


import jakarta.servlet.http.HttpServletResponse;

public interface ProjectDownloadService {
    void downloadProject(String projectPath, String downloadFileName, HttpServletResponse response);
}
