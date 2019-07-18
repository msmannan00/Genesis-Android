package com.darkweb.genesissearchengine.constants;

public class constants
{
    /*LOCAL URL CONSTANTS*/

    public static String blackMarket = "https://boogle.store/search?q=black+market&p_num=1&s_type=all";
    public static String leakedDocument = "https://boogle.store/search?q=leaked+document&p_num=1&s_type=all&p_num=1&s_type=all";
    public static String news = "https://boogle.store/search?q=latest%20news&p_num=1&s_type=news";
    public static String softwares = "https://boogle.store/search?q=softwares+tools&p_num=1&s_type=all&p_num=1&s_type=all";

    /*URL CONSTANTS*/

    public static String backendGenesis = "https://boogle.store/";
    public static String backendUrlHost = "boogle.store";
    public static String updateUrl = "https://boogle.store/manual?abi=";

    public static String frontEndUrlHost = "genesis.store";
    public static String frontEndUrlHost_v1 = "genesis.onion";

    public static String backendGoogle = "https://www.google.com/";
    public static String backendBing = "https://www.bing.com/";
    public static String allowedHost = ".onion";
    public static String playstoreUrl = "https://play.google.com/store/apps/details?id=com.darkweb.genesissearchengine";

    /*BUILD CONSTANTS*/

    public static String build_type = "playstore";
    //public static String build_type = "local";

    /*PROXY CONSTANTS*/

    public static int proxy_type = 1;
    public static String proxy_socks = "127.0.0.1";
    public static int proxy_socks_version  = 5;
    public static boolean proxy_socks_remote_dns  = true;
    public static boolean proxy_cache  = false;
    public static boolean proxy_memory  = false;
    public static String proxy_useragent_override  = "Mozilla/5.0 (Android 9; Mobile; rv:67.0) Gecko/67.0 Firefox/67.0";
    public static boolean proxy_donottrackheader_enabled  = false;
    public static int proxy_donottrackheader_value  = 1;

    /*MENU CONSTANTS*/

    public static int list_history  = 1;
    public static int list_bookmark  = 2;

    /*SETTINGS CONSTANTS*/

    public static int max_history_size=500;
    public static int max_bookmark_size=500;
    public static String databae_name="genesis_dbase";

}
