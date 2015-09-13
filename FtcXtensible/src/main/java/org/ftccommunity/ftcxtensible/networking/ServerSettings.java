package org.ftccommunity.ftcxtensible.networking;

public class ServerSettings {
    private String webDirectory;
    private String index;
    private String hardwareMapJsonPage;
    private String logPage;

    private ServerSettings() {
        setWebDirectory("/sdcard/FIRST/web");
        setIndex("/index.html");
        setHardwareMapJsonPage("/robot.json");
        setLogPage("/robot-log.html");
    }

    public static ServerSettings createServerSettings() {
        return new ServerSettings();
    }

    public String getWebDirectory() {
        return webDirectory;
    }

    public void setWebDirectory(String webDirectory) {
        this.webDirectory = webDirectory;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getHardwareMapJsonPage() {
        return hardwareMapJsonPage;
    }

    public void setHardwareMapJsonPage(String hardwareMapJsonPage) {
        this.hardwareMapJsonPage = hardwareMapJsonPage;
    }

    public String getLogPage() {
        return logPage;
    }

    public void setLogPage(String logPage) {
        this.logPage = logPage;
    }
}