# OAuth Sample

Sample Project that use OAuth 2.1 to secure SpringBoot API backend and Next.js frontend.

## Step 1: Create resource server

Create an ordinary springboot project serving restful api. It'll serve the role of resource server and client of OAuth.

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

### Set Application properties

Set port and application name in `application.properties`:

```text
spring.application.name=resource-server
server.port=8082
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

```text
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

### Test Resource Server API

Browse `http://localhost:8082/apidoc/swagger-ui.html` and try the api

![test-api-doc](./doc/img/test-api-doc.jpeg)

## Step 2: Create Auth Server

Create an ordinary springboot project serving a static page. It'll serve the role of authorization server of OAuth and support Authentication via OpenId.

### Add static page

Add a simple static page to auth-server, I'll serve several shortcuts uri for further tests.

Add page `index.html` to `src\main\resources\static` with following content.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Auth Server</title>
</head>
<body>
    <h1>Auth Server</h1>
    <p>
        Auth server is running.
    </p>
</body>
</html>
```

### Change port

Add configuration on `application.properties` file to set a different port so the auth-server can run alone with resource-server.

Also change logging lever of Spring Security to `TRACE` for debug

```text
spring.application.name=auth-server
server.port=8081
logging.level.org.springframework.security=TRACE
```

### Test Auth Server Index

Browse `http://localhost:8081` and see the welcome page.

![auth-server-index](./doc/img/auth-server-index-001.jpeg)

### Enable Spring Security

Add Spring Security to POM of auth-server project.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Start auth-server, there will automatically set to username password authentication with a rundom password.
The random password can be found in the log of server.

![Securtiy-Random-Password](./doc/img/random-password.jpg)

When accessing to [http://localhost:8081](http://localhost:8081) it will promt a login page, use username `user` with the genrated password can authenticate the user and get access to the index page.

The default arrangement of Spring Boot and Spring Security affords the following behaviors at runtime:

- Requires an authenticated user for any endpoint (including Boot’s /error endpoint)
- Registers a default user with a generated password at startup (the password is logged to the console; in the preceding example, the password is `b61b2bb3-c401-4aa1-bd5d-3230a41b03bf`)
- Protects password storage with BCrypt as well as others
- Provides form-based login and logout flows
- Authenticates form-based login as well as HTTP Basic
- Provides content negotiation; for web requests, redirects to the login page; for service requests, returns a 401 Unauthorized
- Mitigates CSRF attacks
- Mitigates Session Fixation attacks
- Writes Strict-Transport-Security to ensure HTTPS
- Writes X-Content-Type-Options to mitigate sniffing attacks
- Writes Cache Control headers that protect authenticated resources
- Writes X-Frame-Options to mitigate Clickjacking
- Integrates with HttpServletRequest's authentication methods
- Publishes authentication success and failure events

## Step 3: Auth Server SecurityConfiguration

### Add basic users info for test

Create `/configuration/SecurityConfiguration.java` add a `@Configuration` and `@EnableWebSecurity` class to customize security settings.

For now, just add a simple UserDetailsService and give two basic users `admin` and `test`.

Addtionally use `DelegatingPasswordEncoder` which SpringSecurity provides as the default PasswordEncorder.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration 
{
    @Bean
    UserDetailsService testUserDetailsService() 
    {
        UserDetails testUser = User.withUsername("test")
            .password("{noop}test")
            .roles("USER")
            .build();

        UserDetails testAdmin = User.withUsername("admin")
            .password("{noop}admin")
            .roles("USER", "ADMIN")
            .build();

        UserDetailsService result = new InMemoryUserDetailsManager(Arrays.asList(testUser, testAdmin));

        return result;
    }
}
```

`DelegatingPasswordEncoder` encode/decode password string with mutiple algrithms, with a hint prefix.

e.g:

- `{bcrypt}XXXXXXXX` indicates the `XXXXXXXX` is the original password encoded by BCrypt.
- `{noop}XXXXXXXX` indicates that the `XXXXXXXX` is the original password.

### Addtionally Configuraion

For site to properly function, add basic security config.

The key is to permit all access home page, but need to authenticated to view other page.

Form login is enabled.
User can logout and clean cache.
CSRF is disabled for the `<form>` in static page to function properly.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration 
{
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
    {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize ->authorize
                .requestMatchers("/", "/index.html").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
    
    // ... othre configurations
}
```

### User Custome UserDetails Service

Remove `testUserDetailsService` bean in `SecurityConfiguration`, we will use a UserDetailsService class to serve as user database.

Create `service/AuthUserDetailsService.java` and add following code.

```java
@Service
public class AuthUserDetailsService implements UserDetailsService
{
    private InMemoryUserDetailsManager userDetailsManager;

    public AuthUserDetailsService()
    {
         UserDetails testUser = User.withUsername("test")
            .password("{noop}test")
            .roles("USER")
            .build();

        UserDetails testAdmin = User.withUsername("admin")
            .password("{noop}admin")
            .roles("USER", "ADMIN")
            .build();

        userDetailsManager = new InMemoryUserDetailsManager(testUser, testAdmin);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        return userDetailsManager.loadUserByUsername(username);
    }
}
```

Currently the `AuthUserDetailsService` just a functional copy of `testUserDetailsService` just deleted. It will me modified in the future for production use.

### Configure OAuth login for test

### Add OAuth clinet denpency for auth server

In `pom.xml` add dependency for oauth2.1 client.

```xml
<project>
    <!-- otther configuration ommited -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-client</artifactId>
        </dependency>
    </dependencies>
</projec>
```

### Add OAuth client registry of GitHub for test

Login to github and in `profile>settings>developer settins>Oauth apps` page, set up an oauth page, note `client id` and `client secret`, set redirect url to `http://localhost:8081/`;

Then configure `resources/application.properties` add following properties

```text
spring.security.oauth2.client.registration.github.client-id={github client id}
spring.security.oauth2.client.registration.github.client-secret={git hub client secret}
spring.security.oauth2.client.registration.github.scope=user:email
spring.security.oauth2.client.registration.github.client-name=GitHub
```

### Add oauth login in securtiy settings of auth server

Add `oauth2Login` config in securtiyFilterChain bean in SecurityConfiguration class.

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
{
    http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize ->authorize
            .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE).permitAll()
            .requestMatchers("/", "/index.html").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(Customizer.withDefaults())
        .formLogin(formLogin -> formLogin
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        );

    return http.build();
} 
```

Then visit some protected page like `http://localhost:8081/tests/method-overriding.html?continue`, The poped up login page will have github login link on it.

