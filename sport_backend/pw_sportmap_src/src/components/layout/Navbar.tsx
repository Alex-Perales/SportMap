import React, { useState, useEffect, useCallback } from 'react';
import { MapPin, Menu, X, Smartphone, ChevronRight, User } from 'lucide-react';

export type PageId = 'inicio' | 'nosotros' | 'beneficios' | 'contacto';

interface NavbarProps {
  currentPage: PageId;
  onPageChange: (page: PageId) => void;
  onOpenDownloadModal: () => void;
}

const NAV_PAGES: { id: PageId; label: string }[] = [
  { id: 'inicio', label: 'Inicio' },
  { id: 'nosotros', label: 'Nosotros' },
  { id: 'beneficios', label: 'Beneficios' },
  { id: 'contacto', label: 'Contacto' },
];

export const Navbar: React.FC<NavbarProps> = ({
  currentPage,
  onPageChange,
  onOpenDownloadModal,
}) => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleNavClick = useCallback(
    (pageId: PageId) => {
      onPageChange(pageId);
      setMobileMenuOpen(false);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    [onPageChange]
  );

  const toggleMobileMenu = useCallback(() => setMobileMenuOpen((open) => !open), []);

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-40 shadow-lg shadow-black/40 border-b-2 border-white transition-all duration-300 ${
        isScrolled
          ? 'bg-black/95 backdrop-blur-md py-3'
          : 'bg-black backdrop-blur-md py-4'
      }`}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between">
          {/* Logo */}
          <button
            onClick={() => handleNavClick('inicio')}
            className="flex items-center gap-2.5 group text-left"
          >
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-600 flex items-center justify-center text-white shadow-md shadow-emerald-500/20 group-hover:scale-105 transition-transform">
              <MapPin className="w-5 h-5" />
            </div>
            <div className="flex flex-col">
              <span className="text-xl font-extrabold tracking-tight text-white leading-none">
                Sport<span className="text-emerald-500">Map</span>
              </span>
              <span className="text-xs font-bold text-emerald-400 tracking-wider uppercase mt-0.5">
                Perú 🇵🇪
              </span>
            </div>
          </button>

          {/* Desktop Links (High Contrast Nav Bar) */}
          <nav className="hidden md:flex items-center gap-2 bg-neutral-900/90 p-1.5 rounded-2xl border border-neutral-800 shadow-inner">
            {NAV_PAGES.map((page) => (
              <button
                key={page.id}
                onClick={() => handleNavClick(page.id)}
                className={`px-4 py-2 rounded-xl ui-text-lead font-black tracking-wide transition-all ${
                  currentPage === page.id
                    ? 'bg-emerald-600 text-white shadow-md shadow-emerald-600/40 ring-1 ring-emerald-400/30'
                    : 'text-slate-100 hover:text-white hover:bg-neutral-800'
                }`}
              >
                {page.label}
              </button>
            ))}
          </nav>

          {/* Action Buttons */}
          <div className="hidden sm:flex items-center gap-3">
            {/* Login: lleva al panel de administrador (mismo dominio/backend) */}
            <a
              href="/admin/login"
              className="inline-flex items-center gap-2 px-3.5 py-2.5 bg-neutral-900 hover:bg-neutral-800 text-white font-bold ui-text-lead rounded-xl border border-neutral-800 hover:border-neutral-700 transition-all cursor-pointer shadow-sm"
              title="Iniciar Sesión"
            >
              <User className="w-4 h-4 text-emerald-400" />
              <span className="hidden lg:inline text-slate-100">Iniciar Sesión</span>
            </a>

            {/* Download App Button */}
            <button
              onClick={onOpenDownloadModal}
              className="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-extrabold ui-text-lead rounded-xl shadow-md shadow-emerald-600/30 transition-all hover:scale-[1.02] active:scale-[0.98]"
            >
              <Smartphone className="w-4 h-4 text-white" />
              <span>Descargar App</span>
            </button>
          </div>

          {/* Mobile Menu & Login Button */}
          <div className="flex items-center gap-2 md:hidden">
            <a
              href="/admin/login"
              className="p-2 text-slate-200 hover:text-white bg-neutral-900 rounded-lg border border-neutral-800"
              title="Iniciar Sesión"
            >
              <User className="w-5 h-5 text-emerald-400" />
            </a>
            <button
              onClick={onOpenDownloadModal}
              className="px-3 py-1.5 bg-emerald-600 text-white ui-text-lead font-extrabold rounded-lg shadow-sm"
            >
              App
            </button>
            <button
              onClick={toggleMobileMenu}
              className="p-2 text-slate-200 hover:text-white rounded-lg hover:bg-neutral-800 transition-colors"
              aria-label="Abrir menú"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-black border-b border-neutral-800 px-4 pt-3 pb-6 space-y-3 animate-in slide-in-from-top duration-200 shadow-2xl">
          <nav className="flex flex-col space-y-1">
            {NAV_PAGES.map((page) => (
              <button
                key={page.id}
                onClick={() => handleNavClick(page.id)}
                className={`px-3.5 py-2.5 ui-text-lead font-extrabold rounded-xl flex items-center justify-between text-left transition-colors ${
                  currentPage === page.id
                    ? 'bg-emerald-600/20 text-emerald-400 border border-emerald-500/30'
                    : 'text-slate-100 hover:bg-neutral-900'
                }`}
              >
                <span>{page.label}</span>
                <ChevronRight className="w-4 h-4 text-slate-400" />
              </button>
            ))}
          </nav>
          <div className="pt-2 space-y-2">
            <a
              href="/admin/login"
              className="w-full flex items-center justify-center gap-2 py-2.5 bg-neutral-900 text-slate-100 font-bold ui-text-lead rounded-xl border border-neutral-800"
            >
              <User className="w-4 h-4 text-emerald-400" />
              <span>Iniciar Sesión</span>
            </a>
            <button
              onClick={() => {
                setMobileMenuOpen(false);
                onOpenDownloadModal();
              }}
              className="w-full flex items-center justify-center gap-2 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white font-extrabold ui-text-lead rounded-xl shadow-md"
            >
              <Smartphone className="w-4 h-4" />
              <span>Descargar SportMap para Android</span>
            </button>
          </div>
        </div>
      )}
    </header>
  );
};
