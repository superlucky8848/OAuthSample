import Link from "next/link";
import { getAuthSession } from "@/app/api/authSession";

export default async function Home()
{
  const session = await getAuthSession();
  return (
    <div className="grid grid-rows-[5rem_1fr_1fr] h-full w-full items-center justify-items-center p-8 gap-16 sm:p-20">
      <h1 className="text-7xl">Main Page</h1>
      <ul>
        <li><Link href="/public"> Public Page </Link></li>
        <li><Link href="/private"> Private Page </Link></li>
      </ul>
      <pre>{JSON.stringify(session, null, 2)}</pre>
    </div>
  );
}
