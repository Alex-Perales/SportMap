import React, { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight, ArrowRight } from 'lucide-react';
import heroSoccerField from '../../assets/images/hero-soccer-field.jpg';
import heroPlayersAction from '../../assets/images/hero-players-action.jpg';
import heroTennisPool from '../../assets/images/hero-tennis-pool.jpg';

interface HeroProps {
  onOpenDownloadModal: () => void;
  onExploreCanchas?: () => void;
}

const HERO_SLIDES = [
  {
    id: 1,
    location: 'LIMA, PERÚ',
    titleLine1: 'CANCHAS & RESERVAS',
    titleLine2: 'PARA TU EQUIPO',
    description: 'Transformamos la forma de jugar en Lima. Reserva canchas de fútbol, tenis, básquet y más en segundos con disponibilidad en tiempo real.',
    image: heroSoccerField,
    primaryCta: 'Reservar cancha',
    secondaryCta: 'Ver servicios',
  },
  {
    id: 2,
    location: 'LIMA DEPORTIVA · PICHANGAS',
    titleLine1: 'ORGANIZA Y JUEGA',
    titleLine2: 'SIN COMPLICACIONES',
    description: 'Coordina partidos, confirma la asistencia de tus amigos y paga la cuota por Yape o Plin al instante sin llamadas ni grupos interminables.',
    image: heroPlayersAction,
    primaryCta: 'Descargar App',
    secondaryCta: 'Ver canchas',
  },
  {
    id: 3,
    location: 'CANCHAS & TIENDA · DEPORTE',
    titleLine1: 'NATACIÓN, TENIS',
    titleLine2: 'Y EQUIPAMIENTO TOP',
    description: 'Explora instalaciones verificadas en Lima, e implementa tu juego con productos deportivos oficiales con delivery.',
    image: heroTennisPool,
    primaryCta: 'Explorar tienda',
    secondaryCta: 'Ver rutas',
  },
];

export const Hero: React.FC<HeroProps> = ({ onOpenDownloadModal, onExploreCanchas }) => {
  const [currentSlide, setCurrentSlide] = useState(0);

  // Preload hero slide images for instant transitions
  useEffect(() => {
    HERO_SLIDES.forEach((slide) => {
      const img = new Image();
      img.src = slide.image;
    });
  }, []);

  const handlePrev = () => {
    setCurrentSlide((prev) => (prev === 0 ? HERO_SLIDES.length - 1 : prev - 1));
  };

  const handleNext = () => {
    setCurrentSlide((prev) => (prev === HERO_SLIDES.length - 1 ? 0 : prev + 1));
  };

  // Auto slide interval optimized with clean cleanup
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentSlide((prev) => (prev === HERO_SLIDES.length - 1 ? 0 : prev + 1));
    }, 7000);
    return () => clearInterval(timer);
  }, []);

  const activeSlide = HERO_SLIDES[currentSlide];

  return (
    <section id="inicio" className="relative min-h-[90vh] md:min-h-screen flex items-center justify-center bg-slate-950 text-white overflow-hidden pt-20">
      {/* Background Photo with Smooth Transition */}
      <div className="absolute inset-0 z-0 overflow-hidden">
        <img
          key={activeSlide.id}
          src={activeSlide.image}
          alt={`${activeSlide.titleLine1} ${activeSlide.titleLine2}`}
          className="w-full h-full object-cover object-center transition-all duration-700 brightness-100"
        />

        {/* Soft readable gradient overlay */}
        <div className="absolute inset-0 bg-gradient-to-r from-slate-950/70 via-slate-950/30 to-transparent" />
      </div>

      {/* Floating Left Circle Navigation Button */}
      <button
        onClick={handlePrev}
        aria-label="Slide anterior"
        className="absolute left-4 sm:left-8 top-1/2 -translate-y-1/2 z-30 w-12 h-12 sm:w-14 sm:h-14 rounded-full bg-slate-950/60 hover:bg-emerald-600 text-white border border-white/20 flex items-center justify-center transition-all cursor-pointer backdrop-blur-md shadow-2xl hover:scale-110 active:scale-95 group"
      >
        <ChevronLeft className="w-6 h-6 group-hover:-translate-x-0.5 transition-transform" />
      </button>

      {/* Floating Right Circle Navigation Button */}
      <button
        onClick={handleNext}
        aria-label="Siguiente slide"
        className="absolute right-4 sm:right-8 top-1/2 -translate-y-1/2 z-30 w-12 h-12 sm:w-14 sm:h-14 rounded-full bg-slate-950/60 hover:bg-emerald-600 text-white border border-white/20 flex items-center justify-center transition-all cursor-pointer backdrop-blur-md shadow-2xl hover:scale-110 active:scale-95 group"
      >
        <ChevronRight className="w-6 h-6 group-hover:translate-x-0.5 transition-transform" />
      </button>

      {/* Hero Central Content */}
      <div className="max-w-6xl mx-auto px-6 sm:px-12 text-left relative z-20 w-full py-16">
        <div className="max-w-3xl space-y-5 animate-fade-in">
          {/* Location Badge Tag */}
          <div>
            <span className="px-4 py-1.5 text-sm sm:text-base font-bold tracking-widest text-slate-200 uppercase border border-white/25 rounded-lg bg-slate-900/60 backdrop-blur-md inline-block">
              {activeSlide.location}
            </span>
          </div>

          {/* Massive Display Title */}
          <h1 className="ui-title-hero text-white uppercase leading-[0.95] drop-shadow-2xl">
            {activeSlide.titleLine1}
            <span className="block text-emerald-500 drop-shadow-[0_4px_20px_rgba(16,185,129,0.35)] mt-1">
              {activeSlide.titleLine2}
            </span>
          </h1>

          {/* Description Paragraph */}
          <p className="ui-text-lead text-slate-200 font-medium max-w-2xl pt-2 drop-shadow-md">
            {activeSlide.description}
          </p>

          {/* CTAs Row */}
          <div className="flex flex-wrap items-center gap-4 pt-4">
            <button
              onClick={onOpenDownloadModal}
              className="px-8 py-4 bg-emerald-600 hover:bg-emerald-500 text-white font-extrabold text-base sm:text-base rounded-xl transition-all shadow-xl shadow-emerald-600/30 hover:scale-105 active:scale-95 cursor-pointer"
            >
              {activeSlide.primaryCta}
            </button>

            {onExploreCanchas && (
              <button
                onClick={onExploreCanchas}
                className="px-8 py-4 bg-white/10 hover:bg-white/20 text-white border border-white/30 font-extrabold text-base sm:text-base rounded-xl backdrop-blur-md transition-all hover:scale-105 active:scale-95 cursor-pointer flex items-center gap-2"
              >
                <span>{activeSlide.secondaryCta}</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Bottom Slider Pagination Dots */}
      <div className="absolute bottom-8 left-1/2 -translate-x-1/2 z-30 flex items-center gap-3 bg-slate-950/40 backdrop-blur-md px-4 py-2 rounded-full border border-white/10">
        {HERO_SLIDES.map((slide, index) => (
          <button
            key={slide.id}
            onClick={() => setCurrentSlide(index)}
            aria-label={`Ir al slide ${index + 1}`}
            className={`transition-all duration-300 cursor-pointer ${
              index === currentSlide
                ? 'w-8 h-3 rounded-full bg-emerald-500'
                : 'w-3 h-3 rounded-full bg-white/40 hover:bg-white/80'
            }`}
          />
        ))}
      </div>
    </section>
  );
};

