import { UdataResult } from "@/lib/types";
import { udataScriptError, udataScriptResult } from "@/lib/utils";

const apiHost: string = process.env.NEXT_PUBLIC_API_HOST || "";

export async function publicHello(name?: string | null): Promise<UdataResult> 
{
    const methodName: string = "publicHello";

    try
    {
        const url = new URL(`${apiHost}/api/public/hello`);
        if (name) url.searchParams.append("name", name);

        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        });
        const body = await response.json();
        
        return udataScriptResult(body);
    }
    catch (error) 
    {
        console.error(`Error in ${methodName}: ${error}`);
        if(error instanceof Error)
        {
            return udataScriptError(methodName, error.message, JSON.stringify(error));
        }
        else
        {
            return udataScriptError(methodName, "Unknown error", JSON.stringify(error));
        }
    }    
}


export async function privateHello(token: string, name?: string | null): Promise<UdataResult> 
{
    const methodName: string = "privateHello";

    try
    {
        const url = new URL(`${apiHost}/api/private/hello`);
        if (name) url.searchParams.append("name", name);

        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Accept": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });
        const body = await response.json();
        return udataScriptResult(body);
    }
    catch (error) 
    {
        console.error(`Error in ${methodName}: ${error}`);
        if (error instanceof Error)
        {
            return udataScriptError(methodName, error.message, JSON.stringify(error));
        }
        else
        {
            return udataScriptError(methodName, "Unknown error", JSON.stringify(error));
        }
    }
}