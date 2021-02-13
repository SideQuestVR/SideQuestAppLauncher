package com.sidequest.launcher.gui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.sidequest.launcher.R;

/**
 * Store view (shows AppLab)
 */
public class StoreActivity extends CustomFragment
{
    private static final String target_url = "https://applab.games/";
    private Context mContext;
    private WebView mWebview;
    private WebView mWebviewPop;
    private FrameLayout mContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.storeactivity, container, false);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        mWebview = rootView.findViewById(R.id.webview);
        mContainer = rootView.findViewById(R.id.webview_frame);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        mWebview.setWebViewClient(new UriWebViewClient());
        mWebview.setWebChromeClient(new UriChromeClient());
        mWebview.loadUrl(target_url);

        mContext = rootView.getContext().getApplicationContext();

        return rootView;
    }

    @Override
    public boolean onBackPressed()
    {
        if (mWebview.canGoBack())
        {
            mWebview.goBack();
            return false;
        }
        else
        {
            return super.onBackPressed();
        }
    }


    private class UriWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            if (host.equals("m.facebook.com"))
            {
                return false;
            }
            if (host.equals("sidequestvr.com"))
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            if (mWebviewPop != null)
            {
                mWebviewPop.setVisibility(View.GONE);
                mContainer.removeView(mWebviewPop);
                mWebviewPop = null;
            }
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
        {
        }
    }

    class UriChromeClient extends WebChromeClient
    {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg)
        {
            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setSavePassword(false);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window)
        {
        }
    }
}
