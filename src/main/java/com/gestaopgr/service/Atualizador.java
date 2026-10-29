package com.gestaopgr.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Atualizador {
    public static final String VERSAO_ATUAL = "1.0.0";
    public static final String GITHUB_OWNER = "maxwellsantos";
    public static final String GITHUB_REPO = "gestao-pgr";
    public static final String GITHUB_API_URL = "https://api.github.com/repos/" + GITHUB_OWNER + "/" + GITHUB_REPO + "/releases/latest";

    private String versaoDisponivel = "";
    private String downloadUrl = "";
    private String releaseNotes = "";

    public interface ProgressCallback {
        void onProgress(int percent, String message);
    }

    public boolean verificarAtualizacao(ProgressCallback callback) {
        try {
            callback.onProgress(10, "Conectando ao GitHub...");
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            callback.onProgress(30, "Verificando versão disponível...");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                callback.onProgress(50, "Analisando resposta...");

                // Parse JSON manually (simple extraction)
                String json = response.toString();
                versaoDisponivel = extractJsonValue(json, "tag_name");
                releaseNotes = extractJsonValue(json, "body");
                downloadUrl = extractDownloadUrl(json);

                callback.onProgress(70, "Comparando versões...");

                if (!versaoDisponivel.isEmpty() && !versaoDisponivel.equals("v" + VERSAO_ATUAL) && !versaoDisponivel.equals(VERSAO_ATUAL)) {
                    callback.onProgress(100, "Nova versão disponível: " + versaoDisponivel);
                    return true;
                } else {
                    callback.onProgress(100, "Você já está na versão mais recente.");
                    return false;
                }
            } else {
                callback.onProgress(100, "Não foi possível verificar atualizações. Código: " + conn.getResponseCode());
                return false;
            }
        } catch (Exception e) {
            callback.onProgress(100, "Erro ao verificar: " + e.getMessage());
            return false;
        }
    }

    public boolean baixarAtualizacao(String destino, ProgressCallback callback) {
        try {
            if (downloadUrl.isEmpty()) {
                callback.onProgress(100, "URL de download não disponível.");
                return false;
            }

            callback.onProgress(5, "Iniciando download...");
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            int fileSize = conn.getContentLength();
            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(destino);

            byte[] buffer = new byte[8192];
            int bytesRead;
            int totalRead = 0;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (fileSize > 0) {
                    int percent = (int) ((totalRead * 100.0) / fileSize);
                    callback.onProgress(Math.min(percent, 99), "Baixando... " + (totalRead / 1024) + " KB");
                }
            }
            out.close();
            in.close();

            callback.onProgress(100, "Download concluído!");
            return true;
        } catch (Exception e) {
            callback.onProgress(100, "Erro no download: " + e.getMessage());
            return false;
        }
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int colon = json.indexOf(":", idx);
        if (colon == -1) return "";
        int start = json.indexOf("\"", colon);
        if (start == -1) return "";
        int end = json.indexOf("\"", start + 1);
        if (end == -1) return "";
        return json.substring(start + 1, end);
    }

    private String extractDownloadUrl(String json) {
        // Look for browser_download_url in assets
        int idx = json.indexOf("browser_download_url");
        if (idx == -1) return "";
        int colon = json.indexOf(":", idx);
        if (colon == -1) return "";
        int start = json.indexOf("\"", colon);
        if (start == -1) return "";
        int end = json.indexOf("\"", start + 1);
        if (end == -1) return "";
        return json.substring(start + 1, end);
    }

    public String getVersaoDisponivel() { return versaoDisponivel; }
    public String getDownloadUrl() { return downloadUrl; }
    public String getReleaseNotes() { return releaseNotes; }
    public String getVersaoAtual() { return VERSAO_ATUAL; }
}
