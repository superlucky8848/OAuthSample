package net.sperluckyworks.oauthsample.resource_server.model;

public class ResultEntity<T> 
{
    private boolean success;
    private ErrorInfo errorInfo;
    private T result;

    public static <T> ResultEntity<T> success(T data)
    {
        return new ResultEntity<T>(true, null, data);
    }

    public static <T> ResultEntity<T> fail(String errorName, String errorMessage, String errorDetails) 
    {
        ErrorInfo errorInfo = new ErrorInfo(errorName, errorMessage, errorDetails);
        return new ResultEntity<T>(false, errorInfo, null);
    }

    public static <T> ResultEntity<T> fail(Throwable throwable) 
    {
        ErrorInfo errorInfo = new ErrorInfo(throwable);
        return new ResultEntity<T>(false, errorInfo, null);
    }

    public static <T> ResultEntity<T> fail(String errorName, Throwable throwable) 
    {
        ErrorInfo errorInfo = new ErrorInfo(throwable);
        errorInfo.setErrorName(errorName);
        return new ResultEntity<T>(false, errorInfo, null);
    }

    public ResultEntity(boolean success, ErrorInfo errorInfo, T result)
    {
        this.success = success;
        this.errorInfo = errorInfo;
        this.result = result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
    public void setErrorInfo(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }
    public T getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result = result;
    }
    @Override
    public String toString() {
        return "ResultEntity" + 
            " [success=" + success + 
            ", errorInfo=" + errorInfo + 
            ", result=" + result + 
            "]";
    }
}
