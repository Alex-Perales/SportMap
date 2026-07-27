import React, { useState, useCallback } from 'react';
import { Navbar, PageId } from './components/layout/Navbar';
import { Footer } from './components/layout/Footer';
import { Hero } from './components/sections/Hero';
import { SportsCategories } from './components/sections/SportsCategories';
import { VenuesCatalog } from './components/sections/VenuesCatalog';
import { StoreShowcase } from './components/sections/StoreShowcase';
import { ContactAndFAQ } from './components/sections/ContactAndFAQ';
import { DownloadModal } from './components/modals/DownloadModal';
import {
  PageBanner,
  Benefits,
  HowItWorks,
  AboutUs,
  QuickDownload,
} from './components/sections/Sections';

import bannerNosotros from './assets/images/banner-nosotros.jpg';
import bannerBeneficios from './assets/images/banner-beneficios.jpg';
import bannerContacto from './assets/images/banner-contacto.jpg';

export default function App() {
  const [currentPage, setCurrentPage] = useState<PageId>('inicio');
  const [selectedSport, setSelectedSport] = useState('Todos');
  const [isDownloadModalOpen, setIsDownloadModalOpen] = useState(false);

  const handlePageChange = useCallback((page: PageId) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, []);

  const handleOpenDownloadModal = useCallback(() => {
    setIsDownloadModalOpen(true);
  }, []);

  const handleCloseDownloadModal = useCallback(() => {
    setIsDownloadModalOpen(false);
  }, []);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 font-sans selection:bg-emerald-600 selection:text-white antialiased flex flex-col justify-between">
      <div>
        {/* Navigation bar with page state */}
        <Navbar
          currentPage={currentPage}
          onPageChange={handlePageChange}
          onOpenDownloadModal={handleOpenDownloadModal}
        />

        {/* Dynamic Multi-Page Content */}
        <main className="transition-all duration-300">
          {currentPage === 'inicio' && (
            <div className="space-y-0">
              {/* Hero Banner */}
              <Hero
                onOpenDownloadModal={handleOpenDownloadModal}
                onExploreCanchas={() => handlePageChange('beneficios')}
              />

              {/* How It Works Steps */}
              <HowItWorks onOpenDownloadModal={handleOpenDownloadModal} />

              {/* High-Impact Download Call To Action */}
              <QuickDownload onOpenDownloadModal={handleOpenDownloadModal} />
            </div>
          )}

          {currentPage === 'nosotros' && (
            <div>
              {/* Top Page Header Banner */}
              <PageBanner
                title="Quiénes Somos, Historia, Misión y Visión"
                description="Conoce la identidad, el propósito y la visión que impulsan a SportMap a transformar el deporte en Lima."
                image={bannerNosotros}
              />

              {/* About Us Story */}
              <AboutUs />

              <QuickDownload onOpenDownloadModal={handleOpenDownloadModal} />
            </div>
          )}

          {currentPage === 'beneficios' && (
            <div>
              {/* Top Page Header Banner */}
              <PageBanner
                title="Beneficios Exclusivos de SportMap"
                description="Descubre las ventajas diseñadas para revolucionar tu experiencia deportiva: reservas instantáneas, pagos con Yape/Plin y tienda deportiva integrada."
                image={bannerBeneficios}
              />

              {/* 1. Deportes: 5 Disciplinas en Lima */}
              <SportsCategories
                selectedSport={selectedSport}
                onSelectSport={setSelectedSport}
              />

              {/* 2. Canchas & Rutas Catalog */}
              <VenuesCatalog
                selectedSport={selectedSport}
                onSelectSport={setSelectedSport}
                onOpenDownloadModal={handleOpenDownloadModal}
              />

              {/* 3. Store Showcase ("Equípate con lo mejor sin salir de la app") */}
              <StoreShowcase onOpenDownloadModal={handleOpenDownloadModal} />

              {/* 4. Benefits Pillars ("Diseñado para tu experiencia deportiva en Perú") */}
              <Benefits onOpenDownloadModal={handleOpenDownloadModal} />

              <QuickDownload onOpenDownloadModal={handleOpenDownloadModal} />
            </div>
          )}

          {currentPage === 'contacto' && (
            <div>
              {/* Top Page Header Banner */}
              <PageBanner
                title="Contacto y Preguntas Frecuentes"
                description="¿Tienes alguna duda, necesitas ayuda con tu reserva o deseas afiliar tu complejo deportivo a nuestra plataforma? Estamos aquí para atenderte."
                image={bannerContacto}
              />

              {/* Contact, Coverage & FAQ */}
              <ContactAndFAQ />

              <QuickDownload onOpenDownloadModal={handleOpenDownloadModal} />
            </div>
          )}
        </main>
      </div>

      {/* Footer */}
      <Footer
        onPageChange={handlePageChange}
        onOpenDownloadModal={handleOpenDownloadModal}
      />

      {/* Download App Popup Modal */}
      <DownloadModal
        isOpen={isDownloadModalOpen}
        onClose={handleCloseDownloadModal}
      />
    </div>
  );
}
