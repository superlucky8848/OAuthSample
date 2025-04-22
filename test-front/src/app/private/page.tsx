import { getAuthSession } from "@/app/api/authSession";
import PrivatePage from "@/ui/PrivatePage";
import { redirect } from "next/navigation";

export default async function Page()
{
    const session = await getAuthSession();

    const searchParams = new URLSearchParams();
    searchParams.set("header", "Auth Error");
    searchParams.set("message", "User not authenticated");
    
    if(!session)
    {
        redirect("/error?" + searchParams.toString());
    }

    return <PrivatePage />
}