![github-login-page](./doc/img/oauth-login.jpeg)

The protected page shoule be accessable after github authentication.

### Test Map Authorities from OAuth user to local user

To map local user's GrantedAuthorties from OAuth user scopes, Add a @Bean of GrantedAuthoritiesMapper in security settings.

```java
@Bean
GrantedAuthoritiesMapper OAuth2UserAuthoritiesMapper() 
{
    return (authorities) -> {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        authorities.forEach(authority -> {
            if (authority instanceof OidcUserAuthority)
            {
                OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                String email = oidcUserAuthority.getUserInfo().getEmail();
                Set<GrantedAuthority> userAuthorities = load3rdPartyUserAuthorities(email);
                mappedAuthorities.addAll(userAuthorities);
            }
            else if(authority instanceof OAuth2UserAuthority)
            {
                OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
                String email = (String) oauth2UserAuthority.getAttributes().get("email");
                Set<GrantedAuthority> userAuthorities = load3rdPartyUserAuthorities(email);
                mappedAuthorities.addAll(userAuthorities);
            }
            else
            {
                mappedAuthorities.add(authority);
            }
        });

        System.out.println("Mapped Authorities: " + mappedAuthorities.toString());

        return mappedAuthorities;
    };
    
}

private Set<GrantedAuthority> load3rdPartyUserAuthorities(String email)
{
    Set<GrantedAuthority> result = new HashSet<>();
    if(email.compareTo("mail.superlucky@gmail.com") == 0)
    {
        result.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        result.add(new SimpleGrantedAuthority("ROLE_USER"));
    }
    else
    {
        result.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    return result;
}
```

The `OAuth2UserAuthoritiesMapper` returns a function that map the authorities of the current login user. It will run the first time user logged in. By filter the class of authority, one can just process users logged by OAuth or OIDC.

The `load3rdPartyUserAuthorities` is a dummy function to do the authority lookup. It should be a repository function to lookup user authorities in the database.

Next, add some endpoints in `BaseController` to show current user's basic info and mapped authorities.

