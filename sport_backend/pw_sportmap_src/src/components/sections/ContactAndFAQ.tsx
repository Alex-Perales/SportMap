import React, { useState } from 'react';
import { FAQ_DATA } from '../../data/sportmapData';
import { ChevronDown, ChevronUp, Mail, Phone, MapPin, Send, CheckCircle2, HelpCircle, MessageSquare } from 'lucide-react';

export const ContactAndFAQ: React.FC = () => {
  const [openIndex, setOpenIndex] = useState<number | null>(0);
  const [formData, setFormData] = useState({ name: '', email: '', subject: 'Consulta General', message: '' });
  const [submitted, setSubmitted] = useState(false);

  const toggleFAQ = (index: number) => {
    setOpenIndex(openIndex === index ? null : index);
  };

  const handleFormSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.email || !formData.message) return;
    setSubmitted(true);
    setTimeout(() => {
      setSubmitted(false);
      setFormData({ name: '', email: '', subject: 'Consulta General', message: '' });
    }, 4000);
  };

  return (
    <section id="contacto" className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto space-y-3 mb-16">
          <h2 className="ui-title-section text-slate-900">
            Estamos aquí para <span className="text-emerald-600 italic">ayudarte</span>
          </h2>
          <p className="ui-text-lead text-slate-600">
            ¿Tienes alguna duda sobre la app, quieres inscribir tu cancha o consultar sobre la cobertura en tu distrito?
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
          
          {/* FAQ Accordion - 7 Cols */}
          <div className="lg:col-span-7 space-y-4">
            <h3 className="ui-title-sub text-slate-900 mb-4 flex items-center gap-2">
              <MessageSquare className="w-5 h-5 text-emerald-600" /> Preguntas Frecuentes
            </h3>

            <div className="space-y-3">
              {FAQ_DATA.map((faq, index) => {
                const isOpen = openIndex === index;
                return (
                  <div
                    key={index}
                    className="border border-slate-200 rounded-2xl overflow-hidden transition-all duration-200 bg-slate-50/50"
                  >
                    <button
                      onClick={() => toggleFAQ(index)}
                      className="w-full p-4 sm:p-5 text-left flex items-center justify-between font-bold text-slate-900 hover:text-emerald-600 text-lg sm:text-xl gap-3"
                    >
                      <span className="flex-1">{faq.question}</span>
                      <div className={`p-1.5 rounded-lg shrink-0 transition-transform ${isOpen ? 'bg-emerald-600 text-white rotate-180' : 'bg-slate-200 text-slate-700'}`}>
                        <ChevronDown className="w-4 h-4" />
                      </div>
                    </button>

                    {isOpen && (
                      <div className="px-4 sm:px-5 pb-5 text-base sm:text-lg text-slate-600 leading-relaxed border-t border-slate-200/60 pt-3 animate-in fade-in">
                        {faq.answer}
                      </div>
                    )}
                  </div>
                );
              })}
            </div>

            {/* Coverage Expansion Note */}
            <div className="mt-8 p-5 rounded-2xl bg-gradient-to-r from-emerald-50 to-teal-50 border border-emerald-200 space-y-2">
              <h4 className="font-extrabold text-slate-900 text-lg flex items-center gap-2">
                <MapPin className="w-4 h-4 text-emerald-600" /> Cobertura en Lima Metropolitana
              </h4>
              <p className="text-base text-slate-600 leading-relaxed">
                SportMap está diseñado para servir a todo Lima. Actualmente contamos con alta densidad de canchas, complejos y rutas en <strong>San Borja, Surco, Miraflores, San Isidro, La Victoria, Cercado de Lima, Barranco y Costa Verde</strong>, incorporando nuevos establecimientos deportivos cada semana.
              </p>
            </div>
          </div>

          {/* Contact Form & Info - 5 Cols */}
          <div className="lg:col-span-5 space-y-6">
            <div className="bg-slate-900 text-white p-6 sm:p-8 rounded-3xl shadow-xl border border-slate-800">
              <h3 className="ui-title-sub text-white mb-2">Envíanos un mensaje</h3>
              <p className="text-base text-slate-400 mb-6">
                Escríbenos para sugerencias, soporte técnico o alianzas comerciales con canchas y tiendas.
              </p>

              <form onSubmit={handleFormSubmit} className="space-y-4">
                <div>
                  <label className="block text-base font-semibold text-slate-300 mb-1">Nombre completo</label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    placeholder="Ej. Juan Pérez"
                    className="w-full px-3.5 py-2.5 bg-slate-800 border border-slate-700 rounded-xl text-lg text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>

                <div>
                  <label className="block text-base font-semibold text-slate-300 mb-1">Correo electrónico</label>
                  <input
                    type="email"
                    required
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    placeholder="juan@ejemplo.com"
                    className="w-full px-3.5 py-2.5 bg-slate-800 border border-slate-700 rounded-xl text-lg text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>

                <div>
                  <label className="block text-base font-semibold text-slate-300 mb-1">Asunto</label>
                  <select
                    value={formData.subject}
                    onChange={(e) => setFormData({ ...formData, subject: e.target.value })}
                    className="w-full px-3.5 py-2.5 bg-slate-800 border border-slate-700 rounded-xl text-lg text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="Consulta General">Consulta General</option>
                    <option value="Inscribir Cancha o Complejo">Inscribir Cancha o Complejo</option>
                    <option value="Unir Tienda Deportiva">Unir Tienda Deportiva</option>
                    <option value="Soporte App">Soporte App</option>
                  </select>
                </div>

                <div>
                  <label className="block text-base font-semibold text-slate-300 mb-1">Mensaje</label>
                  <textarea
                    rows={3}
                    required
                    value={formData.message}
                    onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                    placeholder="Escribe tu mensaje aquí..."
                    className="w-full px-3.5 py-2.5 bg-slate-800 border border-slate-700 rounded-xl text-lg text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>

                <button
                  type="submit"
                  className="w-full py-3 bg-emerald-600 hover:bg-emerald-500 text-white font-extrabold text-lg rounded-xl transition-all shadow-md flex items-center justify-center gap-2"
                >
                  <Send className="w-4 h-4" /> Enviar Mensaje
                </button>

                {submitted && (
                  <div className="p-3 bg-emerald-500/20 border border-emerald-400/30 rounded-xl flex items-center gap-2 text-emerald-300 text-base font-medium animate-in fade-in">
                    <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                    ¡Mensaje enviado con éxito! Te responderemos en breve.
                  </div>
                )}
              </form>

              {/* Direct Info */}
              <div className="mt-8 pt-6 border-t border-slate-800 space-y-3 text-base text-slate-300">
                <div className="flex items-center gap-2">
                  <Mail className="w-4 h-4 text-emerald-400" />
                  <span>Soporte: <strong>contacto@sportmap.pe</strong></span>
                </div>
                <div className="flex items-center gap-2">
                  <Mail className="w-4 h-4 text-emerald-400" />
                  <span>Aliados Canchas: <strong>aliados@sportmap.pe</strong></span>
                </div>
              </div>

            </div>
          </div>

        </div>

      </div>
    </section>
  );
};
