package com.econest.model.util;

/** SMTP configuration is read from environment variables; credentials are never stored in source code. */
public record EmailConfig(String host, int port, String username, String password, String from, boolean startTls) {
    public static EmailConfig fromEnvironment() {
        String host=System.getenv("ECONEST_SMTP_HOST"), user=System.getenv("ECONEST_SMTP_USER"), pass=System.getenv("ECONEST_SMTP_PASSWORD");
        if(host==null||host.isBlank()||user==null||user.isBlank()||pass==null||pass.isBlank())
            throw new IllegalStateException("Configure ECONEST_SMTP_HOST, ECONEST_SMTP_USER and ECONEST_SMTP_PASSWORD first.");
        String portValue=System.getenv().getOrDefault("ECONEST_SMTP_PORT","587");
        int port=Integer.parseInt(portValue);
        String from=System.getenv().getOrDefault("ECONEST_EMAIL_FROM",user);
        return new EmailConfig(host,port,user,pass,from,true);
    }
}
