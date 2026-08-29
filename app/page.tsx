"use client";

import { useEffect, useRef, useState } from "react";

type Language = "pt" | "en";

const reels = [
  {
    shortcode: "Dcj9c7dpGnu",
    title: "JURAMENTO DA COROA ANCESTRAL",
    category: { pt: "TRILHA ORIGINAL · FANTASIA MEDIEVAL", en: "ORIGINAL SOUNDTRACK · MEDIEVAL FANTASY" },
  },
  {
    shortcode: "DchAH80pxjQ",
    title: "PORTFOLIO UPDATE 05",
    category: { pt: "NOVO VÍDEO · PORTFÓLIO", en: "NEW VIDEO · PORTFOLIO" },
  },
  {
    shortcode: "DcZY4d6pwSl",
    title: "PORTFOLIO UPDATE 01",
    category: { pt: "NOVO VÍDEO · PORTFÓLIO", en: "NEW VIDEO · PORTFOLIO" },
  },
  {
    shortcode: "DcWk5YnurWS",
    title: "PORTFOLIO UPDATE 02",
    category: { pt: "NOVO VÍDEO · PORTFÓLIO", en: "NEW VIDEO · PORTFOLIO" },
  },
  {
    shortcode: "DcUoC4VpMQ5",
    title: "PORTFOLIO UPDATE 03",
    category: { pt: "NOVO VÍDEO · PORTFÓLIO", en: "NEW VIDEO · PORTFOLIO" },
  },
  {
    shortcode: "DcUWZOlp-zg",
    title: "PORTFOLIO UPDATE 04",
    category: { pt: "NOVO VÍDEO · PORTFÓLIO", en: "NEW VIDEO · PORTFOLIO" },
  },
  {
    shortcode: "DcTu2uVpMn2",
    title: "FORGOTTEN KINGDOM",
    category: { pt: "RESCORE · FANTASIA", en: "RESCORE · FANTASY" },
  },
  {
    shortcode: "DcOhLVnpXzH",
    title: "RETRÔ BOSSA",
    category: { pt: "EP AUTORAL · BOSSA NOVA PARA GAMES", en: "ORIGINAL EP · BOSSA NOVA FOR GAMES" },
  },
  {
    shortcode: "DcNT6WWpDQC",
    title: "ASSAULT · CONTRA III",
    category: { pt: "RESCORE · AÇÃO 16-BIT", en: "RESCORE · 16-BIT ACTION" },
  },
  {
    shortcode: "DcMfCZBJbCd",
    title: "POLYGON · BANJO-TOOIE",
    category: { pt: "RESCORE · AVENTURA N64", en: "RESCORE · N64 ADVENTURE" },
  },
  {
    shortcode: "DcMdbhfJVW-",
    title: "BEAST HUNGER · PAC-MAN",
    category: { pt: "RESCORE · ARCADE", en: "RESCORE · ARCADE" },
  },
  {
    shortcode: "DcMUKQpJlOI",
    title: "HYPER · BARRY LEITCH TRIBUTE",
    category: { pt: "HOMENAGEM · RETRO RACING", en: "TRIBUTE · RETRO RACING" },
  },
  {
    shortcode: "DcMSUW0pJ9G",
    title: "DEVIL'S TUNE · SYMPHONY OF THE NIGHT",
    category: { pt: "RESCORE · AÇÃO GÓTICA", en: "RESCORE · GOTHIC ACTION" },
  },
  {
    shortcode: "DcMQe5WprX9",
    title: "TRASH · MEGA MAN ZERO",
    category: { pt: "RESCORE · AÇÃO GBA", en: "RESCORE · GBA ACTION" },
  },
  {
    shortcode: "DcRg2ENJ-E4",
    title: "PORTFOLIO TRACK 08",
    category: { pt: "GAME MUSIC · VIDEO SHOWCASE", en: "GAME MUSIC · VIDEO SHOWCASE" },
  },
  {
    shortcode: "DcRbG_qJGEn",
    title: "PORTFOLIO TRACK 09",
    category: { pt: "GAME MUSIC · VIDEO SHOWCASE", en: "GAME MUSIC · VIDEO SHOWCASE" },
  },
  {
    shortcode: "DcRJEIMpToo",
    title: "PORTFOLIO TRACK 10",
    category: { pt: "GAME MUSIC · VIDEO SHOWCASE", en: "GAME MUSIC · VIDEO SHOWCASE" },
  },
  {
    shortcode: "DcPPuCUp7IM",
    title: "ALÉM DO VÉU DA MAGIA",
    category: { pt: "TRILHA ORIGINAL · FANTASIA", en: "ORIGINAL SOUNDTRACK · FANTASY" },
  },
  {
    shortcode: "DcO8QJeJGAu",
    title: "PORTFOLIO TRACK 12",
    category: { pt: "GAME MUSIC · VIDEO SHOWCASE", en: "GAME MUSIC · VIDEO SHOWCASE" },
  },
  {
    shortcode: "DcMyEGsJcFv",
    title: "ROCK MANIA 86",
    category: { pt: "TRILHA ORIGINAL · ARCADE ROCK", en: "ORIGINAL SOUNDTRACK · ARCADE ROCK" },
  },
  {
    shortcode: "DcMwc9HJH-4",
    title: "THE UNRAVELING",
    category: { pt: "TRILHA ORIGINAL · DARK AMBIENT", en: "ORIGINAL SOUNDTRACK · DARK AMBIENT" },
  },
];

