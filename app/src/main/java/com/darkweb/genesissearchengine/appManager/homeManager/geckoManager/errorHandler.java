package com.darkweb.genesissearchengine.appManager.homeManager.geckoManager;

import androidx.appcompat.app.AppCompatActivity;
import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.example.myapplication.R;
import org.mozilla.geckoview.WebRequestError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class errorHandler
{
    private AppCompatActivity mContext;
    private String mErrorTemplate;

    public String createErrorPage(final int category, final int error,AppCompatActivity mContext,String url, InputStream mResourceURL) {
        this.mContext = mContext;
        if (mErrorTemplate == null) {
            StringBuilder builder = new StringBuilder();
            try  {

                InputStream stream = mResourceURL;
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }

                mErrorTemplate = builder.toString();
            } catch (Exception ex) {
                return null;
            }
        }
        String title = helperMethod.getHost(url);

        if(url==null){
            url = "Hidden Error";
        }
        if(title==null){
            title = "Hidden Error";
        }

        String replaceUrl = errorToString(error).replace("$URL",url);
        String errorPage = createErrorPage("CODE : " + categoryToString(category) + " <br>TYPE : " + replaceUrl.replace("$TITLE",title),url,mResourceURL);
        errorPage = translateMessage(errorPage,"CODE : " + categoryToString(category));
        return errorPage;
    }

    private String translateMessage(String message,String error){
        message = message.replace("$ERROR_M1",mContext.getString(R.string.ERROR_M1));
        message = message.replace("$ERROR_M2",mContext.getString(R.string.ERROR_M2));
        message = message.replace("$ERROR_M3",mContext.getString(R.string.ERROR_M3));
        message = message.replace("$ERROR_M4",mContext.getString(R.string.ERROR_M4));
        message = message.replace("$ERROR_M5",mContext.getString(R.string.ERROR_M5));
        message = message.replace("$ERROR_M6",mContext.getString(R.string.ERROR_M6));
        message = message.replace("$ERROR", error);

        return message;
    }

    private String errorToString(final int error) {
        switch (error) {
            case WebRequestError.ERROR_UNKNOWN:
                return "ERROR_UNKNOWN | The network request tried to use an unknown socket type"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            ;
            case WebRequestError.ERROR_SECURITY_SSL:
                return "ERROR_SECURITY_SSL | SSL negotiation problems has occured";
            case WebRequestError.ERROR_SECURITY_BAD_CERT:
                return "ERROR_SECURITY_BAD_CERT | SSL certificate validation error has occured";
            case WebRequestError.ERROR_NET_RESET:
                return "ERROR_NET_RESET | The connection was reset";
            case WebRequestError.ERROR_NET_INTERRUPT:
                return "ERROR_NET_INTERRUPT | The network connection was interrupted";
            case WebRequestError.ERROR_NET_TIMEOUT:
                return "ERROR_NET_TIMEOUT | The network request timed out";
            case WebRequestError.ERROR_CONNECTION_REFUSED:
                return "ERROR_CONNECTION_REFUSED | The network request was refused by the server";
            case WebRequestError.ERROR_UNKNOWN_PROTOCOL:
                return "ERROR_UNKNOWN_PROTOCOL | An unknown protocol was specified";
            case WebRequestError.ERROR_UNKNOWN_HOST:
                return "ERROR_UNKNOWN_HOST | The host could not be resolved";
            case WebRequestError.ERROR_UNKNOWN_SOCKET_TYPE:
                return "ERROR_UNKNOWN_SOCKET_TYPE | The network request tried to use an unknown socket type";
            case WebRequestError.ERROR_UNKNOWN_PROXY_HOST:
                return "ERROR_UNKNOWN_PROXY_HOST | The host name of the proxy server could not be resolved";
            case WebRequestError.ERROR_MALFORMED_URI:
                return "ERROR_MALFORMED_URI | An invalid URL was specified";
            case WebRequestError.ERROR_REDIRECT_LOOP:
                return "ERROR_REDIRECT_LOOP | A redirect loop was detected";
            case WebRequestError.ERROR_SAFEBROWSING_PHISHING_URI:
                return "ERROR_SAFEBROWSING_PHISHING_URI | The requested URI was present in the \"phishing\" blocklist";
            case WebRequestError.ERROR_SAFEBROWSING_MALWARE_URI:
                return "ERROR_SAFEBROWSING_MALWARE_URI | The requested URI was present in the \"malware\" blocklist";
            case WebRequestError.ERROR_SAFEBROWSING_UNWANTED_URI:
                return "ERROR_SAFEBROWSING_UNWANTED_URI | The requested URI was present in the \"unwanted\" blocklist";
            case WebRequestError.ERROR_SAFEBROWSING_HARMFUL_URI:
                return "ERROR_SAFEBROWSING_HARMFUL_URI | The requested URI was present in the \"harmful\" blocklist";
            case WebRequestError.ERROR_CONTENT_CRASHED:
                return "ERROR_CONTENT_CRASHED | The content process crashed";
            case WebRequestError.ERROR_OFFLINE:
                return "ERROR_OFFLINE | This device does not have a network connection";
            case WebRequestError.ERROR_PORT_BLOCKED:
                return "ERROR_PORT_BLOCKED | The request tried to use a port that is blocked by either the OS or Gecko";
            case WebRequestError.ERROR_PROXY_CONNECTION_REFUSED:
                return "ERROR_PROXY_CONNECTION_REFUSED | The proxy server refused the connection";
            case WebRequestError.ERROR_FILE_NOT_FOUND:
                return "ERROR_FILE_NOT_FOUND | A file was not found";
            case WebRequestError.ERROR_FILE_ACCESS_DENIED:
                return "ERROR_FILE_ACCESS_DENIED | The OS blocked access to a file";
            case WebRequestError.ERROR_INVALID_CONTENT_ENCODING:
                return "ERROR_INVALID_CONTENT_ENCODING | The content has an invalid encoding";
            case WebRequestError.ERROR_UNSAFE_CONTENT_TYPE:
                return "ERROR_UNSAFE_CONTENT_TYPE | A content type was returned which was deemed unsafe";
            case WebRequestError.ERROR_CORRUPTED_CONTENT:
                return "ERROR_CORRUPTED_CONTENT | The content returned was corrupted";
            default:
                return "UNKNOWN";
        }
    }
    private String categoryToString(final int category)
    {
        switch (category)
        {
            case WebRequestError.ERROR_CATEGORY_UNKNOWN:
                return "ERROR_CATEGORY_UNKNOWN | " + "Unknown error occured from the server side";
            case WebRequestError.ERROR_CATEGORY_SECURITY:
                return "ERROR_CATEGORY_SECURITY | SSL certificate validation error has occured";
            case WebRequestError.ERROR_CATEGORY_NETWORK:
                return "ERROR_CATEGORY_NETWORK | A network related problem has occured";
            case WebRequestError.ERROR_CATEGORY_CONTENT:
                return "ERROR_CATEGORY_CONTENT | Invalid or corrupt webpage";
            case WebRequestError.ERROR_CATEGORY_URI:
                return "ERROR_CATEGORY_URI";
            case WebRequestError.ERROR_CATEGORY_PROXY:
                return "ERROR_CATEGORY_PROXY";
            case WebRequestError.ERROR_CATEGORY_SAFEBROWSING:
                return "ERROR_CATEGORY_SAFEBROWSING";
            default:
                return "UNKNOWN";
        }
    }
    private String createErrorPage(final String error,String url, InputStream mResourceURL) {
        if(error==null){
            return strings.GENERIC_EMPTY_STR;
        }

        if (mErrorTemplate == null) {
            StringBuilder builder = new StringBuilder();
            try (InputStream stream = mResourceURL; BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }

                mErrorTemplate = builder.toString();
            } catch (IOException e) {
                return null;
            }
        }
        String replaceUrl = mErrorTemplate.replace("$URL",url);

        return replaceUrl;
    }

}
