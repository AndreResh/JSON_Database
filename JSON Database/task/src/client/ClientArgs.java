package client;

import com.beust.jcommander.Parameter;

public class ClientArgs {
    @Parameter(
            names = "-t"
    )
    private String type;
    @Parameter(
            names = "-k"
    )
    private String key;
    @Parameter(
            names = "-v"
    )
    private String value;
    @Parameter(
            names = "-in"
    )
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