const skills = [
  {
    title: "ORIGINAL GAME SOUNDTRACKS",
    description: {
      pt: "Composição autoral para narrativa, emoção e identidade.",
      en: "Original music shaped around narrative, emotion and identity.",
    },
  },
  {
    title: "GAME AUDIO & SFX",
    description: {
      pt: "Soluções sonoras para ações, ambientes e interfaces.",
      en: "Sound solutions for actions, environments and interfaces.",
    },
  },
  {
    title: "RESCORE",
    description: {
      pt: "Novas leituras musicais para cenas, trailers e gameplays.",
      en: "New musical perspectives for scenes, trailers and gameplay.",
    },
  },
  {
    title: "MUSIC PRODUCTION",
    description: {
      pt: "Da ideia inicial ao arranjo e à produção final.",
      en: "From the first idea to arrangement and final production.",
    },
  },
  {
    title: "ARRANGEMENT & ORCHESTRATION",
    description: {
      pt: "Estrutura, timbres e dinâmica a serviço da experiência.",
      en: "Structure, tone and dynamics serving the player experience.",
    },
  },
  {
    title: "MIXING & MASTERING",
    description: {
      pt: "Equilíbrio, impacto e acabamento para cada composição.",
      en: "Balance, impact and polish for every composition.",
    },
  },
  {
    title: "CHIPTUNE 8/16-BIT",
    description: {
      pt: "Estética retrô com identidade musical contemporânea.",
      en: "Retro aesthetics with a contemporary musical identity.",
    },
  },
  {
    title: "MULTI-INSTRUMENTALIST",
    description: {
      pt: "Vivência prática de performance aplicada à composição.",
      en: "Hands-on performance experience applied to composition.",
    },
  },
];

const services = [
  { value: "Original soundtrack", pt: "Trilha sonora original", en: "Original soundtrack" },
  { value: "Music licensing", pt: "Licenciamento musical", en: "Music licensing" },
  { value: "Game Audio and SFX", pt: "Game Audio & SFX", en: "Game Audio & SFX" },
  { value: "Rescore", pt: "Rescore", en: "Rescore" },
  { value: "Music production", pt: "Produção musical", en: "Music production" },
  { value: "Mixing and mastering", pt: "Mixagem e masterização", en: "Mixing & mastering" },
  { value: "Partnership or collaboration", pt: "Parceria / colaboração", en: "Partnership / collaboration" },
];

