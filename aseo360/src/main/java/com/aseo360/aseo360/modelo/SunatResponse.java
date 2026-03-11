package com.aseo360.aseo360.modelo;

import lombok.Data;

@Data
public class SunatResponse {
    private boolean success;
    private String message;
    private Payload payload;

    @Data
    public static class Payload {
        private String estado;
        private String hash;
        private String xml;
        private String cdr;
        private PdfLinks pdf;
    }

    @Data
    public static class PdfLinks {
        private String a4;
        private String ticket;
    }
}
