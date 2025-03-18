'use client';

import { signIn, signOut } from "next-auth/react";
import Link from "next/link";

export default function Heeader()
{
    return (
        <header className="flex p-2 gap-1 align-middle justify-between bg-slate-600 text-slate-200">
            <Link className="flex-1" href="/">Header</Link>
            <button 
                className="bg-blue-500 hover:bg-blue-700 text-white font-bold rounded px-4"
                onClick={() => signIn()}
            >
                Sign-In
            </button>
            <button 
                className="bg-red-500 hover:bg-red-700 text-white font-bold rounded px-4"
                onClick={() => signOut()}
            >
                Sign-Out
            </button>
        </header>
    );
}