const copy = {
  pt: {
    skip: "Ir para o portfólio",
    status: "Status: disponível",
    socialNav: "Redes e portfólios externos",
    home: "Rafael Hunter — início",
    whatsappAria: "Conversar com Rafael Hunter pelo WhatsApp",
    whatsappSmall: "ORÇAMENTO & LICENCIAMENTO",
    instagramAria: "Instagram de Rafael Hunter",
    itchAria: "Portfólio de Rafael Hunter no itch.io",
    linkedinAria: "Encontrar Rafael Hunter no LinkedIn",
    heroTaglineA: "SEU JOGO MERECE",
    heroTaglineB: "MEU SOM.",
    heroIntro: "Trilhas originais, rescores e experiências sonoras criadas para dar identidade, tensão e vida a cada fase.",
    startPortfolio: "START PORTFÓLIO",
    talkWhatsapp: "FALAR NO WHATSAPP",
    scroll: "Descer para o portfólio",
    heroStats: [["EXPERIÊNCIA", "25+ ANOS"], ["VÍDEOS", "21"], ["BASE", "BR → MUNDO"]],
    marquee: ["TRILHAS ORIGINAIS", "RESCORES", "GAME AUDIO", "MULTI-INSTRUMENTISTA"],
    profileAlt: "Retrato em preto e branco de Rafael Hunter",
    profileTitleA: "EXPERIÊNCIA QUE",
    profileTitleB: "VIRA IDENTIDADE.",
    profileLead: "Rafael Hunter é compositor, produtor musical, sound designer e multi-instrumentista brasileiro, com mais de 25 anos dedicados à música.",
    profileText: "Cria trilhas originais, rescores, efeitos sonoros e experiências musicais para games. Seu trabalho percorre do chiptune 8/16-bit ao horror, orquestral, synthwave, ambient e cinematográfico — sempre buscando uma identidade que o jogador reconheça e lembre.",
    profileStats: [["EXPERIÊNCIA MUSICAL", "25+ ANOS"], ["FUNÇÃO", "COMPOSITOR"], ["ATUAÇÃO", "MUNDIAL"]],
    profileDetails: [
      ["NOME ARTÍSTICO", "Rafael Hunter"], ["NOME", "Rafael Mendes Maciel"],
      ["ESTÚDIO", "Nythera Studios"], ["BASE", "Pelotas · Rio Grande do Sul · Brasil"],
      ["E-MAIL", "studiosnythera@gmail.com"], ["WHATSAPP", "+55 53 98124-0783"],
    ],
    viewPortfolio: "VER PORTFÓLIO",
    expertiseKicker: "ESPECIALIDADES PROFISSIONAIS",
    expertiseTitle: "COMPETÊNCIAS PROFISSIONAIS",
    expertiseCount: "08 ÁREAS DE ATUAÇÃO",
    portfolioTitleA: "PORTFÓLIO",
    portfolioTitleB: "MUSICAL",
    portfolioNote: "Escolha uma fase, ative o som e dê o play.",
    videoCount: "21 VÍDEOS",
    playerInstagram: "PLAYER INSTAGRAM",
    reelTitle: (index: number) => `Vídeo ${index + 1} do portfólio musical de Rafael Hunter`,
    contactTitleA: "CONTE SOBRE",
    contactTitleB: "SEU JOGO.",
    contactIntro: "Envie os detalhes do projeto, peça uma trilha original ou converse sobre licenciamento. Sua mensagem chega diretamente ao meu e-mail.",
    socialLinks: "Links profissionais",
    linkedinSmall: "PERFIL PROFISSIONAL",
    successTitle: "MISSÃO ENVIADA!",
    successText: "Sua mensagem foi encaminhada. Responderei pelo e-mail informado.",
    subject: "Novo projeto pelo portfólio — Rafael Hunter",
    autoresponse: "Recebi sua mensagem pelo meu portfólio. Obrigado pelo contato! Em breve responderei pessoalmente. — Rafael Hunter",
    name: "NOME *", namePlaceholder: "Seu nome", email: "E-MAIL *",
    projectType: "TIPO DE PROJETO *", selectOption: "Selecione uma opção",
    gameStudio: "NOME DO JOGO / ESTÚDIO", gameStudioPlaceholder: "Projeto, game ou estúdio",
    message: "MENSAGEM *", messagePlaceholder: "Conte sobre o jogo, estilo musical, referências, prazo e o que você precisa.",
    send: "ENVIAR MENSAGEM", formNote: "ENVIO PROTEGIDO · RESPOSTA DIRETA NO SEU E-MAIL",
    ctaKicker: "PRÓXIMA MISSÃO", ctaTitleA: "PRONTO PARA", ctaTitleB: "O PLAYER 2?",
    ctaDescription: "Vamos criar o som que vai fazer seu jogo ser lembrado.",
    ctaButton: "INICIAR PROJETO", ctaSmall: "PELO WHATSAPP", footerHome: "Voltar ao início",
  },
  en: {
    skip: "Skip to portfolio",
    status: "Status: available",
    socialNav: "Social links and external portfolios",
    home: "Rafael Hunter — home",
    whatsappAria: "Talk to Rafael Hunter on WhatsApp",
    whatsappSmall: "QUOTES & LICENSING",
    instagramAria: "Rafael Hunter on Instagram",
    itchAria: "Rafael Hunter portfolio on itch.io",
    linkedinAria: "Find Rafael Hunter on LinkedIn",
    heroTaglineA: "YOUR GAME DESERVES",
    heroTaglineB: "MY SOUND.",
    heroIntro: "Original soundtracks, rescores and sonic experiences created to bring identity, tension and life to every level.",
    startPortfolio: "START PORTFOLIO",
    talkWhatsapp: "TALK ON WHATSAPP",
    scroll: "Scroll to portfolio",
    heroStats: [["EXPERIENCE", "25+ YEARS"], ["VIDEOS", "21"], ["BASE", "BR → WORLD"]],
    marquee: ["ORIGINAL SCORES", "RESCORES", "GAME AUDIO", "MULTI-INSTRUMENTALIST"],
    profileAlt: "Black-and-white portrait of Rafael Hunter",
    profileTitleA: "EXPERIENCE THAT",
    profileTitleB: "BECOMES IDENTITY.",
    profileLead: "Rafael Hunter is a Brazilian composer, music producer, sound designer and multi-instrumentalist with over 25 years dedicated to music.",
    profileText: "He creates original soundtracks, rescores, sound effects and musical experiences for games. His work moves from 8/16-bit chiptune to horror, orchestral, synthwave, ambient and cinematic music — always building an identity players can recognize and remember.",
    profileStats: [["MUSIC EXPERIENCE", "25+ YEARS"], ["ROLE", "COMPOSER"], ["AVAILABILITY", "WORLDWIDE"]],
    profileDetails: [
      ["ARTIST NAME", "Rafael Hunter"], ["NAME", "Rafael Mendes Maciel"],
      ["STUDIO", "Nythera Studios"], ["BASE", "Pelotas · Rio Grande do Sul · Brazil"],
      ["E-MAIL", "studiosnythera@gmail.com"], ["WHATSAPP", "+55 53 98124-0783"],
    ],
    viewPortfolio: "VIEW PORTFOLIO",
    expertiseKicker: "PROFESSIONAL EXPERTISE",
    expertiseTitle: "PROFESSIONAL SKILLS",
    expertiseCount: "08 AREAS OF EXPERTISE",
    portfolioTitleA: "MUSIC",
    portfolioTitleB: "PORTFOLIO",
    portfolioNote: "Choose a level, turn up the sound and press play.",
    videoCount: "21 VIDEOS",
    playerInstagram: "INSTAGRAM PLAYER",
    reelTitle: (index: number) => `Video ${index + 1} from Rafael Hunter's music portfolio`,
    contactTitleA: "TELL ME ABOUT",
    contactTitleB: "YOUR GAME.",
    contactIntro: "Share your project details, request an original soundtrack or discuss licensing. Your message goes directly to my email.",
    socialLinks: "Professional links",
    linkedinSmall: "PROFESSIONAL PROFILE",
    successTitle: "MISSION SENT!",
    successText: "Your message has been delivered. I will reply to the email you provided.",
    subject: "New project from the portfolio — Rafael Hunter",
    autoresponse: "I received your message through my portfolio. Thank you for reaching out! I will reply personally soon. — Rafael Hunter",
    name: "NAME *", namePlaceholder: "Your name", email: "E-MAIL *",
    projectType: "PROJECT TYPE *", selectOption: "Select an option",
    gameStudio: "GAME / STUDIO NAME", gameStudioPlaceholder: "Project, game or studio",
    message: "MESSAGE *", messagePlaceholder: "Tell me about the game, musical style, references, deadline and what you need.",
    send: "SEND MESSAGE", formNote: "SECURE DELIVERY · DIRECT REPLY TO YOUR E-MAIL",
    ctaKicker: "NEXT MISSION", ctaTitleA: "READY FOR", ctaTitleB: "PLAYER 2?",
    ctaDescription: "Let's create the sound that will make your game unforgettable.",
    ctaButton: "START A PROJECT", ctaSmall: "ON WHATSAPP", footerHome: "Back to top",
  },
} as const;

