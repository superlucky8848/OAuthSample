import { GetServerSidePropsContext, NextApiRequest, NextApiResponse } from "next";
import { AuthOptions, getServerSession } from "next-auth";
import Github from "next-auth/providers/github";

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
            wellKnown: "http://localhost:8081/.well-known/openid-configuration",
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
                    email: profile.udata_email || "(Unknown)"
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
                console.log("refresh token needed");
                return token;
            }
        },
        async session({session, token})
        {
            session.user = {name: token.name, email: token.email};
            session.access_token = token.access_token;
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