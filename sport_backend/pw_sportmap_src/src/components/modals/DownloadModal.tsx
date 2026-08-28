import React from 'react';
import { X, QrCode, Play, Download } from 'lucide-react';

interface DownloadModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const DownloadModal: React.FC<DownloadModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  const handleDownloadApk = () => {
    // El backend sirve el APK real en /download (ver sport_backend/app/main.py).
    window.location.href = "/download";
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/70 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="relative w-full max-w-lg bg-white rounded-2xl shadow-2xl overflow-hidden border border-slate-100">
        {/* Header background */}
        <div className="bg-gradient-to-r from-emerald-600 to-teal-700 px-6 py-6 text-white relative">
          <button
            onClick={onClose}
            className="absolute top-4 right-4 p-2 text-white/80 hover:text-white rounded-full hover:bg-white/10 transition-colors cursor-pointer"
            aria-label="Cerrar modal"
          >
            <X className="w-5 h-5" />
          </button>
          <div className="flex items-center gap-2 mb-1">
            <span className="px-2.5 py-0.5 rounded-full text-sm font-semibold bg-emerald-400 text-slate-900">
              App Oficial Perú 🇵🇪
            </span>
          </div>
          <h3 className="text-2xl font-extrabold tracking-tight">Descarga SportMap gratis</h3>
          <p className="text-emerald-100 text-base mt-1">
            Encuentra canchas, rutas y equipamiento deportivo en todo Lima al instante.
          </p>
        </div>

        {/* Content */}
        <div className="p-6 space-y-5">
          {/* Store & APK Buttons */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-2">
            <a
              href="#download"
              onClick={onClose}
              className="flex items-center justify-center gap-2.5 px-4 py-3 bg-slate-900 text-white rounded-xl hover:bg-slate-800 transition-all shadow-md group cursor-pointer"
            >
              <Play className="w-6 h-6 text-emerald-400 fill-emerald-400 group-hover:scale-110 transition-transform shrink-0" />
              <div className="text-left">
                <div className="text-[10px] uppercase font-medium text-slate-300">Disponible en</div>
                <div className="text-sm sm:text-base font-bold leading-tight">Google Play</div>
              </div>
            </a>

            <button
              onClick={() => {
                handleDownloadApk();
                onClose();
              }}
              className="flex items-center justify-center gap-2.5 px-4 py-3 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-all shadow-md group cursor-pointer"
            >
              <Download className="w-6 h-6 text-white group-hover:scale-110 transition-transform shrink-0" />
              <div className="text-left">
                <div className="text-[10px] uppercase font-medium text-emerald-100">Instalador Web</div>
                <div className="text-sm sm:text-base font-bold leading-tight">Descargar APK</div>
              </div>
            </button>
          </div>

          {/* QR Code Section */}
          <div className="flex flex-col sm:flex-row items-center text-center sm:text-left gap-4 p-4 bg-slate-50 rounded-xl border border-slate-200 mb-6">
            <div className="bg-white p-2 rounded-lg border border-slate-200 shadow-sm shrink-0">
              {/* QR real generado por el backend, apunta a /download */}
              <img
                src="/download/qr.svg"
                alt="Código QR para descargar la app SportMap"
                className="w-20 h-20"
              />
            </div>
            <div>
              <div className="flex items-center gap-1.5 text-sm font-bold text-emerald-600 uppercase tracking-wide">
                <QrCode className="w-4 h-4" /> Escanea con tu cámara
              </div>
              <h4 className="text-base font-bold text-slate-800 mt-0.5">Escaneo directo</h4>
              <p className="text-sm text-slate-600 mt-1">
                Apunta la cámara de tu celular al código QR para ir directo a la descarga en tu tienda.
              </p>
            </div>
          </div>

          <div className="mt-5 text-center text-sm text-slate-500">
            Compatible con Android 7.0+ • 100% Gratuito
          </div>
        </div>
      </div>
    </div>
  );
};
