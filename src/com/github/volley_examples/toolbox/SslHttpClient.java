/**
 * Copyright 2013 Ognyan Bankov
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

package com.github.volley_examples.toolbox;

import java.io.InputStream;
import java.security.GeneralSecurityException;

import khandroid.ext.apache.http.client.params.ClientPNames;
import khandroid.ext.apache.http.conn.ClientConnectionManager;
import khandroid.ext.apache.http.conn.scheme.PlainSocketFactory;
import khandroid.ext.apache.http.conn.scheme.Scheme;
import khandroid.ext.apache.http.conn.scheme.SchemeRegistry;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;
import khandroid.ext.apache.http.impl.conn.PoolingClientConnectionManager;
import khandroid.ext.apache.http.params.HttpParams;


public class SslHttpClient extends DefaultHttpClient {
    private static final int HTTP_DEFAULT_PORT = 80;
    private static final String HTTP_SCHEME = "http";
    private static final int HTTP_DEFAULT_SSL_PORT = 443;
    private static final String HTTP_SSL_SCHEME = "https";
    
    
    private InputStream keyStore;
    private String keyStorePassword;
    
    @SuppressWarnings("unused")
    private SslHttpClient() {
        // no parameterless constructor
    }

    
    public SslHttpClient(InputStream keyStore, String keyStorePassword) {
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
    }   
    

    public SslHttpClient(ClientConnectionManager conman, InputStream keyStore, String keyStorePassword) {
        super(conman);
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
    }


    public SslHttpClient(final ClientConnectionManager conman, 
                                final HttpParams params,
                                InputStream keyStore, 
                                String keyStorePassword
                                ) {
        super(conman, checkForInvalidParams(params));
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
    }
    
    
    public SslHttpClient(final HttpParams params, InputStream keyStore, String keyStorePassword) {
        super(null, checkForInvalidParams(params));
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
    }   
    

    @SuppressWarnings("deprecation") // we check intentionally for an old parameter
    private static HttpParams checkForInvalidParams(HttpParams params) {
        String className = (String) params.getParameter(ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME);
        if (className != null) {
            throw new IllegalArgumentException("Don't try to pass ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME parameter. We use our own connection manager factory anyway...");
        }
        
        return params;
    }
 
    
    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        
        PlainSocketFactory pfs = PlainSocketFactory.getSocketFactory();
        Scheme s = new Scheme(HTTP_SCHEME, HTTP_DEFAULT_PORT, pfs);
        registry.register(s);
        
        PoolingClientConnectionManager ret; 
        try {
            registry.register(new Scheme(HTTP_SSL_SCHEME, HTTP_DEFAULT_SSL_PORT, new SslSocketFactory(keyStore, keyStorePassword)));
            ret = new PoolingClientConnectionManager(registry); 
        } catch (GeneralSecurityException e) {
            ret = null;
        }
        
        
        return ret;
    }
}


