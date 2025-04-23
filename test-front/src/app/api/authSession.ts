import { GetServerSidePropsContext, NextApiRequest, NextApiResponse } from "next";
import { AuthOptions, getServerSession } from "next-auth";
import Github from "next-auth/providers/github";

const authHost = process.env.NEXT_PUBLIC_AUTH_HOST || "http://localhost:8081";

export const nextAuthOption: AuthOptions = {
    providers: [
        Github({
            clientId: 'Ov23liaI4PlnSCCRlBnX',
            clientSecret: "a8b7a5be15bfc27c22f93b7a7a5f4133c6f84053"
        }),
        {
            id: "front-client",
            name: "Front Client",
            type: "oauth",
            version: "2.0",
            wellKnown: `${authHost}/.well-known/openid-configuration`,
            idToken: true,
            clientId: "front-client",
            clientSecret: "654321",
            // authorization: {
            //     url: "http://localhost:8081/auth/oauth2/authorize",
            //     params: {scope: "openid email", response_type: "code"}
            // },
            // token: "http://localhost:8081/auth/oauth2/token",
            // userinfo: "http://localhost:8081/auth/oidc/userinfo",
            // jwks_endpoint: "http://localhost:8081/auth/oauth2/jwks",
            // issuer: "http://localhost:8081",
            profile(profile, tokens) {
                console.log("Profile", profile);
                console.log("Token Set", tokens);
                return {
                    id: profile.sub,
                    name: profile.udata_name || "(Unknown)",
                    email: profile.udata_email || "(Unknown)",
                    image: profile.udata_avatar || null
                }
            }
        }
    ],
    callbacks:{
        async jwt({token, account, profile})
        {
            console.log("jwt-info-token", token);
            console.log("jwt-info-account", account);
            console.log("jwt-info-profile", profile);
            if(account) 
            {
                return {
                    ...token,
                    access_token: account.access_token || "",
                    expires_at: account.expires_at || 0,
                    refresh_token: account.refresh_token
                };
            }
            else if(Date.now() < token.expires_at * 1000) 
            {
                return token;
            }
            else
            {
                console.log("Token expired, refreshing...");
                if (!token.refresh_token) throw new TypeError("Missing refresh_token");

                try
                {
                    const tokenEndpoint = `${authHost}/auth/oauth2/token`;
                    const params = new URLSearchParams({
                        client_id: "front-client",
                        client_secret: "654321",
                        grant_type: "refresh_token",
                        refresh_token: token.refresh_token!
                    });

                    const response = await fetch(tokenEndpoint, {
                        method: "POST",
                        body: params,
                    });

                    const tokensOrError = await response.json();

                    if (!response.ok) throw tokensOrError;

                    const newTokens = tokensOrError as {
                        access_token: string
                        expires_in: number
                        refresh_token?: string
                    }

                    return {
                        ...token,
                        access_token: newTokens.access_token,
                        expires_at: Math.floor(Date.now() / 1000 + newTokens.expires_in),
                        // Some providers only issue refresh tokens once, so preserve if we did not get a new one
                        refresh_token: newTokens.refresh_token || token.refresh_token
                    }
                }
                catch(error)
                {
                    console.error("Error refreshing access_token", error)
                    // If we fail to refresh the token, return an error so we can handle it on the page
                    token.error = "RefreshTokenError"
                    return token
                }
            }
        },
        async session({session, token})
        {
            session.user = {name: token.name, email: token.email};
            session.access_token = token.access_token;
            session.error = token.error;
            return session;
        }
    }
};

export function getAuthSession(...args: [GetServerSidePropsContext["req"], GetServerSidePropsContext["res"]]
    | [NextApiRequest, NextApiResponse]
    | [])
{
    return getServerSession(...args, nextAuthOption);
}