const siteUrl = "https://rafaelhuntercompositor.github.io";
const instagramProfile = "https://www.instagram.com/rafaelgameaudio/";
const itchProfile = "https://rafael-hunter-game-comp.itch.io/";
const linkedinProfile = "https://www.linkedin.com/in/rafael-mendes-76b96842b/";

function InstagramIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="3" width="18" height="18" rx="5" /><circle cx="12" cy="12" r="4.25" /><circle className="icon-dot" cx="17.4" cy="6.7" r="1" /></svg>;
}

function ItchIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 4h14l2 5-2 2v8H5v-8L3 9l2-5Z" /><path d="M8.2 14.5h7.6M10 12.7v3.6M14.7 13.7h.1M17 15.2h.1" /></svg>;
}

function LinkedinIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><path className="icon-fill" d="M6.5 8.3H3.2V21h3.3V8.3ZM4.85 3A1.93 1.93 0 1 0 4.85 6.86 1.93 1.93 0 0 0 4.85 3ZM21 13.72c0-3.83-2.04-5.61-4.77-5.61-2.2 0-3.18 1.21-3.72 2.06V8.3H9.2V21h3.31v-6.29c0-1.66.31-3.27 2.38-3.27 2.04 0 2.06 1.91 2.06 3.38V21H21v-7.28Z" /></svg>;
}

