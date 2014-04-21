/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sopinet.android.nethelper;

import com.github.kevinsawicki.http.HttpRequest;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class SimpleContent {
    public static String PREFIX_URL = "simplecontent_";
    public Context context;
    public String cacheString;
    // long offset = 0 // Sin caché
    // long offset = -1 // Ilimitado
    // long offset = 86400000; // 1 DÍA
    // long offset = 3600000; // 1 HORA
    // long offset = 3; // 3 milisegundos (pruebas)
    public int offset;
    public File cacheDir;

    public SimpleContent(Context context, String cacheString, int offset) {
        this.context = context;
        this.cacheString = cacheString;
        this.offset = offset;
        initCacheDir(this.cacheString);
    }

    public SimpleContent(Context context, String cacheString) {
        // 1 HORA, 3600000 por defecto
        this(context, cacheString, 3600000);
    }

    public void initCacheDir(String cacheString) {
        this.cacheDir = this.context.getExternalFilesDir(null);
        Log.d("TEMA", "DIR: " + this.cacheDir);
    	/*
		if (Environment.getExternalStorageDirectory().equals(
				android.os.Environment.MEDIA_MOUNTED))
			this.cacheDir = new File(Environment.getExternalStorageDirectory(),
					cacheString + "_temp_cache_folder");
		else
			this.cacheDir = this.context.getCacheDir();
		if (!this.cacheDir.exists())
			this.cacheDir.mkdirs();
		*/
    }

    /**
     * Thrown when there were problems contacting the remote API server, either
     * because of a network error, or the server returned a bad status code.
     */
    @SuppressWarnings("serial")
    public static class ApiException extends Exception {
        public ApiException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public ApiException(String detailMessage) {
            super(detailMessage);
        }
    }

    /**
     * Thrown when there were problems parsing the response to an API call,
     * either because the response was empty, or it was malformed.
     */
    @SuppressWarnings("serial")
    public static class ParseException extends Exception {
        public ParseException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /**
     * Get content from URL with get or post request
     * Obtiene el contenido desde una URL con método GET o POST de envío de parámetros.
     *
     * @param url "http://api..."
     * @param data "key1=value1&key2=value2..."
     * @param f file for cache content
     * @return String content from URL
     * @throws ApiException
     */
    public synchronized String UrlContentDO(String url, String data, File f, String method) throws ApiException {
        try {
            HttpRequest request = null;
            if (method.equals("post")) {
                if (data == null) {
                    request = HttpRequest.post(url);
                } else {
                    try {
                        request = HttpRequest.post(url).send(data);
                    } catch(Exception e) {
                        Log.d("TEMA", "Exception: " + e.getMessage());
                        Log.d("TEMA", "printStackTrace: " + e.getMessage());
                        return null;
                    }
                }
            } else if (method.equals("get")) {
                if (data == null) {
                    //.accept("application/json")
                    request = HttpRequest.get(url);
                } else {
                    request = HttpRequest.get(url + "?" + data);
                }
            }
            String body = request.body();

            PrintWriter out = new PrintWriter(f);
            out.print(body);
            out.close();

            return body;
        } catch (IOException e) {
            throw new ApiException("Problem communicating with API", e);
        }
    }

    /**
     * Obtiene el contenido de una URL o el fichero cacheado de la misma
     * Dependiendo de si hay conexión a Internet y el caché aún no se considera expirado
     *
     * @param url "http://api..."
     * @param data "key1=value1&key2=value2..."
     * @param method "post" or "get"
     * @return String content from URL or file
     * @throws ApiException
     */
    @SuppressWarnings("resource")
    public synchronized String UrlContent(String url, String data, String method) throws ApiException {
        String urlComplete = "";
        if (data == null) {
            urlComplete = url;
        } else {
            urlComplete = url + "?" + data;
        }
        Log.d("TEMA", "URLCOMPLETE: "+urlComplete);
        String filename = String.valueOf(PREFIX_URL + StringHelper.md5(urlComplete));

        File f = new File(cacheDir, filename);
        InputStream is;
        try {
            is = new FileInputStream(f);

            long lastMod = f.lastModified();
            long now = System.currentTimeMillis();

            if (now - lastMod > offset && offset != -1) {
                if (NetHelper.isOnline(this.context)) {
                    return UrlContentDO(url, data, f, method);
                    // Si está desactualizado pero no hay internet se muestra el fichero
                } else {
                    return NetHelper.getContents(f);
                }
            } else {
                return NetHelper.getContents(f);
            }
            //Devolvemos el fichero siempre
        } catch (FileNotFoundException e1) {
            // NADA: SEGUIMOS, no se encontró el fichero
            // Si no se encuentra y hay internet se obtiene
            if (NetHelper.isOnline(this.context)) {
                return UrlContentDO(url, data, f, method);
                // Si no se encuentra y no hay internet se muestra un error
            } else {
                return "no network";
            }
        }
    }

    public synchronized String getUrlContent(String url, String data) throws ApiException {
        return UrlContent(url, data, "get");
    }

    public synchronized String postUrlContent(String url, String data) throws ApiException {
        return UrlContent(url, data, "post");
    }

    public static synchronized boolean nowPostJSON(String url, String json) {
        HttpClient httpClient = new DefaultHttpClient();

        Log.d("TEMA", "json: "+json);

        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(url);
            StringEntity params =new StringEntity(json);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            response = httpClient.execute(request);

            // handle response here...
        }catch (Exception ex) {
            Log.d("TEMA", "ex1: "+ex.getMessage());
            // handle exception here
        }

        String serverResponse = null;
        try {
            serverResponse = EntityUtils.toString(response.getEntity());
        } catch (org.apache.http.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("TEMA", "BODY: " + serverResponse);

        httpClient.getConnectionManager().shutdown();

        return true;
    }

    public void clearCache() {
        // clear SD cache
        File[] files = this.cacheDir.listFiles();
        for (File f : files)
            f.delete();
    }

    @SuppressWarnings("resource")
    public synchronized boolean clearFile(String url, String data) {
        String urlComplete = "";
        if (data == null) {
            urlComplete = url;
        } else {
            urlComplete = url + "?" + data;
        }
        String filename = String.valueOf(PREFIX_URL + StringHelper.md5(urlComplete));
        File f = new File(cacheDir, filename);
        InputStream is;
        try {
            is = new FileInputStream(f);

            if (NetHelper.isOnline(this.context)) {
                f.delete();
                return true;
            } else {
                return false;
            }
            //Devolvemos el fichero siempre
        } catch (FileNotFoundException e1) {
            return false;
        }
    }
}