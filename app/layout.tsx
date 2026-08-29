import type { Metadata, Viewport } from "next";
import "./globals.css";

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  maximumScale: 5,
  userScalable: true,
  viewportFit: "cover",
};

export const metadata: Metadata = {
  metadataBase: new URL("https://rafaelhuntercompositor.github.io"),
  title: "Rafael Hunter — Game Composer",
  description:
    "Portfólio de Rafael Hunter: trilhas originais, rescores e game audio para dar vida ao seu jogo.",
  keywords: [
    "Rafael Hunter",
    "game composer",
    "game music",
    "game audio",
    "trilha sonora para jogos",
    "rescore",
  ],
  authors: [{ name: "Rafael Hunter" }],
  creator: "Rafael Hunter",
  icons: {
    icon: "/favicon.svg",
    shortcut: "/favicon.svg",
  },
  openGraph: {
    type: "website",
    locale: "pt_BR",
    alternateLocale: "en_US",
    title: "Rafael Hunter — Game Composer",
    description: "Seu jogo merece meu som. Trilhas originais, rescores e game audio em vídeo.",
    url: "https://rafaelhuntercompositor.github.io",
    siteName: "Rafael Hunter — Game Composer",
    images: [
      {
        url: "/og.webp",
        width: 1200,
        height: 630,
        alt: "Rafael Hunter — Game Composer",
      },
    ],
  },
  twitter: {
    card: "summary_large_image",
    title: "Rafael Hunter — Game Composer",
    description: "Seu jogo merece meu som. Trilhas originais, rescores e game audio em vídeo.",
    images: ["/og.webp"],
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body>{children}</body>
    </html>
  );
}