function LanguageGate({ onSelect }: { onSelect: (language: Language) => void }) {
  const audioRef = useRef<HTMLAudioElement>(null);
  const [isPlaying, setIsPlaying] = useState(false);

  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) return;

    audio.volume = 0.28;
    void audio.play().catch(() => {
      // Mobile browsers commonly wait for the visitor's first tap.
    });

    return () => {
      audio.pause();
      audio.currentTime = 0;
    };
  }, []);

  const toggleAudio = async () => {
    const audio = audioRef.current;
    if (!audio) return;

    if (audio.paused) {
      try {
        await audio.play();
      } catch {
        setIsPlaying(false);
      }
      return;
    }

    audio.pause();
  };

  return (
    <main className="language-gate">
      <audio
        ref={audioRef}
        src="/language-theme-github.mp3"
        autoPlay
        loop
        playsInline
        preload="auto"
        onPlay={() => setIsPlaying(true)}
        onPause={() => setIsPlaying(false)}
        aria-hidden="true"
      />
      <div className="language-stars" aria-hidden="true" />
      <section className="language-room" aria-labelledby="language-title">
        <p className="language-kicker"><span aria-hidden="true">◆</span> PLAYER LANGUAGE SELECT</p>
        <h1 id="language-title">ESCOLHA SEU IDIOMA</h1>
        <p className="language-subtitle">CHOOSE YOUR LANGUAGE</p>

        <div className="language-rig">
          <span className="language-antenna" aria-hidden="true"><i /><i /></span>
          <div className="language-tv">
            <div className="language-screen">
              <p>SELECT PLAYER</p>
              <div className="language-options">
                <button type="button" onClick={() => onSelect("pt")} aria-label="Entrar no site em português">
                  <span className="language-flag" aria-hidden="true">🇧🇷</span>
                  <strong>PORTUGUÊS</strong>
                  <small>PLAYER BR</small>
                </button>
                <button type="button" onClick={() => onSelect("en")} aria-label="Enter the website in English">
                  <span className="language-flag" aria-hidden="true">🇺🇸</span>
                  <strong>ENGLISH</strong>
                  <small>PLAYER US</small>
                </button>
              </div>
              <span className="language-scanlines" aria-hidden="true" />
            </div>
            <div className="language-tv-controls">
              <span aria-hidden="true">RH-1986</span>
              <button
                type="button"
                className={`language-audio-control${isPlaying ? " is-playing" : ""}`}
                onClick={toggleAudio}
                aria-label={isPlaying ? "Pausar música / Pause music" : "Ativar música / Enable music"}
                aria-pressed={isPlaying}
              >
                <b aria-hidden="true" />
                <span className="language-audio-copy">
                  <strong>{isPlaying ? "SOM ATIVO" : "ATIVAR SOM"}</strong>
                  <small>{isPlaying ? "SOUND ON" : "ENABLE SOUND"}</small>
                </span>
              </button>
              <i aria-hidden="true" /><i aria-hidden="true" />
            </div>
          </div>

          <span className="language-cable" aria-hidden="true" />
          <div className="language-console" aria-hidden="true">
            <span className="console-slot" />
            <span className="console-power">POWER <i /></span>
            <span className="console-logo">RH 16-BIT</span>
            <span className="console-port" /><span className="console-port" />
          </div>
        </div>

        <p className="language-instruction">▲ CHOOSE A PLAYER · ESCOLHA UM PLAYER ▲</p>
      </section>
    </main>
  );
}

