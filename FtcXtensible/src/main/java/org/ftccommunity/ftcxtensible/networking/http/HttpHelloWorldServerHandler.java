/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ftccommunity.ftcxtensible.networking.http;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.ftccommunity.ftcxtensible.RobotContext;
import org.ftccommunity.ftcxtensible.networking.ServerSettings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Values;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpHelloWorldServerHandler extends ChannelInboundHandlerAdapter {
    private HashMap<String, String> cache;
    private HashMap<String, String> mimeTypes;
    private ServerSettings serverSettings;
    private RobotContext context;

    public HttpHelloWorldServerHandler(RobotContext ctx) {
        if (ctx == null) {
            throw new NullPointerException();
        }
        serverSettings = ctx.getServerSettings();
        context = ctx;
        cache = new HashMap<>();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String page;

            if (req.getMethod() == HttpMethod.POST) {
                LinkedList<InterfaceHttpData> postData = new LinkedList<>();
                HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), req);
                postRequestDecoder.getBodyHttpDatas();
                for (InterfaceHttpData data : postRequestDecoder.getBodyHttpDatas()) {
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        postData.add(data);
                    }
                }
                context.addPostData(postData);
            }

            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            HttpResponseStatus responseStatus = OK;
            String uri = (req.getUri().equals("/") ? context.getServerSettings().getIndex() : req.getUri());
            if (uri.equals(context.getServerSettings().getHardwareMapJsonPage())) {
                GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization();
                Gson gson = gsonBuilder.create();
                HardwareMap hardwareMap = context.getHardwareMap();
                page = gson.toJson(ImmutableSet.copyOf(hardwareMap.dcMotor));
            } else if (uri.equals(context.getServerSettings().getLogPage())) {
                page = context.getStatus().getLog();
            } else {
                if (cache.containsKey(uri)) {
                    page = cache.get(uri);
                    responseStatus = NOT_MODIFIED;
                } else {
                    try {
                        page = Files.toString(new File(serverSettings.getWebDirectory() + uri), Charsets.UTF_8);
                    } catch (FileNotFoundException e) {
                        Log.e("NET_OP_MODE::", "Cannot find main: + " + serverSettings.getWebDirectory() + uri);
                        page = "File Not Found!";
                        responseStatus = NOT_FOUND;
                    } catch (IOException e) {
                        page = "An Error Occurred!\n" +
                                e.toString();
                        responseStatus = NOT_FOUND;
                        Log.e("NET_OP_MODE::", e.toString(), e);
                    }
                    cache.put(uri, page);
                }
            }

            boolean keepAlive = HttpHeaders.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, responseStatus,
                    Unpooled.wrappedBuffer(page.getBytes(Charsets.UTF_8)));

            String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
            String MIME = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            response.headers().set(CONTENT_TYPE, (MIME != null ? MIME : MimeForExtension(extension)) + (MIME != null && MIME.equals("application/octet-stream") ? "" : "; charset=utf-8"));
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Log.e("HTTP_SERVER::", cause.toString(), cause);
        ctx.close();
    }

    private String MimeForExtension(final String ext) {
        if (mimeTypes == null) {
            HashMap<String, String> mimes = new HashMap<>();
            mimes.put("json", "application/json");
            mimes.put("xml", "application/xml");
            mimes.put("js", "application/javascript");
            mimes.put("jpg", "image/jpeg");
            mimes.put("png", "image/png");
            mimes.put("css", "text/css");
            mimes.put("html", "text/html");
            mimeTypes = mimes;
        }

        if (mimeTypes.containsKey(ext)) {
            return mimeTypes.get(ext);
        } else {
            return "text/plain";
        }
    }
}