```Java
//BaseController.java
@GetMapping(path = "/user-info", produces = MediaType.APPLICATION_JSON_VALUE)
public Object userInfo(Authentication authentication)
{
    Object principal = authentication.getPrincipal();

    if(principal instanceof OAuth2User)
    {
        OAuth2User oauth2User = (OAuth2User) principal;
        String email = (String) oauth2User.getAttribute("email");

        System.out.println("email: " + email);
        System.out.println("authorities: ");
        oauth2User.getAuthorities().forEach(System.out::println);
    }

    return principal;
}

@GetMapping(path = "/user-authorities", produces = MediaType.APPLICATION_JSON_VALUE)
public List<GrantedAuthority> userAuthorities(Authentication authentication) 
{
    return new ArrayList<GrantedAuthority>(authentication.getAuthorities());
}
```

Add links on index.html for convenience.

```html
<h2>Test Links:</h2>
<ul>
    <!-- Other test links -->
    <li>
        <a href="/user-info" target="_blank">Show User Info</a>
    </li>
    <li>
        <a href="/user-authorities" target="_blank">Show User Authorities</a>
    </li>
</ul>
```

The result pages should be like:

1. User Info:
![user-info](./doc/img/page-user-info.jpeg)
2. User Authorities:
![user-authorities](./doc/img/page-user-authorities.jpeg)

### Test Hybrid UserDetails of AuthServer

Sometimes the authority mapping is not enought, in case that auth server need to produce both authorities and user infos, we need to combine user details form local login and OAuth login.

This sector shows how to archieve that.

First, Add a new class for our hybrid user in `/model/HybridUser.java`, it combines common `UserDetails` and `OAuth2User` interfaces.

```java
public class HybridUser extends User implements OAuth2User
{
    private Map<String, Object> attributes = new HashMap<>();

    public HybridUser(UserDetails user)
    {
        super(
            user.getUsername(), 
            user.getPassword(), 
            user.isEnabled(), 
            user.isAccountNonExpired(), 
            user.isCredentialsNonExpired(), 
            user.isAccountNonLocked(), 
            user.getAuthorities());
    }

    public HybridUser(UserDetails user, Map<String, Object> attributes)
    {
        this(user);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() 
    {
        return attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }
    
}
```

`HybridUser` is Just a normal UserDetails adding `OAuth2User`'s attribures.

Next, we change BaseController to make `/user-info` entrypoint to show users for all the type in SecurityContext

```java
// BaseController.java
//... Other entries
@GetMapping(path = "/user-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object userInfo(Authentication authentication)
    {
        Object principal = authentication.getPrincipal();

        return principal;
    }
```

Next, we modify `AuthUserDetailsService` and add local user info of github users, make the email as the identifier.

```java
@Service
public class AuthUserDetailsService implements UserDetailsService
{
    private InMemoryUserDetailsManager userDetailsManager;

    public AuthUserDetailsService()
    {
        // Load some test users;
        List<UserDetails> users = Arrays.asList(
            User.withUsername("test")
                .password("{noop}test")
                .roles("USER")
                .build()
            ,User.withUsername("admin")
                .password("{noop}admin")
                .roles("USER", "ADMIN")
                .build()
            ,User.withUsername("mail.superlucky@gmail.com")
                .password("{noop}superlucky")
                .roles("USER", "ADMIN")
                .build()
        );

        userDetailsManager = new InMemoryUserDetailsManager(users);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        return userDetailsManager.loadUserByUsername(username);
    }
}
```

Next we add `service/MapOAuthUserService.java` a class which extends `DefaultOAuth2UserService` to map OAuth users to Hybrid Users

```java
@Service
public class MapOAuthUserService extends DefaultOAuth2UserService
{
    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException 
    {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");

        try
        {
            UserDetails localUser = authUserDetailsService.loadUserByUsername(email);
            return new HybridUser(localUser, oAuth2User.getAttributes());
        }
        catch (UsernameNotFoundException e)
        {
            OAuth2Error oauth2Error = new OAuth2Error("NO_LOCAL_USER", email, null);
            throw new OAuth2AuthenticationException(oauth2Error, e); 
        }
    }
}
```

Note that a special `OAuth2Error` is set in the senario that the github user do not have the local user correspondence, it will be used to redirect user to register page later.

Finally modify the `/configuration/SecurityConfiguration.java` class, remove `OAuth2UserAuthoritiesMapper` and `load3rdPartyUserAuthorities` fuctions from previouse sector. Configure autowired `MapOAuthUserService` to OAuth2Login filter and set redirect to register page for OAuth users not locally registered.

