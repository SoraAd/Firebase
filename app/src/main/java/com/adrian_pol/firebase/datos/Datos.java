package com.adrian_pol.firebase.datos;

public class Datos {
    private static final String URL_FIREBASE_BBDD = "https://fir-c3182-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String DOCUMENT_ID = "rEBXTDc0rxIOh2WkokZM";
    private static final String DOCUMENT_ID_TOKEN = "d4CmqKyFwWDmcyVwfnky";

    public static String getUrlFirebaseBbdd() {
        return URL_FIREBASE_BBDD;
    }

    public static String getDocumentId() {
        return DOCUMENT_ID;
    }

    public static String getDocumentIdToken(){return DOCUMENT_ID_TOKEN;}
}
