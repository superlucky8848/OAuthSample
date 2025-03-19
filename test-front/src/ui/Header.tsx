'use client';

import { signIn, signOut, useSession } from "next-auth/react";
import Link from "next/link";

export default function Heeader()
{
    const { status } = useSession();
    return (
        <header className="flex p-2 gap-1 align-middle justify-between bg-slate-600 text-slate-200">
            <Link className="flex-1" href="/">Header</Link>
            {status === "authenticated" &&
                <button
                    className="bg-red-700 hover:bg-red-500 text-white font-bold rounded px-4"
                    onClick={() => signOut()}
                >
                    Sign-Out
                </button>
            }
            {status !== "authenticated" &&
                <button
                    className="bg-blue-700 hover:bg-blue-500 text-white font-bold rounded px-4"
                    onClick={() => signIn()}
                >
                    Sign-In
                </button>
            }
            {status != "authenticated" &&
                <button
                    className="bg-green-700 hover:bg-green-500 text-white font-bold rounded px-4"
                    onClick={() => alert("Sign-Up")}
                >
                    Sign-Up
                </button>
            }         
        </header>
    );
}