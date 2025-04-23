'use client';
import { privateHello } from "@/lib/api-common";
import { processUdataResultPromise } from "@/lib/utils";
import { signIn, useSession } from "next-auth/react";

export default function PrivatePage()
{
    const { data: session } = useSession();
    console.log("PrivatePage session", session);

    if (session?.error === "RefreshTokenError")
    {
        // If the session has an error, we need to sign in again
        signIn();
    }

    function sayHello()
    {
        if (!session)
        {
            alert("User not authenticated");
            return;
        }
        else
        {
            processUdataResultPromise(
                privateHello(session.access_token, "PrivatePage"),
                "processUdataResultPromise",
                (result) => alert(result as string),
                (error) => alert(`Error: ${JSON.stringify(error)}`)
            );
        }
    }

    return (
        <div>
            <h2>Private Page</h2>
            <div>
                <button
                    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                    onClick={sayHello}
                >
                    hello
                </button>
            </div>
        </div>
    );
}