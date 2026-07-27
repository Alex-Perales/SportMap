import React, { useState, useMemo } from 'react';
import { STORE_ITEMS } from '../../data/sportmapData';
import { ShoppingBag, Star, CheckCircle2, ArrowRight, ExternalLink } from 'lucide-react';

interface StoreShowcaseProps {
  onOpenDownloadModal: () => void;
}

const CATEGORIES = ['Todas', 'Calzado', 'Balones', 'Hidratación', 'Accesorios', 'Ropa'];

export const StoreShowcase: React.FC<StoreShowcaseProps> = ({ onOpenDownloadModal }) => {
  const [selectedCategory, setSelectedCategory] = useState<string>('Todas');

  const filteredItems = useMemo(() => {
    if (selectedCategory === 'Todas') return STORE_ITEMS;
    return STORE_ITEMS.filter((item) => item.category === selectedCategory);
  }, [selectedCategory]);

  return (
    <section id="tienda" className="bg-white py-12 sm:py-16 border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto space-y-3 mb-10">
          <h2 className="ui-title-section text-slate-900">
            Equípate con lo mejor <span className="text-emerald-600 italic">sin salir de la app</span>
          </h2>
          <p className="ui-text-lead text-slate-600">
            Muestra del catálogo de artículos deportivos oficial de SportMap. Compra directo desde la app con envío rápido a tu casa o cancha en Lima.
          </p>
        </div>

        {/* Category Filter Pills */}
        <div className="flex flex-wrap items-center justify-center gap-2 mb-10">
          {CATEGORIES.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-4 py-2 rounded-xl text-sm font-bold transition-all ${
                selectedCategory === cat
                  ? 'bg-emerald-600 text-white shadow-md shadow-emerald-500/20'
                  : 'bg-white text-slate-700 border border-slate-200 hover:border-emerald-300'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* Store Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredItems.map((item) => (
            <div
              key={item.id}
              className="group bg-white rounded-3xl border border-slate-200/90 overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 flex flex-col justify-between"
            >
              <div>
                <div className="relative h-56 bg-slate-100 overflow-hidden">
                  <img
                    src={item.image}
                    alt={item.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                  <div className="absolute top-3 left-3 bg-slate-900/80 text-white text-[10px] font-extrabold px-2.5 py-1 rounded-lg backdrop-blur-sm">
                    {item.category}
                  </div>
                  {item.inStock && (
                    <div className="absolute top-3 right-3 bg-emerald-500 text-white text-[10px] font-bold px-2.5 py-1 rounded-lg shadow-sm">
                      Stock en Lima
                    </div>
                  )}
                </div>

                <div className="p-5 space-y-2">
                  <div className="flex items-center justify-between text-sm font-semibold text-slate-500">
                    <span className="text-emerald-600 font-bold">{item.sport}</span>
                    <div className="flex items-center gap-1 text-amber-500 font-bold">
                      <Star className="w-3.5 h-3.5 fill-amber-400" /> {item.rating}
                    </div>
                  </div>

                  <h3 className="font-bold text-slate-900 text-base leading-snug group-hover:text-emerald-600 transition-colors">
                    {item.name}
                  </h3>

                  <p className="text-sm text-slate-500">
                    Disponible para pedido directo en la app con envío express en Lima.
                  </p>
                </div>
              </div>

              <div className="p-5 pt-0 border-t border-slate-100 mt-2">
                <div className="flex items-center justify-between pt-3">
                  <span className="text-sm font-semibold text-slate-600 flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> Catálogo en App
                  </span>
                  <button
                    onClick={onOpenDownloadModal}
                    className="px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white text-sm font-bold rounded-xl transition-all flex items-center gap-1.5 shadow-sm group-hover:bg-emerald-600"
                  >
                    <span>Ver en la app</span>
                    <ExternalLink className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

      </div>
    </section>
  );
};
