# OAuth Sample

Sample Project that use OAuth 2.1 to secure SpringBoot API backend and Next.js frontend.

## Step 1

Create an ordinary springboot project serving restful api.

### Add dependecies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- spring doc -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0-RC1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Create Some model classes

1. General Exception for the api

    ```java
    public class ApiException extends RuntimeException {
        private String name;

        public ApiException(String name, String message) {
            super(message);
            this.name = name;
        }

        public ApiException(String name, String message, Throwable cause) {
            super(message, cause);
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "ApiException [name = " + name + ", " + super.toString() + "]";
        }
    }
    ```

2. ResultEntity & ErrorInfo

    ```java
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

        // getters and setters and toString() ...
    }
    ```

    ```java
    public class ResultEntity<T> 
    {
        private boolean success;
        private ErrorInfo errorInfo;
        private T result;

        public static <T> ResultEntity<T> from(T data)
        {
            return new ResultEntity<T>(true, null, data);
        }

        public static <T> ResultEntity<T> fromError(String errorName, String errorMessage, String errorDetails) 
        {
            ErrorInfo errorInfo = new ErrorInfo(errorName, errorMessage, errorDetails);
            return new ResultEntity<T>(false, errorInfo, null);
        }

        public static <T> ResultEntity<T> fromThrowable(Throwable throwable) 
        {
            ErrorInfo errorInfo = new ErrorInfo(throwable);
            return new ResultEntity<T>(false, errorInfo, null);
        }

        public static <T> ResultEntity<T> fromThrowable(String errorName, Throwable throwable) 
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
        
        // getters and setters and toString() ...
    }
    ```

### Restful Controller Class

```java
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class MainController 
{
    @Operation(description = "Say Hello")
    @GetMapping("/hello")    
    public ResultEntity<String> hello(
        @Parameter(description = "Say hello to whom?")
        @RequestParam(defaultValue = "World")
        String name)
    {
        String result = String.format("Hello %s!", name);
        return ResultEntity.from(result);
    }
}
```

### Restful Controller Advice Class

```java
@RestControllerAdvice(basePackages = "net.sperluckyworks.oauthsample.resource_server.controller")
public class ControllerAdvicer 
{
    @ExceptionHandler(ApiException.class)
    ResultEntity<?> handleExcetpiton(ApiException exception)
    {
        if(exception == null) return ResultEntity.fromError("Empty ApiException", "(NULL)", null);
        else return ResultEntity.fromThrowable(exception.getName(), exception);
    }

    @ExceptionHandler(IOException.class)
    ResultEntity<?> handleExcetpiton(IOException exception)
    {
        if(exception == null) return ResultEntity.fromError("Empty IOException", "(NULL)", null);
        else return ResultEntity.fromThrowable(exception);
    }

    @ExceptionHandler(RuntimeException.class)
    ResultEntity<?> handleExcetpiton(RuntimeException exception)
    {
        if(exception == null) return ResultEntity.fromError("Empty RuntimeException", "(NULL)", null);
        else return ResultEntity.fromThrowable(exception);
    }
}
```

### API Doc Configuration

Set doc path in `application.properties`:

```yml
springdoc.swagger-ui.path=/apidoc/swagger-ui.html
```

Add Spring Doc Configuraion

```java
@Configuration
public class ApiDocConfiguration 
{
    @Bean
    public OpenAPI apiDefination()
    {
        return new OpenAPI()
            .info(
                new Info().title("OAuth Test API")
                .description("Test APIs for OAuth test project")
                .version("v1.0.1")
                .license(
                    new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                )
            );
    }
}
```

### Test

Browse `http://localhost:8080/apidoc/swagger-ui.html` and try the api

![test-api-doc](./doc/img/test-api-doc.jpeg)
