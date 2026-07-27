import React, { useState, useMemo, useCallback } from 'react';
import { VENUES_DATA, LIMA_DISTRICTS } from '../../data/sportmapData';
import { MapPin, Star, Filter, Sparkles, CheckCircle, Smartphone, ExternalLink, Flame } from 'lucide-react';

interface VenuesCatalogProps {
  selectedSport: string;
  onSelectSport: (sport: string) => void;
  onOpenDownloadModal: () => void;
}

const SPORTS_FILTER_LIST = [
  { id: 'Todos', label: 'Todos' },
  { id: 'Fútbol', label: 'Fútbol' },
  { id: 'Vóley', label: 'Vóley' },
  { id: 'Básquetbol', label: 'Básquetbol' },
  { id: 'Tenis', label: 'Tenis' },
  { id: 'Natación', label: 'Natación' },
];

export const VenuesCatalog: React.FC<VenuesCatalogProps> = ({
  selectedSport,
  onSelectSport,
  onOpenDownloadModal,
}) => {
  const [selectedDistrict, setSelectedDistrict] = useState('Todos los distritos');

  const isSportActive = useCallback((sportId: string) => {
    if (sportId === 'Todos') {
      return selectedSport === 'Todos';
    }
    const normSport = sportId.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    const normSelected = selectedSport.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    return normSelected.includes(normSport) || normSport.includes(normSelected);
  }, [selectedSport]);

  const filteredVenues = useMemo(() => {
    return VENUES_DATA.filter((item) => {
      // District filter
      if (selectedDistrict !== 'Todos los distritos' && item.district !== selectedDistrict) {
        return false;
      }

      // Sport filter
      if (selectedSport !== 'Todos') {
        const normSelected = selectedSport.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        const normVenueSport = item.sport.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        const matchSport = normVenueSport.includes(normSelected) || normSelected.includes(normVenueSport);
        if (!matchSport) return false;
      }

      return true;
    });
  }, [selectedDistrict, selectedSport]);

  const displayedVenues = useMemo(() => filteredVenues.slice(0, 6), [filteredVenues]);

  return (
    <section id="canchas" className="bg-slate-50/70 py-12 sm:py-16 border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto space-y-3 mb-10">
          <h2 className="ui-title-section text-slate-900">
            Descubre canchas y recintos <span className="text-emerald-600 italic">cerca de ti</span>
          </h2>
          <p className="ui-text-lead text-slate-600 font-medium">
            Mostramos las sedes principales destacadas en Lima. Explora instalaciones verificadas con horarios flexibles, reseñas reales y confirmación inmediata.
          </p>
        </div>

        {/* Filter Controls Bar */}
        <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 mb-8 space-y-4">
          <div className="flex flex-wrap items-center justify-between gap-4">
            
            {/* 5 Deportes en Fila */}
            <div className="flex flex-wrap items-center bg-white p-1 rounded-xl border border-slate-200 shadow-xs gap-1">
              {SPORTS_FILTER_LIST.map((s) => {
                const active = isSportActive(s.id);
                return (
                  <button
                    key={s.id}
                    onClick={() => onSelectSport(s.id)}
                    className={`px-3.5 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer ${
                      active
                        ? 'bg-emerald-600 text-white shadow-xs'
                        : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                    }`}
                  >
                    {s.label}
                  </button>
                );
              })}
            </div>

            {/* District Selector */}
            <div className="flex items-center gap-2">
              <Filter className="w-4 h-4 text-slate-500" />
              <label className="text-sm font-bold text-slate-700">Distrito:</label>
              <select
                value={selectedDistrict}
                onChange={(e) => setSelectedDistrict(e.target.value)}
                className="bg-white border border-slate-300 rounded-xl px-3 py-2 text-sm font-semibold text-slate-800 focus:outline-none focus:border-emerald-600 cursor-pointer"
              >
                {LIMA_DISTRICTS.map((district) => (
                  <option key={district} value={district}>
                    {district}
                  </option>
                ))}
              </select>
            </div>

          </div>
        </div>

        {/* Venues Card Grid (Max 6) */}
        {displayedVenues.length === 0 ? (
          <div className="text-center py-16 bg-slate-50 rounded-3xl border border-dashed border-slate-300">
            <MapPin className="w-12 h-12 text-slate-400 mx-auto mb-3" />
            <h3 className="text-lg font-bold text-slate-800">No se encontraron resultados</h3>
            <p className="text-base text-slate-500 mt-1 max-w-md mx-auto">
              Intenta cambiar los filtros de distrito o deporte para encontrar más canchas y rutas en Lima.
            </p>
            <button
              onClick={() => {
                setSelectedDistrict('Todos los distritos');
                onSelectSport('Todos');
              }}
              className="mt-4 px-4 py-2 bg-emerald-600 text-white text-sm font-bold rounded-xl"
            >
              Restablecer filtros
            </button>
          </div>
        ) : (
          <div className="space-y-10">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {displayedVenues.map((venue) => (
                <div
                  key={venue.id}
                  className="group bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 flex flex-col justify-between"
                >
                  <div>
                    {/* Photo with Overlay badges */}
                    <div className="relative h-52 overflow-hidden bg-slate-100">
                      <img
                        src={venue.image}
                        alt={venue.name}
                        referrerPolicy="no-referrer"
                        onError={(e) => {
                          e.currentTarget.src = 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?auto=format&fit=crop&q=80&w=800';
                        }}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                      />
                      <div className="absolute top-3 left-3 flex gap-2">
                        <span className="px-2.5 py-1 rounded-lg text-sm font-extrabold bg-slate-900/80 text-white backdrop-blur-md">
                          {venue.sport}
                        </span>
                        {venue.type === 'ruta' && (
                          <span className="px-2.5 py-1 rounded-lg text-sm font-extrabold bg-emerald-600 text-white">
                            Ruta Libre
                          </span>
                        )}
                      </div>

                      {venue.featured && (
                        <div className="absolute top-3 right-3 px-2.5 py-1 bg-amber-400 text-slate-900 font-extrabold text-[10px] uppercase tracking-wider rounded-lg shadow-md flex items-center gap-1">
                          <Flame className="w-3.5 h-3.5 fill-slate-900" /> Destacado
                        </div>
                      )}
                    </div>

                    {/* Card Content */}
                    <div className="p-5 space-y-3">
                      <div className="flex items-center justify-between gap-2">
                        <span className="inline-flex items-center gap-1 text-sm font-bold text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-lg">
                          <MapPin className="w-3.5 h-3.5" /> {venue.district}
                        </span>
                        <div className="flex items-center gap-1 text-sm font-bold text-slate-800">
                          <Star className="w-4 h-4 text-amber-400 fill-amber-400" />
                          <span>{venue.rating}</span>
                          <span className="text-slate-400 text-[10px]">({venue.reviewsCount})</span>
                        </div>
                      </div>

                      <h3 className="text-lg font-bold text-slate-900 leading-snug group-hover:text-emerald-600 transition-colors">
                        {venue.name}
                      </h3>

                      <p className="text-sm text-slate-500 line-clamp-1">
                        {venue.address}
                      </p>

                      {/* Tag list */}
                      <div className="flex flex-wrap gap-1.5 pt-1">
                        {venue.tags.map((tag, idx) => (
                          <span
                            key={idx}
                            className="text-[10px] font-semibold text-slate-600 bg-slate-100 px-2 py-0.5 rounded-md"
                          >
                            {tag}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>

                  {/* Card Action Footer */}
                  <div className="p-5 pt-0 border-t border-slate-100 mt-2">
                    <div className="flex items-center justify-between pt-3">
                      <span className="text-sm font-semibold text-emerald-600 flex items-center gap-1">
                        <CheckCircle className="w-3.5 h-3.5" /> Disponibilidad en App
                      </span>
                      <button
                        onClick={onOpenDownloadModal}
                        className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-bold rounded-xl transition-all shadow-md shadow-emerald-500/20 flex items-center gap-1.5 group-hover:scale-105"
                      >
                        <span>Ver en la app</span>
                        <ExternalLink className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>

                </div>
              ))}
            </div>

            {/* Banner Callout for More Venues */}
            <div className="bg-gradient-to-r from-emerald-600 via-emerald-600 to-teal-700 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-center justify-between gap-6 border border-emerald-500/30">
              <div className="space-y-2 text-center md:text-left">
                <h3 className="text-xl sm:text-2xl font-black text-white">
                  ¿Buscas más canchas en tu distrito?
                </h3>
                <p className="text-emerald-50 text-sm sm:text-base max-w-xl font-medium">
                  Estas sedes son algunas de las principales destacadas, pero en la app de SportMap tenemos muchas más canchas de fútbol, tenis, básquet, vóley y piscinas con reserva inmediata.
                </p>
              </div>

              <button
                onClick={onOpenDownloadModal}
                className="shrink-0 px-6 py-3.5 bg-slate-900 hover:bg-slate-800 text-white font-black text-base rounded-2xl transition-all shadow-xl hover:scale-105 active:scale-95 cursor-pointer flex items-center gap-2"
              >
                <Smartphone className="w-4 h-4 text-emerald-400" />
                <span>Ver todas las canchas en la App</span>
              </button>
            </div>
          </div>
        )}

      </div>
    </section>
  );
};
