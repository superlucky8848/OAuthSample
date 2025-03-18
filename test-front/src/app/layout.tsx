import type { Metadata } from "next";
import "./globals.css";
import Header from "@/ui/Header";
import Footer from "@/ui/Footer";
import { SessionProvider } from "@/provider/SessionProvider";
import { getAuthSession } from "@/app/api/authSession";

export const metadata: Metadata = {
  title: "Test frontend for Next.js",
  description: "Test authentication frontend for Next.js",
};

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const session = await getAuthSession();

  return (
    <html lang="en">
      <body className="bg-background text-foreground h-screen w-screen flex flex-col rounded-3xl">
        <SessionProvider session={session}>
          <Header />
          <main className="flex-1">{children}</main>
          <Footer />
        </SessionProvider>
      </body>
    </html>
  );
}
