import { GetServerSidePropsContext, NextApiRequest, NextApiResponse } from "next";
import { AuthOptions, getServerSession } from "next-auth";
import Github from "next-auth/providers/github";

export const nextAuthOption: AuthOptions = {
    providers: [
            Github({
                clientId: 'Ov23liaI4PlnSCCRlBnX',
                clientSecret: "a8b7a5be15bfc27c22f93b7a7a5f4133c6f84053"
            })
        ]
};

export function getAuthSession(...args: [GetServerSidePropsContext["req"], GetServerSidePropsContext["res"]]
    | [NextApiRequest, NextApiResponse]
    | [])
{
    return getServerSession(...args, nextAuthOption);
}