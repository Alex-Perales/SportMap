import React from 'react';
import {
  Layers,
  Eye,
  CreditCard,
  Award,
  Search,
  CalendarCheck,
  Trophy,
  Navigation,
  Smartphone,
  ArrowRight,
  MapPin,
  Store,
  Target,
  Play,
  Download,
} from 'lucide-react';
import stepsGraphic from '../../assets/images/how-it-works-steps.jpg';
import guideGraphic from '../../assets/images/app-preview-pitch-map.jpg';

/* -------------------------------------------------------------------------- */
/* PageBanner Component                                                       */
/* -------------------------------------------------------------------------- */
interface PageBannerProps {
  title: string;
  description: string;
  image: string;
}

export const PageBanner: React.FC<PageBannerProps> = ({
  title,
  description,
  image,
}) => {
  return (
    <div className="relative w-full h-48 sm:h-56 mt-16 sm:mt-20 overflow-hidden">
      <img
        src={image}
        alt={title}
        referrerPolicy="no-referrer"
        className="absolute inset-0 w-full h-full object-cover"
      />
      <div className="absolute inset-0 bg-gradient-to-r from-slate-950/85 via-slate-900/55 to-slate-950/25" />

      <div className="relative z-10 h-full flex items-center max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="max-w-2xl text-white">
          <h1 className="text-2xl sm:text-3xl md:text-4xl font-black tracking-tight text-white mb-3 drop-shadow-sm leading-tight">
            {title}
          </h1>
          <p className="ui-text-lead text-slate-200 font-medium max-w-xl whitespace-pre-line line-clamp-2">
            {description}
          </p>
        </div>
      </div>
    </div>
  );
};

/* -------------------------------------------------------------------------- */
/* Benefits Component                                                         */
/* -------------------------------------------------------------------------- */
interface BenefitsProps {
  onOpenDownloadModal: () => void;
}

