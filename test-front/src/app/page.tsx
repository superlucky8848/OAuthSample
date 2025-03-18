import Link from "next/link";

export default function Home() {
  return (
    <div className="grid grid-rows-[5rem_1fr_1fr] h-full w-full items-center justify-items-center p-8 gap-16 sm:p-20">
      <h1 className="text-7xl">Main Page</h1>
      <ul>
        <li><Link href="/public"> Public Page </Link></li>
        <li><Link href="/private"> Private Page </Link></li>
      </ul>
      <p>Paragraph 02</p>
    </div>
  );
}
