package net.sperluckyworks.oauthsample.resource_server.model;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorInfo 
{
    private String errorName;
    private String errorMessage;
    private String errorDetails;

    public ErrorInfo(String errorName, String errorMessage) 
    {
        this(errorName, errorMessage, null);
    }
    
    public ErrorInfo(String errorName, String errorMessage, String errorDetails) 
    {
        this.errorName = errorName;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
    }

    public ErrorInfo(Throwable throwable)
    {
        this.errorName = throwable.getClass().getName();
        this.errorMessage = throwable.getMessage();
        try(StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw))
        {
            throwable.printStackTrace(pw);
            this.errorDetails = sw.toString();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String getErrorName() {
        return errorName;
    }
    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorDetails() {
        return errorDetails;
    }
    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    @Override
    public String toString() {
        return "ErrorInfo" + 
            " [errorName=" + errorName + 
            ", errorMessage=" + errorMessage + 
            ", errorDetails=" + errorDetails + 
            "]";
    }
}