export const Benefits: React.FC<BenefitsProps> = ({ onOpenDownloadModal }) => {
  const benefitsList = [
    {
      icon: Layers,
      badge: 'All-in-one',
      title: 'Todo en un solo lugar',
      description: 'Canchas para reservar, recintos deportivos y tienda de artículos oficiales en una sola aplicación sin saltar entre plataformas.',
    },
    {
      icon: Eye,
      badge: 'Transparente',
      title: 'Precios y servicios claros',
      description: 'Consulta fotos reales de la cancha, piso sintético o arcilla, si cuenta con iluminación LED, vestuarios, estacionamiento y disponibilidad en tiempo real.',
    },
    {
      icon: CreditCard,
      badge: 'Pagos Perú',
      title: 'Pago fácil con Yape o Plin',
      description: 'Reserva al instante sin llamadas ni transferencias complicadas. Paga directamente con Yape o Plin de forma rápida y segura.',
    },
    {
      icon: Award,
      badge: 'Recompensas',
      title: 'Medallas y Plan Premium',
      description: 'Gana insignias y puntos por cada partido u hora jugada. Accede a horas prioritarias en canchas cotizadas y ofertas exclusivas en la tienda.',
    },
  ];

  return (
    <section id="beneficios" className="bg-slate-50/70 py-12 sm:py-16 border-b border-slate-200 relative overflow-hidden">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="text-center max-w-3xl mx-auto space-y-3 mb-12">
          <h2 className="ui-title-section text-slate-900">
            Diseñado para tu experiencia deportiva en <span className="text-emerald-600 italic">Perú</span>
          </h2>
          <p className="ui-text-lead text-slate-600 font-medium">
            Olvídate de las llamadas eternas para consultar si hay cancha libre. SportMap simplifica todo el proceso antes, durante y después del juego.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {benefitsList.map((item, idx) => {
            const Icon = item.icon;
            return (
              <div
                key={idx}
                onClick={onOpenDownloadModal}
                className="group relative p-7 bg-white rounded-3xl border border-slate-200/80 shadow-md hover:shadow-xl hover:border-emerald-500/50 hover:-translate-y-1 transition-all duration-300 flex flex-col justify-between cursor-pointer"
              >
                <div>
                  <div className="flex items-center justify-between mb-6">
                    <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center border border-emerald-100 group-hover:bg-emerald-600 group-hover:text-white transition-all duration-300 shadow-xs">
                      <Icon className="w-6 h-6" />
                    </div>
                    <span className="ui-badge bg-slate-100 text-slate-700 border border-slate-200/60">
                      {item.badge}
                    </span>
                  </div>

                  <h3 className="ui-title-card text-slate-900 mb-2.5 group-hover:text-emerald-600 transition-colors">
                    {item.title}
                  </h3>

                  <p className="ui-text-lead text-slate-600 font-medium">
                    {item.description}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

/* -------------------------------------------------------------------------- */
/* HowItWorks Component                                                       */
/* -------------------------------------------------------------------------- */
interface HowItWorksProps {
  onOpenDownloadModal: () => void;
}

export const HowItWorks: React.FC<HowItWorksProps> = ({ onOpenDownloadModal }) => {
  return (
    <section id="como-funciona" className="py-16 bg-white relative overflow-hidden">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-16 relative z-10">
        <div className="bg-slate-50 rounded-3xl p-6 sm:p-10 border border-slate-200/80 shadow-xs">
          <div className="text-center max-w-3xl mx-auto space-y-2 mb-8">
            <h2 className="ui-title-section text-slate-900 uppercase">
              Hazlo <span className="text-emerald-600 italic">fácil</span>
            </h2>
            <p className="ui-text-lead text-slate-600 font-medium max-w-2xl mx-auto">
              Compara precios, turnos y servicios de las canchas. ¡Tienes toda la información para hacer tu reserva de forma instantánea!
            </p>
          </div>

          <div className="relative rounded-2xl overflow-hidden shadow-md border border-slate-200/80 mb-10 max-w-5xl mx-auto bg-white">
            <img
              src={stepsGraphic}
              alt="Hazlo fácil - Busca, Reserva, Juega"
              referrerPolicy="no-referrer"
              className="w-full h-auto max-h-[420px] object-cover object-center"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-5xl mx-auto">
            <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs hover:border-emerald-500/50 transition-all">
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 font-black flex items-center justify-center mb-4 text-base">
                1
              </div>
              <h3 className="ui-title-card text-slate-900 mb-1.5 flex items-center gap-2">
                <Search className="w-4 h-4 text-emerald-600" />
                <span>BUSCA</span>
              </h3>
              <p className="ui-text-lead text-slate-600 font-medium">
                Filtra por disciplina (Fútbol, Tenis, Vóley, Básquet) y tu distrito preferido en Lima.
              </p>
            </div>

            <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs hover:border-emerald-500/50 transition-all">
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 font-black flex items-center justify-center mb-4 text-base">
                2
              </div>
              <h3 className="ui-title-card text-slate-900 mb-1.5 flex items-center gap-2">
                <CalendarCheck className="w-4 h-4 text-emerald-600" />
                <span>RESERVA</span>
              </h3>
              <p className="ui-text-lead text-slate-600 font-medium">
                Elige el horario disponible en tiempo real y confirma pagando con Yape o Plin.
              </p>
            </div>

            <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-xs hover:border-emerald-500/50 transition-all">
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 font-black flex items-center justify-center mb-4 text-base">
                3
              </div>
              <h3 className="ui-title-card text-slate-900 mb-1.5 flex items-center gap-2">
                <Trophy className="w-4 h-4 text-emerald-600" />
                <span>JUEGA</span>
              </h3>
              <p className="ui-text-lead text-slate-600 font-medium">
                Muestra tu pase digital QR al ingresar a la sede y disfruta tu partido sin demoras.
              </p>
            </div>
          </div>
        </div>

        <div className="bg-gradient-to-br from-emerald-50/80 via-teal-50/40 to-slate-50 rounded-3xl p-6 sm:p-10 text-slate-900 shadow-sm border border-emerald-100/80">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-8 items-center max-w-6xl mx-auto">
            <div className="md:col-span-6 space-y-5 text-center md:text-left">
              <h2 className="ui-title-section text-slate-900">
                ¿Quieres disfrutar de un gran partido?
                <span className="block text-emerald-600 mt-1 uppercase italic">
                  Únete a SportMap
                </span>
              </h2>

              <div className="space-y-2 text-slate-600">
                <p className="ui-title-card text-emerald-700 flex items-center justify-center md:justify-start gap-2">
                  <Navigation className="w-5 h-5 text-emerald-600" />
                  <span>Te guiamos a tu cancha</span>
                </p>
                <p className="ui-text-lead text-slate-600 font-medium max-w-md">
                  ¿No sabes cómo llegar? Nosotros te guiamos con GPS integrado directo al lugar exacto de tu reserva confirmada.
                </p>
              </div>

              <div className="pt-3">
                <button
                  onClick={onOpenDownloadModal}
                  className="px-6 py-3.5 bg-emerald-600 hover:bg-emerald-500 text-white font-black text-base rounded-xl transition-all shadow-md hover:scale-105 active:scale-95 cursor-pointer inline-flex items-center gap-2"
                >
                  <Smartphone className="w-4 h-4" />
                  <span>Descargar App de SportMap</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>

            <div className="md:col-span-6">
              <div className="rounded-2xl overflow-hidden border border-slate-200 shadow-md bg-white">
                <img
                  src={guideGraphic}
                  alt="Te guiamos a tu cancha en SportMap"
                  referrerPolicy="no-referrer"
                  className="w-full h-auto object-cover object-center"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

/* -------------------------------------------------------------------------- */
/* AboutUs Component                                                          */
/* -------------------------------------------------------------------------- */
export const AboutUs: React.FC = () => {
  return (
    <div>
      <section className="bg-white py-12 sm:py-16 border-b border-slate-200">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
          <div className="space-y-4">
            <h2 className="ui-title-section text-slate-900">
              ¿Quiénes <span className="text-emerald-600 italic">Somos?</span>
            </h2>
            <p className="ui-text-lead text-slate-600 font-normal">
              Somos la plataforma deportiva integral pionera en Lima, creada por y para deportistas. Nuestra tecnología geolocaliza canchas, conecta a apasionados de cada disciplina y facilita la práctica del deporte en toda la ciudad.
            </p>
            <p className="ui-text-lead text-slate-600 font-normal">
              Buscamos transformar la cultura deportiva peruana promoviendo la actividad física, la salud y la convivencia en comunidad mediante soluciones digitales intuitivas y accesibles.
            </p>
          </div>

          <div className="flex flex-col gap-4 pt-2">
            <div className="bg-slate-50 p-6 rounded-2xl border border-slate-200 flex items-start gap-4">
              <div className="w-11 h-11 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center shrink-0">
                <MapPin className="w-5 h-5" />
              </div>
              <div>
                <h3 className="ui-title-card text-slate-900 mb-1">Canchas Verificadas</h3>
                <p className="ui-text-lead text-slate-600">
                  Fútbol, tenis, básquet, vóley y natación con disponibilidad y precios en tiempo real.
                </p>
              </div>
            </div>

            <div className="bg-slate-50 p-6 rounded-2xl border border-slate-200 flex items-start gap-4">
              <div className="w-11 h-11 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center shrink-0">
                <Smartphone className="w-5 h-5" />
              </div>
              <div>
                <h3 className="ui-title-card text-slate-900 mb-1">Reservas Instantáneas</h3>
                <p className="ui-text-lead text-slate-600">
                  Confirma tu turno en segundos sin llamadas molestas ni esperas por WhatsApp.
                </p>
              </div>
            </div>

            <div className="bg-slate-50 p-6 rounded-2xl border border-slate-200 flex items-start gap-4">
              <div className="w-11 h-11 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center shrink-0">
                <Store className="w-5 h-5" />
              </div>
              <div>
                <h3 className="ui-title-card text-slate-900 mb-1">Tienda Integrada</h3>
                <p className="ui-text-lead text-slate-600">
                  Equípate con indumentaria, balones y accesorios oficiales con envío a tu domicilio.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="bg-slate-50/70 py-12 sm:py-16 border-b border-slate-200">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
          <h2 className="ui-title-section text-slate-900">
            Nuestra <span className="text-emerald-600 italic">Historia</span>
          </h2>

          <p className="ui-text-lead text-slate-600 font-normal">
            <strong>SportMap nació en las canchas de Lima.</strong> Todo comenzó al experimentar lo complicado que era coordinar un partido entre amigos: coordinar horarios, llamar por teléfono a varios locales y dudar si la cancha estaría disponible.
          </p>

          <p className="ui-text-lead text-slate-600 font-normal">
            Decidimos crear una solución moderna: un mapa centralizado e interactivo que conecta directamente a los deportistas con los mejores complejos deportivos de Miraflores, San Borja, Surco, La Victoria y todo Lima Metropolitana.
          </p>

          <blockquote className="p-5 rounded-2xl bg-white border-l-4 border-emerald-600 text-slate-800 ui-text-lead font-medium shadow-2xs">
            "Nuestra meta es que cualquier persona en Lima pueda encontrar dónde jugar y asegurar su reserva en menos de tres clics."
          </blockquote>
        </div>
      </section>

      <section className="bg-white py-12 sm:py-16">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
          <div className="space-y-2">
            <h2 className="ui-title-section text-slate-900">
              Nuestra Misión y <span className="text-emerald-600 italic">Visión</span>
            </h2>
            <p className="ui-text-lead text-slate-600 font-normal">
              Principios que guían nuestro trabajo diario para impulsar el deporte en el Perú.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-slate-50 p-7 rounded-3xl border border-slate-200 space-y-4">
              <div className="w-12 h-12 rounded-2xl bg-emerald-600 text-white flex items-center justify-center font-bold shadow-xs">
                <Target className="w-6 h-6" />
              </div>
              <h3 className="ui-title-sub text-slate-900">Misión</h3>
              <p className="ui-text-lead text-slate-600 font-normal">
                Facilitar el acceso rápido y transparente a la actividad física en Lima, permitiendo que cualquier persona pueda encontrar su cancha ideal, reservar en segundos con confirmación inmediata y equiparse con productos deportivos sin complicaciones.
              </p>
            </div>

            <div className="bg-slate-50 p-7 rounded-3xl border border-slate-200 space-y-4">
              <div className="w-12 h-12 rounded-2xl bg-emerald-600 text-white flex items-center justify-center font-bold shadow-xs">
                <Eye className="w-6 h-6" />
              </div>
              <h3 className="ui-title-sub text-slate-900">Visión</h3>
              <p className="ui-text-lead text-slate-600 font-normal">
                Convertirnos en el ecosistema digital deportivo de referencia en todo el Perú y Latinoamérica, promoviendo el deporte, el trabajo en equipo y la vida saludable mediante tecnología accesible e integración total con la comunidad local.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

/* -------------------------------------------------------------------------- */
/* QuickDownload Component                                                    */
/* -------------------------------------------------------------------------- */
interface QuickDownloadProps {
  onOpenDownloadModal: () => void;
}

export const QuickDownload: React.FC<QuickDownloadProps> = ({ onOpenDownloadModal }) => {
  const handleDownloadApk = () => {
    const element = document.createElement('a');
    const file = new Blob(['SportMap App Android APK Versión de Prueba'], { type: 'text/plain' });
    element.href = URL.createObjectURL(file);
    element.download = 'SportMap_v1.0.apk';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  };

  return (
    <section id="download" className="w-full bg-gradient-to-br from-emerald-600 via-teal-700 to-slate-900 text-white py-14 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
      <div className="absolute top-10 left-10 w-80 h-80 bg-emerald-400/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-10 right-10 w-96 h-96 bg-teal-400/20 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-6xl mx-auto relative z-10">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
          <div className="lg:col-span-7 space-y-5 text-center lg:text-left">
            <h2 className="ui-title-section text-white">
              Descarga la app gratis y <span className="text-amber-300 italic">empieza a jugar hoy</span>
            </h2>

            <p className="ui-text-lead text-emerald-100 max-w-lg mx-auto lg:mx-0">
              Únete a más de 25,000 deportistas en Lima que ya encuentran sus canchas, rutas y productos deportivos sin complicaciones.
            </p>

            <div className="flex flex-wrap items-center justify-center lg:justify-start gap-4 pt-2">
              <button
                onClick={onOpenDownloadModal}
                className="flex items-center gap-3 px-6 py-4 bg-slate-900 text-white rounded-2xl hover:bg-black transition-all shadow-xl hover:-translate-y-0.5 active:translate-y-0 group border border-white/10 cursor-pointer"
              >
                <Play className="w-8 h-8 text-emerald-400 fill-emerald-400 group-hover:scale-110 transition-transform" />
                <div className="text-left">
                  <div className="text-[10px] uppercase font-bold text-slate-300">Disponible en</div>
                  <div className="text-base font-black">Google Play</div>
                </div>
              </button>

              <button
                onClick={handleDownloadApk}
                className="flex items-center gap-3 px-6 py-4 bg-emerald-400 hover:bg-emerald-300 text-slate-950 font-bold rounded-2xl transition-all shadow-xl hover:-translate-y-0.5 active:translate-y-0 group border border-emerald-300/50 cursor-pointer"
              >
                <Download className="w-8 h-8 text-slate-950 group-hover:scale-110 transition-transform shrink-0" />
                <div className="text-left">
                  <div className="text-[10px] uppercase font-extrabold text-slate-900/80">Instalador Web</div>
                  <div className="text-base font-black text-slate-950">Descargar APK</div>
                </div>
              </button>
            </div>
          </div>

          <div className="lg:col-span-5 flex flex-col items-center justify-center">
            <div className="bg-white text-slate-900 p-6 rounded-3xl shadow-2xl border border-slate-100 max-w-sm text-center space-y-4 relative">
              <h3 className="font-extrabold text-xl text-slate-900">Apunta tu cámara aquí</h3>

              <div className="p-4 bg-slate-50 border border-slate-200 rounded-2xl inline-block shadow-inner">
                <svg className="w-44 h-44 text-slate-900" viewBox="0 0 100 100" fill="currentColor">
                  <rect x="0" y="0" width="100" height="100" fill="white" />
                  <rect x="10" y="10" width="25" height="25" fill="#059669" />
                  <rect x="15" y="15" width="15" height="15" fill="white" />
                  <rect x="18" y="18" width="9" height="9" fill="#059669" />

                  <rect x="65" y="10" width="25" height="25" fill="#059669" />
                  <rect x="70" y="15" width="15" height="15" fill="white" />
                  <rect x="73" y="18" width="9" height="9" fill="#059669" />

                  <rect x="10" y="65" width="25" height="25" fill="#059669" />
                  <rect x="15" y="70" width="15" height="15" fill="white" />
                  <rect x="18" y="73" width="9" height="9" fill="#059669" />

                  <rect x="42" y="10" width="8" height="8" fill="#1F2937" />
                  <rect x="52" y="18" width="8" height="8" fill="#1F2937" />
                  <rect x="42" y="26" width="8" height="8" fill="#1F2937" />
                  <rect x="10" y="42" width="8" height="8" fill="#1F2937" />
                  <rect x="26" y="42" width="8" height="8" fill="#1F2937" />
                  <rect x="42" y="42" width="16" height="16" fill="#047857" />
                  <rect x="65" y="42" width="8" height="8" fill="#1F2937" />
                  <rect x="81" y="42" width="8" height="8" fill="#1F2937" />
                  <rect x="42" y="65" width="8" height="8" fill="#1F2937" />
                  <rect x="52" y="75" width="12" height="12" fill="#059669" />
                  <rect x="70" y="65" width="18" height="18" fill="#1F2937" />
                </svg>
              </div>

              <p className="ui-text-lead text-slate-500 font-medium">
                Compatible con la cámara estándar de cualquier iPhone o Android.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
