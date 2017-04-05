package com.translator.tester.visiontranslator;

import okhttp3.HttpUrl;


public class TranslateURL {

        private static final String KEY = "AIzaSyC1Iu2YwcvkwrY2jczBZQ_vSfnqj8IDwhk";
        private static final String URL = "https://translation.googleapis.com/language/translate/v2";

        private static String source = null;
        private static String target = "en";
        public static String targetLanguageFull = "English";
        private static String sent = "";
        private static final String[] supportedLangsAbbr={"ca", "da", "nl", "en", "fi", "fr", "de", "hu", "it", "la", "no", "pl", "pt", "ro", "es", "sv", "tr"};
        public static final String[] langsAbbr = {"af", "sq", "am", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca", "ceb", "ny", "zh-CN", "zh-TW", "co", "hr", "cs", "da", "nl", "en", "eo", "et", "tl", "fi", "fr", "fy", "gl", "ka", "de", "el", "gu", "ht", "ha", "haw", "iw", "hi", "hmn", "hu", "is", "ig", "id", "ga", "it", "ja", "jw", "kn", "kk", "km", "ko", "ku", "ky", "lo", "la", "lv", "lt", "lb", "mk", "mg", "ms", "ml", "mt", "mi", "mr", "mn", "my", "ne", "no", "ps", "fa", "pl", "pt", "ma", "ro", "ru", "sm", "gd", "sr", "st", "sn", "sd", "si", "sk", "sl", "so", "es", "su", "sw", "sv", "tg", "ta", "te", "th", "tr", "uk", "ur", "uz", "vi", "cy", "xh", "yi", "yo", "zu"};
        public static final String[] langs = {"Afrikaans", "Albanian", "Amharic", "Arabic", "Armenian", "Azeerbaijani", "Basque", "Belarusian", "Bengali", "Bosnian", "Bulgarian", "Catalan", "Cebuano", "Chichewa", "Chinese (Simplified)", "Chinese (Traditional)", "Corsican", "Croatian", "Czech", "Danish", "Dutch", "English", "Esperanto", "Estonian", "Filipino", "Finnish", "French", "Frisian", "Galician", "Georgian", "German", "Greek", "Gujarati", "Haitian Creole", "Hausa", "Hawaiian", "Hebrew", "Hindi", "Hmong", "Hungarian", "Icelandic", "Igbo", "Indonesian", "Irish", "Italian", "Japanese", "Javanese", "Kannada", "Kazakh", "Khmer", "Korean", "Kurdish", "Kyrgyz", "Lao", "Latin", "Latvian", "Lithuanian", "Luxembourgish", "Macedonian", "Malagasy", "Malay", "Malayalam", "Maltese", "Maori", "Marathi", "Mongolian", "Burmese", "Nepali", "Norwegian", "Pashto", "Persian", "Polish", "Portuguese", "Punjabi", "Romanian", "Russian", "Samoan", "Scots Gaelic", "Serbian", "Sesotho", "Shona", "Sindhi", "Sinhala", "Slovak", "Slovenian", "Somali", "Spanish", "Sundanese", "Swahili", "Swedish", "Tajik", "Tamil", "Telugu", "Thai", "Turkish", "Ukrainian", "Urdu", "Uzbek", "Vietnamese", "Welsh", "Xhosa", "Yiddish", "Yoruba", "Zulu"};

        public static boolean hasSupportedLangSpeech(){
            for(String abbr:supportedLangsAbbr){
                if(abbr.compareTo(target)==0)
                    return true;
            }
            return false;
        }

        public static void TranslateConfig(String src, String tar, String lang) {
            TranslateConfig(tar, lang);
            source=src;
        }

        public static void TranslateConfig(String tar, String lang) {
            source=null;
            target=tar;
            sent=lang;
        }

        public static void TranslateConfig(String tar) {
            target=tar;
        }

        public static void TranslateConfig(int index) {
            target=langsAbbr[index];
            targetLanguageFull=langs[index];
        }

        public static void setLanguage(String lang) {
            sent=lang;
        }

        public static String getURL(){
            HttpUrl.Builder urlBuilder = HttpUrl.parse(URL).newBuilder();
            urlBuilder.addQueryParameter("key", KEY);
            if(source!=null) urlBuilder.addQueryParameter("source", source);
            urlBuilder.addQueryParameter("target", target);
            urlBuilder.addQueryParameter("q", sent);
            String url = urlBuilder.build().toString();
            return url;
        }
}
