import React from 'react';
import { MapPin, Heart, Instagram, Facebook } from 'lucide-react';
import { PageId } from './Navbar';

interface FooterProps {
  onPageChange: (page: PageId) => void;
  onOpenDownloadModal: () => void;
}

export const Footer: React.FC<FooterProps> = ({ onPageChange, onOpenDownloadModal }) => {
  const handleNav = (page: PageId) => {
    onPageChange(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <footer className="bg-black text-white pt-16 pb-12 border-t border-neutral-900">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10 pb-12 border-b border-neutral-800">
          
          {/* Col 1 & 2: Logo & Mission */}
          <div className="lg:col-span-2 space-y-4">
            <button
              onClick={() => handleNav('inicio')}
              className="flex items-center gap-2.5 text-left"
            >
              <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-600 flex items-center justify-center text-white shadow-md">
                <MapPin className="w-5 h-5" />
              </div>
              <div className="flex flex-col">
                <span className="text-xl font-extrabold tracking-tight text-white leading-none">
                  Sport<span className="text-emerald-500">Map</span>
                </span>
                <span className="text-[10px] font-bold text-emerald-400 tracking-wider uppercase mt-0.5">
                  Perú 🇵🇪
                </span>
              </div>
            </button>

            <p className="text-slate-400 ui-text-lead max-w-sm">
              La plataforma peruana que reúne canchas deportivas, rutas de running y ciclismo, y tienda de artículos en un solo mapa interactivo.
            </p>

            {/* Redes Sociales */}
            <div className="pt-2 space-y-2">
              <span className="text-sm font-bold text-slate-300 block uppercase tracking-wider">Síguenos</span>
              <div className="flex items-center gap-3">
                <a
                  href="https://www.instagram.com/absalon.ap/"
                  target="_blank"
                  rel="noopener noreferrer"
                  aria-label="Instagram @absalon.ap"
                  title="Instagram @absalon.ap"
                  className="w-10 h-10 bg-neutral-900 hover:bg-gradient-to-tr hover:from-amber-500 hover:via-rose-500 hover:to-purple-600 text-slate-200 hover:text-white rounded-xl transition-all duration-300 flex items-center justify-center border border-neutral-800 shadow-sm hover:scale-110"
                >
                  <Instagram className="w-5 h-5" />
                </a>
                <a
                  href="https://www.facebook.com/"
                  target="_blank"
                  rel="noopener noreferrer"
                  aria-label="Facebook"
                  title="Facebook"
                  className="w-10 h-10 bg-neutral-900 hover:bg-blue-600 text-slate-200 hover:text-white rounded-xl transition-all duration-300 flex items-center justify-center border border-neutral-800 shadow-sm hover:scale-110"
                >
                  <Facebook className="w-5 h-5" />
                </a>
              </div>
            </div>
          </div>

          {/* Col 3: Secciones Principales */}
          <div className="space-y-3">
            <h4 className="text-base font-extrabold uppercase tracking-wider text-slate-200">Navegación</h4>
            <ul className="space-y-2 ui-text-lead text-slate-400 font-medium">
              <li>
                <button onClick={() => handleNav('inicio')} className="hover:text-emerald-400 transition-colors">
                  Inicio
                </button>
              </li>
              <li>
                <button onClick={() => handleNav('nosotros')} className="hover:text-emerald-400 transition-colors">
                  Nosotros
                </button>
              </li>
              <li>
                <button onClick={() => handleNav('beneficios')} className="hover:text-emerald-400 transition-colors">
                  Beneficios
                </button>
              </li>
              <li>
                <button onClick={() => handleNav('contacto')} className="hover:text-emerald-400 transition-colors">
                  Contacto
                </button>
              </li>
            </ul>
          </div>

          {/* Col 4: Cobertura */}
          <div className="space-y-3">
            <h4 className="text-base font-extrabold uppercase tracking-wider text-slate-200">Distritos Lima</h4>
            <ul className="space-y-2 ui-text-lead text-slate-400 font-medium">
              <li>San Borja</li>
              <li>San Isidro</li>
              <li>Miraflores</li>
              <li>Surco</li>
              <li>La Victoria</li>
              <li>Cercado de Lima</li>
              <li>Costa Verde</li>
            </ul>
          </div>

          {/* Col 5: Legal & Aliados */}
          <div className="space-y-3">
            <h4 className="text-base font-extrabold uppercase tracking-wider text-slate-200">Información</h4>
            <ul className="space-y-2 ui-text-lead text-slate-400 font-medium">
              <li>
                <button onClick={() => handleNav('nosotros')} className="hover:text-emerald-400 transition-colors">
                  Sobre Nosotros
                </button>
              </li>
              <li>
                <button onClick={() => handleNav('contacto')} className="hover:text-emerald-400 transition-colors">
                  Contacto & Soporte
                </button>
              </li>
              <li>
                <button onClick={() => handleNav('contacto')} className="hover:text-emerald-400 transition-colors">
                  Inscribir mi Cancha
                </button>
              </li>
              <li>
                <button onClick={() => handleNav('contacto')} className="hover:text-emerald-400 transition-colors">
                  Preguntas Frecuentes
                </button>
              </li>
            </ul>
          </div>

        </div>

        {/* Bottom copyright row */}
        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between ui-text-lead text-slate-500 gap-4">
          <p>© SportMap Perú. Todos los derechos reservados.</p>
          <p className="flex items-center gap-1">
            Hecho con <Heart className="w-3.5 h-3.5 text-red-500 fill-red-500" /> en Lima, Perú
          </p>
        </div>

      </div>
    </footer>
  );
};