The full configuration class would be like:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration 
{
    @Autowired 
    MapOAuthUserService mapOAuthUserService;
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
    {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize ->authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE).permitAll()
                .requestMatchers("/", "/index.html", "/register.html").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login -> oauth2Login
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(mapOAuthUserService)
                )
                .failureHandler((req, res, e) -> {
                    if(e instanceof OAuth2AuthenticationException)
                    {
                        OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) e;
                        if(oauth2Exception.getError().getErrorCode().compareTo("NO_LOCAL_USER") == 0)
                        {
                            res.sendRedirect("/register.html?email=" + oauth2Exception.getError().getDescription());
                        }
                        else res.sendRedirect("/login?error");
                    }
                    else res.sendRedirect("/login?error");
                })
            )
            .formLogin(formLogin -> formLogin
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    } 
}
```

Also notify `/register.html` is permited to all users in order to register.

Now add a simple demo html page `/resources/static/register.html` for showing register ui. No register login is provided.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <h1>Register New User</h1>

    <form>
        <ul>
            <li>
                <input type="text" name="email" placeholder="somebody@somesite.com" required />
            </li>
            <li>
                <input type="password" name="password" placeholder="Password" required />
            </li>
            <li>
                <input type="password" name="repeat" placeholder="repeat" required />
            </li>
            <li>
                <button type="submit">Register</button>
            </li>
        </ul>
    </form>
    <script type="text/javascript">
        const urlParams = new URLSearchParams(window.location.search);
        const email = urlParams.get('email');
        if (email) {
            document.querySelector('input[name="email"]').value = email;
        }
    </script>
</body>
</html>
```

Add register link on `index.html`

```html
<!-- other elements omitted -->
<p>
    <a href="/register.html">Register</a>
    <span> | </span>
    <a href="/logout">Logout</a>
</p>
```

Note that email is loaded automatically from url query paramters.

Test with normal user, github user `mail.superlucky@gmail.com` and other github user.

```json
// Normal User Info
{
  "password": null,
  "username": "test",
  "authorities": [
    {
      "authority": "ROLE_USER"
    }
  ],
  "accountNonExpired": true,
  "accountNonLocked": true,
  "credentialsNonExpired": true,
  "enabled": true
}
// Noraml User Authorities
[
  {
    "authority": "ROLE_USER"
  }
]
// Registerd GitHub User Info
{
  "password": null,
  "username": "mail.superlucky@gmail.com",
  "authorities": [
    {
      "authority": "ROLE_ADMIN"
    },
    {
      "authority": "ROLE_USER"
    }
  ],
  "accountNonExpired": true,
  "accountNonLocked": true,
  "credentialsNonExpired": true,
  "enabled": true,
  "attributes": {
    "login": "superlucky8848",
    "email": "mail.superlucky@gmail.com",
    // Other attributes omitted.
  },
  "name": "mail.superlucky@gmail.com"
}
// Registerd GitHub User Authorities
[
  {
    "authority": "ROLE_ADMIN"
  },
  {
    "authority": "ROLE_USER"
  }
]
```

For other github users a direction to regisiter page is shown

![register-page](./doc/img/page-register.jpeg)

## Step 4: Resource Sever Security Configuration

### Add spring security dependency for resource server

In `pom.xml` add dependency for spring security.

```xml
<project>
    <!-- otther configuration ommited -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    </dependencies>
</projec>
```

### Create Spring Security Config

Create `configuration/SecurityConfiguration.java` to add security config. Add `@Configuration` and `@EnableWebSecurity` annotations to the configuration class.

