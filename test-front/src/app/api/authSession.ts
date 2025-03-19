import { GetServerSidePropsContext, NextApiRequest, NextApiResponse } from "next";
import { AuthOptions, getServerSession } from "next-auth";
import Github from "next-auth/providers/github";

export const nextAuthOption: AuthOptions = {
    providers: [
        Github({
            clientId: 'Ov23liaI4PlnSCCRlBnX',
            clientSecret: "a8b7a5be15bfc27c22f93b7a7a5f4133c6f84053"
        })
    ],
    callbacks:{
        async jwt({token, account})
        {
            if(account) 
            {
                return {
                    ...token,
                    access_token: account.access_token || "",
                    expires_at: account.expires_at || 0,
                    refresh_token: account.refresh_token
                };
            }
            else if(Date.now() < token.expires_at) 
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