export default function Home() {
  const [language, setLanguage] = useState<Language | null>(null);
  const lang = language ?? "pt";
  const t = copy[lang];
  const whatsappText = lang === "pt"
    ? "Olá, Rafael! Vi seu portfólio e gostaria de conversar sobre trilha sonora, game audio ou licenciamento para meu projeto."
    : "Hello, Rafael! I saw your portfolio and would like to discuss original music, game audio or licensing for my project.";
  const whatsappLink = `https://wa.me/5553981240783?text=${encodeURIComponent(whatsappText)}`;

  useEffect(() => {
    document.documentElement.lang = lang === "pt" ? "pt-BR" : "en";
  }, [lang]);

  if (language === null) return <LanguageGate onSelect={setLanguage} />;

  return (
    <main id="top" className="game-shell">
      <a className="skip-link" href="#portfolio">{t.skip}</a>
      <div className="screen-texture" aria-hidden="true" />

      <a className="whatsapp-float" href={whatsappLink} target="_blank" rel="noreferrer" aria-label={t.whatsappAria}>
        <span className="whatsapp-icon" aria-hidden="true">WA</span>
        <span className="whatsapp-copy"><strong>WHATSAPP</strong><small>{t.whatsappSmall}</small></span>
      </a>

      <header className="game-header">
        <a className="pixel-brand" href="#top" aria-label={t.home}>
          <span className="brand-icon" aria-hidden="true">RH</span>
          <span className="brand-copy"><strong>RAFAEL HUNTER</strong><small>GAME COMPOSER</small></span>
        </a>

        <div className="player-status" aria-label={t.status}>
          <span className="status-light" aria-hidden="true" /><span>PLAYER 01</span><strong>READY</strong>
        </div>

        <div className="header-tools">
          <div className="language-toggle" role="group" aria-label="Language / Idioma">
            <button type="button" className={lang === "pt" ? "active" : ""} aria-pressed={lang === "pt"} onClick={() => setLanguage("pt")}><span aria-hidden="true">🇧🇷</span> PT</button>
            <button type="button" className={lang === "en" ? "active" : ""} aria-pressed={lang === "en"} onClick={() => setLanguage("en")}><span aria-hidden="true">🇺🇸</span> EN</button>
          </div>

          <nav className="header-socials" aria-label={t.socialNav}>
            <a href={instagramProfile} target="_blank" rel="noreferrer" aria-label={t.instagramAria}><InstagramIcon /></a>
            <a href={itchProfile} target="_blank" rel="noreferrer" aria-label={t.itchAria}><ItchIcon /></a>
            <a href={linkedinProfile} target="_blank" rel="noreferrer" aria-label={t.linkedinAria}><LinkedinIcon /></a>
          </nav>
        </div>
      </header>

      <section className="hero" aria-labelledby="hero-title">
        <div className="hero-art" aria-hidden="true" /><div className="hero-shade" aria-hidden="true" />
        <div className="hero-content">
          <p className="system-message"><span aria-hidden="true">◆</span> ORIGINAL GAME AUDIO</p>
          <p className="hero-kicker">GAME COMPOSER <i aria-hidden="true">/</i> MUSIC PRODUCER</p>
          <h1 id="hero-title"><span>RAFAEL</span><strong>HUNTER</strong></h1>
          <p className="hero-tagline">{t.heroTaglineA}<br /><em>{t.heroTaglineB}</em></p>
          <p className="hero-intro">{t.heroIntro}</p>
          <div className="hero-actions">
            <a className="start-button" href="#portfolio"><span className="play-icon" aria-hidden="true">▶</span><span>{t.startPortfolio}</span></a>
            <a className="text-button" href={whatsappLink} target="_blank" rel="noreferrer">{t.talkWhatsapp} <b aria-hidden="true">↗</b></a>
          </div>
          <dl className="hero-stats">{t.heroStats.map(([label, value]) => <div key={label}><dt>{label}</dt><dd>{value}</dd></div>)}</dl>
        </div>
        <div className="hero-hud" aria-hidden="true"><span>STEREO</span><div className="hud-bars"><i /><i /><i /><i /><i /><i /><i /><i /></div><small>96 KHZ / 24 BIT</small></div>
        <a className="scroll-cue" href="#portfolio" aria-label={t.scroll}><span>SCROLL</span><i aria-hidden="true">↓</i></a>
      </section>

      <div className="marquee" aria-label={t.expertiseKicker}>
        <div className="marquee-track">
          {[...t.marquee, ...t.marquee].map((item, index) => <span key={`${item}-${index}`} aria-hidden={index >= t.marquee.length}>{item}<i>◆</i></span>)}
        </div>
      </div>

      <section className="profile-section" id="profile" aria-labelledby="profile-title">
        <div className="profile-grid">
          <div className="profile-visual">
            <div className="profile-photo-frame">
              <img src="/rafael-hunter-profile.jpg" alt={t.profileAlt} loading="lazy" />
              <span className="profile-scanlines" aria-hidden="true" /><span className="profile-corner profile-corner-a" aria-hidden="true" /><span className="profile-corner profile-corner-b" aria-hidden="true" /><span className="profile-corner profile-corner-c" aria-hidden="true" /><span className="profile-corner profile-corner-d" aria-hidden="true" />
              <p aria-hidden="true">PLAYER 01 <b>● ONLINE</b></p>
            </div>
            <div className="profile-id"><span>{lang === "pt" ? "ID DO PERFIL" : "PROFILE ID"}</span><strong>RAFAEL HUNTER</strong><small>GAME COMPOSER · NYTHERA STUDIOS</small></div>
          </div>

          <div className="profile-copy">
            <p className="level-label"><span>01</span> PLAYER PROFILE</p>
            <h2 id="profile-title">{t.profileTitleA}<br /><em>{t.profileTitleB}</em></h2>
            <p className="profile-lead">{t.profileLead}</p><p className="profile-text">{t.profileText}</p>
            <dl className="profile-stats">{t.profileStats.map(([label, value]) => <div key={label}><dt>{label}</dt><dd>{value}</dd></div>)}</dl>
            <dl className="profile-details">{t.profileDetails.map(([label, value]) => <div key={label}><dt>{label}</dt><dd>{value}</dd></div>)}</dl>
            <a className="profile-action" href="#portfolio"><span aria-hidden="true">▶</span> {t.viewPortfolio}</a>
          </div>
        </div>

        <div className="skill-tree">
          <div className="skill-tree-head"><div><p>{t.expertiseKicker}</p><h3>{t.expertiseTitle}</h3></div><span><i aria-hidden="true" /> {t.expertiseCount}</span></div>
          <div className="skills-grid">
            {skills.map((skill, index) => (
              <article className="skill-card" key={skill.title}>
                <span className="skill-code" aria-hidden="true">{String(index + 1).padStart(2, "0")}</span>
                <div><h4>{skill.title}</h4><p>{skill.description[lang]}</p></div><i className="skill-status" aria-hidden="true">+</i>
              </article>
            ))}
          </div>
        </div>
      </section>

      <section className="portfolio" id="portfolio" aria-labelledby="portfolio-title">
        <div className="portfolio-head">
          <div><p className="level-label"><span>02</span> STAGE SELECT</p><h2 id="portfolio-title">{t.portfolioTitleA}<br /><em>{t.portfolioTitleB}</em></h2></div>
          <div className="portfolio-note"><p>{t.portfolioNote}</p><span>{t.videoCount} <i aria-hidden="true" /> {t.playerInstagram}</span></div>
        </div>

        <div className="reels-grid">
          {reels.map((reel, index) => {
            const reelUrl = `https://www.instagram.com/reel/${reel.shortcode}/`;
            return (
              <article className="reel-card" key={reel.shortcode}>
                <span className="tv-antenna" aria-hidden="true"><i /><i /></span>
                <div className="reel-frame"><iframe src={`${reelUrl}embed/`} title={t.reelTitle(index)} loading={index < 2 ? "eager" : "lazy"} allow="autoplay; clipboard-write; encrypted-media; picture-in-picture; web-share" allowFullScreen referrerPolicy="strict-origin-when-cross-origin" /></div>
                <div className="tv-console" aria-hidden="true"><span className="tv-speaker" /><span className="tv-channel">CH {String(index + 1).padStart(2, "0")}</span><span className="tv-dials"><i /><i /></span></div>
              </article>
            );
          })}
        </div>
      </section>

      <section className="contact-section" id="contact" aria-labelledby="contact-title">
        <div className="contact-copy">
          <p className="level-label"><span>03</span> CONTACT MODE</p><h2 id="contact-title">{t.contactTitleA}<br /><em>{t.contactTitleB}</em></h2><p className="contact-intro">{t.contactIntro}</p>
          <div className="social-cards" aria-label={t.socialLinks}>
            <a href={instagramProfile} target="_blank" rel="noreferrer" className="social-card social-instagram"><span className="social-icon"><InstagramIcon /></span><span><strong>INSTAGRAM</strong><small>@rafaelgameaudio</small></span><b aria-hidden="true">↗</b></a>
            <a href={itchProfile} target="_blank" rel="noreferrer" className="social-card social-itch"><span className="social-icon"><ItchIcon /></span><span><strong>ITCH.IO</strong><small>GAME AUDIO PORTFOLIO</small></span><b aria-hidden="true">↗</b></a>
            <a href={linkedinProfile} target="_blank" rel="noreferrer" className="social-card social-linkedin"><span className="social-icon"><LinkedinIcon /></span><span><strong>LINKEDIN</strong><small>{t.linkedinSmall}</small></span><b aria-hidden="true">↗</b></a>
          </div>
          <a className="direct-email" href="mailto:studiosnythera@gmail.com"><span aria-hidden="true">✉</span>studiosnythera@gmail.com</a>
        </div>

        <div className="form-terminal">
          <div className="terminal-bar" aria-hidden="true"><span><i /> NEW_PROJECT.EXE</span><b>ONLINE</b></div>
          <p className="form-success" id="form-success" role="status"><strong>{t.successTitle}</strong>{t.successText}</p>
          <form action="https://formsubmit.co/studiosnythera@gmail.com" method="POST">
            <input type="hidden" name="_subject" value={t.subject} /><input type="hidden" name="_template" value="table" /><input type="hidden" name="_next" value={`${siteUrl}/#form-success`} /><input type="hidden" name="_url" value={`${siteUrl}/#contact`} /><input type="hidden" name="_autoresponse" value={t.autoresponse} /><input type="hidden" name="Language" value={lang === "pt" ? "Português" : "English"} />
            <input className="form-honey" type="text" name="_honey" tabIndex={-1} autoComplete="off" />
            <div className="form-row">
              <label><span>{t.name}</span><input type="text" name="Name" autoComplete="name" placeholder={t.namePlaceholder} required /></label>
              <label><span>{t.email}</span><input type="email" name="email" autoComplete="email" placeholder="you@email.com" required /></label>
            </div>
            <div className="form-row">
              <label><span>{t.projectType}</span><select name="Project type" defaultValue="" required><option value="" disabled>{t.selectOption}</option>{services.map((service) => <option value={service.value} key={service.value}>{service[lang]}</option>)}</select></label>
              <label><span>{t.gameStudio}</span><input type="text" name="Game or studio" placeholder={t.gameStudioPlaceholder} /></label>
            </div>
            <label><span>{t.message}</span><textarea name="Message" rows={6} placeholder={t.messagePlaceholder} required /></label>
            <button type="submit"><span aria-hidden="true">▶</span>{t.send}<b aria-hidden="true">↗</b></button><p className="form-note">{t.formNote}</p>
          </form>
        </div>
      </section>

      <section className="final-cta" aria-labelledby="cta-title">
        <div className="cta-pixels" aria-hidden="true" /><div className="cta-copy"><p><span aria-hidden="true">◆</span> {t.ctaKicker}</p><h2 id="cta-title">{t.ctaTitleA}<br /><em>{t.ctaTitleB}</em></h2><span className="cta-description">{t.ctaDescription}</span></div>
        <a href={whatsappLink} target="_blank" rel="noreferrer"><span>{t.ctaButton}<br /><small>{t.ctaSmall}</small></span><b aria-hidden="true">↗</b></a>
      </section>

      <footer>
        <a className="pixel-brand" href="#top" aria-label={t.footerHome}><span className="brand-icon" aria-hidden="true">RH</span><span className="brand-copy"><strong>RAFAEL HUNTER</strong><small>GAME COMPOSER</small></span></a>
        <p>ORIGINAL SCORES · RESCORES · GAME AUDIO</p><span>© 2026 · BRAZIL · WORLDWIDE</span>
      </footer>
    </main>
  );
}