Currently resource server is config to use Basic Auth Type for protected api, for tests only. It will be configured to use oauth token in product enviornment.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration 
{
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/**").permitAll()    //Permit acutuator api
                .requestMatchers("/apidoc/**", "/v3/api-docs*/**").permitAll() //Permit Spring Doc Swagger UI
                .requestMatchers("/api/private/**").authenticated() //Require authentication for private api
                .requestMatchers("/api/public/**", "/api/**").permitAll() //Permit public api
                .anyRequest().authenticated()   //Require authentication for all other requests
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }   
}
```

Things to node:

1. CSRF is disabled because the api is designed stateless and use no cookie.
2. `/actuator/**` apis do not need authentication
3. Spring Doc end points: `/apidoc/**` and `/v3/api-docs*/**` need to be accessable without authentication in order for swagger-ui generation.
4. Explicitly protect `/api/private/**` with basic authentication
5. Allow unauthorized acesss to `/api/public/**` and `/api/**`(other than `/api/private/**`)
6. Disable unauthorized access to any other urls as a backup policy.

### Add basic Users info for test

Create `/service/Authuer.java` to populate test accounts.

For now, just add a simple UserDetailsService and give two basic users `admin` and `test`.

```java
@Service
public class AuthUserDetailsService implements UserDetailsService
{
    private InMemoryUserDetailsManager userDetailsManager;

    public AuthUserDetailsService()
    {
       UserDetails testUser = User.withUsername("test")
            .password("{noop}test")
            .roles("USER")
            .build();

        UserDetails testAdmin = User.withUsername("admin")
            .password("{noop}admin")
            .roles("USER", "ADMIN")
            .build();

        userDetailsManager = new InMemoryUserDetailsManager(testUser, testAdmin);
    
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        return userDetailsManager.loadUserByUsername(username);
    }
}
```

### Add public and private apis for test

Delete `controller/MainController.java` and add `controller/PublicController.java` and `controller/PrivateController.java` add add some restful api for test.

```java
package net.superluckyworks.oauthsample.resource_server.controller;

// imports ...

@RestController
@RequestMapping(path = "/api/public", produces = {MediaType.APPLICATION_JSON_VALUE})
public class PublicController 
{
    @Operation(description = "Say Hello Public Call")
    @GetMapping("/hello")    
    public ResultEntity<String> hello(
        @Parameter(description = "Say hello to whom?")
        @RequestParam(defaultValue = "World")
        String name)
    {
        String result = String.format("(Public) Hello %s!", name);
        return ResultEntity.success(result);
    }
}
```

```java
package net.superluckyworks.oauthsample.resource_server.controller;

// imports...

@RestController
@RequestMapping(path = "/api/private", produces = { MediaType.APPLICATION_JSON_VALUE })
@SecurityRequirement(name = "basicScheme")
public class PrivateController 
{
    @Operation(description = "Say Hello Privatge Call")
    @GetMapping("/hello")    
    public ResultEntity<String> hello(
        @Parameter(description = "Say hello to whom?")
        @RequestParam(defaultValue = "World")
        String name)
    {
        String result = String.format("(Private) Hello %s!", name);
        return ResultEntity.success(result);
    }
}
```

Note that `@SecurityRequirement` is added to private api configuration for spring-doc to add basicScheme to swagger-page.

### Change Spring Doc Configuration

Change `configeration/ApiDocConfiguration.java`, Add `@SeurityScheme` to add basic authentication support to doc page, and group public and private apis to seperated pages.

The Configuration class will be like following:

```java
@Configuration
@SecurityScheme(
    name = "basicScheme",
    type = SecuritySchemeType.HTTP,
    scheme = "basic"
)
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

    @Bean
    public GroupedOpenApi publicApi()
    {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/public/**")
            .build();
    }

    @Bean
    public GroupedOpenApi privateApi()
    {
        return GroupedOpenApi.builder()
            .group("private")
            .pathsToMatch("/api/private/**")
            .build();
    }
}
```

### Test resource server

Run resource server and navigate to `http://localhost:8082/apidoc/swagger-ui/index.html`

You can see how private and public apis are seperated, and a authorize button is opened. Add currect credentials to the doc page will automatically add Auth Headers to following calls. Otherwise the spring security will come in and notify the browser to ask for credentials.

If authenticated with browser, Basic Auth is cached by browser, restart browser to clean cache.

Private API doc page:

![private-api-doc](./doc/img/test-api-doc-private.jpeg)

Public API doc page:

![public-api-doc](./doc/img/test-api-doc-public.jpeg)

Basic Auth page by swgger:

![swagger-basic-auth](./doc/img/spring-doc-auth-basic.jpeg)

Basic Auth dialog by browser:

![browser-basic-auth](./doc/img/browser-auth-basic.jpeg)

## Step 5: Configure Auth Server

### Add Spring Security Configure for oauth auth-sever

```xml
<project>
    <!-- otther configuration ommited -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-authorization-server</artifactId>
        </dependency>
    </dependencies>
</projec>
```

### Add Auth Server properties

```properties
spring.security.oauth2.authorizationserver.issuer=http://localhost:8081
spring.security.oauth2.authorizationserver.client.udata-client.registration.client-id=udata-client
spring.security.oauth2.authorizationserver.client.udata-client.registration.client-name=Udata
spring.security.oauth2.authorizationserver.client.udata-client.registration.client-secret={noop}123456
spring.security.oauth2.authorizationserver.client.udata-client.registration.client-authentication-methods=client_secret_post,client_secret_basic
spring.security.oauth2.authorizationserver.client.udata-client.registration.scopes=openid,profile,email
spring.security.oauth2.authorizationserver.client.udata-client.registration.authorization-grant-types=authorization_code,refresh_token
spring.security.oauth2.authorizationserver.client.udata-client.registration.redirect-uris=http://localhost:8082/login/oauth2/code/udata-client,http://localhost:8082/apidoc/swagger-ui/oauth2-redirect.html
spring.security.oauth2.authorizationserver.client.udata-client.registration.post-logout-redirect-uris=http://localhost:8082/logout
spring.security.oauth2.authorizationserver.client.udata-client.require-authorization-consent=false
```

`spring.security.oauth2.authorizationserver.issuer` configuration enables jwt support for authorization server. At default `http://localhost:8081/.well-known/openid-configuration` will give all endpoints configured. Notable endpoints encludes:

1. issuer: The iss part of JWT.
2. authorization_endpoint: Endpoint to get authentication code.
3. token_endpoint: Endpoint to get authentication token.
4. jwks_uri: Public key an other information to decode JWT.

### Add Security Configuration

```java
@Autowired 
MapOAuthUserService mapOAuthUserService;

@Bean
@Order(1)
SecurityFilterChain authserverFilterChain(HttpSecurity http) throws Exception
{
    OAuth2AuthorizationServerConfigurer oauth2AuthorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

    http.securityMatcher(oauth2AuthorizationServerConfigurer.getEndpointsMatcher())
        .with(oauth2AuthorizationServerConfigurer, authserver -> authserver.oidc(Customizer.withDefaults()))
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .cors(cors -> cors.disable())
        .exceptionHandling(exceptions -> exceptions
            .defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"), 
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
        );
    
    return http.build();
}

@Bean
@Order(2)
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
{
    http.cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize ->authorize
            .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE).permitAll()
            .requestMatchers("/", "/index.html", "/register.html").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth2Login -> oauth2Login
            .userInfoEndpoint(userInfo -> userInfo
                .userService(mapOAuthUserService)
            )
            .failureHandler((req, res, e) -> {
                if(e instanceof OAuth2AuthenticationException)
                {
                    OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) e;
                    if(oauth2Exception.getError().getErrorCode().compareTo("NO_LOCAL_USER") == 0)
                    {
                        res.sendRedirect("/register.html?email=" + oauth2Exception.getError().getDescription());
                    }
                    else res.sendRedirect("/login?error");
                }
                else res.sendRedirect("/login?error");
            })
        )
        .formLogin(formLogin -> formLogin
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        );

    return http.build();
}
```

Currently the CORS protection is disabled to avoid affecting the tests.

### Test OAuth token flow use Postman

Start auth-server and resource-server.

Download postman, disable auto follow redirection in File>Setting>General.

1. Login and get SESSION ID

    At browser, login and visit `http://localhost:8081/user-info/` and get cookie `JSESSIONID` from dev console.

    ![get-cookie](./doc/img/get-session.jpeg)

2. Get Access Code:

    Add a request on postman
    GET `http://localhost:8081/oauth2/authorize?response_type=code&client_id=udata-client&redirect_uri=http://localhost:8082/login/oauth2/code/udata-client&scope=email`

    ![postman-code-001](./doc/img/postman-code-001.jpeg)

    Add Cookie to `localhost` at postman's cookie store.

    ![postman-code-002](./doc/img/postman-code-002.jpeg)
    ![postman-code-003](./doc/img/postman-code-003.jpeg)

    Sent the request and get an 302 response, look at the `Location` header, copy the code.

    ![postman-code-004](./doc/img/postman-code-004.jpeg)

3. Get Authorization Token

    Add a request on postman
    POST `localhost:8081/oauth2/token`
    With `x-www-form-urlendoed` body:

    - grant_type: `authorization_code`
    - code: `{The code from code redirection url}`
    - redirect_uri: `http://localhost:8082/login/oauth2/code/udata-client`
    - scope: `email`

    Note that the `redirect_uri` and `scope` must pre-registered when configure the auth server.

    Run the Requst and get access_token from response body.

    ![postman-token-002](./doc/img/postman-token-002.jpeg)

4. Call API using authorization token

    Add a request on postman
    GET `http://localhost:8082/api/private/hello?name=World`

    ![postman-api-001](./doc/img/postman-api-001.jpeg)

    At Authorization page, add a `Bearea Token` Auth with the token from previours request.

    ![postman-api-002](./doc/img/postman-api-002.jpeg)

    Send the request and check the response.

    ![postman-api-003](./doc/img/postman-api-003.jpeg)

### Add CORS configuration for browser apps

Browser automatically add preflight requests to non-simple requests that javascript calls, like fetch(). Swagger-ui oauth authorization and SPA apps need Auth server correctly configure CORS. Disable it is not enough.

So At security settings change cors settings to use default on both `SecurityFilterChain`, so the configuration Bean would be like:

```java
@Bean
@Order(1)
SecurityFilterChain authserverFilterChain(HttpSecurity http) throws Exception
{
    OAuth2AuthorizationServerConfigurer oauth2AuthorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

    http.securityMatcher(oauth2AuthorizationServerConfigurer.getEndpointsMatcher())
        .with(oauth2AuthorizationServerConfigurer, authserver -> authserver.oidc(Customizer.withDefaults()))
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .cors(Customizer.withDefaults())
        .exceptionHandling(exceptions -> exceptions
            .defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"), 
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
        );
    
    return http.build();
}

@Bean
@Order(2)
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
{
    http.cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize ->authorize
            .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE).permitAll()
            .requestMatchers("/", "/index.html", "/register.html").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth2Login -> oauth2Login
            .userInfoEndpoint(userInfo -> userInfo
                .userService(mapOAuthUserService)
            )
            .failureHandler((req, res, e) -> {
                if(e instanceof OAuth2AuthenticationException)
                {
                    OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) e;
                    if(oauth2Exception.getError().getErrorCode().compareTo("NO_LOCAL_USER") == 0)
                    {
                        res.sendRedirect("/register.html?email=" + oauth2Exception.getError().getDescription());
                    }
                    else res.sendRedirect("/login?error");
                }
                else res.sendRedirect("/login?error");
            })
        )
        .formLogin(formLogin -> formLogin
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        );

    return http.build();
}
```

Then add a `CorsConfigurationSource` Bean and allow CORS requests from client (Swagger-ui of the resource server for example)

```java
@Bean
CorsConfigurationSource corsConfigurationSource()
{
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.addAllowedOrigin("http://localhost:8081");
    config.addAllowedOrigin("http://localhost:8082");
    config.setAllowCredentials(true);
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

### Add autorities claims to jwt token

Because the system don't load authorities by scope, but authorities in database of the auth server, so a custom claims `authorities` need to add to the JWT.

To archieve this, add a custom `OAuth2TokenCustomizer<JwtEncodingContext>` Bean to the `SecurityConfiguration` class.

```java
@Bean
OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer()
{
    return context -> {
        if(OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()))
        {
            context.getClaims().claims(claims -> 
            {
                Set<String> authorities = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities());
                claims.put("authorities", authorities);
            });
        }
    };
}
```

## Step 6: Configure resource server

### Configure API defination

Change the @SecurityScheme annotation of `ApiDocConfiguration` add OAuth config to our Auth Server. The `basicScheme` from previous steps is keeped for tests so we combine the two configuration annotation in `@SecuritySchemes`.

```java
@Configuration
@SecuritySchemes({
    @SecurityScheme(
        name = "basicScheme", 
        type = SecuritySchemeType.HTTP, 
        scheme = "basic"),
    @SecurityScheme(
        name = "oauth2Scheme",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = 
            @OAuthFlow(
                authorizationUrl = "http://localhost:8081/oauth2/authorize",
                tokenUrl = "http://localhost:8081/oauth2/token",
                scopes = {
                    @OAuthScope(name="openid"),
                    @OAuthScope(name="profile"),
                    @OAuthScope(name="email")
                }
            )
        )
    )
})
public class ApiDocConfiguration 
{
    // Api definations omitted
}
```

Note that the `authorizationUrl`, `tokenUrl` and `scopes` must match the settings of Auth Server.

### Configure controller settings

At `PrivateController` class add annotation to notify those api use both `basicScheme` and `oauth2Scheme`.

```java
@RestController
@RequestMapping(path = "/api/private", produces = { MediaType.APPLICATION_JSON_VALUE })
@SecurityRequirements({
    @SecurityRequirement(name = "basicScheme"),
    @SecurityRequirement(name = "oauth2Scheme")
})
public class PrivateController 
{
    // Api endpoints omitted.
}
```

### Configure Swagger-UI default value

At `application.properties` add configurations to add default values to swagger-ui's oauth client.

```properties
springdoc.swagger-ui.csrf.enabled=false
springdoc.swagger-ui.oauth.app-name=Udata
springdoc.swagger-ui.oauth.client-id=udata-client
springdoc.swagger-ui.oauth.client-secret=123456
springdoc.swagger-ui.oauth.scopes=openid,profile,email
```

### Test oauth authorization use swagger-ui

Start both auth and resource server, browse the private api swagger page.

[http://localhost:8082/apidoc/swagger-ui/index.html?urls.primaryName=private](http://localhost:8082/apidoc/swagger-ui/index.html?urls.primaryName=private)

![resource-server-oauth-001](./doc/img/resource-server-oauth-001.jpeg)

The Client information is auto filled.

![resource-server-oauth-002](./doc/img/resource-server-oauth-002.jpeg)

If the auth-server has not logged in (have session cookie), it will show login page:

![resource-server-oauth-003](./doc/img/resource-server-oauth-003.jpeg)

After logged in, it will redirect to swagger page with success notification.

![resource-server-oauth-004](./doc/img/resource-server-oauth-004.jpeg)

Then one can try to call the private api using oauth token. Note the `Authorization: Beare` token in the request and 200 code in response.

![resource-server-oauth-005](./doc/img/resource-server-oauth-005.jpeg)

### Add apis to retrieve current principle and authorities

Add following apis in `PrivateController` to retrieve principal and authorities attribute of the current logged in user.

```java
@Operation(description = "Get Current User Information")
@GetMapping("/user-info")
public ResultEntity<Object> userInfo(Authentication auth)
{
    return ResultEntity.success(auth.getPrincipal());
}

@Operation(description = "Get Current User Authorities")
@GetMapping("/user-authorities")
public ResultEntity<List<GrantedAuthority>> userAuthorities(Authentication auth) 
{
    List<GrantedAuthority> result = new ArrayList<GrantedAuthority>(auth.getAuthorities());
    return ResultEntity.success(result);
}
```

### Configure resource server to load custom claims in JWT Token to load currect authorities

To load JWT claim: `authorities` value to GrantedAuthories of SecurityContext of resouce server, add a custom `JwtAuthenticationConverter` to the `SecurityConfiguration` class of resouce server.

```java
@Bean
JwtAuthenticationConverter jwtAuthenticationConverter()
{
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
}
```

## Step 7: Create fornt end

### Creaet Next App

Create a next.js app useing npx create-next-app

```cmd
npx create-next-app@latest
# options
```

and the options are:

- √ What is your project named? ... test-front
- √ Would you like to use ESLint? ... <u>No</u> / Yes
- √ Would you like to use Tailwind CSS? ... No / <u>Yes</u>
- √ Would you like your code inside a `src/` directory? ... No / <u>Yes</u>
- √ Would you like to use App Router? (recommended) ... No / <u>Yes</u>
- √ Would you like to use Turbopack for `next dev`? ... No / <u>Yes</u>
- √ Would you like to customize the import alias (`@/*` by default)? ...  <u>No</u> / Yes
- √ Would you like to use TypeScript? ... No / <u>Yes</u>

### Add test pages

Add following components for test

`/src/ui/Footer.tsx`

```tsx
export default function Footer()
{
    return <footer className="bg-slate-600 text-slate-200 text-center">Useless Footer</footer>;
}
```

`/src/ui/Header.tsx`

```tsx
import Link from "next/link";

export default function Heeader()
{
    return (
        <header className="bg-slate-600 text-slate-200">
            <Link href="/">Header</Link>
        </header>
    );
}
```

`/src/app/layout.tsx`

```tsx
import type { Metadata } from "next";
import "./globals.css";
import Header from "@/ui/Header";
import Footer from "@/ui/Footer";

export const metadata: Metadata = {
  title: "Create Next App",
  description: "Generated by create next app",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="bg-background text-foreground h-screen w-screen flex flex-col rounded-3xl">
        <Header />
        <main className="flex-1">{children}</main>
        <Footer />
      </body>
    </html>
  );
}
```

`/src/app/page.tsx`

```tsx
import Link from "next/link";

export default function Home() {
  return (
    <div className="grid grid-rows-[5rem_1fr_1fr] h-full w-full items-center justify-items-center p-8 gap-16 sm:p-20">
      <h1 className="text-7xl">Main Page</h1>
      <ul>
        <li><Link href="/public"> Public Page </Link></li>
        <li><Link href="/private"> Private Page </Link></li>
      </ul>
      <p>Paragraph 02</p>
    </div>
  );
}
```

`/src/app/public/page.tsx`

```tsx
export default function Page()
{
    return <h2>Public Home</h2>
}
```

`/src/app/private/page.tsx`

```tsx
export default function Page()
{
    return <h2>Private Home</h2>
}
```

### Add and configure NextAuth.js
