import NextAuth, { AuthOptions } from "next-auth";
import { nextAuthOption } from "@/app/api/authSession";

const handler = NextAuth(nextAuthOption);

export {handler as GET, handler as POST};