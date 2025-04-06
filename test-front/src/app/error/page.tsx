import Link from "next/link";

export default async function Page(
{
    searchParams
}:
{
    searchParams: Promise<{ [key: string]: string | string[] | undefined }>
})
{
    const {header, message} = await searchParams;

    return (
        <div>
            <h2>{header || "Error Page"}</h2>
            <p>{message || "Error Message"}</p>
            <Link className="underline hover:text-blue-500" href="/"> Back to Home</Link>
        </div>
    );
}