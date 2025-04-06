import Link from "next/link";
import { getAuthSession } from "@/app/api/authSession";

export default async function Home()
{
  const session = await getAuthSession();
  return (
    <div className="grid grid-rows-[5rem_1fr_1fr] h-full w-full items-center justify-items-center p-8 gap-16 sm:p-20">
      <h1 className="text-7xl">Main Page</h1>
      <ul>
        <li><Link className="underline hover:text-blue-500" href="/public"> Public Page </Link></li>
        <li><Link className="underline hover:text-blue-500" href="/private"> Private Page </Link></li>
      </ul>
      <pre className="max-w-[80vw] overflow-auto whitespace-pre-wrap">{JSON.stringify(session, null, 2)}</pre>
    </div>
  );
}
