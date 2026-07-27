import React from 'react';
import { Filter, X } from 'lucide-react';

interface SportsCategoriesProps {
  selectedSport: string;
  onSelectSport: (sportName: string) => void;
}

export const FIVE_SPORTS = [
  {
    id: 'futbol',
    name: 'Fútbol',
    shortTitle: 'Fútbol',
    fullName: 'Fútbol & Futsal',
    image: 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&q=80&w=800',
    description: 'Sintético, losa y gras natural',
  },
  {
    id: 'voley',
    name: 'Vóley',
    shortTitle: 'Vóley',
    fullName: 'Vóley & Playa',
    image: 'https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?auto=format&fit=crop&q=80&w=800',
    description: 'Piso flotante, losa y arena',
  },
  {
    id: 'basquet',
    name: 'Básquetbol',
    shortTitle: 'Básquet',
    fullName: 'Básquetbol',
    image: 'https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&q=80&w=800',
    description: 'Losa, techada y parquet',
  },
  {
    id: 'tenis',
    name: 'Tenis',
    shortTitle: 'Tenis',
    fullName: 'Tenis & Pádel',
    image: 'https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?auto=format&fit=crop&q=80&w=800',
    description: 'Arcilla, dura y pádel',
  },
  {
    id: 'natacion',
    name: 'Natación',
    shortTitle: 'Natación',
    fullName: 'Natación',
    image: 'https://images.unsplash.com/photo-1530549387789-4c1017266635?auto=format&fit=crop&q=80&w=800',
    description: 'Piscinas temperadas y olímpicas',
  },
];

export const SportsCategories: React.FC<SportsCategoriesProps> = ({ selectedSport, onSelectSport }) => {
  return (
    <section className="bg-white py-12 sm:py-16 border-b border-slate-200 text-slate-900 relative overflow-hidden">
      {/* Subtle Light Emerald Glow */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-96 h-96 bg-emerald-500/5 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        
        {/* Header Title in clean, prominent typography */}
        <div className="text-center max-w-3xl mx-auto mb-12 space-y-3">
          <h2 className="ui-title-section text-slate-900 uppercase">
            DEPORTES EN <span className="text-emerald-600 italic">SPORTMAP</span>
          </h2>
          
          <p className="ui-text-lead text-slate-600 font-medium">
            Explora las principales disciplinas deportivas en Lima. Pasa el cursor sobre cada deporte para seleccionarlo y filtrar las canchas disponibles.
          </p>

          {selectedSport !== 'Todos' && (
            <div className="pt-2 flex items-center justify-center">
              <div className="inline-flex items-center gap-2.5 px-4 py-2 rounded-full bg-slate-100/90 border border-slate-200/80 text-sm sm:text-base text-slate-700 font-medium shadow-2xs">
                <Filter className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                <span>
                  Filtrando por <strong className="font-extrabold text-slate-900">{selectedSport}</strong>
                </span>
                <span className="text-slate-300">|</span>
                <button
                  onClick={() => onSelectSport('Todos')}
                  className="inline-flex items-center gap-1 font-semibold text-emerald-700 hover:text-emerald-900 transition-colors cursor-pointer"
                >
                  <span>Ver todos</span>
                  <X className="w-3.5 h-3.5 text-emerald-700" />
                </button>
              </div>
            </div>
          )}
        </div>

        {/* 5 Sports Circle Cards Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-6 sm:gap-8 items-start justify-center max-w-6xl mx-auto">
          {FIVE_SPORTS.map((sport) => {
            const isSelected = selectedSport.toLowerCase().includes(sport.name.toLowerCase()) || selectedSport === sport.fullName;
            return (
              <button
                key={sport.id}
                onClick={() => onSelectSport(sport.fullName)}
                className="group flex flex-col items-center text-center cursor-pointer transition-all duration-300 focus:outline-none"
              >
                {/* Circular Image Container with Smooth Hover Brighten Effect */}
                <div
                  className={`relative w-36 h-36 sm:w-40 sm:h-40 md:w-44 md:h-44 rounded-full overflow-hidden shadow-xl border-4 transition-all duration-500 transform group-hover:scale-105 group-hover:shadow-2xl group-hover:shadow-emerald-500/20 ring-4 ${
                    isSelected
                      ? 'border-emerald-600 ring-emerald-400/50 scale-105 shadow-emerald-600/30'
                      : 'border-slate-200 group-hover:border-emerald-500 ring-transparent'
                  }`}
                >
                  {/* Photo with darker tone by default, brightens on hover */}
                  <img
                    src={sport.image}
                    alt={sport.name}
                    referrerPolicy="no-referrer"
                    className={`w-full h-full object-cover transition-all duration-500 ${
                      isSelected
                        ? 'brightness-105 scale-110'
                        : 'brightness-60 group-hover:brightness-110 group-hover:scale-110'
                    }`}
                  />

                  {/* Dark Overlay Vignette (Fades out on hover or selection) */}
                  <div
                    className={`absolute inset-0 bg-slate-950/45 transition-opacity duration-300 ${
                      isSelected ? 'opacity-0' : 'opacity-100 group-hover:opacity-0'
                    }`}
                  />

                  {/* Centered Italic Bold Title Overlay (Disappears on hover and when selected) */}
                  <div
                    className={`absolute inset-0 flex items-center justify-center p-3 text-center z-10 transition-all duration-300 ${
                      isSelected ? 'opacity-0 pointer-events-none' : 'opacity-100 group-hover:opacity-0 group-hover:scale-95'
                    }`}
                  >
                    <h3 className="font-black italic text-white text-xl sm:text-2xl tracking-tight drop-shadow-[0_2px_8px_rgba(0,0,0,0.9)]">
                      {sport.shortTitle}
                    </h3>
                  </div>
                </div>

                {/* Clear Legible Title & Description beneath circle */}
                <div className="mt-4 space-y-1">
                  <span className={`text-lg sm:text-xl font-extrabold block transition-colors ${
                    isSelected ? 'text-emerald-600' : 'text-slate-900 group-hover:text-emerald-600'
                  }`}>
                    {sport.fullName}
                  </span>
                  <p className="text-base sm:text-lg font-medium text-slate-500 group-hover:text-slate-700 transition-colors max-w-[160px] mx-auto leading-normal">
                    {sport.description}
                  </p>
                </div>
              </button>
            );
          })}
        </div>

      </div>
    </section>
  );
};
